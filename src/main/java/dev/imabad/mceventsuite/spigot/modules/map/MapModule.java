package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventBoothPlot;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.commands.GenMapCommand;
import dev.imabad.mceventsuite.spigot.modules.booths.commands.NewBoothPlotCommand;
import net.minecraft.server.v1_16_R2.Block;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.IBlockData;
import org.bukkit.*;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.block.data.CraftBlockData;
import org.bukkit.event.Listener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MapModule extends Module implements Listener {
    @Override
    public String getName() {
        return "map";
    }

    @Override
    public void onEnable() {
        SimpleCommandMap commandMap = EventSpigot.getInstance().getCommandMap();
        commandMap.register("genmap", new GenMapCommand());
        commandMap.register("nbp", new NewBoothPlotCommand());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
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
                        IBlockData blockData;
                        if(plot.isPresent()){
                            EventBoothPlot plot1 = plot.get();
                            Material material = getMaterialForSize(plot1.getBoothType());
                            blockData = ((CraftBlockData) Bukkit.createBlockData(material)).getState();
                        } else {
                            blockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(wx, y, wz));
                        }
                        Block nmsBlock = blockData.getBlock();
                        int px = 16 * (chunk.getX() - cx) + x;
                        int pz = 16 * (chunk.getZ() - cz) + z;
                        if (pixels[px] == null) {
                            pixels[pz] = new int[chunkRadiusZ * 16];
                        }
                        pixels[px][pz] = nmsBlock.s().rgb;
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
}
