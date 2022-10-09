package dev.imabad.mceventsuite.spigot.modules.bubbles;

import com.github.puregero.multilib.MultiLib;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BubbleCommand extends BaseCommand {

    private BubbleManager bubbleManager;

    public BubbleCommand(BubbleManager bubbleManager) {
        super("cb", "eventsuite.cb");
        EventPlayer.addDefault("cb_global", true);
        this.bubbleManager = bubbleManager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return false;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /cb <join/leave/toggle/global>");
            return false;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /cb join <name>");
                return false;
            }
            this.bubbleManager.joinChatBubble(player, this.getLastArgs(args, 1));
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /cb leave <name>");
                return false;
            }
            this.bubbleManager.leaveChatBubble(player, this.getLastArgs(args, 1));
        } else if (args[0].equalsIgnoreCase("toggle")) {
            boolean active = player.getPersistentData("cb_global") == null || player.getPersistentData("cb_global").equals("true");
            if (active) {
                player.setPersistentData("cb_global", "false");
                player.sendMessage(ChatColor.YELLOW + "Chat bubble monitoring " + ChatColor.RED + "disabled");
            } else {
                player.setPersistentData("cb_global", "true");
                player.sendMessage(ChatColor.YELLOW + "Chat bubble monitoring " + ChatColor.GREEN + "enabled");
            }
        } else if (args[0].equalsIgnoreCase("global")) {
            String text = this.getLastArgs(args, 1);
            EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresentOrElse((p) -> {
                Component javaMessage = Component.text().append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&c[Announcement] " +
                                (p.getRank().getJavaPrefix().isEmpty() ? "" : StringUtils.colorizeMessage(p.getRank().getJavaPrefix()))
                                + player.getName()
                                + ": &f" + text)).asComponent();
                Component bedrockMessage = Component.text().append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&c[Announcement] " +
                                (p.getRank().getBedrockPrefix().isEmpty() ? "" : StringUtils.colorizeMessage(p.getRank().getBedrockPrefix()))
                                + player.getName()
                                + ": &f" + text)).asComponent();

                for (Player player1 : Bukkit.getAllOnlinePlayers())
                    if (FloodgateApi.getInstance().isFloodgatePlayer(player1.getUniqueId()))
                        player1.sendMessage(bedrockMessage);
                    else
                        player1.sendMessage(javaMessage);
            }, () -> player.sendMessage("An error occurred, please try again later"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
