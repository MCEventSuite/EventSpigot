package dev.imabad.mceventsuite.spigot.modules.stage;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.interactions.Interaction;
import dev.imabad.mceventsuite.spigot.interactions.InteractionRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class StageModule extends Module {

  private HashMap<UUID, Entity> seats = new HashMap<>();

  @Override
  public String getName() {
    return "stage";
  }

  @Override
  public void onEnable() {
    InteractionRegistry.registerInteraction(Interaction.RIGHT_CLICK_BLOCK, this::enterSeat);
    EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new StageListener(this), EventSpigot.getInstance());
  }

  private void enterSeat(Event event) {
    PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
    //TODO: Check is in a valid region
    if(playerInteractEvent.getClickedBlock().getType().toString().contains("stairs")){
      if(!seats.containsKey(playerInteractEvent.getPlayer().getUniqueId())){
        Location cLocation = playerInteractEvent.getClickedBlock().getLocation();
        cLocation.subtract(0, 0.5, 0);
        Chicken c = (Chicken) playerInteractEvent.getPlayer().getWorld().spawnEntity(cLocation,
            EntityType.CHICKEN);
        c.setInvisible(true);
        c.setInvulnerable(true);
        c.setAI(false);
        c.addPassenger(playerInteractEvent.getPlayer());
        seats.put(playerInteractEvent.getPlayer().getUniqueId(), c);
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
  }

  @Override
  public List<Class<? extends Module>> getDependencies() {
    return Collections.emptyList();
  }
}
