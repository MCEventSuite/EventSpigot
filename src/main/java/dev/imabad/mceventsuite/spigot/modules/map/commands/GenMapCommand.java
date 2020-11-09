package dev.imabad.mceventsuite.spigot.modules.map.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GenMapCommand extends BaseCommand {
    public GenMapCommand() {
        super("genmap");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            int chunkRadiusX = Integer.parseInt(args[0]);
            int chunkRadiusZ = Integer.parseInt(args[1]);
            EventCore.getInstance().getModuleRegistry().getModule(MapModule.class).generateMap(player.getWorld().getName(), chunkRadiusX, chunkRadiusZ, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ()).whenComplete((aBoolean, throwable) -> {
                if(aBoolean){
                    player.sendMessage("Generated map and saved to folder");
                } else {
                    player.sendMessage("Something went wrong...");
                }
            });
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
