package dev.imabad.mceventsuite.spigot.modules.eventpass;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;

import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassDAO;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassReward;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EventPassSpigotModule extends Module {

    private List<EventPassReward> rewards;

    @Override
    public String getName() {
        return "eventpassspigot";
    }

    @Override
    public void onEnable() {
        EventSpigot.getInstance().getCommandMap().register("xpc", new XPCannonCommand());
        EventSpigot.getInstance().getCommandMap().register("kothxp", new KingOfTheHillCommand());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, (event) -> {
            rewards = event.getMySQLDatabase().getDAO(EventPassDAO.class).getAllRewards();
        });
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.singletonList(EventPassModule.class);
    }

    public List<EventPassReward> getRewards() {
        return rewards;
    }

    public Optional<EventPassReward> getRewardForLevel(int level){
        return rewards.stream().filter(eventPassReward -> eventPassReward.getYear() == EventCore.getInstance().getConfig().getCurrentYearAsInt() && eventPassReward.getRequiredLevel() == level).findFirst();
    }
}
