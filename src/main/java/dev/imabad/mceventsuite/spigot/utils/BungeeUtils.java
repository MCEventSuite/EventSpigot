package dev.imabad.mceventsuite.spigot.utils;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.ChangePlayerServerMessage;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BungeeUtils {

    public static void saveLocationSynchronously(Player player) {
        RedisModule redis = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
        redis.storeData("location-" + player.getUniqueId(), LocationHelper.serializeLocation(player.getLocation()));
    }

    public static void saveServer(Player player){
        RedisModule redis = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
            redis.storeData("server-" + player.getUniqueId(), EventCore.getInstance().getIdentifier());
    }

    public static void sendBack(Player player) {
        RedisModule redis = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
        String server = redis.getData("server-" + player.getUniqueId());
        if(server != null) {
            redis.storeData("location-" + player.getUniqueId(), LocationHelper.serializeLocation(player.getLocation()));
            redis.publishMessage(RedisChannel.GLOBAL, new ChangePlayerServerMessage(player.getUniqueId(), server));
        }
    }

    public static void sendToServer(Player player, String server) {
        Bukkit.getScheduler().runTaskAsynchronously(EventSpigot.getInstance(), () -> {
            BungeeUtils.saveLocationSynchronously(player);
            RedisModule redis = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
            redis.publishMessage(RedisChannel.GLOBAL, new ChangePlayerServerMessage(player.getUniqueId(), server));
        });
    }

    public static Optional<Location> getLastLocation(Player player) {
        RedisModule redis = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
        String serializedLocation = redis.getData("location-" + player.getUniqueId());
        return Optional.ofNullable(serializedLocation)
                .map((location) -> LocationHelper.deserializeLocation(serializedLocation));
    }

}
