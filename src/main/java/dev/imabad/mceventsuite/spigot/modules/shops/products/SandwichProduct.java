package dev.imabad.mceventsuite.spigot.modules.shops.products;

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
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SandwichProduct implements ISkullProduct {

  private String name;
  private String textureID;
  private int cost;
  private IShop shop;

  public SandwichProduct(String name, int cost, String textureID, IShop shop) {
    this.name = name;
    this.cost = cost;
    this.textureID = textureID;
    this.shop = shop;
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
      ClientboundEntityEventPacket eatPacket = new ClientboundEntityEventPacket(cPlayer.getHandle(), (byte)9);
      cPlayer.getHandle().networkManager.send(eatPacket);
      player.getInventory().remove(playerInteractEvent.getItem());
      EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresent(eventPlayer -> {
        long lastStarblocks = eventPlayer.getLongProperty("last" + shop.getName());
        if(System.currentTimeMillis() - lastStarblocks >= TimeUnit.HOURS.toMillis(1)){
          eventPlayer.setProperty("last" + shop.getName(), System.currentTimeMillis());
          EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new AwardPlayerXPMessage(eventPlayer.getUUID(), 250, "Ate some food!"));
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

  @Override
  public ItemStack getBedrockItemStack() {
    return ItemUtils.createItemStack(Material.COOKED_BEEF, getDisplayName(), getLore());
  }
}
