package dev.imabad.mceventsuite.spigot.modules.daylight;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DaylightCommand extends BaseCommand {

    public DaylightCommand() {
        super("daylight");
    }

    public boolean execute(CommandSender commandSender, String label, String[] args) {
        TimeType type = TimeType.UTC;
        String typeString = args[0];

        if (typeString.equalsIgnoreCase("LOCAL")) type = TimeType.LOCAL;
        else if (typeString.equalsIgnoreCase("ALWAYS_DAY")) type = TimeType.ALWAYS_DAY;
        else if (typeString.equalsIgnoreCase("ALWAYS_NIGHT")) type = TimeType.ALWAYS_NIGHT;
        else if (typeString.equalsIgnoreCase("MINECRAFT")) type = TimeType.MINECRAFT;
        if (EventCore.getInstance().getModuleRegistry().isModuleEnabled(DaylightModule.class))
            EventCore.getInstance().getModuleRegistry().getModule(DaylightModule.class).setPlayerTime(((Player) commandSender), type);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("utc");
            commands.add("local");
            commands.add("always_day");
            commands.add("always_night");
            commands.add("minecraft");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
