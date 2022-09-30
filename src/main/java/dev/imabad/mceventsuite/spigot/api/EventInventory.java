package dev.imabad.mceventsuite.spigot.api;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class EventInventory {
    public final static List<EventInventory> EVENT_INVENTORIES = new ArrayList<>();

    protected final Inventory inventory;
    private Player player;
    protected EventInventory backInventory;

    public EventInventory(Player player, String name, int size) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, getInvSizeForCount(size), name);
        EVENT_INVENTORIES.add(this);
    }

    public EventInventory(Player player, String name, InventoryType type) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, type, name);
        EVENT_INVENTORIES.add(this);
    }

    public void open(Player player, @Nullable EventInventory backInventory) {
        repopulate();
        this.backInventory = backInventory;
        Bukkit.getScheduler().runTask(EventSpigot.getInstance(), () -> player.openInventory(inventory));
    }


    public Player getPlayer() {
        return player;
    }

    protected abstract void populate();

    protected final void repopulate() {
        this.inventory.clear();
        this.populate();
    }

    public void remove() {
        this.inventory.getViewers().forEach(HumanEntity::closeInventory);
        EVENT_INVENTORIES.remove(this);
    }

    public abstract boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType);

    public boolean onPlayerDrag() {
        return true;
    }

    public boolean isInventory(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    protected final int getInvSizeForCount(int count) {
        return Math.min(roundNumberUpToDivisableBy(count, 9), 54);
    }

    public static boolean isDivisable(int number, int divisor) {
        return number % divisor == 0;
    }

    public static int roundNumberUpToDivisableBy(int number, int divisor) {
        int theAmount = number;
        boolean working = true;
        while (working) {
            if(isDivisable(theAmount, divisor)) {
                working = false;
            }else {
                theAmount++;
            }
        }
        return theAmount;
    }
}
