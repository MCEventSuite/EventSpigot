package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.BaseConfig;
import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BossBarModule extends Module implements IConfigProvider<BossBarConfig>, Listener {

    private BossBarConfig config;
    private org.bukkit.boss.BossBar bossBar;

    private Stage stage = Stage.STRINGS;
    private int current = 0;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        bossBar.addPlayer(e.getPlayer());
    }

    @Override
    public String getName() {
        return "bossbar";
    }

    @Override
    public void onEnable() {
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', config.getText().get(current)), BarColor.BLUE, BarStyle.SEGMENTED_20);
        EventSpigot.getInstance().getServer().getScheduler().runTaskTimer(EventSpigot.getInstance(), () -> {
            final List<BossBarConfig.Event> currentEvents = config.getCurrentEvents();
            final List<BossBarConfig.Event> soon = config.getSoonEvents();
            final List<String> strings = config.getText();

            if(stage == Stage.CURRENT) {
                final BossBarConfig.Event event = soon.size() != 0 ? currentEvents.get(current) : new BossBarConfig.Event("No event configured!", BossBarConfig.Event.Location.NONE, System.currentTimeMillis() - 1000, System.currentTimeMillis() + 20000);
                bossBar.setTitle(event.name + " is currently on at the " + event.location);
                bossBar.setProgress(event.getProgress());
                bossBar.setColor(BarColor.GREEN);
            } else if(stage == Stage.COMING) {
                final BossBarConfig.Event event = soon.size() != 0 ? soon.get(current) : new BossBarConfig.Event("No event configured!", BossBarConfig.Event.Location.NONE, System.currentTimeMillis()-20000, System.currentTimeMillis() + 20000);
                bossBar.setTitle(event.name + " will soon be starting at the " + event.location);
                bossBar.setProgress(event.getProgress());
                bossBar.setColor(BarColor.YELLOW);
            } else {
                final String text = strings.get(current);
                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', text));
                bossBar.setProgress(100);
                bossBar.setColor(BarColor.BLUE);
            }
        }, 0, 5);

        EventSpigot.getInstance().getServer().getScheduler().runTaskTimer(EventSpigot.getInstance(), () -> {
            final List<BossBarConfig.Event> currentEvents = config.getCurrentEvents();
            final List<BossBarConfig.Event> soon = config.getSoonEvents();
            final List<String> strings = config.getText();
            int max = 0;

            if(stage == Stage.CURRENT)
                max = currentEvents.size();
            else if(stage == Stage.COMING)
                max = soon.size();
            else if(stage == Stage.STRINGS)
                max = strings.size();

            if(current + 1 >= max) {
                stage = Stage.values().length >= stage.ordinal() + 1 ? Stage.CURRENT : Stage.values()[stage.ordinal() + 1];
                current = 0;
            } else {
                current++;
            }
        }, 0, 20 * 5);
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Class<BossBarConfig> getConfigType() {
        return BossBarConfig.class;
    }

    @Override
    public BossBarConfig getConfig() {
        return this.config;
    }

    @Override
    public String getFileName() {
        return "boss_bar.json";
    }

    @Override
    public void loadConfig(BossBarConfig config) {
        this.config = config;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean saveOnQuit() {
        return false;
    }

    public enum Stage {
        CURRENT, COMING, STRINGS;
    }
}
