package dev.imabad.mceventsuite.spigot.modules.warps;

import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum WarpCategory {
    ALL("", ItemUtils.createItemStack(Material.WHITE_CONCRETE, "&l&fAll"), 0),
    HALL("halls",ItemUtils.createItemStack(Material.RED_CONCRETE, "&l&bHalls"), Material.RED_CONCRETE, 0),
    LARGE("large", ItemUtils.createItemStack(Material.LIGHT_BLUE_CONCRETE, "&l&bLarge Booths"), Material.LIGHT_BLUE_CONCRETE, 1),
    MEDIUM("medium", ItemUtils.createItemStack(Material.YELLOW_CONCRETE, "&l&eMedium Booths"), Material.YELLOW_CONCRETE, 2),
    SMALL("small", ItemUtils.createItemStack(Material.GREEN_CONCRETE, "&l&aSmall Booths"), Material.GREEN_CONCRETE, 3),
    OTHER("other", ItemUtils.createItemStack(Material.GRAY_CONCRETE, "&l&9Other"), 5);

    public String name;
    public ItemStack icon;
    public Material stackColor;
    public int lineNumber;
    WarpCategory(String name, ItemStack icon, int lineNumber){
        this.name = name;
        this.icon = icon;
        this.lineNumber = lineNumber;
    }
    WarpCategory(String name, ItemStack icon, Material stack, int lineNumber){
        this.name = name;
        this.icon = icon;
        this.stackColor = stack;
        this.lineNumber = lineNumber;
    }

    static WarpCategory fromName(String name){
        for (WarpCategory wf : values()){
            if(wf.name.equalsIgnoreCase(name)){
                return wf;
            }
        }
        return WarpCategory.ALL;
    }
}
