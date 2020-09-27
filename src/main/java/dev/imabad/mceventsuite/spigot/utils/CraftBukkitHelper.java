package dev.imabad.mceventsuite.spigot.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftBukkitHelper {

    private static final String PACKAGE_VERSION;

    static {
        Class<?> server = Bukkit.getServer().getClass();
        Matcher matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$").matcher(server.getName());
        if(matcher.matches()){
            PACKAGE_VERSION = matcher.group(1);
        } else {
            PACKAGE_VERSION = "";
        }
    }

    public static String getPackageVersion() {
        return PACKAGE_VERSION;
    }
}
