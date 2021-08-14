package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnCommand extends BaseCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (BoothModule.BOOTH_WORLDS.contains(player.getWorld().getName())) {
            player.teleport(player.getWorld().getSpawnLocation());
        } else {
            player.teleport(player.getWorld().getName().equals("creative")
                    ? new Location(player.getWorld(), 0.5, 52, -1.5, 0, 0)
                    : new Location(player.getWorld(), -2, 51, 0.5, 270, 0));
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
