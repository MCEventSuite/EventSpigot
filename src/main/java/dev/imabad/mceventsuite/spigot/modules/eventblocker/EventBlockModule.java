package dev.imabad.mceventsuite.spigot.modules.eventblocker;

import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

public class EventBlockModule extends Module implements IConfigProvider<EventBlockerConfig> {

    private EventBlockerConfig config;

    private RegisteredListener registeredListener = new RegisteredListener(new EventBlockListener(), (listener, event) -> {
        try {
            //All events
            listener.getClass().getDeclaredMethod("onEvent", Event.class).invoke(listener, event);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }, EventPriority.HIGH, EventSpigot.getInstance(), false);

    @Override
    public String getName() {
        return "eventblock";
    }

    @Override
    public void onEnable() {
        //Registers the listener that we just created
        for(HandlerList handler : HandlerList.getHandlerLists())
            handler.register(registeredListener);

    }

    @Override
    public void onDisable() {
        for(HandlerList handler : HandlerList.getHandlerLists())
            handler.unregister(registeredListener);
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Class<EventBlockerConfig> getConfigType() {
        return EventBlockerConfig.class;
    }

    @Override
    public EventBlockerConfig getConfig() {
        return this.config;
    }

    @Override
    public String getFileName() {
        return "blocked_events.json";
    }

    @Override
    public void loadConfig(EventBlockerConfig config) {
        this.config = config;
        ((EventBlockListener) this.registeredListener.getListener()).setConfig(config);
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean saveOnQuit() {
        return false;
    }

}
