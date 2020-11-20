package dev.imabad.mceventsuite.spigot.modules.bedrock;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Collections;
import java.util.List;

public class BedrockModule extends Module implements Listener {

    private static StateFlag bedrockAllowed;

    public static StateFlag getBedrockAllowed() {
        return bedrockAllowed;
    }

    @Override
    public String getName() {
        return "bedrock";
    }

    @Override
    public void onEnable() {
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
        try {
            bedrockAllowed = RegionUtils.getOrRegisterFlag(new StateFlag("is-bedrock-allowed", true));
        } catch(Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("WorldGuard")) {
            WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(BedrockFlagHandler.FACTORY, null);
        }
    }


    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }
}
