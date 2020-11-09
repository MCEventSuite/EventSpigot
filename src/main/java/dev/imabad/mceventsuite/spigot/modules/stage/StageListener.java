package dev.imabad.mceventsuite.spigot.modules.stage;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class StageListener implements Listener {

  private StageModule module;

  public StageListener(StageModule module) {
    this.module = module;
  }

  @EventHandler
  public void onDismount(EntityDismountEvent entityDismountEvent){
    if(entityDismountEvent.getEntityType() == EntityType.PLAYER){
      if(entityDismountEvent.getDismounted().getType() == EntityType.CHICKEN){
        module.removeSeat(entityDismountEvent.getEntity().getUniqueId());
      }
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event){
    module.removeSeat(event.getPlayer().getUniqueId());
  }

}
