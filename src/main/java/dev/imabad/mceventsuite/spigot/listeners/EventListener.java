package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.stage.StageModule;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onHunger(FoodLevelChangeEvent foodLevelChangeEvent){
        foodLevelChangeEvent.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent){
        if(entityDamageEvent.getEntity() instanceof Player){
            Player player = (Player) entityDamageEvent.getEntity();
            if(RegionUtils.isInRegion(player, "KOTH")){
                entityDamageEvent.setDamage(0);
                entityDamageEvent.setCancelled(false);
            } else {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

}
