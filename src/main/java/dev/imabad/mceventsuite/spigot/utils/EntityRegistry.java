package dev.imabad.mceventsuite.spigot.utils;

import com.destroystokyo.paper.entity.PaperPathfinder;
import com.destroystokyo.paper.entity.ai.PaperMobGoals;
import com.google.common.collect.ImmutableSet;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.entities.EntityTypes;
import dev.imabad.mceventsuite.spigot.entities.VillagerNPC;
import net.minecraft.server.v1_16_R2.Activity;
import net.minecraft.server.v1_16_R2.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

public class EntityRegistry {

    private static final HashMap<String, Consumer<PlayerInteractEntityEvent>> villagers = new HashMap<>();

    public static void registerInteraction(String itemName,
                                           Consumer<PlayerInteractEntityEvent> eventConsumer) {
        villagers.put(itemName, eventConsumer);
    }

    public static Villager registerVillager(Location location, String name) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        Bukkit.getMobGoals().removeAllGoals(villager);
        EntityVillager entityVillager = ((CraftVillager)villager).getHandle();
        entityVillager.getBehaviorController().a(ImmutableSet.of(Activity.IDLE));
        entityVillager.getBehaviorController().a(Collections.emptyList());
        entityVillager.getBehaviorController().a(Activity.IDLE);
        entityVillager.getBehaviorController().b(Activity.IDLE);
        entityVillager.getBehaviorController().a(0, 0);
        villager.setAdult();
        villager.setBreed(false);
        villager.setCustomName(StringUtils.colorizeMessage(name));
        villager.setCustomNameVisible(true);
        villager.setHealth(20D);
        villager.setRemoveWhenFarAway(false);
        return villager;
    }

    public static Villager registerNPCWithInventory(Location location, String name,
                                                       EventInventory inventory) {
        Villager npc = registerVillager(location, name);
        registerInteraction(name, event -> inventory.open(event.getPlayer(), null));
        return npc;
    }

}
