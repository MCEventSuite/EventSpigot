package dev.imabad.mceventsuite.spigot.commands;

import com.github.puregero.multilib.MultiLib;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TpaCommand extends BaseCommand {
    private static HashMap<String, HashMap<String, Long>> requests = new HashMap();

    public TpaCommand() {
        super("tpa");
        MultiLib.onString(EventSpigot.getInstance(), "eventspigot:tpa", (data) -> {
            final UUID from = UUID.fromString(data.split(":")[0]);
            final UUID to = UUID.fromString(data.split(":")[1]);
            final Player fromPlayer = Bukkit.getPlayer(from);
            final Player toPlayer = Bukkit.getPlayer(to);
            if(fromPlayer != null && toPlayer != null && toPlayer.isLocalPlayer()) {
                HashMap<String, Long> playerRequests = requests.get(toPlayer.getUniqueId().toString());
                if(playerRequests == null) {
                    playerRequests = new HashMap<>();
                    requests.put(toPlayer.getUniqueId().toString(), playerRequests);
                }
                playerRequests.put(from.toString(), System.currentTimeMillis());
            }
        });

        MultiLib.onString(EventSpigot.getInstance(), "eventspigot:tpaccept", (data) -> {
            final UUID from = UUID.fromString(data.split(":")[0]);
            final UUID to = UUID.fromString(data.split(":")[1]);

            if(requests.containsKey(to.toString())) {
                requests.get(to.toString()).remove(from.toString());
                if(requests.get(to.toString()).isEmpty())
                    requests.remove(to.toString());
            }
        });
    }

    public static HashMap<String, HashMap<String, Long>> getTeleportRequests() {
        return requests;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        if(args.length != 1){
            commandSender.sendMessage(ChatColor.RED + "Incorrect Usage: /tpa <player>");
            return false;
        }

        String name = args[0];
        Player player = Bukkit.getPlayer(name);

        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Player " + name + " isn't on this server.");
            return false;
        }

        Player sender = (Player) commandSender;

        HashMap<String, Long> playerRequests = requests.get(player.getUniqueId().toString());

        if (playerRequests == null) {
            playerRequests = new HashMap<>();
            requests.put(player.getUniqueId().toString(), playerRequests);
        }

        if (playerRequests.containsKey(sender.getUniqueId().toString())) {
            long requestTime = playerRequests.get(sender.getUniqueId().toString());
            boolean hasExpired = (System.currentTimeMillis() - requestTime) > (30 * 1000);

            if (!hasExpired) {
                commandSender.sendMessage(ChatColor.RED + "You already have an active teleport request to this player.");
                return false;
            }
        }

        if (TptoggleCommand.isDisabledForPlayer(player)) {
            commandSender.sendMessage(ChatColor.RED + "This player has teleportation requests disabled.");
            return false;
        }

        playerRequests.put(sender.getUniqueId().toString(), System.currentTimeMillis());

        commandSender.sendMessage(ChatColor.GREEN + "Teleport request sent to " + player.getDisplayName() + ".");
        player.sendMessage(ChatColor.GREEN + commandSender.getName() + " requested to teleport to you. Use /tpaccept to accept.");

        MultiLib.notify("eventspigot:tpa", sender.getUniqueId() + ":" + player.getUniqueId());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
