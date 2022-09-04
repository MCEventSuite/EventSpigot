package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideNSeekGame;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideNSeekModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HideSeekCommand extends BaseCommand {

   final HideNSeekModule module = EventCore.getInstance().getModuleRegistry().getModule(HideNSeekModule.class);

    public HideSeekCommand() {
        super("hns");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0)
            return false;

        final String subCommand = args[0];

        if(player.hasPermission("eventsuite.admin.hns")) {
            if (subCommand.equalsIgnoreCase("start")) {
                module.startGame(new HideNSeekGame(player.getUniqueId()));
            } else if(subCommand.equalsIgnoreCase("addseeker")) {
                String target = args[1];
                Player targetPlayer = Bukkit.getPlayer(target);
                if(targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "Could not find a player with that name!");
                    return false;
                }
                module.getGame().addSeeker(targetPlayer.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Made " + targetPlayer.getName() + " a seeker!");
            } else if(subCommand.equalsIgnoreCase("end")) {
                module.getGame().runEnd();
            } else if(subCommand.equalsIgnoreCase("addall")) {
                for(Player player1 : Bukkit.getAllOnlinePlayers()) {
                    if(!module.getGame().getSeekers().contains(player1.getUniqueId())
                        && !module.getGame().getHiders().contains(player1.getUniqueId())) {
                        module.getGame().addSeeker(player1.getUniqueId());
                    }
                }
            }
        }

        if(module.getGame() == null || module.getGame().getStatus() == HideNSeekGame.GameStatus.ENDED) {
            player.sendMessage(ChatColor.RED + "There is no Hide and Seek game in progress! Try again later.");
            return true;
        }

        if(subCommand.equalsIgnoreCase("join")) {
            if(module.getGame().getStatus() == HideNSeekGame.GameStatus.WAITING) {
                module.getGame().join(player.getUniqueId());
            } else {
                player.sendMessage(ChatColor.RED + "It's too late to join - the game has already started!");
            }
        } else if(subCommand.equalsIgnoreCase("leave")) {
            if(module.getGame().getHiders().contains(player.getUniqueId())) {
                module.getGame().leave(player.getUniqueId());
            } else if(module.getGame().getSeekers().contains(player.getUniqueId())) {
                module.getGame().leaveSeeker(player.getUniqueId());
            } else {
                player.sendMessage(ChatColor.RED + "You're not currently playing!");
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
