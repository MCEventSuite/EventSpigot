package dev.imabad.mceventsuite.spigot.entities;

import com.mojang.authlib.GameProfile;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.ProfileLoader;
import org.bukkit.Bukkit;

public class ProfileManager {

    private static ProfileLoader mallSecurity = new ProfileLoader(
            "c5a4d25c-a2d0-41c1-9c8a-054423c5f993", "Mall Security", "MallMC");
    private static GameProfile mallSecurityGameProfile;

    public static void loadProfiles() {
        Bukkit.getScheduler().runTaskAsynchronously(EventSpigot.getInstance(), () -> mallSecurityGameProfile = mallSecurity.loadProfile());
    }

    public static GameProfile getMallSecurity() {
        return mallSecurityGameProfile;
    }

}
