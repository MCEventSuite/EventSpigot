package dev.imabad.mceventsuite.spigot.impl;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.actions.Action;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.api.player.ILocation;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.Transient;
import java.util.UUID;

public class SpigotPlayer extends EventPlayer {

    public static SpigotPlayer asSpigot(EventPlayer player, Player bukkitPlayer){
        return new SpigotPlayer(player, bukkitPlayer);
    }

    @Transient
    private Player player;
    private boolean visible;

    public SpigotPlayer(Player player){
        super(player.getUniqueId(), player.getDisplayName());
        this.player = player;
    }

    protected SpigotPlayer(EventPlayer eventPlayer, Player player){
        super(eventPlayer.getUUID(), eventPlayer.getLastUsername());
        this.setRank(eventPlayer.getRank());
        this.setPermissions(eventPlayer.getPermissions());
        this.setProperties(eventPlayer.getProperties());
        this.player = player;
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }

    @Override
    public void executeAction(Action action) {
        EventCore.getInstance().getActionExecutor().execute(action, this);
    }
}
