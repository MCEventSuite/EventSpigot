package dev.imabad.mceventsuite.spigot.modules.flags;

import dev.imabad.mceventsuite.core.api.modules.Module;

import java.util.List;

public class FlagModule extends Module {

    @Override
    public String getName() {
        return "flags";
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }
}
