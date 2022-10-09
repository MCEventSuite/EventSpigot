package dev.imabad.mceventsuite.spigot.modules.chat;

import dev.imabad.mceventsuite.core.api.BaseConfig;

public class ChatFilterConfig extends BaseConfig {
    private String regionUrl;
    private String apiKey;

    @Override
    public String getName() {
        return "filter";
    }

    public String getRegionUrl() {
        return this.regionUrl;
    }

    public String getApiKey() {
        return this.apiKey;
    }
}
