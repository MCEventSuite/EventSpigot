package dev.imabad.mceventsuite.spigot;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.join.JoinModule;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisMessageListener;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.events.RedisConnectionEvent;
import dev.imabad.mceventsuite.spigot.impl.SpigotActionExecutor;
import dev.imabad.mceventsuite.spigot.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class EventSpigot extends JavaPlugin {

    private static EventSpigot eventSpigot;

    public static EventSpigot getInstance(){
        return eventSpigot;
    }

    @Override
    public void onEnable() {
        new EventCore(getDataFolder());
        EventCore.getInstance().getEventRegistry().registerListener(RedisConnectionEvent.class, redisConnectionEvent -> {
            System.out.println("[EventSpigot] Connected to Redis");
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(TestRedisMessage.class, new RedisMessageListener<>((msg) -> {
                System.out.println("[EventSpigot] Received Hello " + msg.getHello());
            }));
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new TestRedisMessage());
        });
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventSpigotModule());
        EventCore.getInstance().setActionExecutor(new SpigotActionExecutor());
        eventSpigot = this;
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new JoinModule());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        EventCore.getInstance().shutdown();
    }
}
