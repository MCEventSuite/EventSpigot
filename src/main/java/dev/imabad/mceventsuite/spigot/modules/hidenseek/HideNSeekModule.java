package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import dev.imabad.mceventsuite.core.api.modules.Module;

import java.util.Collections;
import java.util.List;

public class HideNSeekModule extends Module {

    @Override
    public String getName() {
        return "hidenseek";
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
