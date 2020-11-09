package dev.imabad.mceventsuite.spigot.modules.eventpass;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventPassSpigotModule extends Module {
    @Override
    public String getName() {
        return "eventpassspigot";
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.singletonList(EventPassModule.class);
    }
}
