package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.BaseConfig;
import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.eventblocker.EventBlockListener;
import dev.imabad.mceventsuite.spigot.modules.eventblocker.EventBlockerConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;

public class BossBarModule extends Module implements IConfigProvider, Listener {

    private BossBarConfig config;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Component name = Component.text(ChatColor.translateAlternateColorCodes('&', config.getText()));
        BossBar fullBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);
        e.getPlayer().showBossBar(fullBar);
    }

    @Override
    public String getName() {
        return "bossbar";
    }

    @Override
    public void onEnable() {
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
    public void loadConfig(BaseConfig config) {
        this.config = (BossBarConfig)config;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean saveOnQuit() {
        return false;
    }
}
