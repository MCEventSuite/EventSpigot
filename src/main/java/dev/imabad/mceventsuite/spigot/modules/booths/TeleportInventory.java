package dev.imabad.mceventsuite.spigot.modules.booths;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TeleportInventory extends EventInventory {

    private static final ItemStack CREATIVE = ItemUtils.createItemStack(Material.GRASS_BLOCK, "&aCreative");
    private static final ItemStack CONTEST = ItemUtils.createItemStack(Material.CLOCK, "&aContest");
    private static final ItemStack SMALL = ItemUtils.createItemStack(Material.GREEN_CONCRETE, "&aSmall Booths");
    private static final ItemStack MEDIUM = ItemUtils.createItemStack(Material.YELLOW_CONCRETE, "&aMedium Booths");
    private static final ItemStack LARGE = ItemUtils.createItemStack(Material.BLUE_CONCRETE, "&aLarge Booths");

    public TeleportInventory(Player player) {
        super(player, "Click to Teleport", 27);
    }

    @Override
    protected void populate() {
        inventory.setItem(11, CREATIVE);
        inventory.setItem(12, CONTEST);
        inventory.setItem(13, SMALL);
        inventory.setItem(14, MEDIUM);
        inventory.setItem(15, LARGE);
    }

    @Override
    public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType) {
        Player player = (Player) whoClicked;

        if (slot == 11) {
            World world = Bukkit.getWorld("creative");
            player.teleport(new Location(world, -2, 52, 0.5, 270, 0));
            return false;
        } else if (slot == 12) {
            World world = Bukkit.getWorld("contest");
            player.teleport(new Location(world, -2, 51, 0.5, 270, 0));
            return false;
        }

        EventPlayer eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).get();

        if (eventPlayer.getRank().getPower() < 70) {
            player.sendMessage(ChatColor.RED + "Booth building is now closed.");
            player.closeInventory();
            return false;
        }

        if (slot == 13) {
            World world = Bukkit.getWorld("small");

            if (!player.hasPermission("multiverse.access.small")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to teleport to this world.");
                return false;
            }

            player.teleport(world.getSpawnLocation());
        } else if (slot == 14) {
            World world = Bukkit.getWorld("medium");

            if (!player.hasPermission("multiverse.access.medium")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to teleport to this world.");
                return false;
            }

            player.teleport(world.getSpawnLocation());
        } else if (slot == 15) {
            World world = Bukkit.getWorld("large");

            if (!player.hasPermission("multiverse.access.large")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to teleport to this world.");
                return false;
            }

            player.teleport(world.getSpawnLocation());
        }

        return false;
    }

}
