package dev.imabad.mceventsuite.spigot.modules.shops.starblocks.products;


import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.AwardPlayerXPMessage;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.shops.api.IShop;
import dev.imabad.mceventsuite.spigot.modules.shops.api.ISkullProduct;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import dev.imabad.mceventsuite.spigot.utils.SoundHelper;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CoffeeProduct implements ISkullProduct {

  public static ItemStack EMPTY_CUP = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI4MDk4ODlkYTM2YWRmNTYxODU4MWJiZjdiNjZkMmQ4ODM5ZTJlYjcyNTRjMzMzMmU0ZjNhMjMwZmEifX19", "&r&9&lEmpty Cup");

  private final String name;
  private int cost;
  private String textureID;
  private IShop shop;
  private boolean givesCup;

  public CoffeeProduct(String name, int cost, String textureID, IShop shop, boolean givesCup) {
    this.name = name;
    this.cost = cost;
    this.textureID = textureID;
    this.shop = shop;
    this.givesCup = givesCup;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDisplayName() {
    return StringUtils.colorizeMessage(name);
  }

  @Override
  public List<String> getLore() {
    return Collections.emptyList();
  }

  @Override
  public int getCost() {
    return cost;
  }

  @Override
  public void onInteract(PlayerInteractEvent playerInteractEvent) {
    Player player = playerInteractEvent.getPlayer();
    ;
    SoundHelper.playSoundAtPlayer(player, Sound.ENTITY_GENERIC_DRINK);
    EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () -> {
      EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () -> { SoundHelper.playSoundAtPlayer(player, Sound.ENTITY_PLAYER_BURP); }, 20 *2);
      ParticleEffect.WATER_SPLASH.display(player.getLocation(), new Vector(0, 1, 0), 10f, 50, null);
      player.getInventory().remove(playerInteractEvent.getItem());
      if(shouldGiveCup()){
        player.getInventory().setItemInMainHand(EMPTY_CUP);
      }
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 2));
      EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresent(eventPlayer -> {
        int lastStarblocks = eventPlayer.getIntProperty("lastStarblocks");
        if(System.currentTimeMillis() - lastStarblocks >= TimeUnit.HOURS.toMillis(1)){
          eventPlayer.setProperty("lastStarblocks", System.currentTimeMillis());
          EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new AwardPlayerXPMessage(eventPlayer.getUUID(), 250));
        }
      });
    }, 20);
  }


  @Override
  public IShop getShop() {
    return shop;
  }

  private boolean shouldGiveCup(){
    return givesCup;
  }

  @Override
  public String getTextureID() {
    return textureID;
  }
}
