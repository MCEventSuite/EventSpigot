package dev.imabad.mceventsuite.spigot.modules.player;

import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerHotbar {

    private static final List<String> helpPages = Arrays.asList("&9&lCubed! 2021\n" +
                    "&7The In-Game Minecraft Convention\n" +
                    "\n" +
                    "&9Watch the livestream to hear panels and join in with games:\n" +
                    "&2twitch.tv/cubedcon\n" +
                    "\n" +
                    "&9Discord\n" +
                    "&2discord.gg/cubedcon\n" +
                    "\n" +
                    "&9Twitter\n" +
                    "&2@CubedCon",
            "&9Getting lost? Use the &cCompass to warp to a specific location&9.\n" +
                    "\n" +
                    "&9Access your cosmetics using the &cChest&9.\n" +
                    "\n" +
                    "&9Follow your Event Pass progress using the &cPaper or by visiting\n" +
                    "&2pass.cubedcon.com\n");

    public static ItemStack NAVIGATION = ItemUtils.createItemStack(Material.COMPASS, "&cNavigation");
    public static ItemStack HELP_GUIDE = ItemUtils.createBook("&9Help Guide", Collections.emptyList(), helpPages);
    public static ItemStack GADGETS = ItemUtils.createItemStack(Material.CHEST, "&aCosmetics");

    public static void givePlayerInventory(Player player) {
        player.getInventory().clear();
        Inventory inventory = player.getInventory();
        inventory.setItem(0, NAVIGATION);
        inventory.setItem(1, HELP_GUIDE);
        inventory.setItem(7, GADGETS);
        ItemStack EVENT_PASS = ItemUtils.createItemStack(Material.PAPER, "&e" + player.getName() + "'s Event Pass");
        inventory.setItem(8, EVENT_PASS);
        player.updateInventory();
    }
}
