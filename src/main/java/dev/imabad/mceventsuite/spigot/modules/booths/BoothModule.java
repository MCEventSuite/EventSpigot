package dev.imabad.mceventsuite.spigot.modules.booths;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.api.PlotAPI;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BoothModule extends Module {

    private PlotAPI plotAPI;
    private EventRank boothMember;

    @Override
    public String getName() {
        return "booths";
    }

    @Override
    public void onEnable() {
        this.plotAPI = new PlotAPI();
        EventCore.getInstance().getEventRegistry().registerListener(JoinEvent.class, this::onPlayerJoin);
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, this::onMysqlLoad);
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        boothMember = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRankByName("Booth Member").orElse(new EventRank(15, "Booth Member", "", "", Collections.emptyList(), true));
    }

    public void fix(CommandSender sender){
        for(String s : Arrays.asList("small", "medium", "large")){
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

    @Override
    public void onDisable() {

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
