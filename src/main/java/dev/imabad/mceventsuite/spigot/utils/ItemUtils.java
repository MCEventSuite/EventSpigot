package dev.imabad.mceventsuite.spigot.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.*;

public class ItemUtils {

    public static void givePlayerItems(Player player, ItemStack[] itemStacks) {
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] == null) {
                continue;
            }
            player.getInventory().setItem(i, itemStacks[i]);
        }
    }

    public static ItemStack createItemStack(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    public static ItemStack createItemStack(Material material) {
        return createItemStack(material, 1);
    }

    public static ItemStack createItemStack(Material material, String name) {
        ItemStack item = createItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.colorizeMessage(name));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createItemStack(Material material, String name, int amount) {
        ItemStack item = createItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material material, String name, byte durability) {
        ItemStack itemStack = createItemStack(material, name);
        ItemMeta meta = itemStack.getItemMeta();
        if(meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(durability);
        }else {
            throw new UnsupportedOperationException("not a damageble type");
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, String name, List<String> lore) {
        ItemStack itemStack = createItemStack(material, name);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, int amount, List<String> lore) {
        ItemStack itemStack = createItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, String name, int amount, List<String> lore) {
        ItemStack itemStack = createItemStack(material, name, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, String name, String... lore) {
        List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        return createItemStack(material, name, loreList);
    }

    public static ItemStack createItemStack(Material material, String name, int amount, String... lore) {
        List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        return createItemStack(material, name, amount, loreList);
    }


    public static ItemStack createPotion(List<PotionEffect> type) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        type.forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, false));
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }


    public static ItemStack getSkullPlayer(Player player, String name, List<String> lore) {
        ItemStack headItemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) headItemStack.getItemMeta();
        headMeta.setOwningPlayer(player);
        headItemStack.setItemMeta(headMeta);
        return headItemStack;
    }

    public static ItemStack getSkull(String url, String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skullMeta.setDisplayName(StringUtils.colorizeMessage(name));
        skull.setItemMeta(skullMeta);
        return skull;
    }


}
