package dev.imabad.mceventsuite.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpHereCommand extends BaseCommand {

    public TpHereCommand() {
        super("tphere", "mceventsuite.mod.tphere");
    }

    // Player teleport command
    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(commandSender instanceof Player player) {
            if(args.length != 1) {
                commandSender.sendMessage(ChatColor.RED + "Usage: /tphere <player>");
                return false;
            }else{
                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    commandSender.sendMessage(ChatColor.RED + "Player not found!");
                    return false;
                }
                player.teleportAsync(target.getLocation());
                commandSender.sendMessage(ChatColor.GREEN + "Teleported " + player.getName() + " to you");
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        return null;
    }
}
