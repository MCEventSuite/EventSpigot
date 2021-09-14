package dev.imabad.mceventsuite.spigot.modules.eventblocker;

import dev.imabad.mceventsuite.core.api.BaseConfig;

import java.util.ArrayList;
import java.util.List;

public class EventBlockerConfig extends BaseConfig {

    private List<String> blockedEvents = new ArrayList<>();

    @Override
    public String getName() {
        return "blocked_events";
    }

    public List<String> getBlockedEvents() {
        return blockedEvents;
    }

}
