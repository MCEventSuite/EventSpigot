package dev.imabad.mceventsuite.spigot.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RegionUtils {

    public static ApplicableRegionSet getPlayerRegions(Player player){
        WorldGuardPlatform plat = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform();
        World world = plat.getMatcher().getWorldByName(player.getWorld().getName());
        RegionManager manager = plat.getRegionContainer().get(world);
        Vector v = player.getLocation().toVector();
        return manager.getApplicableRegions(BlockVector3.at(v.getX(),v.getY(), v.getZ()));
    }

    public static boolean isInRegion(Player player, String regionName){
        return getPlayerRegions(player).getRegions().stream().anyMatch(protectedRegion -> protectedRegion.getId().equalsIgnoreCase(regionName));
    }

    public static boolean isInFlag(Player player, Flag flag){
        return getPlayerRegions(player).getRegions().stream().anyMatch(protectedRegion -> protectedRegion.getFlag(flag) != null);
    }

    public static <T extends Flag<?>> T getOrRegisterFlag(T flag) {
        try{
            WorldGuard.getInstance().getFlagRegistry().register(flag);
            return flag;
        } catch(FlagConflictException e){
            Flag<?> existing = WorldGuard.getInstance().getFlagRegistry().get(flag.getName());
            if(flag.getClass().isInstance(existing)){
                return (T) existing;
            }
            throw new RuntimeException("Something went wrong whilst registering region flags");
        }
    }
}
