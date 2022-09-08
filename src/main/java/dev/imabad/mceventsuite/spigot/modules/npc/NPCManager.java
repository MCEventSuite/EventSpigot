package dev.imabad.mceventsuite.spigot.modules.npc;

import com.mojang.authlib.GameProfile;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
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
import java.util.function.BiConsumer;
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

    public NPC createNpc(String displayName, EntityType type, BiConsumer<Player, NPC> onClick, Location location) {
        Entity entity = null;
        if(type == EntityType.PLAYER) {
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), displayName);
            entity = new ServerPlayer(server, level, gameProfile, null);
        } else if (type == EntityType.VILLAGER) {
            entity = new Villager(
                    net.minecraft.world.entity.EntityType.VILLAGER,
                    this.level,
                    VillagerType.PLAINS);
        } else if(type == EntityType.IRON_GOLEM) { //TODO at some point generify this
            entity = new IronGolem(
                    net.minecraft.world.entity.EntityType.IRON_GOLEM,
                    this.level
            );
        }

        if(entity != null) {
            entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            final NPC npc = new NPC(displayName, entity, onClick);
            this.npcList.add(npc);
            return npc;
        }

        return null;
    }

    public void handleInteract(Player player, int id) {
        for(NPC npc : this.npcList) {
            if(npc.getEntity().getId() == id) {
                npc.getInteractEvent().accept(player, npc);
            }
        }
    }

    public void setWorld(World world) {
        this.world = world;
        this.level = ((CraftWorld) world).getHandle();
        this.createNpc("David", EntityType.PLAYER, (player, npc) -> player.sendMessage(npc.getDisplayName() + ": Hi!"),
                new Location(Bukkit.getWorld("world"), -49, 30, 120));
    }

    public List<NPC> getNpcList() {
        return this.npcList;
    }
}
