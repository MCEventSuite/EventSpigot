package dev.imabad.mceventsuite.spigot.modules.shops.api;

import dev.imabad.mceventsuite.spigot.utils.LocationHelper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ShopArea {

  private Location cornerOne;
  private Location cornerTwo;

  public ShopArea(Location cornerOne, Location cornerTwo){
    this.cornerOne = cornerOne;
    this.cornerTwo = cornerTwo;
  }

  public boolean isInArea(Player player){
    return isInArea(player.getLocation());
  }

  public boolean isInArea(Location location) {

    for (Block block : LocationHelper.blocksFromTwoPoints(cornerOne, cornerTwo)) {
      if (block.getLocation().equals(location.getBlock().getLocation())) {
        return true;
      }
    }
    return false;
  }


}
