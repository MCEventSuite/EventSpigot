package dev.imabad.mceventsuite.spigot.modules.player;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.interactions.Interaction;
import dev.imabad.mceventsuite.spigot.interactions.InteractionRegistry;
import dev.imabad.mceventsuite.spigot.modules.eventpass.inventories.EventPassInventoryPage;
import dev.imabad.mceventsuite.spigot.modules.warps.inventories.WarpInventoryPage;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerModule extends Module implements Listener {

  @Override
  public String getName() {
    return "player";
  }

  @Override
  public void onEnable() {
    InteractionRegistry.registerInteraction(Interaction.RIGHT_CLICK, this::onPlayerRightClick);
  }

  @Override
  public void onDisable() {

  }


  @Override
  public List<Class<? extends Module>> getDependencies() {
    return Collections.emptyList();
  }

  public void onPlayerRightClick(Event event){
    PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
    Player player = playerInteractEvent.getPlayer();
    if(playerInteractEvent.getItem() != null && playerInteractEvent.getItem().getType() != Material.AIR){
      ItemStack itemStack = playerInteractEvent.getItem();
      if(itemStack.getType().equals(PlayerHotbar.GADGETS.getType())){
        // Open Gadgets
      } else if(itemStack.getType().equals(PlayerHotbar.NAVIGATION.getType())){
        playerInteractEvent.setCancelled(true);
        new WarpInventoryPage(player).open(player, null);
      } else if(itemStack.getType().equals(PlayerHotbar.HELP_GUIDE.getType())){
        // Open book
      } else if(itemStack.getType().equals(Material.PAPER) && ItemUtils.equalsItemName(itemStack, player.getName() + "'s Event Pass")){
        // Open Player info
        EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresent( eventPlayer -> {
          new EventPassInventoryPage(player, eventPlayer, 0, 1, 25, 1).open(player, null);
        });
      }
    }
  }
}
