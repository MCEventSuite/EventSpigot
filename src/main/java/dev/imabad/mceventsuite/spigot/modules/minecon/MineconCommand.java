package dev.imabad.mceventsuite.spigot.modules.minecon;

import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MineconCommand extends BaseCommand {
    private MineconModule module;

    public MineconCommand(MineconModule module) {
        super("mse");
        this.module = module;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(module.getWorld() == null || module.getSpawn() == null) {
                sender.sendMessage(ChatColor.RED + "The world is not yet loaded! Please try again later.");
                return true;
            }

            player.teleport(module.getSpawn());
            sender.sendMessage(ChatColor.GREEN + "Sending you " + ChatColor.ITALIC + "back in time...");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
