package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.ChangePlayerServerMessage;
import dev.imabad.mceventsuite.spigot.utils.BungeeUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SendToServerCommand extends BaseCommand{
    public SendToServerCommand() {
        super("goserver", "eventsuite.goserver");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String server = args[0];
                BungeeUtils.sendToServer(player, server);
                return true;
            }
        } else if (args.length == 2 && sender.hasPermission("eventsuite.goserver.other")) {
            String username = args[0];
            String server = args[1];
            Player player = Bukkit.getPlayer(username);
            if (player == null) {
                return false;
            }
            BungeeUtils.sendToServer(player, server);
            audience(sender).sendMessage(Component.text("Sent to server."));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
