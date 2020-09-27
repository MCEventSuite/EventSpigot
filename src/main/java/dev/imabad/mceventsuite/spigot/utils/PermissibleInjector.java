package dev.imabad.mceventsuite.spigot.utils;

import dev.imabad.mceventsuite.spigot.impl.EventPermissible;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class PermissibleInjector {

    private static Field HUMAN_ENTITY_PERM;
    private static Field ATTACHMENTS_FIELD;

    static {
        try {
            Field humanPerm = null;
            try {
                humanPerm = Class.forName("org.bukkit.craftbukkit." + CraftBukkitHelper.getPackageVersion() + ".entity.CraftHumanEntity").getDeclaredField("perm");
                humanPerm.setAccessible(true);
                ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
                ATTACHMENTS_FIELD.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            if(humanPerm != null)
                HUMAN_ENTITY_PERM = humanPerm;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void inject(Player player, EventPermissible eventPermissible) throws Exception{
        PermissibleBase permissibleBase = (PermissibleBase) HUMAN_ENTITY_PERM.get(player);
        if(permissibleBase instanceof EventPermissible){
            throw new IllegalStateException("Already injected EventPermission into player");
        }
//        List<PermissionAttachment> permissionAttachments = (List<PermissionAttachment>) ATTACHMENTS_FIELD.get(permissibleBase);
//        eventPermissible.loadExistingAttachments(permissionAttachments);
//        permissionAttachments.clear();
        permissibleBase.clearPermissions();
        eventPermissible.setOldPermissible(permissibleBase);
        HUMAN_ENTITY_PERM.set(player, eventPermissible);
    }

    public static void uninject(Player player) throws IllegalAccessException {
        PermissibleBase permissibleBase = (PermissibleBase) HUMAN_ENTITY_PERM.get(player);
        if(permissibleBase instanceof EventPermissible){
            EventPermissible eventPermissible = (EventPermissible) permissibleBase;
            permissibleBase.clearPermissions();
            HUMAN_ENTITY_PERM.set(player, eventPermissible.getOldPermissible());
        }
    }


}
