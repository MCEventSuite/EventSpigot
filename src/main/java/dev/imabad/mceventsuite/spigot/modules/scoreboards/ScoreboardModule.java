package dev.imabad.mceventsuite.spigot.modules.scoreboards;

import dev.imabad.mceventsuite.core.api.modules.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class ScoreboardModule extends Module implements Listener {
    private ScoreboardManager scoreboardManager;

    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public void onEnable() {
        this.scoreboardManager = new ScoreboardManager();
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }
}
