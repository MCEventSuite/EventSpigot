package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import com.github.puregero.multilib.MultiLib;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.player.PlayerHotbar;
import dev.imabad.mceventsuite.spigot.modules.scoreboards.EventScoreboard;
import dev.imabad.mceventsuite.spigot.modules.scoreboards.Row;
import dev.imabad.mceventsuite.spigot.modules.scoreboards.ScoreboardModule;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HideNSeekModule extends Module {

    private HideNSeekGame currentGame;
    private Scoreboard scoreboard;
    private EventScoreboard eventScoreboard;

    public HideNSeekModule() {
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new HideNSeekListener(this), EventSpigot.getInstance());
        EventSpigot.getInstance().getCommandMap().register("hns", new HideSeekCommand(this));
        MultiLib.onString(EventSpigot.getInstance(), "eventspigot:hns", (data) -> {
            if(data.equalsIgnoreCase("end")) {
                this.currentGame.end(true);
                return;
            } else if(data.equalsIgnoreCase("start")) {
                this.currentGame.start(true);
                return;
            } else if(data.equalsIgnoreCase("init")) {
                this.currentGame = new HideNSeekGame(this.eventScoreboard);
                return;
            } else if(data.equalsIgnoreCase("wait")) {
                this.currentGame.startWait(true);
                return;
            }

            final String[] parts = data.split(":");

            if(this.currentGame != null && this.currentGame.getStatus() != HideNSeekGame.GameStatus.ENDED) {
                if(parts[0].equalsIgnoreCase("addseeker")) {
                    this.currentGame.addSeeker(UUID.fromString(parts[1]));
                } else if(parts[0].equals("time")) {
                    int time = Integer.parseInt(parts[1]);
                    this.currentGame.updateTimer(time);
                } else if(parts[0].equalsIgnoreCase("rmseeker")) {
                    this.currentGame.leaveSeeker(UUID.fromString(parts[1]));
                } else if(parts[0].equalsIgnoreCase("join")) {
                    if(this.currentGame.getStatus() == HideNSeekGame.GameStatus.WAITING ||
                            this.currentGame.getStatus() == HideNSeekGame.GameStatus.JOINING) {
                        this.currentGame.join(UUID.fromString(parts[1]));
                    }
                } else if(parts[0].equalsIgnoreCase("leave")) {
                    this.currentGame.leave(UUID.fromString(parts[1]));
                } else if(parts[0].equalsIgnoreCase("silentset")) {
                    String side = parts[1];
                    UUID uuid = UUID.fromString(parts[2]);
                    this.currentGame.getHiders().remove(uuid);
                    this.currentGame.getSeekers().remove(uuid);

                    if(side.equalsIgnoreCase("hiders"))
                        this.currentGame.getHiders().add(uuid);
                    else
                        this.currentGame.getSeekers().add(uuid);
                } else if(parts[0].equalsIgnoreCase("caught")) {
                    this.currentGame.convertToSeeker(UUID.fromString(parts[1]), parts[2]);
                }
            }
        });
    }

    public EventScoreboard getEventScoreboard() {
        return this.eventScoreboard;
    }

    public void onWorldLoad(WorldLoadEvent worldLoadEvent) {
        this.scoreboard = EventSpigot.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        final Team seekers = scoreboard.registerNewTeam("Seeker");
        seekers.color(NamedTextColor.GOLD);
        seekers.prefix(Component.text("[Seeker] ").color(NamedTextColor.GOLD));

        final Team hiders = scoreboard.registerNewTeam("Hider");
        hiders.color(NamedTextColor.BLUE);
        hiders.prefix(Component.text("[Hider] ").color(NamedTextColor.BLUE));
        hiders.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);

        final Team waiting = scoreboard.registerNewTeam("Waiting");
        waiting.color(NamedTextColor.GRAY);

        this.eventScoreboard = EventCore.getInstance().getModuleRegistry().getModule(ScoreboardModule.class)
                .getScoreboardManager().createScoreboard(scoreboard,
                        Component.text("Hide & Seek").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        eventScoreboard.addRow(new Row("Phase", new Row.RowRender() {
            @Override
            public String render(Player player) {
                if(currentGame == null)
                    return "&eN/A";
                return switch (currentGame.getStatus()) {
                    case JOINING -> "&eWaiting for Players";
                    case WAITING -> "&eHiding";
                    case STARTED -> "&eSearching";
                    case ENDED -> "&eEnd";
                };
            }
        }));

        eventScoreboard.addRow(new Row("Time Remaining", new Row.RowRender() {
            @Override
            public String render(Player player) {
                if(currentGame == null)
                    return "&eN/A";
                return "&e" + StringUtils.formatSeconds(currentGame.getActualTimeSeconds(currentGame.counter));
            }
        }));

        eventScoreboard.addRow(new Row("Hiders Left", new Row.RowRender() {
            @Override
            public String render(Player player) {
                System.out.println("Render " + currentGame.getHiders().size() + " for " + player.getName());
                if(currentGame == null)
                    return "&eN/A";
                return "&e" + String.valueOf(currentGame.getHiders().size());
            }
        }));
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
            MultiLib.notify("eventspigot:hns", "init");

            Component component = Component.text("----------------------------").color(NamedTextColor.BLUE)
                    .append(Component.text("\n\nHIDE & SEEK\n\n").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text("A game of ").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, false))
                    .append(Component.text("Hide & Seek").color(NamedTextColor.GOLD))
                    .append(Component.text(" has been started!").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("\nClick here").color(NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.runCommand("/hns join"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to join!").color(NamedTextColor.GREEN))))
                    .append(Component.text(" to join the game.").color(NamedTextColor.AQUA).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.UNDERLINED, false))
                    .append(Component.text("\n\n----------------------------").color(NamedTextColor.BLUE));

            // Bukkit.broadcast() only seems to be sending to console
            for(Player player : Bukkit.getAllOnlinePlayers())
                player.sendMessage(component);

            this.currentGame.startCountdown();

            return true;
        }
        return false;
    }

    public void joinAsHider(Player player) {
        if(this.currentGame == null || (this.currentGame.getStatus() != HideNSeekGame.GameStatus.JOINING
                && this.currentGame.getStatus() != HideNSeekGame.GameStatus.WAITING))
            return;

        eventScoreboard.installPlayer(player);
        PlayerHotbar.givePlayerHideSeekInventory(player);
        if(this.currentGame.getStatus() == HideNSeekGame.GameStatus.JOINING)
            scoreboard.getTeam("Waiting").addPlayer(player);
        else
            scoreboard.getTeam("Hider").addPlayer(player);

        this.getGame().join(player.getUniqueId());
        MultiLib.notify("eventspigot:hns", "join:" + player.getUniqueId());
    }

    public void joinAsSeeker(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED)
            return;

        player.setScoreboard(scoreboard);
        PlayerHotbar.givePlayerHideSeekInventory(player);
        if(this.currentGame.getStatus() == HideNSeekGame.GameStatus.JOINING)
            scoreboard.getTeam("Waiting").addPlayer(player);
        else
            scoreboard.getTeam("Seeker").addPlayer(player);

        this.getGame().addSeeker(player.getUniqueId());
        MultiLib.notify("eventspigot:hns", "addseeker:" + player.getUniqueId());
    }

    public void leave(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED)
            return;

        if(currentGame.getSeekers().contains(player.getUniqueId())) {
            this.leaveSeeker(player);
            return;
        }

        this.getGame().leave(player.getUniqueId());
        player.setScoreboard(EventSpigot.getInstance().getScoreboard());
        PlayerHotbar.givePlayerInventory(player);
        MultiLib.notify("eventspigot:hns", "leave:" + player.getUniqueId());
    }

    public void leaveSeeker(Player player) {
        if(this.currentGame == null || this.currentGame.getStatus() == HideNSeekGame.GameStatus.ENDED)
            return;
        this.getGame().leaveSeeker(player.getUniqueId());
        player.setScoreboard(EventSpigot.getInstance().getScoreboard());
        PlayerHotbar.givePlayerInventory(player);
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
