package dev.imabad.mceventsuite.spigot.modules.scoreboards;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import joptsimple.internal.Strings;
import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class EventScoreboard {
    private List<Row> rowList;
    private List<Row> toRemove;
    private CraftScoreboard scoreboard;
    private Objective objective;
    private int lastSpacerAmount;

    public EventScoreboard(Scoreboard scoreboard, Component title) {
        this.rowList = new ArrayList<>();
        this.toRemove = new ArrayList<>();
        this.scoreboard = (CraftScoreboard) scoreboard;
        this.objective = this.scoreboard.registerNewObjective("objective", "dummy");
        this.objective.displayName(title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int addRow(Row row) {
        this.rowList.add(row);
        this.update();
        return this.rowList.size() - 1;
    }

    public void removeRow(int id) {
        this.toRemove.add(this.rowList.get(id));
        this.rowList.remove(id);
        this.update();
    }

    public void installPlayer(Player player) {
        player.setScoreboard(this.scoreboard);
        this.update();
    }

    public void update() {
        for(Player player : Bukkit.getLocalOnlinePlayers()) {
            final CraftPlayer craftPlayer = ((CraftPlayer) player);
            if(player.getScoreboard().equals(this.scoreboard)) {
                final Connection connection = craftPlayer.getHandle().connection.connection;

                int firstSpacerId = 3 * rowList.size() + 2;
                int rowId = firstSpacerId - 1;
                int spacerId = 3;

                connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, "objective", " ", firstSpacerId));
                connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, "objective", StringUtils.colorizeMessage("&9play.cubedcon.com"), 1));

                for(Row row : toRemove) {
                    String lastRender = row.getLastRender(player);
                    connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, "objective", row.getTitle(), 0));
                    connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, "objective", lastRender, 0));
                }

                for(Row row : rowList) {
                    String lastRender = row.getLastRender(player);
                    String renderedString = row.render(player);

                    connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, "objective", row.getTitle(), rowId));
                    rowId--;

                    if (renderedString.equals(lastRender)) {
                        connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, "objective", lastRender, rowId));
                    } else {
                        if(lastRender != null)
                            connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, "objective", lastRender, 0));

                        if(renderedString.isEmpty()) {
                            renderedString = Strings.repeat(' ', spacerId);
                            spacerId++;
                            row.forceLastRender(player, renderedString);
                        }

                        connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, "objective", renderedString, rowId));
                    }
                    rowId--;

                    connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, "objective", Strings.repeat(' ', spacerId), rowId));
                    rowId--;
                    spacerId++;
                }
                if(lastSpacerAmount > spacerId) {
                    for(int i = spacerId + 1; i <= lastSpacerAmount; i++) {
                        connection.send(new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, "objective", Strings.repeat(' ', i), 0));
                    }
                }
                lastSpacerAmount = spacerId;
            }
        }
    }
}
