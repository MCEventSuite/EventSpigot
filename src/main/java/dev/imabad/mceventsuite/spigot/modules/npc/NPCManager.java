package dev.imabad.mceventsuite.spigot.modules.npc;

import com.mojang.authlib.GameProfile;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class NPCManager {
    private ServerLevel level;
    final DedicatedServer server;
    final NPCModule npcModule;
    final List<NPC> npcList;

    World world;

    public NPCManager(NPCModule npcModule) {
        this.npcModule = npcModule;
        this.server = ((CraftServer) EventSpigot.getInstance().getServer()).getServer();
        this.npcList = new ArrayList<>();
    }

    public NPC createNpc(String displayName, EntityType type, Consumer<Player> onClick, Location location) {
        if(type == EntityType.PLAYER) {
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), displayName);
            final ServerPlayer player = new ServerPlayer(server, level, gameProfile, null);
            player.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            final NPC npc = new NPC(displayName, player, onClick);
            this.npcList.add(npc);
            return npc;
        }
        return null;
    }

    public void handleInteract(Player player, int id) {
        for(NPC npc : this.npcList) {
            if(npc.getEntity().getId() == id) {
                npc.getInteractEvent().accept(player);
            }
        }
    }

    public void setWorld(World world) {
        this.world = world;
        this.level = ((CraftWorld) world).getHandle();
        this.createNpc("David", EntityType.PLAYER, (player) -> player.sendMessage("Hi!"),
                new Location(Bukkit.getWorld("world"), -49, 30, 120));
    }

    public List<NPC> getNpcList() {
        return this.npcList;
    }
}
