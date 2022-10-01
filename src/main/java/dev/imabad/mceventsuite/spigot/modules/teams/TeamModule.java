package dev.imabad.mceventsuite.spigot.modules.teams;

import dev.imabad.mceventsuite.core.api.modules.Module;

import java.util.List;

public class TeamModule extends Module {

    private TeamManager teamManager;

    @Override
    public String getName() {
        return "teams";
    }

    @Override
    public void onEnable() {
        this.teamManager = new TeamManager();
    }

    @Override
    public void onDisable() {

    }

    public TeamManager getTeamManager() {
        return this.teamManager;
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }
}
