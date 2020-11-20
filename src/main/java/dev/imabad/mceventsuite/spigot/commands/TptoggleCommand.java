package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TptoggleCommand extends BaseCommand {
    private static final String TP_TOGGLE_PLAYER_PROPERTY = "tpToggle";

    public TptoggleCommand() {
        super("tptoggle", "eventsuite.tptoggle");
    }

    public static boolean isDisabledForPlayer(Player player) {
        Optional<EventPlayer> eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId());

        if (!eventPlayer.isPresent()) {
            return false;
        }

        return isDisabledForPlayer(eventPlayer.get());
    }

    public static boolean isDisabledForPlayer(EventPlayer player) {
        return player.getBooleanProperty(TP_TOGGLE_PLAYER_PROPERTY);
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        Player sender = (Player) commandSender;
        UUID uuid = UUID.fromString(sender.getUniqueId().toString());

        EventPlayer eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(uuid).get();

        if (!isDisabledForPlayer(eventPlayer)) {
            eventPlayer.setProperty(TP_TOGGLE_PLAYER_PROPERTY, true);
            sender.sendMessage(ChatColor.GREEN + "Teleportation requests disabled.");
        } else {
            eventPlayer.setProperty(TP_TOGGLE_PLAYER_PROPERTY, false);
            sender.sendMessage(ChatColor.GREEN + "Teleportation requests enabled.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
