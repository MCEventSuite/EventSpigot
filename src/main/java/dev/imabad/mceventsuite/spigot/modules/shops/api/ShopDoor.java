package dev.imabad.mceventsuite.spigot.modules.shops.api;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.LocationHelper;
import dev.imabad.mceventsuite.spigot.utils.SoundHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

import java.util.List;

public class ShopDoor {

  private DoorType doorType;
  private final Material DOOR_CLOSED;
  private final Location TOP_LEFT;
  private final Location BOTTOM_RIGHT;

  public ShopDoor(Location topLeft, Location topRight, Material DOOR_CLOSED, DoorType doorType) {
    this.TOP_LEFT = topLeft;
    this.BOTTOM_RIGHT = topRight;
    this.doorType = doorType;
    this.DOOR_CLOSED = DOOR_CLOSED;
  }

  public void openDoor() {
      switch (doorType) {
        case UP_AND_DOWN:
          final int[] Y = {(int) BOTTOM_RIGHT.getY()};
          Location tl = TOP_LEFT;
          Location br = BOTTOM_RIGHT;
          for (int i = 0; i < 3; i++) {
            EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () -> {
              tl.setY(Y[0]);
              br.setY(Y[0]);
              Y[0]++;
              List<Block> blocks = LocationHelper.blocksFromTwoPoints(tl, br);
              SoundHelper.playSoundAtLocation(blocks.get(1).getLocation(), Sound.BLOCK_ANVIL_BREAK);
              for (Block block : blocks) {
                block.setType(Material.AIR);
              }
            }, 20 + (i * 20));
          }
          break;
      }
  }

  public void closeDoor() {
      switch (doorType) {
        case UP_AND_DOWN:
          final int[] Y = {(int) TOP_LEFT.getY()};
          Location tl = TOP_LEFT;
          Location br = BOTTOM_RIGHT;
          for (int i = 0; i < 3; i++) {

            EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () -> {
              tl.setY(Y[0]);
              br.setY(Y[0]);
              Y[0]--;
              List<Block> blocks = LocationHelper.blocksFromTwoPoints(tl, br);
              SoundHelper.playSoundAtLocation(blocks.get(1).getLocation(), Sound.BLOCK_ANVIL_BREAK);
              for (Block block : blocks) {
                block.setType(DOOR_CLOSED);
              }
            }, 20 + (i * 20));
          }
          break;
      }
  }


  public enum DoorType {
    UP_AND_DOWN
  }

}
