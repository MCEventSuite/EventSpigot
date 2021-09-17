package dev.imabad.mceventsuite.spigot.modules.daylight;

import dev.imabad.mceventsuite.core.EventCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;

public class DaylightModule extends Module implements Listener {

    private static final int UTC_AWAY = (5 * (1000 * (60 * 60)));

    Map<UUID, TimeType> playerTime = new LinkedHashMap<>();

    //Runs every so often
    BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            EventSpigot.getInstance().getServer().getOnlinePlayers().forEach(player -> setTime(player));
        }
    };

    public DaylightModule() {

    }
    /**
     * Runs whenever a player joins
     * @param e Event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        EventCore.getInstance().getEventPlayerManager().getPlayer(e.getPlayer().getUniqueId()).ifPresent(player -> {
            String property = Optional.ofNullable(player.getStringProperty("time")).orElse(TimeType.UTC.name());
            TimeType timeType = TimeType.valueOf(property);
            playerTime.put(e.getPlayer().getUniqueId(), timeType);
        });
        setTime(e.getPlayer());
    }


    /**
     * Sets the player's time (either local or global, depending on localTime variable)
     * @param player
     */
    private void setTime(Player player) {
        //a few utility variables
        boolean changeable = false;
        long serverTime = 0;
        String time = "0";

        //Checks if we're using local times
        if (!playerTime.containsKey(player.getUniqueId())) {
            playerTime.put(player.getUniqueId(), TimeType.UTC);
        }
        if (!playerTime.get(player.getUniqueId()).equals(TimeType.MINECRAFT)) {
            if (playerTime.get(player.getUniqueId()).equals(TimeType.LOCAL)) {
                changeable = true;
                try {
                    time = IPUtils.ipToTime(Objects.requireNonNull(player.getAddress()).getHostString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    time = "Error: MalformedURLException";
                }
                //If IPUtils has any issues, notify console, and use global time.
                if (time.startsWith("Error")) {
                    EventSpigot.getInstance().getLogger().log(Level.WARNING, "[DaylightModule] " + time + " for " + player.getAddress().getHostString());
                    playerTime.put(player.getUniqueId(), TimeType.UTC);
                }
            }
            //If global times are enabled, get the time
            if (playerTime.get(player.getUniqueId()).equals(TimeType.UTC)) {
                changeable = true;
                time = String.valueOf(System.currentTimeMillis() - UTC_AWAY);
            }
            //Convert real life timestamp to ticks and set user's time
            if (changeable)
                serverTime = 18000 + (Long.parseLong(time) / (60 * 60));

            else {
                if (playerTime.get(player.getUniqueId()).equals(TimeType.ALWAYS_DAY)) serverTime = 6000;
                else if (playerTime.get(player.getUniqueId()).equals(TimeType.ALWAYS_NIGHT)) serverTime = 18000;
            }
            player.setPlayerTime(serverTime, false);
        }
        else player.resetPlayerTime();
    }

    /**
     * Gets the name of the module
     * @return
     */
    @Override
    public String getName() {
        return "Daylight";
    }

    /**
     * Runs when the module is enabled
     */
    @Override
    public void onEnable() {
        runnable.runTaskTimer(EventSpigot.getInstance(), 0, 20 * 60);
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(this, EventSpigot.getInstance());
        EventSpigot.getInstance().getCommandMap().register("daylight", new DaylightCommand());
    }

    /**
     * Disables the module
     */
    @Override
    public void onDisable() {
        runnable.cancel();

        //Reset player's time to default minecraft
        EventSpigot.getInstance().getServer().getOnlinePlayers().forEach(Player::resetPlayerTime);
        for (OfflinePlayer offlinePlayer : EventSpigot.getInstance().getServer().getOfflinePlayers()) Objects.requireNonNull(offlinePlayer.getPlayer()).resetPlayerTime();

    }

    /**
     * List of dependencies for this module
     * @return
     */
    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    public void setPlayerTime(Player player, TimeType type) {
        playerTime.put(player.getUniqueId(), type);
        EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresent(player1 -> {
            player1.setProperty("time", type.name());
        });
        setTime(player);
    }
}
