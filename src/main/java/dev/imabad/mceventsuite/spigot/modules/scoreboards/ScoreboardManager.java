package dev.imabad.mceventsuite.spigot.modules.scoreboards;

import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager {
    private List<EventScoreboard> scoreboardList;

    public ScoreboardManager() {
        this.scoreboardList = new ArrayList<>();
    }

    public EventScoreboard createScoreboard(Scoreboard scoreboard, Component title) {
        final EventScoreboard eventScoreboard = new EventScoreboard(scoreboard, title);
        this.scoreboardList.add(eventScoreboard);
        return eventScoreboard;
    }

    public EventScoreboard getScoreboard(Scoreboard scoreboard) {
        for(EventScoreboard eventScoreboard : scoreboardList) {
            if(eventScoreboard.getScoreboard().equals(scoreboard))
                return eventScoreboard;
        }
        return null;
    }
}
