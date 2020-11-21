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
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CakeProduct implements ISkullProduct {

  private String name;
  private String textureID;
  private int cost;
  private IShop shop;

  public CakeProduct(String name, int cost, String textureID, IShop shop) {
    this.name = name;
    this.cost = cost;
    this.textureID = textureID;
    this.shop = shop;
  }

  @Override
  public ItemStack getBedrockItemStack() {
    return ItemUtils.createItemStack(Material.CAKE, getDisplayName(), getLore());
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
    SoundHelper.playSoundAtPlayer(player, Sound.ENTITY_GENERIC_EAT);
    EventSpigot.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(EventSpigot.getInstance(), () -> {
      EventSpigot.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(EventSpigot.getInstance(), () -> { SoundHelper.playSoundAtPlayer(player, Sound.ENTITY_PLAYER_BURP); }, 20 *2);
      CraftPlayer cPlayer = ((CraftPlayer)player);
      PacketPlayOutEntityStatus eatPacket = new PacketPlayOutEntityStatus(cPlayer.getHandle(), (byte)9);
      cPlayer.getHandle().playerConnection.sendPacket(eatPacket);
      player.getInventory().remove(playerInteractEvent.getItem());
      EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresent(eventPlayer -> {
        long lastStarblocks = eventPlayer.getLongProperty("lastStarblocks");
        if(System.currentTimeMillis() - lastStarblocks >= TimeUnit.HOURS.toMillis(1)){
          eventPlayer.setProperty("lastStarblocks", System.currentTimeMillis());
          EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new AwardPlayerXPMessage(eventPlayer.getUUID(), 250, "Ate some cake!"));
        }
      });
    }, 20);
  }

  @Override
  public String getTextureID() {
    return textureID;
  }

  @Override
  public IShop getShop() {
    return shop;
  }
}
