package dev.imabad.mceventsuite.spigot.entities;

import java.lang.reflect.Field;

import net.minecraft.server.v1_16_R2.Entity;
import net.minecraft.server.v1_16_R2.EnumCreatureType;
import net.minecraft.server.v1_16_R2.IRegistry;
import net.minecraft.server.v1_16_R2.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.Registry;

public enum EntityTypes {
    VILLAGER("villager", 120, net.minecraft.server.v1_16_R2.EntityTypes.Builder.a(VillagerNPC::new, EnumCreatureType.CREATURE));
    private String name;
    private int id;
    private net.minecraft.server.v1_16_R2.EntityTypes.Builder<?> builder;

    EntityTypes(String name, int id, final net.minecraft.server.v1_16_R2.EntityTypes.Builder<?> builder) {
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

    private static void addToMaps(net.minecraft.server.v1_16_R2.EntityTypes.Builder<?> builder, String name, int id) {
        String nameSpace = "eventspigot_" + name;
        net.minecraft.server.v1_16_R2.IRegistry.a(IRegistry.ENTITY_TYPE, nameSpace, builder.a(nameSpace));
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
