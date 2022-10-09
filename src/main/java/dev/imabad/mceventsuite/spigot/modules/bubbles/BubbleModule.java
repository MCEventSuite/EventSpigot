package dev.imabad.mceventsuite.spigot.modules.bubbles;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;

import java.util.List;

public class BubbleModule extends Module {

    private BubbleManager bubbleManager;

    @Override
    public String getName() {
        return "bubbles";
    }

    @Override
    public void onEnable() {
        this.bubbleManager = new BubbleManager();
        Bukkit.getPluginManager().registerEvents(new BubbleListener(this.bubbleManager), EventSpigot.getInstance());
        Bukkit.getCommandMap().register("cb", new BubbleCommand(this.bubbleManager));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }

    public BubbleManager getBubbleManager() {
        return this.bubbleManager;
    }
}
