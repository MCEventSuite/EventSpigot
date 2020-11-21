package dev.imabad.mceventsuite.spigot.entities;

import java.lang.reflect.Field;
import java.util.*;

import com.destroystokyo.paper.entity.ai.MobGoalHelper;
import com.destroystokyo.paper.entity.ai.PaperMobGoals;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class VillagerNPC extends EntityVillager {

    public static final String busyTag = "&c&lBusy";
    private String name;
    private Location spawn;
    private boolean moving = false;
    private Location currentlyMovingTo;
    private Location spawnLocation = null;
    private boolean isBusy = false;

    public VillagerNPC(net.minecraft.server.v1_16_R3.EntityTypes<EntityVillager> entitytypes, World world){
        super(entitytypes, world);
        System.out.println("C1 Removing goals");
        Bukkit.getMobGoals().removeAllGoals((Villager)this.getBukkitCreature());
//        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F));
    }

    public VillagerNPC(World world, Location spawnLocation, String name) {
        super(net.minecraft.server.v1_16_R3.EntityTypes.VILLAGER, world);
        this.spawnLocation = spawnLocation;
        this.name = name;
        System.out.println("C2 Removing goals");
        Bukkit.getMobGoals().removeAllGoals((Villager)this.getBukkitCreature());
//        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F));
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private void clearGoals() {
        Collection<?> goalC = (Collection<?>) getPrivateField("d", PathfinderGoalSelector.class,
                goalSelector);
        goalC.clear();
        Collection<?> targetC = (Collection<?>) getPrivateField("d", PathfinderGoalSelector.class,
                targetSelector);
        targetC.clear();
    }

    public VillagerNPC spawn(Location loc, String name) {
        spawn = loc;
        spawnLocation = loc;
        World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        Villager merchant = (Villager) this.getBukkitEntity();
        merchant.setAdult();
        merchant.setBreed(false);
        merchant.setAI(false);
        merchant.setCustomName(StringUtils.colorizeMessage(getUnformattedName()));
        merchant.setCustomNameVisible(true);
        merchant.setHealth(20D);
        merchant.setRemoveWhenFarAway(false);
        nmsWorld.addEntity(this, SpawnReason.CUSTOM);
        return this;
    }

    public void moveToBlock(Location l, float speed) {
        clearGoals();
        setNoAI(false);
        moving = true;
        currentlyMovingTo = l;
        setPositionRotation(locX(), locY(), locZ(), l.getYaw(), l.getPitch());
        Villager merchant = (Villager) this.getBukkitEntity();
        merchant.getLocation().setDirection(l.getDirection());
        PathEntity pathEntity = this.navigation.a(new BlockPosition(l.getX(), l.getY(), l.getZ()), 0);

        getNavigation().a(pathEntity, speed);
    }

    public String getNormalName() {
        return StringUtils.colorizeMessage(name);
    }

    public String getBusyName(){ return StringUtils.colorizeMessage(name + " - " + busyTag); }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public String getUnformattedName(){
        return name;
    }

    public String getRawNormalName() {
        return ChatColor.stripColor(name);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        if (busy) {
            this.getBukkitEntity().setCustomName(getBusyName());
            this.isBusy = true;
        }
        if (!busy) {
            this.getBukkitEntity().setCustomName(getNormalName());
            this.isBusy = false;
        }
    }
}
