package dev.imabad.mceventsuite.spigot.modules.shops;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.entities.VillagerNPC;
import dev.imabad.mceventsuite.spigot.interactions.Interaction;
import dev.imabad.mceventsuite.spigot.interactions.InteractionRegistry;
import dev.imabad.mceventsuite.spigot.modules.shops.api.IMovingVillagerShop;
import dev.imabad.mceventsuite.spigot.modules.shops.api.IProduct;
import dev.imabad.mceventsuite.spigot.modules.shops.api.IShop;
import dev.imabad.mceventsuite.spigot.modules.shops.api.ShopVillagerInfo;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.StarblocksShop;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ShopsModule extends Module implements Listener {

    private World mainWorld;

    private HashMap<String, IShop> registeredShops = new HashMap<>();

    private static void removeEntities(String s, IShop iShop) {
        iShop.removeEntities();
    }

    public HashMap<String, IShop> getRegisteredShops(){return registeredShops;}

    public void registerShop(IShop shop){
        registeredShops.put(shop.getName(), shop);
        shop.onRegister();
    }

    public IShop getShop(String name){
        return registeredShops.get(name);
    }

    @Override
    public String getName() {
        return "shops";
    }

    @Override
    public void onEnable() {
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
        registerInteractions();
        if(EventSpigot.getInstance().getServer().getPluginManager().getPlugin("Citizens") != null){
            EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new dev.imabad.mceventsuite.spigot.modules.shops.CitizensListener(), EventSpigot.getInstance());
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent){
        if(worldLoadEvent.getWorld().getName().equalsIgnoreCase("venue")){
            mainWorld = worldLoadEvent.getWorld();
        }
    }

    @Override
    public void onDisable() {
        registeredShops.forEach(ShopsModule::removeEntities);
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    public World getMainWorld() {
        return mainWorld;
    }

    public void registerInteractions(){
        InteractionRegistry.registerInteraction(Interaction.RIGHT_CLICK, this::handleInteract);
        InteractionRegistry.registerInteraction(Interaction.LEFT_CLICK_ENTITY, this::handleEntityDamageByEntityEventItems);
    }

    private void handleEntityDamageByEntityEventItems(Event event) {
        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
        if (entityDamageByEntityEvent.getEntity() instanceof Player) {
            if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                Player damager = (Player) entityDamageByEntityEvent.getDamager();
                for (IShop iShop : getRegisteredShops().values()) {
                    if (iShop.getProducts() == null) {
                        continue;
                    }
                    for (IProduct product : iShop.getProducts()) {
                        if(product.getItemStack().getType().equals(damager.getInventory().getItemInMainHand().getType())) {
                            if (ItemUtils.equalsItemTexture(damager.getInventory().getItemInMainHand(), product.getItemStack())) {
                                product.onEntityDamage(entityDamageByEntityEvent);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleInteract(Event event){
        PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
        if(playerInteractEvent.getItem() != null) {
            for (IShop iShop : getRegisteredShops().values()) {
                if (iShop.getProducts() == null) {
                    continue;
                }
                for (IProduct product : iShop.getProducts()) {
                    ItemStack it = product.getItemStack();
                    for (Enchantment enchantment : it.getEnchantments().keySet()) {
                        it.removeEnchantment(enchantment);
                    }
                    if (it.getType() == Material.PLAYER_HEAD) {
                        if (product.getItemStack().getType().equals(playerInteractEvent.getItem().getType())) {
                            if (ItemUtils.equalsItemTexture(it, playerInteractEvent.getItem())) {
                                product.onInteract(playerInteractEvent);
                            }
                        }
                    } else {
                        if (it.equals(playerInteractEvent.getItem())) {
                            product.onInteract(playerInteractEvent);
                        }
                    }
                }
            }
        }
    }
}
