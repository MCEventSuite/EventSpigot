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
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import dev.imabad.mceventsuite.spigot.modules.player.PlayerHotbar;
import dev.imabad.mceventsuite.spigot.utils.BungeeUtils;
import dev.imabad.mceventsuite.spigot.utils.PermissibleInjector;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.CheckType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
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
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
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
        if(EventSpigot.getInstance().isEvent()){
            if (playerJoinEvent.getPlayer().getGameMode() != GameMode.ADVENTURE) {
                playerJoinEvent.getPlayer().setGameMode(GameMode.ADVENTURE);
            }
        } else {
            if (playerJoinEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                playerJoinEvent.getPlayer().setGameMode(GameMode.CREATIVE);
            }
        }
        if(EventSpigot.getInstance().getRankTeams().size() < 1){
            EventSpigot.getInstance().getRanks().forEach(eventRank -> {
                if (EventSpigot.getInstance().getScoreboard().getTeam(eventRank.getName()) == null) {
                    Team team = EventSpigot.getInstance().getScoreboard().registerNewTeam(eventRank.getName());
                    team.setPrefix(ChatColor.translateAlternateColorCodes('&', eventRank.getPrefix()) + (eventRank.getPrefix().length() > 0 ? " " : ""));
                    team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                    EventSpigot.getInstance().getRankTeams().put(eventRank.getId(), team);
                } else {
                    EventSpigot.getInstance().getLogger().warning("Scoreboard team already exists: " + eventRank.getName());
                }
            });
        }
        if(playerJoinEvent.getPlayer().getScoreboard() != EventSpigot.getInstance().getScoreboard()){
            playerJoinEvent.getPlayer().setScoreboard(EventSpigot.getInstance().getScoreboard());
        }
        Team team = EventSpigot.getInstance().getScoreboard().getTeam(player.getRank().getName());
        if(!team.hasEntry(playerJoinEvent.getPlayer().getDisplayName())) {
            team.addEntry(playerJoinEvent.getPlayer().getDisplayName());
        }
        for(PotionEffect potionEffect : playerJoinEvent.getPlayer().getActivePotionEffects()){
            playerJoinEvent.getPlayer().removePotionEffect(potionEffect.getType());
        }

        Optional<Location> lastLocation = BungeeUtils.getLastLocation(playerJoinEvent.getPlayer());
        lastLocation.ifPresentOrElse(
                (location) -> playerJoinEvent.getPlayer().teleport(location),
                () -> playerJoinEvent.getPlayer().teleport(EventCore.getInstance().getModuleRegistry().getModule(MapModule.class).getRandomLocation())
        );

        PlayerHotbar.givePlayerInventory(playerJoinEvent.getPlayer());
        EventPassPlayer eventPassPlayer = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(EventPassDAO.class).getOrCreateEventPass(player);

        int level = eventPassPlayer.levelFromXP();
        List<EventPassReward> unlockedRewards = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(EventPassDAO.class).getUnlockedRewards(player).stream().map(EventPassUnlockedReward::getUnlockedReward).collect(Collectors.toList());
        List<EventPassReward> rewards = EventCore.getInstance().getModuleRegistry().getModule(EventPassModule.class).getEventPassRewards().stream().filter(eventPassReward -> eventPassReward.getRequiredLevel() > 0 && eventPassReward.getRequiredLevel() <= level && !unlockedRewards.contains(eventPassReward)).sorted(Comparator.comparingInt(EventPassReward::getRequiredLevel)).collect(Collectors.toList());
        for(EventPassReward reward : rewards) {
            EventPassUnlockedReward unlockedReward = new EventPassUnlockedReward(reward, player);
            EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(EventPassDAO.class).saveUnlockedReward(unlockedReward);
        }
        float progressToNext = (float) EventPassModule.experience(level) / (float) EventPassModule.experience(level + 1);
        playerJoinEvent.getPlayer().setLevel(level);
        playerJoinEvent.getPlayer().setExp(progressToNext);
        playerJoinEvent.setJoinMessage("");
        if(FloodgateApi.getInstance().isFloodgatePlayer(playerJoinEvent.getPlayer().getUniqueId())){
            NCPAPIProvider.getNoCheatPlusAPI().getPlayerDataManager().getPlayerData(playerJoinEvent.getPlayer()).exempt(CheckType.ALL);
        }
        NCPAPIProvider.getNoCheatPlusAPI().getPlayerDataManager().getPlayerData(playerJoinEvent.getPlayer()).exempt(CheckType.MOVING_SURVIVALFLY);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Optional<EventPlayer> eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if(eventPlayer.isPresent()) {
            final EventRank rank = eventPlayer.get().getRank();

            //TODO tidy this
            ChatColor chatColor = ChatColor.GRAY;
            String chatColorHex = rank.getChatColor().substring(1);
            if (chatColorHex.equalsIgnoreCase("fff"))
                chatColor = ChatColor.WHITE;

            event.setFormat(
                    (rank.getPrefix().isEmpty() ? "" : StringUtils.colorizeMessage(rank.getPrefix() + " "))
                    + "%s"
                    + (rank.getSuffix().isEmpty() ? "" : StringUtils.colorizeMessage(" " + rank.getSuffix()))
                    + ": " + chatColor + "%s"
            );
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent playerQuitEvent){
        try {
            PermissibleInjector.uninject(playerQuitEvent.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventCore.getInstance().getEventPlayerManager().getPlayer(playerQuitEvent.getPlayer().getUniqueId()).ifPresent(eventPlayer -> {
            TpaCommand.getTeleportRequests().remove(eventPlayer.getUUID().toString());
            Team team = EventSpigot.getInstance().getScoreboard().getTeam(eventPlayer.getRank().getName());
            if(team.hasEntry(eventPlayer.getLastUsername())){
                team.removeEntry(eventPlayer.getLastUsername());
            }
            EventCore.getInstance().getEventPlayerManager().removePlayer(eventPlayer);
        });
        BungeeUtils.saveLocationSynchronously(playerQuitEvent.getPlayer());
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
