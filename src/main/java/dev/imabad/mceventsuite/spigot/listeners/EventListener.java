package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

import java.util.Arrays;
import java.util.List;

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

    private static final List<Material> DISALLOWED_INTERACT_MATERIALS = Arrays.asList(Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.CRIMSON_BUTTON, Material.OAK_BUTTON, Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON, Material.SPRUCE_BUTTON, Material.STONE_BUTTON, Material.WARPED_BUTTON);

    @EventHandler
    public void onExplode(PlayerInteractEvent playerInteractEvent) {
        if(playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getClickedBlock() != null) {
            if(DISALLOWED_INTERACT_MATERIALS.contains(playerInteractEvent.getClickedBlock().getType())){
                playerInteractEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLecternTake(PlayerTakeLecternBookEvent event) {
        event.setCancelled(true);
    }

    //Prevent players from taking armor from armorstands.
    @EventHandler
    public void onArmorStandTake(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }
}
