package dev.imabad.mceventsuite.spigot.impl;

import dev.imabad.mceventsuite.core.api.player.ILocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SpigotLocation implements ILocation {

    public static Location toSpigotLocation(ILocation location){
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }

    private Location location;

    public SpigotLocation(Location location){
        this.location = location;
    }

    @Override
    public int getX() {
        return location.getBlockX();
    }

    @Override
    public int getY() {
        return location.getBlockY();
    }

    @Override
    public int getZ() {
        return location.getBlockZ();
    }

    @Override
    public String getWorld() {
        return location.getWorld().getName();
    }
}
