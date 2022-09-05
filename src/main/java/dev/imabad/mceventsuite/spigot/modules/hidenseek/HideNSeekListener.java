package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import dev.imabad.mceventsuite.core.EventCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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
