package dev.imabad.mceventsuite.spigot.modules.meet;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.BooleanResponse;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.*;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeetCommand extends BaseCommand {

    final RedisModule redisModule;
    final MeetModule meetModule;

    public MeetCommand(MeetModule meetModule) {
        super("meetgreet");
        this.redisModule = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
        this.meetModule = meetModule;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(player.hasPermission("eventsuite.meet.admin")) {
            if (args[0].equalsIgnoreCase("create")) {
                final String name = args[1];
                final int sessionTime = Integer.parseInt(args[2]);
                final int meetTime = Integer.parseInt(args[3]);
                final String displayName = this.getLastArgs(args, 4);

                this.redisModule.publishRequest(RedisChannel.GLOBAL,
                        new AdminCreateSessionRequest(name, displayName, meetTime, sessionTime),
                        (response) -> {
                            if (player.isOnline() && response instanceof BooleanResponse booleanResponse) {
                                if (booleanResponse.getValue())
                                    player.sendMessage(Component.text("Successfully created a Meet & Greet session for ").color(NamedTextColor.GREEN)
                                            .append(Component.text(displayName).color(NamedTextColor.AQUA))
                                            .append(Component.text("\nPlease set the player spawn(s) before starting.").color(NamedTextColor.WHITE)));
                                else
                                    player.sendMessage(Component.text("Couldn't create session! Is there one with the same name already?").color(NamedTextColor.RED));
                            }
                        });
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                final String name = args[1];
                int pos = 0;
                if (args.length == 3)
                    pos = Integer.parseInt(args[2]) - 1;
                int finalPos = pos;
                this.redisModule.publishRequest(RedisChannel.GLOBAL,
                        new AdminCreateSpotMessage(name, pos, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()),
                        (response) -> {
                            if (player.isOnline() && response instanceof BooleanResponse booleanResponse) {
                                if (booleanResponse.getValue())
                                    player.sendMessage(Component.text("Spawn " + (finalPos + 1) + " set for session " + meetModule.getDisplayName(name)).color(NamedTextColor.GREEN));
                                else
                                    player.sendMessage(Component.text("Failed to create pos " + (finalPos + 1) + " for session " + name + ". Did you get the name right?").color(NamedTextColor.RED));
                            }
                        });
            } else if(args[0].equalsIgnoreCase("start")) {
                final String name = args[1];
                this.redisModule.publishRequest(RedisChannel.GLOBAL, new AdminStartSessionRequest(name), (msg) -> {
                    if(player.isOnline() && msg instanceof BooleanResponse response) {
                        if(!response.getValue()) {
                            player.sendMessage(ChatColor.RED + "Could not start session! Does it exist, or is it already in progress?");
                        }
                    }
                });
            } else if(args[0].equalsIgnoreCase("next")) {
                final String name = args[1];
                this.redisModule.publishRequest(RedisChannel.GLOBAL, new AdminMoveAlongSessionRequest(name), (msg) -> {
                    if(player.isOnline() && msg instanceof BooleanResponse response) {
                        if(!response.getValue()) {
                            player.sendMessage(ChatColor.RED + "Could not move along session! Is it in progress, or does it even exist?");
                        } else {
                            player.sendMessage(ChatColor.RED + "Session ended. Queue moved up.");
                        }
                    }
                });
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
