package dev.imabad.mceventsuite.spigot.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHelper
{
    public static void playSoundForPlayer(Player player, String soundName)
    {
      player.playSound(player.getLocation(), soundName, 1F, 0);
    }
    public static void playSoundAtPlayer(Player player, String soundName)
    {
      player.getWorld().playSound(player.getLocation(), soundName, 1F, 0);
    }
    public static void playSoundAtLocation(Location location, String soundName)
    {
      location.getWorld().playSound(location, soundName, 1F, 0);
    }

  public static void playSoundForPlayer(Player player, Sound sound)
  {
    player.playSound(player.getLocation(), sound, 1F, 0);
  }
  public static void playSoundAtPlayer(Player player, Sound sound)
  {
    player.getWorld().playSound(player.getLocation(), sound, 1F, 0);
  }
  public static void playSoundAtLocation(Location location, Sound sound)
  {
    location.getWorld().playSound(location, sound, 1F, 0);
  }
}
