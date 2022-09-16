package dev.imabad.mceventsuite.spigot.modules.meet;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.BooleanResponse;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.PlayerJoinQueueRequest;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.PlayerJoinQueueResponse;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.PlayerLeaveQueueRequest;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimplifiedMeetCommand extends BaseCommand {

    private RedisModule redisModule;
    private MeetModule meetModule;

    public SimplifiedMeetCommand(MeetModule meetModule) {
        super("meet");
        this.meetModule = meetModule;
        this.redisModule = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /meet <[name] | leave>");
                return true;
            }

            if(args[0].equalsIgnoreCase("leave")) {
                redisModule.publishRequest(RedisChannel.GLOBAL, new PlayerLeaveQueueRequest(player.getUniqueId()), (response) -> {
                    if(player.isOnline() && response instanceof BooleanResponse booleanResponse) {
                        if(booleanResponse.getValue())
                            player.sendMessage(ChatColor.RED + "You left the Meet & Greet.");
                        else
                            player.sendMessage(ChatColor.RED + "You are not in the queue for a Meet & Greet!");
                    }
                });
            } else {
                this.redisModule.publishRequest(RedisChannel.GLOBAL,
                        new PlayerJoinQueueRequest(args[0], player.getUniqueId()), (raw) -> {
                            if(player.isOnline() && raw instanceof PlayerJoinQueueResponse response) {
                                if(!response.isSuccess()) {
                                    switch (response.getFailureReason()) {
                                        case NO_SUCH_QUEUE -> player.sendMessage(ChatColor.RED + "There is no queue by that name!");
                                        case IN_OTHER_QUEUE ->
                                                player.sendMessage(ChatColor.GOLD + "You are currently in the queue for the Meet & Greet with " +
                                                        ChatColor.AQUA + meetModule.getDisplayName(response.getExtraData()) + "\n" +
                                                        ChatColor.RESET + "You must leave that queue before joining another one. Use " +
                                                        ChatColor.LIGHT_PURPLE + "/meet leave");
                                        case ALREADY_IN_QUEUE ->
                                                player.sendMessage(ChatColor.RED + "You are already in the queue for this Meet & Greet!");
                                    }
                                } else {
                                    final String eta = response.getExtraData().split(":")[0];
                                    final String pos = response.getExtraData().split(":")[1];
                                    player.sendMessage(ChatColor.GREEN + "You joined the queue for the Meet & Greet with " +
                                            ChatColor.AQUA + meetModule.getDisplayName(args[0]) + ChatColor.GREEN + "!" +
                                            (response.isFull() ? ChatColor.GOLD + "\nThe queue is currently full." + ChatColor.RESET +
                                                    " Please be aware the session may end before your time.\n\n" : "\n" + ChatColor.RESET) +
                                            "You are position " + ChatColor.YELLOW + pos + ChatColor.RESET + " in the queue. Estimated " +
                                            ChatColor.YELLOW + eta + " minute " + ChatColor.RESET + "wait."
                                    );
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
