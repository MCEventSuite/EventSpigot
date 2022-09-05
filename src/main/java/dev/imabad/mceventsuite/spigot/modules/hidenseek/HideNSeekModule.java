package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import com.github.puregero.multilib.MultiLib;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HideNSeekModule extends Module {

    private HideNSeekGame currentGame;

    public HideNSeekModule() {
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new HideNSeekListener(), EventSpigot.getInstance());
        MultiLib.onString(EventSpigot.getInstance(), "eventspigot:hns", (data) -> {
            if(data.equalsIgnoreCase("end")) {
                this.currentGame.end(true);
                return;
            } else if(data.equalsIgnoreCase("start")) {
                this.currentGame.start(true);
                return;
            }

            final String[] parts = data.split(":");
            if (parts[0].equalsIgnoreCase("init")) {
                this.currentGame = new HideNSeekGame(UUID.fromString(parts[1]));
                return;
            }

            if(this.currentGame != null && this.currentGame.getStatus() != HideNSeekGame.GameStatus.ENDED) {
                if(parts[0].equalsIgnoreCase("addseeker")) {
                    this.currentGame.addSeeker(UUID.fromString(parts[1]));
                } else if(parts[0].equalsIgnoreCase("rmseeker")) {
                    this.currentGame.leaveSeeker(UUID.fromString(parts[1]));
                } else if(parts[0].equalsIgnoreCase("join")) {
                    if(this.currentGame.getStatus() == HideNSeekGame.GameStatus.WAITING) {
                        this.currentGame.join(UUID.fromString(parts[1]));
                    }
                } else if(parts[0].equalsIgnoreCase("leave")) {
                    this.currentGame.leave(UUID.fromString(parts[1]));
                } else if(parts[0].equalsIgnoreCase("caught")) {
                    this.currentGame.convertToSeeker(UUID.fromString(parts[1]), parts[2]);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "hidenseek";
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public HideNSeekGame getGame() {
        return this.currentGame;
    }

    public boolean startGame(HideNSeekGame game) {
        if (currentGame == null || currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED) {
            this.currentGame = game;
            MultiLib.notify("eventspigot:hns", "init:" + game.getStarter());

            Component component = Component.text("----------------------------").color(NamedTextColor.BLUE)
                    .append(Component.text("\n\nHIDE & SEEK").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text("A game of ").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("Hide & Seek").color(NamedTextColor.YELLOW))
                    .append(Component.text(" has been started!").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("\nClick here").color(NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.runCommand("/hns join"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to join!").color(NamedTextColor.GREEN))))
                    .append(Component.text(" to join the game.").color(NamedTextColor.AQUA))
                    .append(Component.text("\n\n----------------------------").color(NamedTextColor.BLUE));

            Bukkit.broadcast(component);
            return true;
        }
        return false;
    }

    public void joinAsHider(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() != HideNSeekGame.GameStatus.WAITING)
            return;
        this.getGame().join(player.getUniqueId());
        MultiLib.notify("eventspigot:hns", "join:" + player.getUniqueId());
    }

    public void joinAsSeeker(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED)
            return;
        this.getGame().addSeeker(player.getUniqueId());
        MultiLib.notify("eventspigot:hns", "addseeker:" + player.getUniqueId());
    }

    public void leave(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED)
            return;
        this.getGame().leave(player.getUniqueId());
        MultiLib.notify("eventspigot:hns", "leave:" + player.getUniqueId());
    }

    public void leaveSeeker(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED)
            return;
        this.getGame().leave(player.getUniqueId());
        MultiLib.notify("eventspigot:hns", "rmseeker:" + player.getUniqueId());
    }

    public void catchHider(Player hider, Player seeker) {
        if(this.currentGame == null || this.currentGame.getStatus() != HideNSeekGame.GameStatus.STARTED)
            return;
        MultiLib.notify("eventspigot:hns", "caught:" + hider.getUniqueId() + ":" + seeker.getName());
        this.getGame().convertToSeeker(hider.getUniqueId(), seeker.getName());
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }
}
