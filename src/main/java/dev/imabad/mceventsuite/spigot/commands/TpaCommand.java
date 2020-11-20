package dev.imabad.mceventsuite.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TpaCommand extends BaseCommand {
    private static HashMap<String, HashMap<String, Long>> requests = new HashMap();

    public TpaCommand() {
        super("tpa");
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

        commandSender.sendMessage(ChatColor.GREEN + "Teleport request sent.");
        player.sendMessage(ChatColor.GREEN + commandSender.getName() + " requested to teleport to you. Use /tpaccept to accept.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
