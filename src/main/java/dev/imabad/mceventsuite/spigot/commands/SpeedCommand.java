package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.stage.StageModule;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpeedCommand extends BaseCommand{

    public SpeedCommand() {
        super("speed", "eventsuite.speed");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        if(super.execute(sender, label, args)){
            Player player = (Player) sender;
            if(args.length == 0){
                audience(sender).sendMessage(Component.text("Invalid usage: /speed <speedLevel>").color(NamedTextColor.RED));
                return false;
            }
            int speedLevel = Integer.parseInt(args[0]);
            if(speedLevel < 1 || speedLevel > 5){
                audience(sender).sendMessage(Component.text("Invalid usage: speedLevel must be 1-5").color(NamedTextColor.RED));
                return false;
            }
            if(!sender.hasPermission("eventsuite.speed." + speedLevel)){
                audience(sender).sendMessage(Component.text("You do not have permission for that speed level!").color(NamedTextColor.RED));
                return false;
           }
            if(RegionUtils.isInRegion(player, "sticky")){
                audience(sender).sendMessage(Component.text("You cannot do that there!").color(NamedTextColor.RED));
                return false;
            }
            player.setWalkSpeed((1f / 5) * speedLevel);
            audience(sender).sendMessage(Component.text("Set walk speed to ").color(NamedTextColor.GREEN).append(Component.text(speedLevel).color(NamedTextColor.YELLOW)));
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
