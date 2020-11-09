package dev.imabad.mceventsuite.spigot.modules.stafftrack;

import dev.imabad.mceventsuite.core.EventCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StaffTrackListener implements Listener {

    private StaffTrackModule module;

    public StaffTrackListener(StaffTrackModule module){
        this.module = module;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        EventCore.getInstance().getEventPlayerManager().getPlayer(event.getPlayer().getUniqueId()).ifPresent(player -> {
            if(player.getRank().hasPermission("eventsuite.staffchat")){
                module.addMoved(player.getUUID());
            }
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        EventCore.getInstance().getEventPlayerManager().getPlayer(event.getPlayer().getUniqueId()).ifPresent(player -> {
            if(player.getRank().hasPermission("eventsuite.staffchat")){
                module.addMoved(player.getUUID());
            }
        });
    }
}
