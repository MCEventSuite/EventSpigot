package dev.imabad.mceventsuite.spigot.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ProfileLoader {

    private final String uuid;
    private final String name;
    private final String skinOwner;

    public ProfileLoader(String uuid, String name) {
        this(uuid, name, name);
    }

    public ProfileLoader(String uuid, String name, String skinOwner) {
        this.uuid = uuid == null ? null : uuid.replaceAll("-", ""); //We add these later
        String displayName = ChatColor.translateAlternateColorCodes('&', name);
        this.name = ChatColor.stripColor(displayName);
        this.skinOwner = skinOwner;
    }

    public GameProfile loadProfile() {
        UUID id = uuid == null ? parseUUID(getUUID(name)) : parseUUID(uuid);
        GameProfile profile = new GameProfile(id, name);
        addProperties(profile);
        return profile;
    }

    private void addProperties(GameProfile profile) {
        if (EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).existsData("properties-" + skinOwner)) {
            String json = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).getData("properties-" + skinOwner);
            for (Property prop :
                    getPropertyFromJSON(json)) {
                profile.getProperties().put(prop.getName(), prop);
            }
            return;
        }
        try {
            // Get the name from SwordPVP
            URL url = new URL("https://mcapi.de/api/user/" + skinOwner + "?unsigned=false");
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");

            // Parse it
            String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).storeData("properties-" + skinOwner, json);
            for (Property prop :
                    getPropertyFromJSON(json)) {
                profile.getProperties().put(prop.getName(), prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("deprecation")
    private String getUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId().toString().replaceAll("-", "");
    }

    private List<Property> getPropertyFromJSON(String json) {
        List<Property> propertyList = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONObject properties = (JSONObject) obj.get("properties");
            JSONArray raw = (JSONArray) properties.get("raw");
            for (int i = 0; i < raw.size(); i++) {
                try {
                    JSONObject property = (JSONObject) raw.get(i);
                    String name = (String) property.get("name");
                    String value = (String) property.get("value");
                    String signature =
                            property.containsKey("signature") ? (String) property.get("signature") : null;
                    if (signature != null) {
                        propertyList.add(new Property(name, value, signature));
                    } else {
                        propertyList.add(new Property(value, name));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertyList;
    }


    private UUID parseUUID(String uuidStr) {
        // Split uuid in to 5 components
        String[] uuidComponents = new String[]{uuidStr.substring(0, 8),
                uuidStr.substring(8, 12), uuidStr.substring(12, 16),
                uuidStr.substring(16, 20),
                uuidStr.substring(20, uuidStr.length())
        };

        // Combine components with a dash
        StringBuilder builder = new StringBuilder();
        for (String component : uuidComponents) {
            builder.append(component).append('-');
        }

        // Correct uuid length, remove last dash
        builder.setLength(builder.length() - 1);
        return UUID.fromString(builder.toString());
    }
}
