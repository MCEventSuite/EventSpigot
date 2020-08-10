package dev.imabad.mceventsuite.spigot;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;

import java.util.Arrays;
import java.util.List;

public class EventSpigotModule extends Module {
    @Override
    public String getName() {
        return "spigot";
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Arrays.asList(RedisModule.class, MySQLModule.class);
    }
}
