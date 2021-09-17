package dev.imabad.mceventsuite.spigot.modules.scavengers;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.core.modules.scavenger.ScavengerModule;
import dev.imabad.mceventsuite.core.modules.scavenger.db.ScavengerLocation;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScavengerHuntSpigotModule extends Module {

    ScavengerModule scavengerModule;
    @Override
    public String getName() {
        return "scavengerhuntspigot";
    }

    private List<Location> actualLocations;

    @Override
    public void onEnable() {
        scavengerModule = EventCore.getInstance().getModuleRegistry().getModule(ScavengerModule.class);
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new ScavengerHuntListener(this), EventSpigot.getInstance());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, mySQLLoadedEvent -> {
            World venue = Bukkit.getWorld("world");
            System.out.println("Mysql loaded, getting locations");
            if(venue != null) {
                System.out.println("Venue is loaded.");
                EventSpigot.getInstance().getServer().getScheduler().runTaskTimer(EventSpigot.getInstance(), () -> {
                    List<Location> locations = getLocations(venue);
                    if(locations == null)
                        return;
                    for (Location location : locations) {
                        venue.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.5, 0.5), 5);
                    }
                }, 0, 5 * 20);
            }
        });
    }

    @Override
    public void onDisable() {

    }

    public List<Location> getLocations(World world){
        if(actualLocations != null){
            return actualLocations;
        }
        if(scavengerModule.getLocations() == null){
            return null;
        }
        actualLocations = scavengerModule.getLocations().stream().map(location -> new Location(world, location.getX(), location.getY(), location.getZ())).collect(Collectors.toList());
        return actualLocations;
    }

    public Optional<ScavengerLocation> getFromLocation(Location location) {
        return scavengerModule.getLocations().stream().filter(scavengerLocation -> scavengerLocation.getX() == location.getBlockX() && scavengerLocation.getY() == location.getBlockY() && scavengerLocation.getZ() == location.getBlockZ()).findFirst();
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.singletonList(ScavengerModule.class);
    }
}
