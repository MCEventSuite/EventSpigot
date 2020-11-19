package dev.imabad.mceventsuite.spigot.modules.warps;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.events.CoreEvent;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventBooth;
import dev.imabad.mceventsuite.core.api.objects.EventBoothPlot;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.warps.commands.WarpCommand;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WarpModule extends Module implements Listener {

    private List<EventBoothPlot> plots;
    private List<WarpItem> warpItems;
    private World mainWorld;

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public void onEnable() {
        SimpleCommandMap commandMap = EventSpigot.getInstance().getCommandMap();
        commandMap.register("warp", new WarpCommand());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, this::onMysqlLoad);
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        plots = t.getMySQLDatabase().getDAO(BoothDAO.class).getPlots();
        generateWarpItems();
    }

    public void refresh(){
        plots = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlots();
        generateWarpItems();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent){
        if(worldLoadEvent.getWorld().getName().equalsIgnoreCase("venue")){
            mainWorld = worldLoadEvent.getWorld();
            generateWarpItems();
        }
    }

    public void generateWarpItems(){
        warpItems = new ArrayList<>();
        World world = Bukkit.getWorld("venue");
        plots.sort(Comparator.comparingInt(o -> WarpCategory.fromName(o.getBoothType()).ordinal()));
        for(int i = 0; i < plots.size(); i++){
            EventBoothPlot boothPlot = plots.get(i);
            WarpCategory fi = WarpCategory.fromName(boothPlot.getBoothType());
            String name = boothPlot.getBooth() == null ? "Booth" : boothPlot.getBooth().getName();
            ItemStack item = ItemUtils.createItemStack(fi.stackColor, StringUtils.colorizeMessage("&r&l" + name), 1);
            String[] splits = boothPlot.getFrontPos() == null ? boothPlot.getPosOne().split(",") : boothPlot.getFrontPos().split(",");
            Location l;
            if(splits.length > 3){
                l = new Location(world, Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]), Float.parseFloat(splits[3]), Float.parseFloat(splits[4]));
            } else {
                l = new Location(world, Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]));
            }
            warpItems.add(new WarpItem(name, item, l, fi));
        }
        ItemStack STAGE_ITEM = ItemUtils.createItemStack(Material.NETHER_STAR, "&r&9&lStage");
        warpItems.add(new WarpItem("Stage", STAGE_ITEM, new Location(world, 926,71, 529, -180, 0), WarpCategory.OTHER));
        ItemStack GAMES_ITEM = ItemUtils.createItemStack(Material.TRIDENT, "&r&9&lGames");
        warpItems.add(new WarpItem("Games", GAMES_ITEM, new Location(world, 977, 72, 535), WarpCategory.OTHER));
        ItemStack STICKY_PISTON = ItemUtils.createItemStack(Material.STICKY_PISTON, "&r&a&lStickyPiston");
        warpItems.add(new WarpItem("StickyPiston", STICKY_PISTON, new Location(world, 862, 66, 580, -80, 0), WarpCategory.OTHER));
        ItemStack ENTRANCE = ItemUtils.createItemStack(Material.OAK_DOOR, "&r&9&lEntrance");
        warpItems.add(new WarpItem("Entrance", ENTRANCE, new Location(world, 408, 78, 538), WarpCategory.OTHER));
        ItemStack STATION = ItemUtils.createItemStack(Material.MINECART, "&r&9&lStation");
        warpItems.add(new WarpItem("Station", STATION, new Location(world, 409, 78, 374), WarpCategory.OTHER));
        ItemStack MINIGAMEMASH = ItemUtils.createItemStack(Material.CROSSBOW, "&r&9&lMinigame Mash");
        warpItems.add(new WarpItem("Minigame Mash", MINIGAMEMASH, new Location(world, 558, 66, 569), WarpCategory.OTHER));
    }

    public List<WarpItem> getWarpItems() {
        return warpItems;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    public List<EventBoothPlot> getPlots() {
        return plots;
    }
}
