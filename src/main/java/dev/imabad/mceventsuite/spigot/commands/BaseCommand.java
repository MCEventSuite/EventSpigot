package dev.imabad.mceventsuite.spigot.commands;

import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;

public abstract class BaseCommand extends BukkitCommand implements TabCompleter {

    public BaseCommand(String name) {
        super(name);
    }

    public String getLastArgs(String[] args, int start){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = start; i < args.length; i++){
            if(i != start){
                stringBuilder.append(" ");
            }
            stringBuilder.append(args[i]);
        }
        return stringBuilder.toString();
    }
}
