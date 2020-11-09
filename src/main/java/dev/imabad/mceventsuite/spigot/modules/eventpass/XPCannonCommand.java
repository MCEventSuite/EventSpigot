package dev.imabad.mceventsuite.spigot.modules.eventpass;

import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XPCannonCommand extends BaseCommand {
    public XPCannonCommand() {
        super("xpc");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("eventsuite.xpc")){
            return false;
        }
        int countdown = 10;
        int radius = 10;
        int xp = 250;
        if(args.length >= 1){
            countdown = Integer.parseInt(args[0]);
        }
        if(args.length >= 2){
            radius = Integer.parseInt(args[1]);
        }
        if(args.length == 3){
            xp = Integer.parseInt(args[2]);
        }
        // Particle radius
        // Countdown of x(default 10) seconds
        // after 10 seconds
            // Explosion
            // raining XP orbs
            // give xp to anyone in area
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
