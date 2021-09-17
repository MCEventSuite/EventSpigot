package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.BaseConfig;

public class BossBarConfig extends BaseConfig {

    private String text;

    @Override
    public String getName() {
        return "bossbar";
    }

    public String getText() {
        return text;
    }
}
