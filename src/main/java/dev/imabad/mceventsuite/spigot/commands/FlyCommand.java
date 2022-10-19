package dev.imabad.mceventsuite.spigot.commands;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.modules.player.PlayerModule;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FlyCommand extends BaseCommand {
    public FlyCommand() {
        super("fly");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        Player sender = (Player) commandSender;
        EventPlayer eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(sender.getUniqueId()).get();

        // Checks if user is not in a region with the fly flag & is not a staff member.
        if(!RegionUtils.getHighestPriorityRegionFlag(sender, RegionUtils.getOrRegisterFlag(new BooleanFlag("can-fly")))
                && !sender.hasPermission("mceventsuite.fly.bypass")) {
            if(args.length == 0) {
                if(!eventPlayer.hasPermission("eventsuite.fly")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }else{
                    if(sender.getAllowFlight()) {
                        sender.setFlying(false);
                        sender.setAllowFlight(false);
                        sender.sendMessage(ChatColor.RED + "Flight disabled!");
                    }else{
                        if(PlayerModule.playerCanFly(sender)) {
                            sender.setAllowFlight(true);
                            sender.setFlying(true);
                            sender.sendMessage(ChatColor.GREEN + "Flight enabled!");
                        }else{
                            sender.sendMessage(ChatColor.RED + "You cannot fly at this time.");
                        }
                    }
                }
            }else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("enable")) {
                    if(!eventPlayer.hasPermission("eventsuite.fly")) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    }else{
                        if(PlayerModule.playerCanFly(sender)) {
                            sender.setAllowFlight(true);
                            sender.setFlying(true);
                            sender.sendMessage(ChatColor.GREEN + "Flight enabled!");
                        }else{
                            sender.sendMessage(ChatColor.RED + "You cannot fly at this time.");
                        }
                    }

                }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("disable")) {
                    if(!eventPlayer.hasPermission("eventsuite.fly")) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    }else{
                        sender.setFlying(false);
                        sender.setAllowFlight(false);
                        sender.sendMessage(ChatColor.RED + "Flight disabled!");
                    }
                }
            }else if(args.length == 2) {
                if(!eventPlayer.hasPermission("eventsuite.fly.others")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }else{
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null) {
                        if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("enable")) {
                            if(PlayerModule.playerCanFly(target)) {
                                target.setAllowFlight(true);
                                target.setFlying(true);
                                target.sendMessage(ChatColor.GREEN + "Flight enabled by an ADMIN!");
                                sender.sendMessage(ChatColor.GREEN + "Flight enabled for " + target.getName());
                            }else{
                                sender.sendMessage(ChatColor.RED + "This user cannot fly at this time.");
                            }
                        }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("disable")) {
                            target.setFlying(false);
                            target.setAllowFlight(false);
                            target.sendMessage(ChatColor.GREEN + "Flight disabled by an ADMIN!");
                            sender.sendMessage(ChatColor.RED + "Flight disabled for " + target.getName());
                        }
                    }else{
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                    }
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Flight is disabled in this area!");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
