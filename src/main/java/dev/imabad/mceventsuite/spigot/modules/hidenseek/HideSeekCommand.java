package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideNSeekGame;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideNSeekModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HideSeekCommand extends BaseCommand {

   final HideNSeekModule module;

    public HideSeekCommand(HideNSeekModule module) {
        super("hns");
        this.module = module;
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
            if (subCommand.equalsIgnoreCase("create")) {
                int countdown = 5;
                int duration = 10;
                if(args.length >= 2)
                    countdown = Integer.parseInt(args[1]);
                if(args.length == 3)
                    duration = Integer.parseInt(args[2]);

                countdown = countdown * 60;
                duration = duration * 60;

                if(module.startGame(new HideNSeekGame(countdown, duration)))
                    module.joinAsSeeker(player);
                else
                    player.sendMessage(Component.text("Game failed to start! Is there already in progress?").color(NamedTextColor.RED));
                return true;
            } else if(subCommand.equalsIgnoreCase("seek")) {
                module.joinAsSeeker(player);
                return true;
            } else if(subCommand.equalsIgnoreCase("addseeker")) {
                String target = args[1];
                Player targetPlayer = Bukkit.getPlayer(target);
                if(targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "Could not find a player with that name!");
                    return false;
                }
                module.joinAsSeeker(targetPlayer);
                player.sendMessage(ChatColor.GREEN + "Made " + targetPlayer.getName() + " a seeker!");
                return true;
            } else if(subCommand.equalsIgnoreCase("end")) {
                module.getGame().runEnd();
                return true;
            } else if(subCommand.equalsIgnoreCase("addall")) {
                for(Player player1 : Bukkit.getAllOnlinePlayers()) {
                    if(!module.getGame().getSeekers().contains(player1.getUniqueId())
                        && !module.getGame().getHiders().contains(player1.getUniqueId())) {
                        module.joinAsSeeker(player1);
                    }
                }
                return true;
            } else if(subCommand.equalsIgnoreCase("start")) {
                if(module.getGame() == null || module.getGame().getStatus() != HideNSeekGame.GameStatus.WAITING) {
                    player.sendMessage(ChatColor.RED + "Cannot start game! Is there a game in progress already or have you not ran /hns create?");
                    return true;
                }
                module.getGame().start(false);
                return true;
            }
        }

        if(module.getGame() == null || module.getGame().getStatus() == HideNSeekGame.GameStatus.ENDED) {
            player.sendMessage(ChatColor.RED + "There is no Hide and Seek game in progress! Try again later.");
            return true;
        }

        if(subCommand.equalsIgnoreCase("join")) {
            if(player.getName().equalsIgnoreCase("cubedcam")) {
                module.joinAsSeeker(player);
                return true;
            }
            if(module.getGame().getStatus() == HideNSeekGame.GameStatus.WAITING) {
                module.joinAsHider(player);
            } else {
                player.sendMessage(ChatColor.RED + "It's too late to join - the game has already started!");
            }
        } else if(subCommand.equalsIgnoreCase("leave")) {
            if(module.getGame().getHiders().contains(player.getUniqueId())) {
                module.leave(player);
            } else if(module.getGame().getSeekers().contains(player.getUniqueId())) {
                module.leaveSeeker(player);
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
