package dev.imabad.mceventsuite.spigot.modules.stage;

import com.cubedcon.cosmetics.CosmeticItemCategory;
import com.cubedcon.cosmetics.CubedCosmetics;
import com.cubedcon.cosmetics.managers.CosmeticManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.BungeeUtils;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Arrays;
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

  private boolean isKothServer() {
    return EventCore.getInstance().getIdentifier().equals("venue1");
  }

  @EventHandler
  public void onEnterRegion(RegionEnteredEvent regionEnterEvent){
    boolean isNightVision = regionEnterEvent.getRegion().getFlag(StageModule.getIsNightVision()) == State.ALLOW || regionEnterEvent.getRegion().getFlag(StageModule.getIsNightVision()) == null;
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
    boolean isAllowParticles = regionEnterEvent.getRegion().getFlag(StageModule.getAllowParticles()) == State.ALLOW || regionEnterEvent.getRegion().getFlag(StageModule.getAllowParticles()) == null;
    if(!isAllowParticles){
      Arrays.asList(CosmeticItemCategory.BALLOONS, CosmeticItemCategory.GADGETS, CosmeticItemCategory.PARTICLES, CosmeticItemCategory.TRAILS).forEach(category -> CosmeticManager.getInstance().getCurrentCosmeticItemByCategory(regionEnterEvent.getPlayer().getUniqueId(), category).ifPresent(cosmeticItem -> CosmeticManager.getInstance().removeCosmeticItem(cosmeticItem)));
    }
    if(regionEnterEvent.getRegionId().equalsIgnoreCase("KOTH")){
      if (this.isKothServer()) {
        regionEnterEvent.getPlayer().getInventory().setItem(5, KNOCK_STICK);
        EventSpigot.getInstance().getAudiences().player(regionEnterEvent.getPlayer()).sendMessage(
                Component.text("You have entered king of the hill!").color(NamedTextColor.GREEN));
      } else {
        // Save location and teleport to correct server
        BungeeUtils.sendToServer(regionEnterEvent.getPlayer(), "venue1");
      }
    } else if(regionEnterEvent.getRegionId().equalsIgnoreCase("KOTH-TOP") && this.isKothServer()){
      List<Player> playersOnTop = Bukkit.getOnlinePlayers().stream().filter(player -> RegionUtils.isInRegion(player, "KOTH-TOP")).collect(Collectors.toList());
      List<Player> playersInKOTH = Bukkit.getOnlinePlayers().stream().filter(player -> RegionUtils.isInRegion(player, "KOTH")).collect(Collectors.toList());
      if(playersOnTop.size() > 1){
        playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(TIED));
        ((TextLine)module.getKothHologram().getLine(0)).setText(ChatColor.RED + "TIED");
      } else if(playersOnTop.size() == 1){
        Player playerOnTop = playersOnTop.get(0);
        Component kingOfTheHill = Component.text("King of the Hill - ").color(NamedTextColor.BLUE).append(Component.text(playerOnTop.getName()).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN));
        playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(kingOfTheHill));
        ((TextLine)module.getKothHologram().getLine(0)).setText(ChatColor.GREEN + playerOnTop.getName());
      } else {
        ((TextLine)module.getKothHologram().getLine(0)).setText(ChatColor.GREEN + "NO BODY");
      }
    }
  }

  @EventHandler
  public void onRegionLeave(RegionLeftEvent leftEvent){
    if (!this.isKothServer()) return;
    if(leftEvent.getRegionId().equalsIgnoreCase("KOTH") && !RegionUtils.isInRegion(leftEvent.getPlayer(), "KOTH-TOP")){
      if(leftEvent.getPlayer().getInventory().getItem(5).getType().equals(Material.STICK)){
        leftEvent.getPlayer().getInventory().setItem(5, new ItemStack(Material.AIR));
        EventSpigot.getInstance().getAudiences().player(leftEvent.getPlayer()).sendMessage(
            Component.text("You have left king of the hill!").color(NamedTextColor.RED));
      }
    } else if(leftEvent.getRegionId().equalsIgnoreCase("KOTH-TOP")){
        List<Player> playersOnTop = Bukkit.getOnlinePlayers().stream().filter(player -> RegionUtils.isInRegion(player, "KOTH-TOP")).collect(Collectors.toList());
        List<Player> playersInKOTH = Bukkit.getOnlinePlayers().stream().filter(player -> RegionUtils.isInRegion(player, "KOTH")).collect(Collectors.toList());
        if(playersOnTop.size() > 1){
          playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(TIED));
          ((TextLine)module.getKothHologram().getLine(0)).setText(ChatColor.RED + "TIED");
        } else if(playersOnTop.size() == 1){
          Player playerOnTop = playersOnTop.get(0);
          Component kingOfTheHill = Component.text("King of the Hill - ").color(NamedTextColor.BLUE).append(Component.text(playerOnTop.getName()).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN));
          playersInKOTH.forEach(player -> EventSpigot.getInstance().getAudiences().player(player).sendActionBar(kingOfTheHill));
          ((TextLine)module.getKothHologram().getLine(0)).setText(ChatColor.GREEN + playerOnTop.getName());
        } else {
          ((TextLine)module.getKothHologram().getLine(0)).setText(ChatColor.GREEN + "NO BODY");
        }
    }
  }

  @EventHandler
  public void onPluginEnable(PluginEnableEvent event){
    if (!this.isKothServer()) return;
    if(event.getPlugin().getName().equalsIgnoreCase("HolographicDisplays")){
      Hologram hologram = HologramsAPI.createHologram(EventSpigot.getInstance(), new Location(Bukkit.getWorld("world"), 152.5, 31, 153.5));
      hologram.insertTextLine(0, ChatColor.GREEN + "NO BODY");
      hologram.insertTextLine(1, ChatColor.RED + "KING OF THE HILL");
      module.setKothHologram(hologram);
    }
  }

}
