package dev.imabad.mceventsuite.spigot.modules.booths;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BoothModule extends Module implements Listener {

    private PlotAPI plotAPI;
    private EventRank boothMember;
    private List<String> boothWorlds = Arrays.asList("small", "medium", "large");

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
        updateWorldBorders();
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        boothMember = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRankByName("Booth Member").orElse(new EventRank(15, "Booth Member", "", "", Collections.emptyList(), true));
    }

    public void fix(CommandSender sender){
        for(String s : boothWorlds){
            sender.sendMessage("Fixing booths in " + s);
            plotAPI.getPlotAreas(s).forEach(plotArea -> {
                sender.sendMessage("PlotArea: " + plotArea.getId());
                plotArea.getPlots().forEach(plot -> {
                    sender.sendMessage("Plot: " + plot.getId() + "has " + plot.getTrusted().size() + " trusted and " + plot.getMembers().size() + " members");
                    EventBooth booth = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getBoothFromPlotID(s, plot.getId().toString());
                    if(booth != null){
                        plot.getTrusted().clear();
                        sender.sendMessage("Fixing plot: " + plot.getId());
                        booth.getMembers().forEach(player -> {
                            plot.addTrusted(player.getUUID());
                        });
                        sender.sendMessage("Plot: " + plot.getId() + " now has " + plot.getTrusted().size() + " trusted and " + plot.getMembers().size() + " members");
                    }
                });
            });
        }
    }

    public boolean canPlayerEdit(Player player, Location location){
        if(player.isOp()){
            return true;
        }
        for(PlotArea area: plotAPI.getPlotAreas(location.getWorld().getName())){
            Plot plot = area.getPlot(BukkitUtil.getLocation(location));
            if(plot != null && plot.isAdded(player.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    private void updateWorldBorders() {
        for (String worldName : boothWorlds) {
            updateWorldBorder(worldName, Optional.empty());
        }
    }

    private void updateWorldBorder(String worldName, Optional<Double> size) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            System.err.println("World " + worldName + " not found, skipping border check...");
            return;
        }

        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize((size.isPresent() ? size.get() : determineBorderSize(worldName)) + 5);
    }

    private double determineBorderSize(String worldName) {
        int maxDistanceFromOrigin = 0;

        for (PlotArea area : plotAPI.getPlotAreas(worldName)) {
            for (Plot plot : area.getPlots()) {
                int distanceFromOrigin = plot.getDistanceFromOrigin();
                if (distanceFromOrigin > maxDistanceFromOrigin) {
                    maxDistanceFromOrigin = distanceFromOrigin;
                }
            }
        }

        return maxDistanceFromOrigin * 2;
    }

    @Subscribe
    public void onPlotAssigned(PlayerClaimPlotEvent event) {
        if (!this.isEnabled()) return;
        double distance = event.getPlot().getDistanceFromOrigin();
        updateWorldBorder(event.getPlot().getWorldName(), Optional.of(distance * 2));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (boothWorlds.contains(event.getWorld().getName())) {
            updateWorldBorder(event.getWorld().getName(), Optional.empty());
        }
    }

    @Override
    public void onDisable() {
        // Ideally we would disable the PlotSquared event listeners if we could
        WorldLoadEvent.getHandlerList().unregister(this);
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Arrays.asList(MySQLModule.class, RedisModule.class);
    }

    public void onPlayerJoin(JoinEvent joinEvent){
        EventPlayer player = joinEvent.getPlayer();
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());
        List<EventBooth> booths = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlayerBooths(player);
        booths.stream().filter(eventBooth -> eventBooth.getOwner().equals(player)).filter(eventBooth -> eventBooth.getStatus().equalsIgnoreCase("un-assigned")).forEach(eventBooth -> {
            bukkitPlayer.teleport(Bukkit.getWorld(eventBooth.getBoothType()).getSpawnLocation());
            plotAPI.getPlotAreas(eventBooth.getBoothType()).stream().findFirst().ifPresent(plotArea -> {
                PlotPlayer plotPlayer = PlotPlayer.wrap(player.getUUID());
                Plot plot = plotArea.getNextFreePlot(plotPlayer, null);
                plot.claim(plotPlayer, true, null, true);
                eventBooth.setStatus("assigned");
                eventBooth.getMembers().forEach(eventPlayer -> {
                    plot.addTrusted(eventPlayer.getUUID());
                    if(eventPlayer.getRank().getPower() < boothMember.getPower()){
                        eventPlayer.setRank(boothMember);
                        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(PlayerDAO.class).saveOrUpdatePlayer(eventPlayer);
                    }
                });
                eventBooth.setPlotID(plot.getId().toString());
                EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).saveBooth(eventBooth);
            });
        });
    }

}
