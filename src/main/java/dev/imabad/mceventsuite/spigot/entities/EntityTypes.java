package dev.imabad.mceventsuite.spigot.entities;

import java.lang.reflect.Field;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Location;

public enum EntityTypes {

    VILLAGER("villager", 120, EntityType.Builder.of(VillagerNPC::new, MobCategory.CREATURE));
    private String name;
    private int id;
    private EntityType.Builder<?> builder;

    EntityTypes(String name, int id, final EntityType.Builder<?> builder) {
        this.name = name;
        this.id = id;
        this.builder = builder;
        addToMaps(builder, name, id);
    }

    public static Entity spawnEntity(Entity entity, Location loc, String name) {
        if (entity instanceof VillagerNPC) {
            VillagerNPC villagerNPC = (VillagerNPC) entity;
            return villagerNPC.spawn(loc, name);
        }
        return null;
    }

    private static void addToMaps(EntityType.Builder<?> builder, String name, int id) {
        String nameSpace = "eventspigot_" + name;
        Registry.register(Registry.ENTITY_TYPE, nameSpace, builder.build(nameSpace));
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
}
