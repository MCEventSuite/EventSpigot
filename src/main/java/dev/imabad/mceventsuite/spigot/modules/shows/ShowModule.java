package dev.imabad.mceventsuite.spigot.modules.shows;

import dev.imabad.mceventsuite.core.api.modules.Module;

import java.util.Collections;
import java.util.List;

public class ShowModule extends Module {
    @Override
    public String getName() {
        return "shows";
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }
}
