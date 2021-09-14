package dev.imabad.mceventsuite.spigot.modules.eventblocker;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.apache.logging.log4j.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.Optional;

public class EventBlockListener implements Listener {

    EventBlockerConfig config;

    public void setConfig(EventBlockerConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onEvent(Event e) {
        String cName = e.getClass().getSimpleName().toLowerCase();
        //If the configuration has the event
        if (config.getBlockedEvents().contains(cName)) {
            //If the event is a redstone event, cancel it
            if (BlockRedstoneEvent.class.isAssignableFrom(e.getClass())) {
                ((BlockRedstoneEvent) e).setNewCurrent(((BlockRedstoneEvent) e).getOldCurrent());
            }

            //If the event is cancellable, check to see if you can cancel it.
            else if (Cancellable.class.isAssignableFrom(e.getClass())) {
                //If there's a player involved, set the player
                Optional<Player> player = checkPlayer(e);
                // If there is no player, or the player does not have permissions to allow the event, cancel te event.
                if (!player.isPresent() || (!player.get().hasPermission(cName))) ((Cancellable) e).setCancelled(true);
            }
        }
    }

    private Optional<Player> checkPlayer(Event e) {
        try {
            Object player = e.getClass().getDeclaredMethod("getPlayer").invoke(e);

            if (player instanceof Player) {
                return Optional.of((Player) player);

            }
            else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
