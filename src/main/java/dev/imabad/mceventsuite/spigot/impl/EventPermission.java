package dev.imabad.mceventsuite.spigot.impl;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class EventPermission extends Permission {

    @Override
    public String getName() {
        return "EventCore";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        System.out.println("Checking if player has permission");
        Optional<EventPlayer> eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(Bukkit.getOfflinePlayer(player).getUniqueId());
        return eventPlayer.map(eventPlayer1 -> eventPlayer1.hasPermission(permission)).orElse(false);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        Optional<EventPlayer> optionalPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(Bukkit.getOfflinePlayer(player).getUniqueId());
        if(optionalPlayer.isPresent()){
            EventPlayer eventPlayer = optionalPlayer.get();
            if(eventPlayer.playerHasPermission(permission)){
                return false;
            }
            eventPlayer.addPermission(permission);
            return true;
        }
        return false;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        Optional<EventPlayer> optionalPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(Bukkit.getOfflinePlayer(player).getUniqueId());
        if(optionalPlayer.isPresent()){
            EventPlayer eventPlayer = optionalPlayer.get();
            if(!eventPlayer.playerHasPermission(permission)){
                return false;
            }
            eventPlayer.removePermission(permission);
            return true;
        }
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return new String[]{};
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return "";
    }

    @Override
    public String[] getGroups() {
        return new String[]{};
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }
}
