package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.events.JoinEvent;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.impl.EventPermissible;
import dev.imabad.mceventsuite.spigot.impl.SpigotPlayer;
import dev.imabad.mceventsuite.spigot.utils.PermissibleInjector;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class PlayerListener implements Listener {


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent playerJoinEvent){
        if(EventCore.getInstance().getEventPlayerManager() == null){
            playerJoinEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is still loading....");
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(playerJoinEvent.getPlayer().getGameMode() != GameMode.CREATIVE){
            playerJoinEvent.getPlayer().setGameMode(GameMode.CREATIVE);
        }
        if(EventSpigot.getInstance().getRankTeams().size() < 1){
            EventSpigot.getInstance().getRanks().forEach(eventRank -> {
                Team team = EventSpigot.getInstance().getScoreboard().registerNewTeam(eventRank.getName());
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', eventRank.getPrefix())+ " ");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                EventSpigot.getInstance().getRankTeams().put(eventRank.getId(), team);
            });
        }
        if(playerJoinEvent.getPlayer().getScoreboard() != EventSpigot.getInstance().getScoreboard()){
            playerJoinEvent.getPlayer().setScoreboard(EventSpigot.getInstance().getScoreboard());
            Team team = EventSpigot.getInstance().getScoreboard().getTeam(player.getRank().getName());
            if(!team.hasEntry(playerJoinEvent.getPlayer().getDisplayName())) {
                team.addEntry(playerJoinEvent.getPlayer().getDisplayName());
            }
        }
        for(PotionEffect potionEffect : playerJoinEvent.getPlayer().getActivePotionEffects()){
            playerJoinEvent.getPlayer().removePotionEffect(potionEffect.getType());
        }
        playerJoinEvent.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true, false, false));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent playerQuitEvent){
        try {
            PermissibleInjector.uninject(playerQuitEvent.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventCore.getInstance().getEventPlayerManager().getPlayer(playerQuitEvent.getPlayer().getUniqueId()).ifPresent(eventPlayer -> {
            Team team = EventSpigot.getInstance().getScoreboard().getTeam(eventPlayer.getRank().getName());
            if(team.hasEntry(eventPlayer.getLastUsername())){
                team.removeEntry(eventPlayer.getLastUsername());
            }
            EventCore.getInstance().getEventPlayerManager().removePlayer(eventPlayer);
        });
    }

}
