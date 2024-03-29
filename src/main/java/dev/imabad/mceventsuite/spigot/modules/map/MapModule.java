package dev.imabad.mceventsuite.spigot.modules.map;

import com.google.gson.Gson;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventBooth;
import dev.imabad.mceventsuite.core.api.objects.EventBoothPlot;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.influx.InfluxDBModule;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.map.commands.*;
import dev.imabad.mceventsuite.spigot.modules.map.objects.Tree;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCModule;
import dev.imabad.mceventsuite.spigot.modules.teams.TeamModule;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.util.Vector;

public class MapModule extends Module implements Listener, IConfigProvider<MapConfig> {

    private List<EventBooth> booths = new ArrayList<>();
    private World mainWorld;
    private List<Location> spawnLocations = new ArrayList<>();
    private EditSession editSession;
    private final Random random = new Random();
    private KevinManager kevinManager;

    private MapConfig mapConfig;

    private List<Pair<UUID, Integer>> treeEditMode = new ArrayList<>();

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public void onEnable() {
        SimpleCommandMap commandMap = EventSpigot.getInstance().getCommandMap();
        commandMap.register("genmap", new GenMapCommand());
        commandMap.register("nbp", new NewBoothPlotCommand());
        commandMap.register("loadbooth", new LoadSchemCommand());
        commandMap.register("undobooth", new UndoBoothCommand());
        commandMap.register("sbs", new SetBoothPosCommand());
        commandMap.register("viptree", new TreeDebugCommand(this));
        commandMap.register("newtree", new NewTreePlotCommand(this));
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, this::onMysqlLoad);
        if(EventCore.getInstance().getModuleRegistry().isModuleEnabled(InfluxDBModule.class)) {
            EventSpigot.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(EventSpigot.getInstance(), () -> {
//                List<Point> dataPoints = new ArrayList<>();
//                for (Player p : EventSpigot.getInstance().getServer().getOnlinePlayers()) {
//                    Location l = p.getLocation();
//                    Point dataPoint = Point.measurement("playerLocations").addTag("server", EventCore.getInstance().getIdentifier())
//                            .addTag("world", "venue").addField("value", l.getX() + "," + l.getZ()).time(Instant.now().toEpochMilli(), WritePrecision.MS);
//                    dataPoints.add(dataPoint);
//                }
//                EventCore.getInstance().getModuleRegistry().getModule(InfluxDBModule.class).writePoints(dataPoints);
            }, 0, 5 * (60 * 20));
        }
        EventSpigot
            .getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
    }

    public List<Pair<UUID, Integer>> getTreeEditMode() {
        return this.treeEditMode;
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        booths = t.getMySQLDatabase().getDAO(BoothDAO.class).getBooths();
    }

    public void spawnTree(String name) {
        Tree tree = this.getVacantTree();
        if(tree == null) {
            Bukkit.getLogger().warning("no tree available for " + name);
            return;
        }

        Location location = tree.toLocation(mainWorld);
        location = location.add(0, 1, 0);
        int rotation = tree.getRotation();

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(mainWorld)).build();
        File schemFile = new File(EventSpigot.getInstance().getDataFolder() + File.separator
                + "trees" + File.separator +  "Plant" + (random.nextInt(2) + 1) + ".schem");

        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        try {
            try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                Clipboard clipboard = reader.read();
                AffineTransform transform = new AffineTransform();
                transform = transform.rotateY(rotation);
                ClipboardHolder holder = new ClipboardHolder(clipboard);
                holder.setTransform(holder.getTransform().combine(transform));
                Operation operation = holder
                        .createPaste(editSession)
                        .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
                editSession.close();
            }
        } catch(Exception e){
            e.printStackTrace();
            return;
        }

        Vector signVector = switch (rotation) {
            case 180 -> new Vector(2, -1, 0);
            case 90 -> new Vector(0, -1, 2); // ->
            case -90 -> new Vector(0, -1, -2); // -<
            default -> new Vector(-2, -1, 0);
        };
        final Location signLocation = location.add(signVector);
        if(signLocation.getBlock().getType().data == WallSign.class) {
            Sign sign = (Sign)signLocation.getBlock().getState();
            sign.line(0, Component.text("Planted by").color(NamedTextColor.GRAY));
            sign.line(2, Component.text(name).color(NamedTextColor.GRAY));
            sign.update();
        } else {
            Bukkit.broadcast(Component.text("TREE FOR "+ name + "SIGN COULD NOT BE DONE AT  " + location.toString()), "eventsuite.admin");
        }

        EventPlayer player = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase()
                .getDAO(PlayerDAO.class).getPlayer(name);
        if(player == null) {
            Bukkit.broadcast(Component.text("WARNING! player for" + name + "is null so tree flag cannot be set"), "eventsuite.admin");
            return;
        }
        player.setProperty("tree-planted-2022", true);
        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase()
                .getDAO(PlayerDAO.class).savePlayer(player);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent){
        if(worldLoadEvent.getWorld().getName().equalsIgnoreCase("world")){
            mainWorld = worldLoadEvent.getWorld();
            spawnLocations.add(new Location(mainWorld, 19.5, 19 ,-209.5, 90, 0));
            spawnLocations.add(new Location(mainWorld, 19.5, 19 ,-204.5, 90, 0));
            spawnLocations.add(new Location(mainWorld, 19.5, 19 ,-198.5, 90, 0));
            spawnLocations.add(new Location(mainWorld, -18.5, 19 ,-209.5, -90, 0));
            spawnLocations.add(new Location(mainWorld, -18.5, 19 ,-203.5, -90, 0));
            spawnLocations.add(new Location(mainWorld, -18.5, 19 ,-198.5, -90, 0));

            this.initKevins();
        }
    }

    public Tree getVacantTree() {
        for(Tree tree : this.mapConfig.getTrees()) {
            final Location location = tree.toLocation(this.mainWorld);
            if(location.add(0, -1, 0).getBlock().getType() == Material.GRASS_BLOCK) {
                continue;
            }
            return tree;
        }
        return null;
    }

    public void initKevins(){
        kevinManager = new KevinManager(mainWorld, mapConfig);
    }

    public List<EventBooth> getBooths() {
        return booths;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Location getRandomLocation(){
        return spawnLocations.get(random.nextInt(spawnLocations.size()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        for(Pair<UUID, Integer> pair : this.treeEditMode) {
            if(pair.getKey() == event.getPlayer().getUniqueId()) {
                Location location = event.getBlock().getLocation().add(0,1,0);
                for(Tree tree : this.mapConfig.getTrees()) {
                    if(tree.getX() == location.getX() && tree.getY() == location.getY() && tree.getZ() == location.getZ()) {
                        this.mapConfig.getTrees().remove(tree);
                        this.saveConfig();
                        event.getPlayer().sendMessage(ChatColor.RED + "Removed tree!");

                        event.setCancelled(true);
                        return;
                    }
                }

                int rotation = pair.getValue();

                this.getConfig().getTrees().add(new Tree(event.getBlock().getLocation().add(0,1,0).toVector(), rotation));
                this.saveConfig();

                event.getPlayer().sendMessage(ChatColor.GREEN + "Added tree position with rotation " + rotation + "!");
                event.setCancelled(true);
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        booths.clear();
        if(editSession != null){
            editSession.close();
        }
        kevinManager.byeKevins();
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.singletonList(NPCModule.class);
    }

    public Material getMaterialForSize(String size){
        switch(size.toLowerCase()){
            case "small":
                return Material.GREEN_CONCRETE_POWDER;
            case "medium":
                return Material.YELLOW_CONCRETE_POWDER;
            case "large":
                return Material.RED_CONCRETE_POWDER;
        }
        return Material.AIR;
    }

    public void undoBooth(){
        EditSession newEditSession = WorldEdit.getInstance().newEditSession(editSession.getWorld());
        editSession.undo(newEditSession);
        newEditSession.commit();
    }

    public boolean loadBooth(Player player, String name, int rotationI){
        editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(player.getWorld())).build();
        Optional<EventBooth> boothOptional = EventCore.getInstance().getModuleRegistry().getModule(MapModule.class).getBooths().stream().filter(eventBooth -> eventBooth.getName().toUpperCase().replace(' ', '_').equalsIgnoreCase(name)).findFirst();
        if(!boothOptional.isPresent()){
            player.sendMessage("Invalid booth.");
            return false;
        }
        EventBooth booth = boothOptional.get();
        Optional<EventBoothPlot> plotOptional = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlots().stream().filter(eventBoothPlot -> eventBoothPlot.blocksIsInBooth(player.getLocation().getBlockX(), player.getLocation().getBlockZ())).findFirst();
        if(!plotOptional.isPresent()){
            player.sendMessage("Please stand in a valid booth plot.");
            return false;
        }
        EventBoothPlot plot = plotOptional.get();
        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).saveBoothPlot(plot);
        plot.setBooth(booth);
        player.sendMessage("Loaded booth.");
        return true;
//        File schemFile = new File(EventSpigot.getInstance().getDataFolder() + File.separator + "booths" + File.separator + booth.getId());
//        if(!schemFile.exists()){
//            return false;
//        }
//        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
//        try {
//            try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
//                Clipboard clipboard = reader.read();
//                AffineTransform transform = new AffineTransform();
//                transform = transform.rotateY(-rotationI);
//                ClipboardHolder holder = new ClipboardHolder(clipboard);
//                holder.setTransform(holder.getTransform().combine(transform));
//                Operation operation = holder
//                        .createPaste(editSession)
//                        .to(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
//                        .ignoreAirBlocks(false)
//                        .build();
//                Operations.complete(operation);
//                editSession.close();
//                return true;
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//            player.sendMessage("Error loading");
//            return false;
//        }
    }

    public CompletableFuture<Boolean> generateMap(String worldName, int chunkRadiusX, int chunkRadiusZ, int cx, int cz){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        World world = Bukkit.getServer().getWorld(worldName);
        if(world == null){
            completableFuture.complete(false);
            return completableFuture;
        }
        List<Chunk> chunks = new ArrayList<>();
        int[][] pixels = new int[chunkRadiusX*16][chunkRadiusZ*16];
        for(int x = 0; x < chunkRadiusX; x++){
            for(int y = 0; y < chunkRadiusZ; y++){
                chunks.add(world.getChunkAt(cx + x, cz + y));
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(EventSpigot.getInstance(), () -> {
            List<EventBoothPlot> plots = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlots();
            for (Chunk chunk : chunks) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int wx = 16 * chunk.getX() + x;
                        int wz = 16 * chunk.getZ() + z;
                        int y = world.getHighestBlockAt(wx, wz).getY();
                        Optional<EventBoothPlot> plot = plots.stream().filter(eventBoothPlot -> eventBoothPlot.blockInBooth(wx, wz)).findFirst();
//                        IBlockData blockData;
//                        if(plot.isPresent()){
//                            EventBoothPlot plot1 = plot.get();
//                            Material material = getMaterialForSize(plot1.getBoothType());
//                            blockData = ((CraftBlockData) Bukkit.createBlockData(material)).getState();
//                        } else {
//                            blockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(wx, y, wz));
//                        }
//                        Block nmsBlock = blockData.getBlock();
//                        int px = 16 * (chunk.getX() - cx) + x;
//                        int pz = 16 * (chunk.getZ() - cz) + z;
//                        if (pixels[px] == null) {
//                            pixels[pz] = new int[chunkRadiusZ * 16];
//                        }
//                        pixels[px][pz] = nmsBlock.s().rgb;
                    }
                }
            }
            BufferedImage image = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < (chunkRadiusX * 16); x++) {
                for (int y = 0; y < (chunkRadiusZ * 16); y++) {
                    image.setRGB(x, y, pixels[x][y]);
                }
            }
            File ImageFile = new File(EventSpigot.getInstance().getDataFolder(), worldName + ".png");
            try {
                ImageIO.write(image, "png", ImageFile);
            } catch (IOException e) {
                e.printStackTrace();
                completableFuture.complete(false);
                return;
            }
            System.out.println("Saved image to file");
            completableFuture.complete(true);
        });
        return completableFuture;
    }

    @Override
    public Class<MapConfig> getConfigType() {
        return MapConfig.class;
    }

    @Override
    public MapConfig getConfig() {
        return mapConfig;
    }

    @Override
    public String getFileName() {
        return "map.json";
    }

    @Override
    public void loadConfig(MapConfig config) {
        this.mapConfig = config;
    }

    @Override
    public void saveConfig() {
        final String json = new Gson().toJson(this.mapConfig);
        try(FileWriter fw = new FileWriter(EventSpigot.getInstance().getDataFolder() + File.separator + this.getFileName())) {
            fw.write(json);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public boolean saveOnQuit() {
        return false;
    }
}
