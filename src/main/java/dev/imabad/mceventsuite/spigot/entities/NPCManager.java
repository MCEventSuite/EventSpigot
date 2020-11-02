package dev.imabad.mceventsuite.spigot.entities;

import net.minecraft.server.v1_16_R2.MinecraftServer;
import net.minecraft.server.v1_16_R2.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;

public class NPCManager {

  private static MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
  private static WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
  private static EntityFakePlayer mallSecurity = new EntityFakePlayer(nmsServer, nmsWorld,
      ProfileManager.getMallSecurity());

  public static EntityFakePlayer getMallSecurity() {
    return mallSecurity;
  }

}
