package dev.imabad.mceventsuite.spigot.entities;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

import net.kyori.text.Component;
import net.kyori.text.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R2.*;
import net.minecraft.server.v1_16_R2.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class VillagerNPC extends EntityVillager {

    public static final String busyTag = "Busy";
    private String name;
    private Location spawn;
    private boolean moving = false;
    private Location currentlyMovingTo;
    private Location spawnLocation = null;
    private boolean isBusy = false;

    public VillagerNPC(EntityTypes<EntityVillager> entitytypes, World world){
        super(entitytypes, world);
    }

    public VillagerNPC(World world, Location spawnLocation, String name) {
        super(EntityTypes.VILLAGER, world);
        clearGoals();
        this.spawnLocation = spawnLocation;
        this.name = name;
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F));
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private void clearGoals() {
        LinkedHashSet<?> goalB = (LinkedHashSet<?>) getPrivateField("b", PathfinderGoalSelector.class,
                goalSelector);
        goalB.clear();
        LinkedHashSet<?> goalC = (LinkedHashSet<?>) getPrivateField("c", PathfinderGoalSelector.class,
                goalSelector);
        goalC.clear();
        LinkedHashSet<?> targetB = (LinkedHashSet<?>) getPrivateField("b", PathfinderGoalSelector.class,
                targetSelector);
        targetB.clear();
        LinkedHashSet<?> targetC = (LinkedHashSet<?>) getPrivateField("c", PathfinderGoalSelector.class,
                targetSelector);
        targetC.clear();
    }

    public VillagerNPC spawn(Location loc, String name) {
        spawn = loc;
        World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        Villager merchant = (Villager) this.getBukkitEntity();
        merchant.setAdult();
        merchant.setBreed(false);
        merchant.setCustomName(name);
        merchant.setCustomNameVisible(true);
        merchant.setHealth(20D);
        merchant.setRemoveWhenFarAway(false);
        nmsWorld.addEntity(this, SpawnReason.CUSTOM);
        return this;
    }

    public void moveToBlock(Location l, float speed) {
        moving = true;
        currentlyMovingTo = l;
        setPositionRotation(locX(), locY(), locZ(), l.getYaw(), l.getPitch());
        ;
        NBTTagCompound tag = new NBTTagCompound();
        this.save(tag);
        tag.setInt("NoAI", 0);
        this.load(tag);
        Villager merchant = (Villager) this.getBukkitEntity();
        merchant.getLocation().setDirection(l.getDirection());
        PathEntity pathEntity = this.navigation.a(new BlockPosition(l.getX(), l.getY(), l.getZ()), 0);

        getNavigation().a(pathEntity, speed);
    }

    public ChatComponentText getNormalName() {
        return new ChatComponentText(name);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public String getRawNormalName() {
        return ChatColor.stripColor(name);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        if (busy) {
            setCustomName(getNormalName());
            this.isBusy = true;
        }
        if (!busy) {
            setCustomName(getNormalName());
            this.isBusy = false;
        }
    }
}
