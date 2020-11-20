package dev.imabad.mceventsuite.spigot.modules.shops.api;

import com.plotsquared.core.util.ItemUtil;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.entities.VillagerNPC;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.floodgate.FloodgateAPI;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShopInventory extends EventInventory {

  private String name;
  private Player player;
  private IShop shop;
  private int pageNumber = 0;
  private int previousAmount = 0;
  private int usedAmount  = 0;
  private boolean allowMultiplePlayers;
  private ShopVillagerInfo villagerNPC;


  public ShopInventory(String name, IShop shop, boolean allowMultiplePlayers, Player player) {
    super(player, name, InventoryType.CHEST);
    this.name = name;
    this.shop = shop;
    this.allowMultiplePlayers = allowMultiplePlayers;
    this.player = player;
  }

  public ShopInventory(String name, IShop shop, int pageNumber, int previousAmount, boolean allowMultiplePlayers, ShopVillagerInfo villagerNPC, Player player) {
    super(player, name, InventoryType.CHEST);
    this.name = name;
    this.shop = shop;
    this.pageNumber = pageNumber;
    this.previousAmount = previousAmount;
    this.allowMultiplePlayers = allowMultiplePlayers;
    this.villagerNPC = villagerNPC;
    this.player = player;
  }

  @Override
  protected void populate() {
    int amountOfRows;
    int productSize = shop.getProducts().size();
    if(pageNumber > 0){
      productSize -= previousAmount;
    }
    if(productSize % 9 == 0){
      amountOfRows = productSize / 9;
    }else{
      amountOfRows = productSize / 9 + 1;
    }
    boolean nextPageNeeded = false;
    if(amountOfRows > 6){
      nextPageNeeded = true;
      amountOfRows = 6;
    }
    usedAmount = productSize;
    if(nextPageNeeded){
      this.inventory.setItem((amountOfRows * 9 )- 2, ItemUtils.createItemStack(Material.ARROW, "&cNext Page"));
    }
    this.inventory.setItem((amountOfRows * 9 )- 1,ItemUtils.createItemStack(Material.BARRIER, "&cClose"));
    for(int i = 0; i < shop.getProducts().size(); i++){
      IProduct product = shop.getProducts().get(i);
      if(i >= 52){
        break;
      }
      ItemStack itemStack;
      if(FloodgateAPI.isBedrockPlayer(player)){
        itemStack = product.getBedrockItemStack();
      } else {
        itemStack = product.getItemStack();
      }
      ItemMeta itemMeta  = itemStack.getItemMeta();
      List<String> newLore = itemMeta.getLore();
      itemMeta.setLore(newLore);
      itemStack.setItemMeta(itemMeta);
      this.inventory.setItem(i, itemStack);
    }
  }

  public ShopInventory setVillagerNPC(ShopVillagerInfo villagerNPC) {
    this.villagerNPC = villagerNPC;
    return this;
  }

  @Override
  public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType) {
    ItemStack clickedItem = player.getOpenInventory().getItem(slot);
    Player player = (Player) whoClicked;
    if(clickedItem == null){
      return true;
    }
    if(clickedItem.getType() == Material.ARROW){
      ShopInventory nextPage = new ShopInventory(name, shop, pageNumber++, usedAmount, allowMultiplePlayers, villagerNPC, player);
      nextPage.open(player, this);
      return true;
    }
    if(clickedItem.getType() == Material.BARRIER){
      player.getOpenInventory().close();
      return true;
    }
    if(villagerNPC.isBusy()){
      player.sendMessage("Can't you see I'm busy?!");
      return true;
    }
    if(shop.getProducts().size() < slot){
      return true;
    }
    IProduct iProduct = shop.getProducts().get(slot);
    shop.purchaseAction(villagerNPC, player, iProduct);
    player.getOpenInventory().close();
    return true;
  }
}
