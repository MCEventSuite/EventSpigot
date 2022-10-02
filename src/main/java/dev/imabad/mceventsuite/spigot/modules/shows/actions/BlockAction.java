package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockAction extends ShowAction {

    private Location location;
    private Material material;

    public BlockAction(Show show, long time, Location blockLocation, Material blockMaterial) {
        super(show, time);
        location = blockLocation;
        material = blockMaterial;
    }

    @Override
    public void execute() {
        location.getBlock().setType(material);
    }
}
