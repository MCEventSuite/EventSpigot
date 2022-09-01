package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.BaseConfig;
import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.List;

public class BossBarModule extends Module implements IConfigProvider<BossBarConfig>, Listener {

    private BossBarConfig config;
    private org.bukkit.boss.BossBar bossBar;
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
            if(current + 1 > (config.getText().size() - 1)){
                current = 0;
            } else {
                current++;
            }
            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', config.getText().get(current)));
        }, 10 * 20, 10 * 20);
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
}
