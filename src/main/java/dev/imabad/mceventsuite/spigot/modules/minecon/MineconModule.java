package dev.imabad.mceventsuite.spigot.modules.minecon;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideNSeekListener;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideSeekCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.List;

public class MineconModule extends Module implements Listener {

    private World world;
    private Location spawn;
    private final String genSettings = "{\"lakes\":false,\"features\":false,\"layers\":[{\"block\":\"minecraft:air\",\"height\":1}],\"structures\":{\"structures\":{}}}";

    @Override
    public String getName() {
        return "minecon";
    }

    @Override
    public void onEnable() {
        EventSpigot.getInstance().getCommandMap().register("mse", new MineconCommand(this));
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());

        EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () -> {
            EventSpigot.getInstance().getServer()
                    .createWorld(WorldCreator.name("minecon").environment(World.Environment.NORMAL)
                            .type(WorldType.FLAT).generatorSettings(genSettings).generateStructures(false));
        }, 20L * 10);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if(event.getWorld().getName().equalsIgnoreCase("minecon")) {
            this.world = event.getWorld();
            this.spawn = new Location(this.world, 5, 87, 9);
        }
    }

    public World getWorld() {
        return this.world;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }
}
