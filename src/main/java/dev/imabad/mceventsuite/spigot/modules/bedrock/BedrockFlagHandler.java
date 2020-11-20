package dev.imabad.mceventsuite.spigot.modules.bedrock;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.BukkitWorldGuardPlatform;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.EntryFlag;
import com.sk89q.worldguard.session.handler.Handler;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.FloodgateAPI;

import java.util.Set;
import java.util.stream.Collectors;

public class BedrockFlagHandler extends Handler {

    public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<BedrockFlagHandler> {
        @Override
        public BedrockFlagHandler create(Session session) {
            return new BedrockFlagHandler(session);
        }
    }
    private static final long MESSAGE_THRESHOLD = 1000 * 2;
    private long lastMessage;

    public BedrockFlagHandler(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {

        if(BedrockModule.getBedrockAllowed() == null){
            System.out.println("Don't bother checking Bedrock as the flag is null");
            return super.onCrossBoundary(player, from, to, toSet, entered, exited, moveType);
        }
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        if(CitizensAPI.getNPCRegistry().isNPC(bukkitPlayer)){
            return true;
        }
        boolean allowed = toSet.testState(player, BedrockModule.getBedrockAllowed());
        if(!allowed && moveType.isCancellable() && FloodgateAPI.isBedrockPlayer(bukkitPlayer)){
            String message = toSet.queryValue(player, Flags.ENTRY_DENY_MESSAGE);
            long now = System.currentTimeMillis();

            if ((now - lastMessage) > MESSAGE_THRESHOLD && message != null && !message.isEmpty()) {
                player.printRaw(CommandUtils.replaceColorMacros(message));
                lastMessage = now;
            }
            return false;
        } else {
            return true;
        }
    }
}
