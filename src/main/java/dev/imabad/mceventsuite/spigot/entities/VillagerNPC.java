package dev.imabad.mceventsuite.spigot.entities;

import java.lang.reflect.Field;
import java.util.*;

import com.destroystokyo.paper.entity.ai.MobGoalHelper;
import com.destroystokyo.paper.entity.ai.PaperMobGoals;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class VillagerNPC extends net.minecraft.world.entity.npc.Villager {

    public static final String busyTag = "&c&lBusy";
    private String name;
    private Location spawn;
    private boolean moving = false;
    private Location currentlyMovingTo;
    private Location spawnLocation = null;
    private boolean isBusy = false;

    public VillagerNPC(EntityType<VillagerNPC> type, Level world){
        super(type, world);
        System.out.println("C1 Removing goals");
//        Bukkit.getMobGoals().removeAllGoals((Villager)this.getBukkitCreature());
//        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F));
    }

    public VillagerNPC(Level world, Location spawnLocation, String name) {
        super(EntityType.VILLAGER, world);
        this.spawnLocation = spawnLocation;
        this.name = name;
        System.out.println("C2 Removing goals");
//        Bukkit.getMobGoals().removeAllGoals((Villager)this.getBukkitCreature());
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
        Collection<?> goalC = (Collection<?>) getPrivateField("d", GoalSelector.class,
                goalSelector);
        goalC.clear();
        Collection<?> targetC = (Collection<?>) getPrivateField("d", GoalSelector.class,
                targetSelector);
        targetC.clear();
    }

    public VillagerNPC spawn(Location loc, String name) {
        spawn = loc;
        spawnLocation = loc;
        Level nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        setPos(loc.getX(), loc.getY(), loc.getZ());
        setRot(loc.getYaw(), loc.getPitch());
        Villager merchant = (Villager) this.getBukkitEntity();
        merchant.setAdult();
        merchant.setBreed(false);
        merchant.setAI(false);
        merchant.setCustomName(StringUtils.colorizeMessage(getUnformattedName()));
        merchant.setCustomNameVisible(true);
        merchant.setHealth(20D);
        merchant.setRemoveWhenFarAway(false);
        nmsWorld.addFreshEntity(this, SpawnReason.CUSTOM);
        return this;
    }

    public void moveToBlock(Location l, float speed) {
        clearGoals();
        setNoAi(false);
        moving = true;
        currentlyMovingTo = l;
        setPos(getX(), getY(), getZ());
        setRot(l.getYaw(), l.getPitch());
        Villager merchant = (Villager) this.getBukkitEntity();
        merchant.getLocation().setDirection(l.getDirection());
        Path pathEntity = this.navigation.createPath(new BlockPos(l.getX(), l.getY(), l.getZ()), 0);

        getNavigation().moveTo(pathEntity, speed);
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
