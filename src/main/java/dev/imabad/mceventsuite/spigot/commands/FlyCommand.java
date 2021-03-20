package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.modules.player.PlayerModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FlyCommand extends BaseCommand {
    public FlyCommand() {
        super("fly");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        Player sender = (Player) commandSender;
        EventPlayer eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(sender.getUniqueId()).get();

        if (!eventPlayer.hasPermission("eventsuite.fly") &&
                !eventPlayer.getRank().getName().equals("Special Guest")) {
            return false;
        }

        if (!PlayerModule.playerCanFly(sender)) {
            return false;
        }

        if (sender.isFlying()) {
            sender.setFlying(false);
            sender.setAllowFlight(false);
        } else {
            if (PlayerModule.playerCanFly(sender)) {
                sender.setAllowFlight(true);
                sender.setFlying(true);
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
