package dev.imabad.mceventsuite.spigot.modules.map.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UndoBoothCommand extends BaseCommand {

    public UndoBoothCommand() {
        super("undobooth");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("eventsuite.undobooth")){
            return false;
        }
        EventCore.getInstance().getModuleRegistry().getModule(MapModule.class).undoBooth();
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
