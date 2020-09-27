package dev.imabad.mceventsuite.spigot.impl;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.ServerOperator;

import java.lang.reflect.Field;
import java.util.List;

public class EventPermissible extends PermissibleBase {

    private static Field ATTACHMENTS_FIELD;

    static {
        try {
            ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            ATTACHMENTS_FIELD.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private PermissibleBase oldPermissible;
    private EventPlayer player;

    public EventPermissible(ServerOperator opable, EventPlayer player) {
        super(opable);
        this.player = player;
    }

    @Override
    public boolean hasPermission(String inName) {
        return player.hasPermission(inName);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return player.hasPermission(perm.getName());
    }

    @Override
    public boolean isPermissionSet(String name) {
        return hasPermission(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return hasPermission(perm);
    }

    @Override
    public void recalculatePermissions() {
        super.recalculatePermissions();
        if(player != null){
            EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUUID()).ifPresent(player1 -> {
                player = player1;
            });
        }
    }

    @Override
    public boolean isOp() {
        if(player == null){
            return false;
        }
        return player.hasPermission("eventsuite.op");
    }

    public void loadExistingAttachments(List<PermissionAttachment> attachmentList) throws IllegalAccessException {
        ATTACHMENTS_FIELD.set(attachmentList, this);
    }

    public PermissibleBase getOldPermissible() {
        return oldPermissible;
    }

    public void setOldPermissible(PermissibleBase oldPermissible) {
        this.oldPermissible = oldPermissible;
    }
}
