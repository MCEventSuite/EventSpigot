package dev.imabad.mceventsuite.spigot.modules.eventpass;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;

import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassDAO;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassReward;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassUnlockedReward;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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

    public void awardVipPlus(EventPlayer eventPlayer) {
        EventPassModule eventPassModule = EventCore.getInstance().getModuleRegistry().getModule(EventPassModule.class);
        EventPassDAO eventPassDAO = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase()
                .getDAO(EventPassDAO.class);

        EventPassPlayer eventPassPlayer = eventPassDAO.getOrCreateEventPass(eventPlayer);
        if (eventPassPlayer.levelFromXP() >= 50) {
            return;
        }

        eventPassPlayer.setXp(EventPassModule.experience(50));
        eventPassDAO.saveEventPassPlayer(eventPassPlayer);

        List<EventPassReward> unlocks = eventPassDAO.getUnlockedRewards(eventPlayer).stream().map(EventPassUnlockedReward::getUnlockedReward).toList();
        Set<EventPassUnlockedReward> toUnlock = new HashSet<>();
        for (EventPassReward reward : eventPassModule.getEventPassRewards()) {
            if (!unlocks.contains(reward)) {
                EventPassUnlockedReward unlockedReward = new EventPassUnlockedReward(reward, eventPlayer);
                toUnlock.add(unlockedReward);
            }
        }
        if (toUnlock.size() > 0)
            eventPassDAO.saveUnlockedRewardBatch(toUnlock);

        Player player = Bukkit.getPlayer(eventPlayer.getUUID());
        if(player != null) {
            player.sendMessage(Component.text("Thank you for your generosity! You have been promoted to Level 50 and all cosmetics have been unlocked.")
                    .color(NamedTextColor.GREEN));
        }
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
        return rewards.stream().filter(eventPassReward -> eventPassReward.getRequiredLevel() == level &&
                eventPassReward.getYear() == EventCore.getInstance().getConfig().getCurrentYearAsInt()).findFirst();
    }
}
