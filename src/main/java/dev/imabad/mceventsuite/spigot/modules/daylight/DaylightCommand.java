package dev.imabad.mceventsuite.spigot.modules.daylight;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DaylightCommand extends BaseCommand {

    public DaylightCommand() {
        super("daylight");
    }

    public boolean execute(CommandSender commandSender, String label, String[] args) {
        TimeType type = TimeType.UTC;
        String typeString = args[0];
        commandSender.sendMessage(ChatColor.AQUA + typeString);

        if (typeString.equalsIgnoreCase("LOCAL")) type = TimeType.LOCAL;
        else if (typeString.equalsIgnoreCase("ALWAYS_DAY")) type = TimeType.ALWAYS_DAY;
        else if (typeString.equalsIgnoreCase("ALWAYS_NIGHT")) type = TimeType.ALWAYS_NIGHT;
        else if (typeString.equalsIgnoreCase("MINECRAFT")) type = TimeType.MINECRAFT;
        if (EventCore.getInstance().getModuleRegistry().isModuleEnabled(DaylightModule.class))
            EventCore.getInstance().getModuleRegistry().getModule(DaylightModule.class).setPlayerTime(((Player) commandSender), type);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
