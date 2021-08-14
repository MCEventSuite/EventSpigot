package dev.imabad.mceventsuite.spigot.modules.booths;

import java.util.*;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.events.PlayerAutoPlotEvent;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.plotsquared.core.events.Result;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.events.JoinEvent;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventBooth;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.api.objects.EventRank;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.core.modules.mysql.dao.RankDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class BoothModule extends Module implements Listener {

    private PlotAPI plotAPI;
    private EventRank boothMember;
    private CitizensListener citizensListener;
    public final static List<String> BOOTH_WORLDS = Arrays.asList("small", "medium", "large");

    @Override
    public String getName() {
        return "booths";
    }

    @Override
    public void onEnable() {
        this.plotAPI = new PlotAPI();
        this.plotAPI.registerListener(this);
        Bukkit.getPluginManager().registerEvents(this, EventSpigot.getInstance());
        EventCore.getInstance().getEventRegistry().registerListener(JoinEvent.class, this::onPlayerJoin);
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, this::onMysqlLoad);

        if(EventSpigot.getInstance().getServer().getPluginManager().getPlugin("Citizens") != null) {
            this.citizensListener = new CitizensListener();
            EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this.citizensListener, EventSpigot.getInstance());
        }
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        boothMember = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRankByName("Booth Member").orElse(new EventRank(15, "Booth Member", "", "", Collections.emptyList(), true));
    }

    @Override
    public void onDisable() {
        // Ideally we would disable the PlotSquared event listeners if we could
        WorldLoadEvent.getHandlerList().unregister(this);
        if (this.citizensListener != null) {
            CitizensEnableEvent.getHandlerList().unregister(this.citizensListener);
        }
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Arrays.asList(MySQLModule.class, RedisModule.class);
    }

    public void onPlayerJoin(JoinEvent joinEvent){
        EventPlayer player = joinEvent.getPlayer();
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());
        PlotPlayer plotPlayer = PlotPlayer.from(bukkitPlayer);
        List<EventBooth> booths = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlayerBooths(player);

        if (booths.size() > 0) {
            booths.stream().filter(eventBooth -> eventBooth.getOwner().equals(player)).filter(eventBooth -> eventBooth.getStatus().equalsIgnoreCase("un-assigned")).forEach(eventBooth -> {
                EventSpigot.getInstance().getLogger().info("Adding permissions for " + bukkitPlayer.getName());
                player.getPermissions().add("multiverse.access." + eventBooth.getBoothType());
                EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(PlayerDAO.class).saveOrUpdatePlayer(player);

                EventSpigot.getInstance().getLogger().info("Teleporting " + bukkitPlayer.getName() + " to relevant booth world.");
                bukkitPlayer.teleport(Bukkit.getWorld(eventBooth.getBoothType()).getSpawnLocation());

                plotAPI.getPlotAreas(eventBooth.getBoothType()).stream().findFirst().ifPresent(plotArea -> {
                    EventSpigot.getInstance().getLogger().info("Claiming plot...");
                    Plot plot = plotArea.getNextFreePlot(plotPlayer, null);
                    plot.claim(plotPlayer, true, null, true);
                    eventBooth.setStatus("assigned");

                    EventSpigot.getInstance().getLogger().info("Finding members...");
                    eventBooth.getMembers().forEach(member -> {
                        EventSpigot.getInstance().getLogger().info("Adding permissions for member " + member.getUUID());
                        EventPlayer eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(member.getUUID()).orElse(member);
                        eventPlayer.setPermissions(Arrays.asList("multiverse.access." + eventBooth.getBoothType()));

                        plot.addTrusted(eventPlayer.getUUID());
                        if(eventPlayer.getRank().getPower() < boothMember.getPower()){
                            EventSpigot.getInstance().getLogger().info("Set member rank.");
                            eventPlayer.setRank(boothMember);
                        }

                        EventSpigot.getInstance().getLogger().info("Updating member in database...");
                        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(PlayerDAO.class).saveOrUpdatePlayer(eventPlayer);
                    });
                    eventBooth.setPlotID(plot.getId().toString());
                    EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).saveBooth(eventBooth);
                    EventSpigot.getInstance().getLogger().info("Assignment of new booth completed.");
                });
            });
        } else {
            if (plotPlayer == null || plotAPI.getPlayerPlots(plotPlayer).size() == 0) {
                World world = Bukkit.getWorld("creative");
                bukkitPlayer.teleport(new Location(world, 0.5, 52, -1.5, 0, 0));
            }
        }

        bukkitPlayer.setGameMode(GameMode.CREATIVE);
    }

    public boolean canClaimPlots(PlotPlayer plotPlayer) {
        Optional<EventPlayer> player = EventCore.getInstance().getEventPlayerManager().getPlayer(plotPlayer.getUUID());
        return player.isPresent() && player.get().getRank().getPower() >= 100;
    }

    @Subscribe
    public void onClaimCommand(PlayerClaimPlotEvent event) {
        if (canClaimPlots(event.getPlotPlayer())) return;
        if (BOOTH_WORLDS.contains(event.getPlot().getWorldName())) {
            event.setEventResult(Result.DENY);
        }
    }

    @Subscribe
    public void onAutoCommand(PlayerAutoPlotEvent event) {
        if (canClaimPlots(event.getPlayer())) return;
        if (BOOTH_WORLDS.contains(event.getPlotArea().getWorldName())) {
            event.setEventResult(Result.DENY);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.contains("/multiverse-core:mvtp")
                || command.contains("/multiverse-core:mv tp")
                || command.contains("/mv tp")
                || command.contains("/mvtp")) {
            event.setCancelled(true);
            new TeleportInventory(event.getPlayer()).open(event.getPlayer(), null);
        }
    }

}
