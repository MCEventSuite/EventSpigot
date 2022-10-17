package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

import java.util.Arrays;
import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onHunger(FoodLevelChangeEvent foodLevelChangeEvent){
        foodLevelChangeEvent.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent){
        if(entityDamageEvent.getEntity() instanceof Player){
            Player player = (Player) entityDamageEvent.getEntity();
            if(RegionUtils.isInRegion(player, "KOTH")){
                entityDamageEvent.setDamage(0);
                entityDamageEvent.setCancelled(false);
            } else {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

    private static final List<Material> DISALLOWED_INTERACT_MATERIALS = Arrays.asList(Material.CHEST, Material.BARREL, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.CRIMSON_BUTTON, Material.OAK_BUTTON, Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON, Material.SPRUCE_BUTTON, Material.STONE_BUTTON, Material.WARPED_BUTTON);

    @EventHandler
    public void onExplode(PlayerInteractEvent playerInteractEvent) {
        if(playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getClickedBlock() != null) {
            if(DISALLOWED_INTERACT_MATERIALS.contains(playerInteractEvent.getClickedBlock().getType())){
                playerInteractEvent.setCancelled(true);
            }

            if(playerInteractEvent.getClickedBlock().getType().name().toUpperCase().contains("DOOR")
                    || playerInteractEvent.getClickedBlock().getType().name().toUpperCase().contains("GATE")
                    || playerInteractEvent.getClickedBlock().getType().name().toUpperCase().contains("TRAPDOOR")) {
                playerInteractEvent.setCancelled(true);
            }
        }

        if(playerInteractEvent.getClickedBlock() != null) {
            if(playerInteractEvent.getClickedBlock().getType().name().toUpperCase().contains("SIGN")) {
                Location blockLoc = playerInteractEvent.getClickedBlock().getLocation();
                if(EventSpigot.getInstance().getConfig().isConfigurationSection("signs")) {
                    if(EventSpigot.getInstance().getConfig().getConfigurationSection("signs").getKeys(false).contains(blockLoc.getBlockX() + ";" + blockLoc.getBlockY() + ";" + blockLoc.getBlockZ())) {
                        String response = EventSpigot.getInstance().getConfig().getConfigurationSection("signs").getString(blockLoc.getBlockX() + ";" + blockLoc.getBlockY() + ";" + blockLoc.getBlockZ());
                        Component miniComp = MiniMessage.miniMessage().deserialize(manuallyTranslateColour(response));
                        playerInteractEvent.getPlayer().sendMessage(miniComp);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLecternTake(PlayerTakeLecternBookEvent event) {
        event.setCancelled(true);
    }

    //Prevent players from taking armor from armorstands.
    @EventHandler
    public void onArmorStandTake(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if(event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireworkExplode(EntityDamageEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            event.setCancelled(true);
        }
    }

    // Use adventure to tags to convert legacy text to adventure text
    private String manuallyTranslateColour(String input) {
        input = input.replaceAll("&4", "<dark_red>");
        input = input.replaceAll("&c", "<red>");
        input = input.replaceAll("&6", "<gold>");
        input = input.replaceAll("&e", "<yellow>");
        input = input.replaceAll("&2", "<dark_green>");
        input = input.replaceAll("&a", "<green>");
        input = input.replaceAll("&b", "<aqua>");
        input = input.replaceAll("&3", "<dark_aqua>");
        input = input.replaceAll("&1", "<dark_blue>");
        input = input.replaceAll("&9", "<blue>");
        input = input.replaceAll("&d", "<light_purple>");
        input = input.replaceAll("&5", "<dark_purple>");
        input = input.replaceAll("&f", "<white>");
        input = input.replaceAll("&7", "<gray>");
        input = input.replaceAll("&8", "<dark_gray>");
        input = input.replaceAll("&0", "<black>");
        input = input.replaceAll("&k", "<obfuscated>");
        input = input.replaceAll("&l", "<b>");
        input = input.replaceAll("&m", "<strikethrough>");
        input = input.replaceAll("&n", "<u>");
        input = input.replaceAll("&o", "<i>");
        input = input.replaceAll("&r", "<reset>");
        return input;
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode() != GameMode.CREATIVE) {
            if(event.getMessage().toLowerCase().contains("/image") || event.getMessage().toLowerCase().contains("/customimage")) {
                event.setCancelled(true);
                player.sendMessage(Component.text("This command is disabled."));
            }
        }
    }
}
