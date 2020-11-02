package dev.imabad.mceventsuite.spigot.modules.booths.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SchemBoothsCommand extends BaseCommand {

    public SchemBoothsCommand() {
        super("schembooths");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(label.equalsIgnoreCase("schemBooths") && sender.hasPermission("eventsuite.schemBooths")) {
            EventCore.getInstance().getModuleRegistry().getModule(BoothModule.class).schematicBooths(sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
