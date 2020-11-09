package dev.imabad.mceventsuite.spigot.modules.booths;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.util.MainUtil;
import com.plotsquared.core.util.SchematicHandler;
import com.plotsquared.core.util.task.RunnableVal;
import com.plotsquared.core.util.task.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionBuilder;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
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
import dev.imabad.mceventsuite.spigot.modules.booths.commands.FixBoothsCommand;
import dev.imabad.mceventsuite.spigot.modules.booths.commands.SchemBoothsCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.io.FileOutputStream;
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
        SimpleCommandMap commandMap = EventSpigot.getInstance().getCommandMap();
        commandMap.register("schembooths", new SchemBoothsCommand());
        commandMap.register("fixBooths", new FixBoothsCommand());
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        boothMember = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRankByName("Booth Member").orElse(new EventRank(15, "Booth Member", "", "", Collections.emptyList(), true));
    }

    public int maxY(String type){
        switch(type.toLowerCase()){
            case "small":
                return 11;
            case "medium":
                return 16;
            case "large":
                return 22;
        }
        return 0;
    }

    public void schematicBooths(CommandSender sender){
        Player player = (Player) sender;
        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getBooths().stream().filter(eventBooth -> eventBooth.getStatus().equalsIgnoreCase("assigned")).forEach(eventBooth -> {
            plotAPI.getPlotAreas(eventBooth.getBoothType()).forEach(plotArea -> {
                Plot plot = plotArea.getPlot(PlotId.fromString(eventBooth.getPlotID()));
                if(plot != null) {
                    World world = Bukkit.getWorld(eventBooth.getBoothType());
                    CuboidRegion region = plot.getLargestRegion();
                    System.out.println(region.getMinimumPoint().toParserString() + " to " + region.getMaximumPoint().toParserString());
                    int maxY = 64 + maxY(eventBooth.getBoothType());
                    System.out.println("Max Y for booth is " + maxY);
                    if(region.getPos1().getY() > maxY){
                        BlockVector3 newPos = BlockVector3.at(region.getPos1().getX(), maxY + 1, region.getPos1().getZ());
                        region.setPos1(newPos);
                    } else if(region.getPos2().getY() > maxY){
                        BlockVector3 newPos = BlockVector3.at(region.getPos2().getX(), maxY + 1, region.getPos2().getZ());
                        region.setPos2(newPos);
                    }
                    BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).build();
                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
                    forwardExtentCopy.setCopyingEntities(true);
                    try {
                        Operations.complete(forwardExtentCopy);
                        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(new File(EventSpigot.getInstance().getDataFolder().getAbsolutePath() + File.separator + "booths" + File.separator + eventBooth.getId())))) {
                            writer.write(clipboard);
                            sender.sendMessage("Exported booth - " + eventBooth.getName());
                        }
                    }catch (Exception e){
                        sender.sendMessage("Error exportig booth - " + eventBooth.getName());
                        e.printStackTrace();
                    }
                }
            });
        });
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

    private void updateWorldBorder(String worldName, Optional<Integer> minimumDistanceFromOrigin) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            System.err.println("World " + worldName + " not found, skipping border check...");
            return;
        }

        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(determineBorderSize(worldName, minimumDistanceFromOrigin) + 5);
    }

    private double determineBorderSize(String worldName, Optional<Integer> minimumDistanceFromOrigin) {
        int maxDistanceFromOrigin = minimumDistanceFromOrigin.orElse(0);

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
        int distance = event.getPlot().getDistanceFromOrigin();
        updateWorldBorder(event.getPlot().getWorldName(), Optional.of(distance));
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
