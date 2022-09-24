package dev.imabad.mceventsuite.spigot.modules.map.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import dev.imabad.mceventsuite.spigot.modules.map.objects.Tree;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class NewTreePlotCommand extends BaseCommand {

    private MapModule mapModule;

    public NewTreePlotCommand(MapModule mapModule) {
        super("createtree");
        this.mapModule = mapModule;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final Player player = (Player) sender;
        if(!player.hasPermission("eventsuite.ntp"))
            return false;

        int rotation = 0;
        if(args.length != 0) {
            rotation = Integer.parseInt(args[0]);
        }

        for(Pair<UUID, Integer> pair : this.mapModule.getTreeEditMode()) {
            if(pair.getKey() == player.getUniqueId()) {
                if(args.length == 0) {
                    this.mapModule.getTreeEditMode().remove(pair);
                    player.sendMessage(ChatColor.RED + "Left edit mode!");
                    return true;
                }

                pair.setValue(rotation);
                player.sendMessage(ChatColor.GREEN + "Set rotation to " + rotation);
                return true;
            }
        }

        this.mapModule.getTreeEditMode().add(MutablePair.of(player.getUniqueId(), rotation));
        player.sendMessage(ChatColor.GREEN + "Entered edit mode with rotation " + rotation);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
