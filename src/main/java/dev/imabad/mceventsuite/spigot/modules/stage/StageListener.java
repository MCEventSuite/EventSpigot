package dev.imabad.mceventsuite.spigot.modules.stage;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

public class StageListener implements Listener {

  private static ItemStack KNOCK_STICK = ItemUtils.createItemStack(Material.STICK, "&cKnockback Stick");
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
      regionEnterEvent.getPlayer().getInventory().setItem(4, KNOCK_STICK);
      EventSpigot.getInstance().getAudiences().player(regionEnterEvent.getPlayer()).sendMessage(
          Component.text("You have entered King Of the Hill!").color(NamedTextColor.GREEN));
    }
  }

  @EventHandler
  public void onRegionLeave(RegionLeftEvent leftEvent){
    if(leftEvent.getRegionId().equalsIgnoreCase("KOTH")){
      if(leftEvent.getPlayer().getInventory().getItem(4).getType().equals(Material.STICK)){
        leftEvent.getPlayer().getInventory().setItem(4, new ItemStack(Material.AIR));
        EventSpigot.getInstance().getAudiences().player(leftEvent.getPlayer()).sendMessage(
            Component.text("You have left King Of the Hill!").color(NamedTextColor.RED));
      }
    }
  }

}
