package dev.imabad.mceventsuite.spigot.modules.teams;

import com.github.puregero.multilib.MultiLib;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventRank;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.RankDAO;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamManager {
    private Scoreboard mainScoreboard;
    private Map<Integer, Pair<PlayerTeam, PlayerTeam>> integerPairMap;

    public TeamManager() {
        this.integerPairMap = new HashMap<>();
        this.createTeams(EventSpigot.getInstance().getScoreboard());

        MultiLib.onString(EventSpigot.getInstance(), "eventspigot:teams", (msg) -> {
            String[] parts = msg.split(":");
            if(parts[0].equalsIgnoreCase("add")) {
                String username = parts[1];
                int rank = Integer.parseInt(parts[2]);
                boolean update = parts[3].equalsIgnoreCase("true");
                this.addPlayerToTeam(username, rank, update);
            } else if(parts[0].equalsIgnoreCase("rm")) {
                String username = parts[1];
                boolean update = parts[2].equalsIgnoreCase("true");
                this.removePlayerFromTeam(username, update);
            }
        });
    }

    public void createTeams(org.bukkit.scoreboard.Scoreboard bScoreboard) {
        this.mainScoreboard = ((CraftScoreboard)bScoreboard).getHandle();

        List<EventRank> eventRankList = EventSpigot.getInstance().getRanks();

        for(EventRank eventRank : eventRankList) {
            PlayerTeam javaTeam = new PlayerTeam(this.mainScoreboard, eventRank.nameEnum());
            PlayerTeam bedrockTeam = new PlayerTeam(this.mainScoreboard, eventRank.nameEnum());

            final net.kyori.adventure.text.Component javaComp = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(eventRank.getJavaPrefix());
            final String javaJson = GsonComponentSerializer.gson().serialize(javaComp);

            final net.kyori.adventure.text.Component bedrockComp = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(eventRank.getBedrockPrefix());
            final String bedrockJson = GsonComponentSerializer.gson().serialize(bedrockComp);

            javaTeam.setPlayerPrefix(Component.Serializer.fromJson(javaJson));
            bedrockTeam.setPlayerPrefix(Component.Serializer.fromJson(bedrockJson));
            this.integerPairMap.put(eventRank.getId(), Pair.of(javaTeam, bedrockTeam));
        }
    }

    public List<PlayerTeam> getTeams(Player player) {
        final List<PlayerTeam> teams = new ArrayList<>();
        boolean isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        for(Pair<PlayerTeam, PlayerTeam> teamPair : this.integerPairMap.values()) {
            if (isBedrock)
                teams.add(teamPair.getValue());
            else
                teams.add(teamPair.getKey());
        }
        return teams;
    }

    public void resendTeams() {
        for(Pair<PlayerTeam, PlayerTeam> teamPair : this.integerPairMap.values()) {
            ClientboundSetPlayerTeamPacket javaPacket = ClientboundSetPlayerTeamPacket
                    .createAddOrModifyPacket(teamPair.getKey(), true);
            ClientboundSetPlayerTeamPacket bedrockPacket = ClientboundSetPlayerTeamPacket
                    .createAddOrModifyPacket(teamPair.getValue(), true);

            for(Player player1 : Bukkit.getLocalOnlinePlayers()) {
                if(FloodgateApi.getInstance().isFloodgatePlayer(player1.getUniqueId()))
                    ((CraftPlayer) player1).getHandle().connection.connection.send(bedrockPacket);
                else
                    ((CraftPlayer) player1).getHandle().connection.connection.send(javaPacket);
            }
        }
    }

    public void addPlayerToTeam(Player player, EventRank eventRank) {
        this.addPlayerToTeam(player, eventRank, true);
    }

    public void addPlayerToTeam(Player player, EventRank eventRank, boolean update) {
        this.addPlayerToTeam(player.getName(), eventRank.getId(), update);
        MultiLib.notify("eventspigot:teams", "add:" + player.getName() + ":" + eventRank.getId() + ":" +
                (update ? "true" : "false"));
    }

    public void addPlayerToTeam(String player, int eventRank, boolean update) {
        Pair<PlayerTeam, PlayerTeam> pair = this.integerPairMap.get(eventRank);
        pair.getKey().getPlayers().add(player);
        pair.getValue().getPlayers().add(player);

        if(update)
            this.resendTeams();
    }

    public void removePlayerFromTeam(Player player) {
        this.removePlayerFromTeam(player, true);
    }

    public void removePlayerFromTeam(Player player, boolean update) {
        this.removePlayerFromTeam(player.getName(), update);
        MultiLib.notify("eventspigot:teams", "rm:" + player.getName() + ":" + (update ? "true" : "false"));
    }

    public void removePlayerFromTeam(String player, boolean update) {
        for(Pair<PlayerTeam, PlayerTeam> pair : this.integerPairMap.values()) {
            pair.getKey().getPlayers().remove(player);
            pair.getValue().getPlayers().remove(player);
        }
        if(update)
            this.resendTeams();
    }
}
