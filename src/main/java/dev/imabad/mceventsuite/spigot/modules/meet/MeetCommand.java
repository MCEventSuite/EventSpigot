package dev.imabad.mceventsuite.spigot.modules.meet;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.RedisRequestListener;
import dev.imabad.mceventsuite.core.modules.redis.messages.BooleanResponse;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.*;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.bossbar.BossBarModule;
import dev.imabad.mceventsuite.spigot.modules.bubbles.ChatBubble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.chat.Chat;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
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
                this.meetModule.setDisplayName(name, displayName);

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
            } else if (args[0].equalsIgnoreCase("start")) {
                final String name = args[1];
                this.redisModule.publishRequest(RedisChannel.GLOBAL, new AdminStartSessionRequest(name), (msg) -> {
                    if (player.isOnline() && msg instanceof BooleanResponse response) {
                        if (!response.getValue()) {
                            player.sendMessage(ChatColor.RED + "Could not start session! Does it exist, or is it already in progress?");
                        }
                    }
                });
            } else if (args[0].equalsIgnoreCase("next")) {
                final String name = args[1];
                this.redisModule.publishRequest(RedisChannel.GLOBAL, new AdminMoveAlongSessionRequest(name), (msg) -> {
                    if (player.isOnline() && msg instanceof BooleanResponse response) {
                        if (!response.getValue()) {
                            player.sendMessage(ChatColor.RED + "Could not move along session! Is it in progress, or does it even exist?");
                        } else {
                            player.sendMessage(ChatColor.RED + "Session ended. Queue moved up.");
                        }
                    }
                });
            } else if(args[0].equalsIgnoreCase("pause") || args[0].equalsIgnoreCase("resume")) {
                Pair<String, Integer> session = getCurrentSession(player, args);
                if (session == null) {
                    player.sendMessage(ChatColor.RED + "Usage: /meetgreet pause/resume <name>");
                    return false;
                }

                boolean resume = args[0].equalsIgnoreCase("resume");

                this.redisModule.publishRequest(RedisChannel.GLOBAL, new AdminPauseSessionRequest(session.getKey(), resume),
                        (response) -> {
                            if (response instanceof BooleanResponse booleanResponse) {
                                if (booleanResponse.getValue()) {
                                    for (Player player1 : Bukkit.getAllOnlinePlayers()) {
                                        player1.sendMessage(ChatColor.AQUA + this.meetModule.getDisplayName(session.getKey()) +
                                                ChatColor.RED + " has been " + (resume ? "resumed" : "paused"));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Could not pause/resume " + session.getKey() + "! Is it already paused/resumed? Does it even exist?");
                                }
                            }
                        });
            } else if(args[0].equalsIgnoreCase("meettime") || args[0].equalsIgnoreCase("sessiontime")) {
                String name = args[1];
                int time;
                try {
                    time = Integer.parseInt(args[2]);
                } catch(NumberFormatException exc) {
                    player.sendMessage(ChatColor.RED + "That is not a valid number!");
                    return false;
                }

                boolean session = args[0].equalsIgnoreCase("sessiontime");
                this.redisModule.publishRequest(RedisChannel.GLOBAL, new AdminExtendTimeRequest(name, time, session), (msg) -> {
                    if(msg instanceof BooleanResponse response) {
                        if(!response.getValue()) {
                            player.sendMessage(ChatColor.RED + "Are you sure " + name + " exists?");
                        }
                    }
                });
            } else if (args[0].equalsIgnoreCase("kick")) {
                final String name = args[1];
                final String username = args[2];
                final Player player1 = Bukkit.getPlayer(username);
                if (player1 == null) {
                    player.sendMessage(ChatColor.RED + "Could not find player " + username + "!");
                    return false;
                }

                this.meetModule.removeFromSession(player1).thenAcceptAsync((response) -> {
                    if (response) {
                        player.sendMessage(ChatColor.RED + "Kicked " + username);
                        player1.sendMessage(ChatColor.RED + "You were removed from the session.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Wasn't able to kick " + username + " from " + name + "! Does the queue exist?");
                    }
                });
            }
        }

        return true;
    }

    private Pair<String, Integer> getCurrentSession(Player player, String[] args) {
        if(args.length == 3)
            return Pair.of(args[1], Integer.parseInt(args[2]));
        if(args.length == 2)
            return Pair.of(args[1], -1);

        ChatBubble bubble = this.meetModule.bubbleManager.getChatBubble(player.getUniqueId());
        if(bubble == null)
            return null;
        String[] parts = bubble.getName().split("-");
        if(parts.length == 1)
            return null;
        return Pair.of(parts[0], Integer.parseInt(parts[1]));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
