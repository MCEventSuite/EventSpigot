package dev.imabad.mceventsuite.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpCommand extends BaseCommand {

    public TpCommand() {
        super("tp", "mceventsuite.mod.tp");
    }

    // Player teleport command
    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(args.length == 0 || args.length > 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /tp <player> (<player>)");
            return false;
        }else if(args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if(player == null){
                commandSender.sendMessage(ChatColor.RED + "Player not found!");
                return false;
            }
            ((Player) commandSender).teleportAsync(player.getLocation());
            commandSender.sendMessage(ChatColor.GREEN + "Teleported to " + player.getName());
        }else{
            Player player = Bukkit.getPlayer(args[0]);
            Player target = Bukkit.getPlayer(args[1]);
            if(player == null){
                commandSender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found!");
                return false;
            }
            if(target == null){
                commandSender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found!");
                return false;
            }

            player.teleportAsync(target.getLocation());
            commandSender.sendMessage(ChatColor.GREEN + "Teleported " + player.getName() + " to " + target.getName());
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        return null;
    }
}
