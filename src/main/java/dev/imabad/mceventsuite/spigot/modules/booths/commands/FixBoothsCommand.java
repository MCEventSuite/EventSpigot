package dev.imabad.mceventsuite.spigot.modules.booths.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class FixBoothsCommand extends BukkitCommand {
    public FixBoothsCommand() {
        super("fixBooths");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(label.equalsIgnoreCase("fixBooths") && sender.hasPermission("eventsuite.fix")){
            EventCore.getInstance().getModuleRegistry().getModule(BoothModule.class).fix(sender);
        }
        return false;
    }
}
