package dev.imabad.mceventsuite.spigot.modules.warps.inventories;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.modules.warps.WarpCategory;
import dev.imabad.mceventsuite.spigot.modules.warps.WarpItem;
import dev.imabad.mceventsuite.spigot.modules.warps.WarpModule;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WarpInventoryPage extends EventInventory {

    private WarpCategory filter = WarpCategory.ALL;
    private HashMap<Integer, WarpItem> warpItemHashMap = new HashMap<>();

    public WarpInventoryPage(Player player) {
        super(player, "Cubed! Warp", 54);
    }

    @Override
    protected void populate() {
        warpItemHashMap.clear();
        List<WarpItem> warpItems = new ArrayList(EventCore.getInstance().getModuleRegistry().getModule(WarpModule.class).getWarpItems());
        if (filter == WarpCategory.ALL) {
            // Don't show small booths which may overflow - instead show a quick
            // link to the small booths page
            warpItems = warpItems.stream().filter(warpItem -> warpItem.getCategory() != WarpCategory.SMALL).collect(Collectors.toCollection(ArrayList::new));
            this.inventory.setItem(19, ItemUtils.createItemStack(Material.GREEN_CONCRETE, ChatColor.BLUE + "" + ChatColor.BOLD + "Small Booths"));
        } else {
            warpItems = warpItems.stream().filter(warpItem -> warpItem.getCategory() == filter).collect(Collectors.toCollection(ArrayList::new));
        }
        int prevLineNumber = 0;
        int lineCount = -1;
        for(int i = 0; i < warpItems.size(); i++){
            WarpItem boothPlot = warpItems.get(i);
            int startPos = 0;
            if(filter == WarpCategory.ALL){
                if(boothPlot.getCategory().lineNumber != prevLineNumber){
                    prevLineNumber = boothPlot.getCategory().lineNumber;
                    lineCount = 0;
                } else {
                    lineCount++;
                }
                int x = lineCount;
                startPos = (boothPlot.getCategory().lineNumber * 9) + x;
            } else {
                startPos = i;
            }
            this.inventory.setItem(startPos, boothPlot.getIcon());
            warpItemHashMap.put(startPos, boothPlot);
        }
        this.inventory.setItem(53, filter.icon);
    }

    @Override
    public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType) {
        if(slot == 53){
            if(filter.ordinal() + 1 >= WarpCategory.values().length){
                filter = WarpCategory.values()[0];
            } else {
                filter = WarpCategory.values()[filter.ordinal() + 1];
            }
            repopulate();
            return true;
        } else if (slot == 19 && filter == WarpCategory.ALL) {
            filter = WarpCategory.SMALL;
            repopulate();
            return true;
        } else if(warpItemHashMap.containsKey(slot)) {
            whoClicked.teleport(warpItemHashMap.get(slot).getLocation());
            remove();
            whoClicked.sendMessage(StringUtils.colorizeMessage("&cGoing to &r" + clickItem.getItemMeta().getDisplayName()));
            return true;
        }
        return false;
    }
}
