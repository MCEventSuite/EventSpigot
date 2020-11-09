package dev.imabad.mceventsuite.spigot.modules.map.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventBooth;
import dev.imabad.mceventsuite.core.api.objects.EventBoothPlot;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.warps.WarpModule;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SetBoothPosCommand extends BaseCommand {

    public SetBoothPosCommand() {
        super("sbs");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("eventsuite.sbs")){
            return false;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command");
            return false;
        }
        Player player = (Player) sender;
        if(args.length < 1){
            sender.sendMessage("Usage: /sbs <name>");
            return false;
        }
        String name = getLastArgs(args, 0);
        name = name.toUpperCase().replaceAll(" ", "_");
        final String finalName = name;
        Optional<EventBoothPlot> plotOptional = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).getPlots().stream().filter(eventBoothPlot -> eventBoothPlot.getBooth() != null && eventBoothPlot.getBooth().getName().toUpperCase().replaceAll(" ", "_").equalsIgnoreCase(finalName)).findFirst();
        if(!plotOptional.isPresent()){
            sender.sendMessage("No plot assigned to that booth.");
            return false;
        }
        EventBoothPlot eventBoothPlot = plotOptional.get();
        Location l = player.getLocation();
        eventBoothPlot.setFrontPos(l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getYaw() + "," + l.getPitch());
        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).saveBoothPlot(eventBoothPlot);
        sender.sendMessage("Updated booth location");
        if(EventCore.getInstance().getModuleRegistry().getModule(WarpModule.class).isEnabled()){
            EventCore.getInstance().getModuleRegistry().getModule(WarpModule.class).refresh();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
