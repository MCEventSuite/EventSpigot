package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import dev.imabad.mceventsuite.core.EventCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class HideNSeekListener implements Listener {
    private final HideNSeekModule module;

    public HideNSeekListener(HideNSeekModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof Player clicked) {
            Player player = event.getPlayer();

            if(module.getGame() != null && module.getGame().getStatus() == HideNSeekGame.GameStatus.STARTED) {
                if(module.getGame().getSeekers().contains(player.getUniqueId()) && module.getGame().getHiders().contains(clicked.getUniqueId())) {
                    module.catchHider(clicked, player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        module.leave(event.getPlayer());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.module.onWorldLoad(event);
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent event) {
        if(this.module.getGame() != null && event.getRegionId().equalsIgnoreCase("hideregion")) {
            if(this.module.getGame().getStatus() == HideNSeekGame.GameStatus.WAITING ||
                    this.module.getGame().getStatus() == HideNSeekGame.GameStatus.STARTED) {
                if(this.module.getGame().getAllPlayers().contains(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot leave the venue during Hide & Seek!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(this.module.getGame() != null) {
            if(this.module.getGame().getStatus() == HideNSeekGame.GameStatus.WAITING ||
                    this.module.getGame().getStatus() == HideNSeekGame.GameStatus.STARTED) {
                if(this.module.getGame().getAllPlayers().contains(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot leave the venue during Hide & Seek!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player player && event.getEntity() instanceof Player victim) {
            if(module.getGame() != null && module.getGame().getStatus() == HideNSeekGame.GameStatus.STARTED) {
                if(module.getGame().getSeekers().contains(player.getUniqueId()) && module.getGame().getHiders().contains(victim.getUniqueId())) {
                    module.catchHider(victim, player);
                }
            }
        }
    }
}
