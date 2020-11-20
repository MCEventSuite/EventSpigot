package dev.imabad.mceventsuite.spigot.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
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

}
