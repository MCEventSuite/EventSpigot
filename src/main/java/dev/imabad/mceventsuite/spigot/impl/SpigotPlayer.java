package dev.imabad.mceventsuite.spigot.impl;

import dev.imabad.mceventsuite.core.api.player.ILocation;
import dev.imabad.mceventsuite.core.api.player.IPlayer;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotPlayer implements IPlayer {

    private String username;
    private UUID uuid;
    private Player player;
    private boolean visible;

    public SpigotPlayer(Player player){
        this.username = player.getDisplayName();
        this.uuid = player.getUniqueId();
        this.player = player;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public ILocation getLocation() {
        return new SpigotLocation(this.player.getLocation());
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }

    @Override
    public void kick(String reason) {
        this.player.kickPlayer(reason);
    }

    @Override
    public void teleport(ILocation location) {
        this.player.teleport(SpigotLocation.toSpigotLocation(location));
    }

    /** Deprecated non-existent **/
    @Deprecated
    @Override
    public void changeServer(String server) {

    }

    @Override
    public void toggleFlight() {
        this.player.setAllowFlight(!this.player.getAllowFlight());
    }

    @Override
    public void setFlightEnabled(boolean enabled) {
        this.player.setAllowFlight(enabled);
    }

    @Override
    public boolean isFlightEnabled() {
        return this.player.getAllowFlight();
    }

    @Override
    public void setVisible(boolean visible) {
        if(visible){
            for(Player player : Bukkit.getOnlinePlayers()){
                player.showPlayer(EventSpigot.getInstance(), this.player);
            }
        } else {
            for(Player player : Bukkit.getOnlinePlayers()){
                player.hidePlayer(EventSpigot.getInstance(), this.player);
            }
        }
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}
