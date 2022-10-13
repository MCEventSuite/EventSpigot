package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.util.SpecialTag;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.bossbar.ui.BossBarUI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class BossBarModule extends Module implements IConfigProvider<BossBarConfig>, Listener {

    private BossBarConfig config;
    private org.bukkit.boss.BossBar bossBar;
    private Map<UUID, BossBarUI> directions;

    private Stage stage = Stage.STRINGS;
    private int current = 0;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.directions = new HashMap<>();
        /*if(!FloodgateApi.getInstance().isFloodgatePlayer(e.getPlayer().getUniqueId())) {
            this.directions.put(e.getPlayer().getUniqueId(), new BossBarUI(e.getPlayer(), (player) -> {
                Location destination = new Location(
                        Bukkit.getWorld("world"), 0.5, 30, 8.5, 0, 0);
                String name = this.determineDirection(player.getLocation(), destination);
                SpecialTag tag = SpecialTag.valueOf(name.toUpperCase());
                return Component.text(tag.getJavaString() + " Venue").font(Key.key(Key.MINECRAFT_NAMESPACE, "default"));
            }));
        }*/
        bossBar.addPlayer(e.getPlayer());
    }

    @Override
    public String getName() {
        return "bossbar";
    }

    private String translateRegion(String name) {
        return switch(name){
            case "mainstage" -> "Main Stage";
            case "communitystage" -> "Community Stage";
            case "koth" -> "KOTH";
            case "hideregion" -> "HnS";
            default -> name;
        };
    }

    private String determineDirection(Location from, Location to) {
        float angle = from.getDirection().angle(to.toVector());
        angle = (float) Math.toDegrees(angle);

        if(angle >= 330 || angle <= 30)
            return "NORTH";
        else if(angle >= 30 && angle <= 60)
            return "NORTHEAST";
        else if(angle >= 60 && angle <= 120)
            return "EAST";
        else if(angle >= 120 && angle <= 150)
            return "SOUTHEAST";
        else if(angle >= 150 && angle <= 210)
            return "SOUTH";
        else if(angle >= 210 && angle <= 240)
            return "SOUTHWEST";
        else if(angle >= 240 && angle <= 300)
            return "WEST";
        else
            return "NORTHWEST";
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
                                    + " &r&a&l" + this.getDivider() + " ON NOW " + this.getDivider() + " &r" + event.name
                    ));
                } else {
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                            "&6&lMEET & GREET &r&a&l" + this.getDivider() + " ON NOW " + this.getDivider() + " &r" + "/meet " + event.extraData));
                }
                bossBar.setProgress(Math.max(0.0D, Math.min(1.0D, event.getProgress())));
                bossBar.setColor(BarColor.GREEN);
            } else if(stage == Stage.COMING) {
                final BossBarConfig.Event event = soon.get(current);
                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                        "&" + event.location.color + "&l" + event.location.human.toUpperCase()
                                + " &r&6&l" + this.getDivider() + " UP NEXT " + this.getDivider() + " &r" + event.name
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

    private String getDivider() {
        return String.valueOf(Character.toChars(0x25AA)[0]);
    }

    public void addMeetGreet(String name, String displayName, long starts, long ends) {
        for(BossBarConfig.Event event : this.config.getEvents()) {
            if(event.location == BossBarConfig.Event.Location.MEET && event.extraData.equalsIgnoreCase(name))
                this.config.getEvents().remove(event);
        }

        this.config.getEvents().add(new BossBarConfig.Event(
                displayName, BossBarConfig.Event.Location.MEET, starts, ends, name
        ));
    }

    public void pauseGreet(String name) {
        for (BossBarConfig.Event event : this.config.getEvents()) {
            if (event.location == BossBarConfig.Event.Location.MEET && event.extraData.equalsIgnoreCase(name))
                event.paused = System.currentTimeMillis();
        }
    }

    public void resumeGreet(String name) {
        for (BossBarConfig.Event event : this.config.getEvents()) {
            if (event.location == BossBarConfig.Event.Location.MEET && event.extraData.equalsIgnoreCase(name))
                event.paused = 0;
        }
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.directions.containsKey(event.getPlayer().getUniqueId())) {
            this.directions.get(event.getPlayer().getUniqueId()).update();
        }
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
