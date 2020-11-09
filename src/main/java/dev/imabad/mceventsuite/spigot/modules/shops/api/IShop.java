package dev.imabad.mceventsuite.spigot.modules.shops.api;


import dev.imabad.mceventsuite.spigot.entities.VillagerNPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.List;

public interface IShop {

  ShopArea getShopArea();

  String getName();


  ShopState getShopState();

  boolean setShopState(ShopState enumShopState);

  ShopDoor getDoor();

  void openInventory(Player player, ShopVillagerInfo shopNPC);

  void registerEntities();

  void removeEntities();

  void onPlayerEnter(Player player);

  void onPlayerLeave(Player player);

  default void registerInteractions()
  {

  }

  void onEnable();

  void onDisable();

  default void onRegister(){
    setShopState(ShopState.OPEN);
    registerEntities();
    registerInteractions();
  }


  void purchaseAction(ShopVillagerInfo villagerNPC, Player player, IProduct product);

  List<IProduct> getProducts();

}
