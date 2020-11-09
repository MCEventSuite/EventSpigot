package dev.imabad.mceventsuite.spigot.modules.shops.api;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IProduct {

  String getName();

  String getDisplayName();

  List<String> getLore();

  int getCost();

  default void onInteract(PlayerInteractEvent event) {

  }

  default void onEntityDamage(EntityDamageByEntityEvent event) {

  }

  ItemStack getItemStack();

  IShop getShop();

}
