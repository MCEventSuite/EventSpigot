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
import org.bukkit.entity.Boss;
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
        if(config.getText().size() == 0) {
            throw new RuntimeException("No strings set for bossbar!");
        }
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', config.getText().get(current)), BarColor.BLUE, BarStyle.SEGMENTED_20);
        EventSpigot.getInstance().getServer().getScheduler().runTaskTimer(EventSpigot.getInstance(), () -> {
            final List<BossBarConfig.Event> currentEvents = config.getCurrentEvents();
            final List<BossBarConfig.Event> soon = config.getSoonEvents();
            final List<String> strings = config.getText();

            if(stage.getSize(this) == 0) {
                stage = stage.ordinal() + 1 >= Stage.values ().length ? Stage.CURRENT : Stage.values()[stage.ordinal() + 1];
                return;
            }

            if(stage == Stage.CURRENT) {
                final BossBarConfig.Event event = currentEvents.get(current);
                if(event.location != BossBarConfig.Event.Location.MEET) {
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                            "&" + event.location.color + "&l" + event.location.human.toUpperCase()
                                    + " &r&a&l▪ ON NOW ▪ &r" + event.name
                    ));
                } else {
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                            "&6&lMEET & GREET &r&a&l▪ ON NOW ▪ &r" + "/meet " + event.extraData));
                }
                bossBar.setProgress(Math.max(0.0D, Math.min(1.0D, event.getProgress())));
                bossBar.setColor(BarColor.GREEN);
            } else if(stage == Stage.COMING) {
                final BossBarConfig.Event event = soon.get(current);
                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                        "&" + event.location.color + "&l" + event.location.human.toUpperCase()
                                + " &r&6&l▪ UP NEXT ▪ &r" + event.name
                ));
                bossBar.setProgress(Math.max(0.0D, Math.min(1.0D, event.getProgress())));
                bossBar.setColor(BarColor.YELLOW);
            } else {
                final String text = strings.get(current);
                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', text));
                bossBar.setProgress(1.0);
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
                stage = stage.ordinal() + 1 >= Stage.values().length ? Stage.CURRENT : Stage.values()[stage.ordinal() + 1];
                current = 0;
            } else {
                current++;
            }
        }, 0, 20 * 5);
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
    }

    public void addMeetGreet(String name, String displayName, long ends) {
        this.config.getEvents().add(new BossBarConfig.Event(
                displayName, BossBarConfig.Event.Location.MEET, System.currentTimeMillis(), ends, name
        ));
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

        public int getSize(BossBarModule module) {
            if(this == Stage.STRINGS)
                return module.getConfig().getText().size();
            if(this == Stage.COMING)
                return module.getConfig().getSoonEvents().size();
            return module.getConfig().getCurrentEvents().size();
        }
    }
}
