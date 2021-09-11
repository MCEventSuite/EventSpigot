package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.kyori.adventure.audience.Audience;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class BaseCommand extends BukkitCommand implements TabCompleter {

    private String permission;

    public BaseCommand(String name) {
        super(name);
    }

    public BaseCommand(String name, String permission){
        this(name);
        this.permission = permission;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(permission.length() > 0 && !sender.hasPermission(permission)){
            sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
            return false;
        }
        return true;
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

    public static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<String> originals, final T collection) {
        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }
        return collection;
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public Audience audience(CommandSender sender){
        return EventSpigot.getInstance().getAudiences().sender(sender);
    }
}
