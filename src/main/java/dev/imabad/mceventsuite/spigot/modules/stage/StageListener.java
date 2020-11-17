package dev.imabad.mceventsuite.spigot.modules.stage;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.List;
import java.util.stream.Collectors;

public class StageListener implements Listener {

  private static ItemStack KNOCK_STICK = ItemUtils.createItemStack(Material.STICK, "&cKnockback Stick");
  private static Component TIED = Component.text("King of the Hill - ").color(NamedTextColor.BLUE).append(Component.text("Tied").decorate(TextDecoration.BOLD).color(NamedTextColor.RED));
  static {
    KNOCK_STICK.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
  }

  private StageModule module;

  public StageListener(StageModule module) {
    this.module = module;
  }

  @EventHandler
  public void onDismount(EntityDismountEvent entityDismountEvent){
    if(entityDismountEvent.getEntityType() == EntityType.PLAYER){
      if(entityDismountEvent.getDismounted().getType() == EntityType.CHICKEN){
        module.removeSeat(entityDismountEvent.getEntity().getUniqueId());
      }
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event){
    module.removeSeat(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onEnterRegion(RegionEnteredEvent regionEnterEvent){
    boolean isNightVision = regionEnterEvent.getRegion().getFlag(StageModule.getIsNightVision()) == State.ALLOW;
    if(isNightVision){
      if(regionEnterEvent.getPlayer().getActivePotionEffects().stream().noneMatch(potionEffect -> potionEffect.getType().equals(
          PotionEffectType.NIGHT_VISION))){
          regionEnterEvent.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true, false, false));
      }
    } else {
      if(regionEnterEvent.getPlayer().getActivePotionEffects().stream().anyMatch(potionEffect -> potionEffect.getType().equals(
          PotionEffectType.NIGHT_VISION))) {
        regionEnterEvent.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
      }
    }
    if(regionEnterEvent.getRegionId().equalsIgnoreCase("KOTH")){
      regionEnterEvent.getPlayer().getInventory().setItem(5, KNOCK_STICK);
      EventSpigot.getInstance().getAudiences().player(regionEnterEvent.getPlayer()).sendMessage(
          Component.text("You have entered king of the hill!").color(NamedTextColor.GREEN));
    } else if(regionEnterEvent.getRegionId().equalsIgnoreCase("KOTH-TOP")){
      List<Player> playersOnTop = Bukkit.getOnlinePlayers().stream().filter(player -> module.isInRegion(player, "KOTH-TOP")).collect(Collectors.toList());
      List<Player> playersInKOTH = Bukkit.getOnlinePlayers().stream().filter(player -> module.isInRegion(player, "KOTH")).collect(Collectors.toList());
      if(playersOnTop.size() > 1){
        playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(TIED));
      } else if(playersOnTop.size() == 1){
        Player playerOnTop = playersOnTop.get(0);
        Component kingOfTheHill = Component.text("King of the Hill - ").color(NamedTextColor.BLUE).append(Component.text(playerOnTop.getName()).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN));
        playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(kingOfTheHill));
      }
    }
  }

  @EventHandler
  public void onRegionLeave(RegionLeftEvent leftEvent){
    if(leftEvent.getRegionId().equalsIgnoreCase("KOTH") && !module.isInRegion(leftEvent.getPlayer(), "KOTH-TOP")){
      if(leftEvent.getPlayer().getInventory().getItem(5).getType().equals(Material.STICK)){
        leftEvent.getPlayer().getInventory().setItem(5, new ItemStack(Material.AIR));
        EventSpigot.getInstance().getAudiences().player(leftEvent.getPlayer()).sendMessage(
            Component.text("You have left king of the hill!").color(NamedTextColor.RED));
      }
    } else if(leftEvent.getRegionId().equalsIgnoreCase("KOTH-TOP")){
        List<Player> playersOnTop = Bukkit.getOnlinePlayers().stream().filter(player -> module.isInRegion(player, "KOTH-TOP")).collect(Collectors.toList());
        List<Player> playersInKOTH = Bukkit.getOnlinePlayers().stream().filter(player -> module.isInRegion(player, "KOTH")).collect(Collectors.toList());
        if(playersOnTop.size() > 1){
          playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(TIED));
        } else if(playersOnTop.size() == 1){
          Player playerOnTop = playersOnTop.get(0);
          Component kingOfTheHill = Component.text("King of the Hill - ").color(NamedTextColor.BLUE).append(Component.text(playerOnTop.getName()).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN));
          playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(kingOfTheHill));
        }
    }
  }

}
