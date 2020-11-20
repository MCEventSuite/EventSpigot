package dev.imabad.mceventsuite.spigot.modules.stage;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.interactions.Interaction;
import dev.imabad.mceventsuite.spigot.interactions.InteractionRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.Location;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class StageModule extends Module {

  private HashMap<UUID, Entity> seats = new HashMap<>();
  private Hologram kothHologram;

  private static StateFlag isNightVision;

  public static StateFlag getIsNightVision() {
    return isNightVision;
  }

  @Override
  public String getName() {
    return "stage";
  }

  @Override
  public void onEnable() {
    InteractionRegistry.registerInteraction(Interaction.RIGHT_CLICK_BLOCK, this::enterSeat);
    EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new StageListener(this), EventSpigot.getInstance());
    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
    try {
      StateFlag flag = new StateFlag("is-night-vision", true);
      registry.register(flag);
      isNightVision = flag;
    } catch (FlagConflictException e) {
      Flag<?> existing = registry.get("is-night-vision");
      if (existing instanceof StateFlag) {
        isNightVision = (StateFlag) existing;
      } else {
      }
    }
  }

  private void enterSeat(Event event) {
    PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
    if(RegionUtils.isInRegion(playerInteractEvent.getPlayer(), "stage")) {
      System.out.println(playerInteractEvent.getClickedBlock().getType().toString());
      if (playerInteractEvent.getClickedBlock().getType().toString().toLowerCase().contains("stairs")) {
        if (!seats.containsKey(playerInteractEvent.getPlayer().getUniqueId())) {
          Location cLocation = playerInteractEvent.getClickedBlock().getLocation();
          Stairs stairs = (Stairs) playerInteractEvent.getClickedBlock().getBlockData();
          cLocation.setDirection(stairs.getFacing().getDirection().multiply(-1));
          cLocation.add(0.5, 0, 0.5);
          Chicken c = (Chicken) playerInteractEvent.getPlayer().getWorld().spawnEntity(cLocation,
              EntityType.CHICKEN);
          c.setInvisible(true);
          c.setInvulnerable(true);
          c.setSilent(true);
          c.setAI(false);
          c.addPassenger(playerInteractEvent.getPlayer());
          seats.put(playerInteractEvent.getPlayer().getUniqueId(), c);
          playerInteractEvent.getPlayer().sendMessage("Sitting in chair");
        }
      }
    }
  }

  public void removeSeat(UUID uuid){
    if(seats.containsKey(uuid)){
      seats.get(uuid).remove();
      seats.remove(uuid);
    }
  }

  @Override
  public void onDisable() {
    seats.values().forEach(Entity::remove);
    seats.clear();
    if(getKothHologram() != null){
      getKothHologram().delete();
      kothHologram = null;
    }
  }

  @Override
  public List<Class<? extends Module>> getDependencies() {
    return Collections.emptyList();
  }

  public Hologram getKothHologram() {
    return kothHologram;
  }

  public void setKothHologram(Hologram kothHologram) {
    this.kothHologram = kothHologram;
  }
}
