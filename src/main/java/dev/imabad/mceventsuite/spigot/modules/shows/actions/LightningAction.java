package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;
import org.bukkit.Location;

public class LightningAction extends ShowAction{

    private final Location location;

    public LightningAction(Show show, long time, Location lightningLocation) {
        super(show, time);
        location = lightningLocation;
    }

    @Override
    public void execute() {
        location.getWorld().strikeLightningEffect(location);
    }
}
