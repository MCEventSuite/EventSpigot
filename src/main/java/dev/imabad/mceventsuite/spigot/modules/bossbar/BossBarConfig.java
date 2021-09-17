package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.BaseConfig;

import java.util.List;

public class BossBarConfig extends BaseConfig {

    private List<String> texts;

    @Override
    public String getName() {
        return "bossbar";
    }

    public List<String> getText() {
        return texts;
    }
}
