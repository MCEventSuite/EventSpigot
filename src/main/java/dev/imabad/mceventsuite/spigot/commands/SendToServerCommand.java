package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.ChangePlayerServerMessage;
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
                EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new ChangePlayerServerMessage(player.getUniqueId(), server));
            }
        }
        if(args.length > 2){
            return false;
        }
        if(sender.hasPermission("eventsuite.goserver.other")) {
            String username = args[0];
            String server = args[1];
            Player player = Bukkit.getPlayer(username);
            if (player == null) {
                return false;
            }
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new ChangePlayerServerMessage(player.getUniqueId(), server));
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
