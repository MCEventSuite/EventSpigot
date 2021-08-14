package dev.imabad.mceventsuite.spigot.commands;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TpacceptCommand extends BaseCommand {
    public TpacceptCommand() {
        super("tpaccept");
        registerFlag();
    }

    private static StateFlag allowTeleportFlag;

    private void registerFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("allow-teleport", true);
            registry.register(flag);
            allowTeleportFlag = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("allow-teleport");
            if (existing instanceof StateFlag) {
                allowTeleportFlag = (StateFlag) existing;
            } else {
            }
        }
    }

    private boolean playerCanTeleportTo(Player player, Player destination) {
        for (ProtectedRegion region : RegionUtils.getPlayerRegions(destination).getRegions()) {
            StateFlag.State flagState = region.getFlag(allowTeleportFlag);

            if (flagState != null && flagState.equals(StateFlag.State.DENY)) {
                if (!region.isMember(WorldGuardPlugin.inst().wrapPlayer(player))) {
                    return false;
                }
            }
        }

        return true;
    }

    private String getLatestRequest(String uuid) {
        HashMap<String, HashMap<String, Long>> requests = TpaCommand.getTeleportRequests();

        if (!requests.containsKey(uuid)) return null;
        HashMap<String, Long> playerRequests = requests.get(uuid);

        String latestRequest = null;
        long latestRequestTime = 0;

        for (String senderUuid : playerRequests.keySet()) {
            long requestTime = playerRequests.get(senderUuid);
            if (requestTime > latestRequestTime) {
                long age = System.currentTimeMillis() - requestTime;
                boolean expired = age > (30 * 1000);

                if (!expired) {
                    latestRequest = senderUuid;
                    latestRequestTime = requestTime;
                }
            }
        }

        return latestRequest;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player sender = (Player) commandSender;

        if(args.length > 1){
            sender.sendMessage(ChatColor.RED + "Incorrect Usage: /tpaccept [player]");
            return false;
        }

        Player subject;

        if (args.length == 1) {
            subject = Bukkit.getPlayer(args[0]);
        } else {
            String latestRequest = getLatestRequest(sender.getUniqueId().toString());

            if (latestRequest == null) {
                sender.sendMessage(ChatColor.RED + "Nobody has requested to teleport to you.");
                return false;
            }

            subject = Bukkit.getPlayer(UUID.fromString(latestRequest));
        }

        if (subject == null) {
            sender.sendMessage(ChatColor.RED + "Player isn't on this server.");
            return false;
        }

        HashMap<String, Long> requests = TpaCommand.getTeleportRequests().get(sender.getUniqueId().toString());

        if (requests == null || !requests.containsKey(subject.getUniqueId().toString())
            || (System.currentTimeMillis() - requests.get(subject.getUniqueId().toString()) > (1000 * 30))) {
            sender.sendMessage(ChatColor.RED + "This player hasn't sent you a request");
            return false;
        }

        if (!playerCanTeleportTo(subject, sender)) {
            sender.sendMessage(ChatColor.RED + "Teleportation is disabled in this area.");
            return false;
        }

        subject.teleport(sender, PlayerTeleportEvent.TeleportCause.PLUGIN);
        requests.remove(subject.getUniqueId().toString());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
