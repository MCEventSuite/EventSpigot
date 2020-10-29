package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.minecraft.server.v1_16_R2.Block;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.IBlockData;
import net.minecraft.server.v1_16_R2.MaterialMapColor;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.event.Listener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MapModule extends Module implements Listener {
    @Override
    public String getName() {
        return "map";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
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
            for (Chunk chunk : chunks) {
                int[][] chunkPixels = new int[16][16];
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int wx = 16 * chunk.getX() + x;
                        int wz = 16 * chunk.getZ() + z;
                        int y = world.getHighestBlockAt(wx, wz).getY();
                        IBlockData blockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(wx, y, wz));
                        Block nmsBlock = blockData.getBlock();
                        int px = 16 * (chunk.getX() - cx) + x;
                        int pz = 16 * (chunk.getZ() - cz) + z;
                        if (chunkPixels[x] == null) {
                            chunkPixels[z] = new int[chunkRadiusZ * 16];
                        }
                        chunkPixels[x][z] = nmsBlock.s().rgb;
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
