package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.events.JoinEvent;
import dev.imabad.mceventsuite.spigot.impl.SpigotPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        SpigotPlayer spigotPlayer = new SpigotPlayer(playerJoinEvent.getPlayer());
        EventCore.getInstance().getEventPlayerManager().addPlayer(spigotPlayer);
        EventCore.getInstance().getEventRegistry().handleEvent(new JoinEvent(spigotPlayer));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent playerQuitEvent){
        EventCore.getInstance().getEventPlayerManager().removePlayer(EventCore.getInstance().getEventPlayerManager().getPlayer(playerQuitEvent.getPlayer().getUniqueId()));
    }

}
