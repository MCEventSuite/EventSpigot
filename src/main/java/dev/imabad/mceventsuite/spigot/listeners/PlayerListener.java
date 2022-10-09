package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.events.JoinEvent;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.api.objects.EventRank;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassDAO;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassReward;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassUnlockedReward;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.core.util.SpecialTag;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.commands.TpaCommand;
import dev.imabad.mceventsuite.spigot.impl.EventPermissible;
import dev.imabad.mceventsuite.spigot.impl.SpigotPlayer;
import dev.imabad.mceventsuite.spigot.interactions.Interaction;
import dev.imabad.mceventsuite.spigot.interactions.InteractionRegistry;
import dev.imabad.mceventsuite.spigot.modules.bubbles.BubbleManager;
import dev.imabad.mceventsuite.spigot.modules.bubbles.BubbleModule;
import dev.imabad.mceventsuite.spigot.modules.bubbles.ChatBubble;
import dev.imabad.mceventsuite.spigot.modules.chat.ChatFilterModule;
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import dev.imabad.mceventsuite.spigot.modules.meet.MeetModule;
import dev.imabad.mceventsuite.spigot.modules.player.PlayerHotbar;
import dev.imabad.mceventsuite.spigot.modules.teams.TeamModule;
import dev.imabad.mceventsuite.spigot.utils.BungeeUtils;
import dev.imabad.mceventsuite.spigot.utils.PermissibleInjector;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.CheckType;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent playerJoinEvent){
        if(EventCore.getInstance().getEventPlayerManager() == null){
            playerJoinEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is still loading....");
        }
    }

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event){
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        EventPlayer player = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(PlayerDAO.class).getOrCreatePlayer(playerJoinEvent.getPlayer().getUniqueId(), playerJoinEvent.getPlayer().getDisplayName());
        SpigotPlayer spigotPlayer = SpigotPlayer.asSpigot(player, playerJoinEvent.getPlayer());
        EventCore.getInstance().getEventPlayerManager().addPlayer(spigotPlayer);
        EventCore.getInstance().getEventRegistry().handleEvent(new JoinEvent(spigotPlayer));
        EventPermissible eventPermissible = new EventPermissible(playerJoinEvent.getPlayer(), player);
        try {
            PermissibleInjector.inject(playerJoinEvent.getPlayer(), eventPermissible);
            playerJoinEvent.getPlayer().updateCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (EventSpigot.getInstance().isEvent()) {
            if (playerJoinEvent.getPlayer().getGameMode() != GameMode.ADVENTURE) {
                playerJoinEvent.getPlayer().setGameMode(GameMode.ADVENTURE);
            }
        } else {
            if (playerJoinEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                playerJoinEvent.getPlayer().setGameMode(GameMode.CREATIVE);
            }
        }

        if (playerJoinEvent.getPlayer().getScoreboard() != EventSpigot.getInstance().getScoreboard()) {
            playerJoinEvent.getPlayer().setScoreboard(EventSpigot.getInstance().getScoreboard());
        }

        EventCore.getInstance().getModuleRegistry().getModule(TeamModule.class)
                .getTeamManager().addPlayerToTeam(playerJoinEvent.getPlayer(), player.getRank());

        for (PotionEffect potionEffect : playerJoinEvent.getPlayer().getActivePotionEffects()) {
            playerJoinEvent.getPlayer().removePotionEffect(potionEffect.getType());
        }

        Optional<Location> lastLocation = BungeeUtils.getLastLocation(playerJoinEvent.getPlayer());
        lastLocation.ifPresentOrElse(
                (location) -> playerJoinEvent.getPlayer().teleport(location),
                () -> playerJoinEvent.getPlayer().teleport(EventCore.getInstance().getModuleRegistry().getModule(MapModule.class).getRandomLocation())
        );

        PlayerHotbar.givePlayerInventory(playerJoinEvent.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(EventSpigot.getInstance(), () -> {
            EventPassPlayer eventPassPlayer = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(EventPassDAO.class).getOrCreateEventPass(player);

            int level = eventPassPlayer.levelFromXP();
            List<EventPassReward> unlockedRewards = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(EventPassDAO.class).getUnlockedRewards(player).stream().map(EventPassUnlockedReward::getUnlockedReward).collect(Collectors.toList());
            List<EventPassReward> rewards = EventCore.getInstance().getModuleRegistry().getModule(EventPassModule.class).getEventPassRewards().stream().filter(eventPassReward -> eventPassReward.getRequiredLevel() > 0 && eventPassReward.getRequiredLevel() <= level && !unlockedRewards.contains(eventPassReward)).sorted(Comparator.comparingInt(EventPassReward::getRequiredLevel)).collect(Collectors.toList());
            for (EventPassReward reward : rewards) {
                EventPassUnlockedReward unlockedReward = new EventPassUnlockedReward(reward, player);
                EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(EventPassDAO.class).saveUnlockedReward(unlockedReward);
            }
            float progressToNext = (float) EventPassModule.experience(level) / (float) EventPassModule.experience(level + 1);
            playerJoinEvent.getPlayer().setLevel(level);
            playerJoinEvent.getPlayer().setExp(progressToNext);
        });
        playerJoinEvent.setJoinMessage("");

        if (Bukkit.getPluginManager().isPluginEnabled("NoCheatPlus")) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(playerJoinEvent.getPlayer().getUniqueId())) {
                NCPAPIProvider.getNoCheatPlusAPI().getPlayerDataManager().getPlayerData(playerJoinEvent.getPlayer()).exempt(CheckType.ALL);
            }
            NCPAPIProvider.getNoCheatPlusAPI().getPlayerDataManager().getPlayerData(playerJoinEvent.getPlayer()).exempt(CheckType.MOVING_SURVIVALFLY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        event.setCancelled(true);
        Optional<EventPlayer> eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        BubbleManager bubbleManager = EventCore.getInstance().getModuleRegistry().getModule(BubbleModule.class)
                .getBubbleManager();
        if (eventPlayer.isPresent()) {
            final EventRank rank = eventPlayer.get().getRank();
            if (event.originalMessage() instanceof TextComponent textComponent) {
                EventCore.getInstance().getModuleRegistry().getModule(ChatFilterModule.class)
                        .checkText(event.getPlayer(), textComponent.content()).thenAcceptAsync((allowed) -> {

                            if(!allowed) {
                                event.getPlayer().sendMessage(Component.text("Your message was blocked by the filter!")
                                        .color(NamedTextColor.RED));
                                return;
                            }

                            //TODO tidy this
                            NamedTextColor chatColor = NamedTextColor.GRAY;
                            String chatColorHex = rank.getChatColor().substring(1);
                            if (chatColorHex.equalsIgnoreCase("fff"))
                                chatColor = NamedTextColor.WHITE;

                            Component javaMessage = Component.text().append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                    (rank.getJavaPrefix().isEmpty() ? "" : StringUtils.colorizeMessage(rank.getJavaPrefix()))
                                            + event.getPlayer().getName()
                                            + (rank.getSuffix().isEmpty() ? "" : StringUtils.colorizeMessage(" " + rank.getSuffix()))
                                            + ": ")).asComponent();

                            Component bedrockMessage = Component.text().append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                    (rank.getBedrockPrefix().isEmpty() ? "" : StringUtils.colorizeMessage(rank.getBedrockPrefix()))
                                            + event.getPlayer().getName()
                                            + (rank.getSuffix().isEmpty() ? "" : StringUtils.colorizeMessage(" " + rank.getSuffix()))
                                            + ": ")).asComponent();

                            javaMessage = javaMessage.append(event.originalMessage().color(chatColor));
                            bedrockMessage = bedrockMessage.append(event.originalMessage().color(chatColor));

                            ChatBubble chatBubble = bubbleManager.getChatBubble(event.getPlayer().getUniqueId());
                            if (chatBubble != null) {
                                javaMessage = Component.text("[" + chatBubble.getDisplayName() + "] ").color(NamedTextColor.GREEN).append(javaMessage);
                                bedrockMessage = Component.text("[" + chatBubble.getDisplayName() + "] ").color(NamedTextColor.GREEN).append(bedrockMessage);
                            }

                            for (Player player : Bukkit.getAllOnlinePlayers()) {
                                ChatBubble playerBubble = bubbleManager.getChatBubble(player.getUniqueId());
                                if (playerBubble != null && (chatBubble == null || !chatBubble.getName().equalsIgnoreCase(playerBubble.getName()))) {
                                    boolean global = player.getPersistentData("cb_global") == null || player.getPersistentData("cb_global").equals("true");
                                    if (!player.hasPermission("eventsuite.cb.monitor") || !global)
                                        continue;
                                }

                                if (chatBubble == null || chatBubble.getMembers().contains(player.getUniqueId())) {
                                    if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId()))
                                        player.sendMessage(event.getPlayer(), bedrockMessage);
                                    else
                                        player.sendMessage(event.getPlayer(), javaMessage);
                                }
                            }
                        });
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent playerQuitEvent) {
        try {
            PermissibleInjector.uninject(playerQuitEvent.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventCore.getInstance().getModuleRegistry().getModule(TeamModule.class)
                .getTeamManager().removePlayerFromTeam(playerQuitEvent.getPlayer());

        Bukkit.getScheduler().runTaskAsynchronously(EventSpigot.getInstance(), () -> {
            EventCore.getInstance().getEventPlayerManager().getPlayer(playerQuitEvent.getPlayer().getUniqueId()).ifPresent(eventPlayer -> {
                TpaCommand.getTeleportRequests().remove(eventPlayer.getUUID().toString());
                EventCore.getInstance().getEventPlayerManager().removePlayer(eventPlayer);
            });
            BungeeUtils.saveLocationSynchronously(playerQuitEvent.getPlayer());
        });
        playerQuitEvent.setQuitMessage("");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getClickedInventory() != null) {
            for (EventInventory eventInventory : EventInventory.EVENT_INVENTORIES) {
                if(eventInventory.isInventory(event.getClickedInventory())) {
                    // TODO: Potentially allow handler to override this (but safely if the handler throws)
                    event.setCancelled(true);

                    ClickType clickType = event.getClick();
                    InventoryType.SlotType slotType = event.getSlotType();
                    boolean isPlayerInventory = event.getClickedInventory().equals(event.getWhoClicked().getInventory());
                    ItemStack clickItem = event.getCurrentItem();
                    int slot = event.getSlot();
                    eventInventory.onPlayerClick(event.getWhoClicked(), slot, isPlayerInventory, clickItem, slotType, clickType);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        InteractionRegistry.handleEvent(Interaction.MOVE, event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
                InteractionRegistry.handleEvent(Interaction.RIGHT_CLICK, event);
                InteractionRegistry.handleEvent(Interaction.RIGHT_CLICK_AIR, event);
                break;
            case RIGHT_CLICK_BLOCK:
                InteractionRegistry.handleEvent(Interaction.RIGHT_CLICK, event);
                InteractionRegistry.handleEvent(Interaction.RIGHT_CLICK_BLOCK, event);
                break;
            case LEFT_CLICK_AIR:
                InteractionRegistry.handleEvent(Interaction.LEFT_CLICK, event);
                InteractionRegistry.handleEvent(Interaction.LEFT_CLICK_AIR, event);
                break;
            case LEFT_CLICK_BLOCK:
                InteractionRegistry.handleEvent(Interaction.LEFT_CLICK, event);
                InteractionRegistry.handleEvent(Interaction.LEFT_CLICK_BLOCK, event);
                break;
        }
    }

    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event) {
        InteractionRegistry.handleEvent(Interaction.RIGHT_CLICK_ENTITY, event);
        InteractionRegistry.handleEvent(Interaction.INTERACT_ENTITY, event);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        InteractionRegistry.handleEvent(Interaction.CLICK_INSIDE_INVENTORY, event);
        if(event.getClickedInventory() instanceof PlayerInventory && !event.getWhoClicked().hasPermission("eventsuite.inventory")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event) {
        InteractionRegistry.handleEvent(Interaction.LEFT_CLICK_ENTITY, event);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event){
        InteractionRegistry.handleEvent(Interaction.CLOSE_INVENTORY, event);
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event){
        InteractionRegistry.handleEvent(Interaction.DRAG_INVENTORY, event);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(!event.getPlayer().hasPermission("eventsuite.inventory")){
            event.setCancelled(true);
        }
    }
}
