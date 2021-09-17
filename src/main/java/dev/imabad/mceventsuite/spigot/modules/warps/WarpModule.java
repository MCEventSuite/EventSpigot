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
        if(worldLoadEvent.getWorld().getName().equalsIgnoreCase("world")){
            mainWorld = worldLoadEvent.getWorld();
            if (plots != null) {
                generateWarpItems();
            }
        }
    }

    public void generateWarpItems(){
        warpItems = new ArrayList<>();
        World world = Bukkit.getWorld("world");
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
        ItemStack ENTRANCE = ItemUtils.createItemStack(Material.OAK_DOOR, "&r&9&lEntrance");
        warpItems.add(new WarpItem("Entrance", ENTRANCE, new Location(world, 1, 30, -99), WarpCategory.OTHER));
        ItemStack EXPO_HALL = ItemUtils.createItemStack(Material.YELLOW_WOOL, "&r&9&lExpo Hall");
        warpItems.add(new WarpItem("Expo Hall", EXPO_HALL, new Location(world, 18, 30, 153, -90, 0), WarpCategory.OTHER));
        ItemStack STAGE_ITEM = ItemUtils.createItemStack(Material.NETHER_STAR, "&r&9&lMain Stage");
        warpItems.add(new WarpItem("Main Stage", STAGE_ITEM, new Location(world, -27,30, 169, 21, 0), WarpCategory.OTHER));
        ItemStack OUTDOOR_STAGE_ITEM = ItemUtils.createItemStack(Material.JUKEBOX, "&r&9&lOutdoor Stage");
        warpItems.add(new WarpItem("Outdoor Stage", OUTDOOR_STAGE_ITEM, new Location(world, -75,30, 147, 90, 0), WarpCategory.OTHER));
        ItemStack GAMES_ITEM = ItemUtils.createItemStack(Material.STICKY_PISTON, "&r&9&lStickyPiston Arcade Games");
        warpItems.add(new WarpItem("StickyPiston Arcade Games", GAMES_ITEM, new Location(world, -45, 30, 23, 90, 0), WarpCategory.OTHER));
        ItemStack STICKY_PISTON = ItemUtils.createItemStack(Material.OAK_BOAT, "&r&a&lVIP Yacht");
        warpItems.add(new WarpItem("VIP Yacht", STICKY_PISTON, new Location(world, -206, 30, 298, 180, 0), WarpCategory.OTHER));
        ItemStack STATION = ItemUtils.createItemStack(Material.GRASS, "&r&9&lBiome Tour Experience");
        warpItems.add(new WarpItem("Biome Tour Experience", STATION, new Location(world, 42, 30, 12, -140, 0), WarpCategory.OTHER));
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
