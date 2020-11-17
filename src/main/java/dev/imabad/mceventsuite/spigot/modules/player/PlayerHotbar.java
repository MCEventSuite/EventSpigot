package dev.imabad.mceventsuite.spigot.modules.player;

import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerHotbar {

  public static ItemStack NAVIGATION = ItemUtils.createItemStack(Material.COMPASS, "&cNavigation");
  public static ItemStack HELP_GUIDE = ItemUtils.createItemStack(Material.WRITTEN_BOOK, "&9Help Guide");
  public static ItemStack GADGETS = ItemUtils.createItemStack(Material.CHEST, "&aGadgets");

  public static void givePlayerInventory(Player player){
    player.getInventory().clear();
    Inventory inventory = player.getInventory();
    inventory.setItem(0, NAVIGATION);
    inventory.setItem(1, HELP_GUIDE);
    inventory.setItem(7, GADGETS);
    ItemStack EVENT_PASS = ItemUtils.createItemStack(Material.PAPER, "&e" + player.getName() + "'s Event Pass");
    inventory.setItem(8, EVENT_PASS);
    player.updateInventory();
  }
}
