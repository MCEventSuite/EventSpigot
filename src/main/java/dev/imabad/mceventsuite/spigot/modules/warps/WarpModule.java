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
        System.out.println("LOADING PLOTS: " + plots.toString());
        generateWarpItems();
    }

    public void refresh(){
        plots = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlots();
        generateWarpItems();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent){
        if(worldLoadEvent.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
            mainWorld = worldLoadEvent.getWorld();
            if (plots != null) {
                generateWarpItems();
            }
        }
    }

    public void generateWarpItems(){
        warpItems = new ArrayList<>();
        World world = Bukkit.getWorlds().get(0);
        plots.sort(Comparator.comparingInt(o -> WarpCategory.fromName(o.getBoothType()).ordinal()));
        plots.forEach(plot -> {
            WarpCategory category = WarpCategory.fromName(plot.getBoothType());
            String name = plot.getBooth().getName();
            ItemStack item = ItemUtils.createItemStack(category.stackColor, StringUtils.colorizeMessage("&r&l" + name), 1);
            String[] splits = plot.getFrontPos() == null ? plot.getPosOne().split(",") : plot.getFrontPos().split(",");
            Location location;
            if(splits.length > 3) {
                location = new Location(world, Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]), Float.parseFloat(splits[3]), Float.parseFloat(splits[4]));
            }else{
                location = new Location(world, Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]));
            }
            warpItems.add(new WarpItem(name, item, location, category));
        });

        ItemStack ENTRANCE = ItemUtils.createItemStack(Material.OAK_DOOR, "&r&9&lEntrance");
        warpItems.add(new WarpItem("Entrance", ENTRANCE, new Location(world, 0.5, 30, -99.5, 0, 0), WarpCategory.OTHER));
        ItemStack EXPO_HALL = ItemUtils.createItemStack(Material.YELLOW_CONCRETE, "&r&9&lExpo Hall");
        warpItems.add(new WarpItem("Expo Hall", EXPO_HALL, new Location(world, 58.5, 30, 153.5, -90, 0), WarpCategory.OTHER));
        ItemStack STAGE_ITEM = ItemUtils.createItemStack(Material.NETHER_STAR, "&r&9&lMain Stage");
        warpItems.add(new WarpItem("Main Stage", STAGE_ITEM, new Location(world, -33.5, 30, 190.5, 0, 0), WarpCategory.OTHER));
        ItemStack OUTDOOR_STAGE_ITEM = ItemUtils.createItemStack(Material.JUKEBOX, "&r&9&lOutdoor Stage");
        warpItems.add(new WarpItem("Outdoor Stage", OUTDOOR_STAGE_ITEM, new Location(world, -96.5, 30, 148.5, 90, 0), WarpCategory.OTHER));
        ItemStack GAMES_ITEM = ItemUtils.createItemStack(Material.STICKY_PISTON, "&r&9&lStickyPiston Arcade Games");
        warpItems.add(new WarpItem("StickyPiston Arcade Games", GAMES_ITEM, new Location(world, -45, 30, 23, 90, 0), WarpCategory.OTHER));
        ItemStack STICKY_PISTON = ItemUtils.createItemStack(Material.OAK_BOAT, "&r&a&lVIP Yacht");
        warpItems.add(new WarpItem("VIP Yacht", STICKY_PISTON, new Location(world, -206, 30, 298, 180, 0), WarpCategory.OTHER));
        ItemStack STATION = ItemUtils.createItemStack(Material.PAPER, "&r&9&lMeet & Greet");
        warpItems.add(new WarpItem("Meet & Greet", STATION, new Location(world, 53.5, 30, 11.5, 180, 0), WarpCategory.OTHER));


        ItemStack dreamKingdom = ItemUtils.createItemStack(Material.RED_CONCRETE, "&r&lDreamKingdom");
        ItemStack paradiseIsles = ItemUtils.createItemStack(Material.RED_CONCRETE, "&r&lParadise Isles");
        ItemStack imaginears = ItemUtils.createItemStack(Material.RED_CONCRETE, "&r&lImaginears Club");
        warpItems.add(new WarpItem("DreamKingdom", dreamKingdom, new Location(world, 277.5, 30, 330.5, 0, 0), WarpCategory.HALL));
        warpItems.add(new WarpItem("Paradise Isles", paradiseIsles, new Location(world, 210.5, 30, 323.5, 0, 0), WarpCategory.HALL));
        warpItems.add(new WarpItem("Imaginears Club", imaginears, new Location(world, 142.5, 30, 322.5, 0, 0), WarpCategory.HALL));
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
