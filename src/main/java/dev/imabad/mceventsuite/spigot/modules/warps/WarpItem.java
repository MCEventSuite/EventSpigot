package dev.imabad.mceventsuite.spigot.modules.warps;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class WarpItem {

    private String name;
    private ItemStack icon;
    private Location location;
    private WarpCategory category;

    public WarpItem(String name, ItemStack icon, Location location, WarpCategory category) {
        this.name = name;
        this.icon = icon;
        this.location = location;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Location getLocation() {
        return location;
    }

    public WarpCategory getCategory() {
        return category;
    }
}
