package dev.imabad.mceventsuite.spigot.entities;

import com.mojang.authlib.GameProfile;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class EntityFakePlayer {

  private MinecraftServer nmsServer;
  private WorldServer nmsWorld;
  private GameProfile gameProfile;

  public EntityFakePlayer(MinecraftServer nmsServer, WorldServer nmsWorld,
      GameProfile gameProfile) {
    this.nmsServer = nmsServer;
    this.gameProfile = gameProfile;
    this.nmsWorld = nmsWorld;
  }

  public void create(Player p, Location location) {
    final EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile,
        new PlayerInteractManager(nmsWorld));
    npc.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
        location.getPitch());
    final PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

    connection.sendPacket(
        new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
    connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
    Bukkit.getScheduler().runTaskLater(EventSpigot.getInstance(), () -> connection.sendPacket(
        new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
            npc)), 200L);
  }


  public void setGameProfile(GameProfile profile) {
    this.gameProfile = profile;
  }
}
