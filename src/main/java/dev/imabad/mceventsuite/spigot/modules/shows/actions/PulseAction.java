package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;
import org.bukkit.Location;
import org.bukkit.Material;

public class PulseAction extends ShowAction{

    private Location location;

    public PulseAction(Show show, long time, Location pulseLocation) {
        super(show, time);
        location = pulseLocation;
    }

    @Override
    public void execute() {
        Material previousBlock = location.getBlock().getType();
        location.getBlock().setType(Material.REDSTONE_BLOCK);
        location.getBlock().setType(previousBlock);
    }
}
