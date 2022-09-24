package dev.imabad.mceventsuite.spigot.modules.npc;

import com.mojang.authlib.GameProfile;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NPCManager {
    final DedicatedServer server;
    final NPCModule npcModule;
    final List<NPC> npcList;
    private Team npcTeam;
    World world;

    public NPCManager(NPCModule npcModule) {
        this.npcModule = npcModule;
        this.server = ((CraftServer) EventSpigot.getInstance().getServer()).getServer();
        this.npcList = new ArrayList<>();
    }

    public NPC createNpc(String displayName, EntityType type, NPCInteraction interaction, Location location) {
        return this.createNpc(displayName, type, interaction, location, null);
    }


    public NPC createNpc(String displayName, EntityType type, NPCInteraction interaction, Location location,
                         Character icon) {
        final ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        LivingEntity entity = null;
        if (type == EntityType.PLAYER) {
            final String strippedName = ChatColor.stripColor(StringUtils.colorizeMessage(displayName));
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(),
                    strippedName.length() >= 16 ? strippedName.substring(0, 15) : strippedName);
            entity = new ServerPlayer(server, level, gameProfile, null);

            if (this.npcTeam != null)
                this.npcTeam.addEntry(gameProfile.getName());
            entity.getEntityData().set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 126);
        } else if (type == EntityType.VILLAGER) {
            entity = new Villager(
                    net.minecraft.world.entity.EntityType.VILLAGER,
                    level,
                    VillagerType.PLAINS);
        } else if (type == EntityType.IRON_GOLEM) { //TODO at some point generify this
            entity = new IronGolem(
                    net.minecraft.world.entity.EntityType.IRON_GOLEM,
                    level
            );
        }

        if (entity != null) {
            entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            this.setEntityName(entity, displayName);
            final NPC npc = new NPC(displayName, entity, interaction, icon);
            if (icon != null)
                this.setEntityName(npc.getArmorStand(), icon.toString());
            this.npcList.add(npc);
            return npc;
        }

        return null;
    }

    public void setEntityName(Entity entity, String displayName) {
        SynchedEntityData data = entity.getEntityData();
        final Optional<Component> componentOptional = Optional.of(Component.literal(StringUtils.colorizeMessage(displayName)));
        data.set(new EntityDataAccessor<>(2, EntityDataSerializers.OPTIONAL_COMPONENT), componentOptional);
        data.set(new EntityDataAccessor<>(3, EntityDataSerializers.BOOLEAN), true);
    }

    public void handleInteract(Player player, int id) {
        for (NPC npc : this.npcList) {
            if (npc.getEntity().getId() == id) {
                npc.getInteractEvent().interact(player, npc);
            }
        }
    }

    public void setWorld(World world) {
        this.world = world;

        if (EventSpigot.getInstance().getScoreboard().getTeam("NPC") == null) {
            final Team npcTeam = EventSpigot.getInstance().getScoreboard().registerNewTeam("NPC");
            npcTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            npcTeam.color(NamedTextColor.LIGHT_PURPLE);
            this.npcTeam = npcTeam;
        } else {
            this.npcTeam = EventSpigot.getInstance().getScoreboard().getTeam("NPC");
        }

        for (NPC npc : npcList) {
            if (npc.getEntity() instanceof ServerPlayer player) {
                this.npcTeam.addEntry(player.getGameProfile().getName());
            }
        }
    }

    public List<NPC> getNpcList() {
        return this.npcList;
    }
}