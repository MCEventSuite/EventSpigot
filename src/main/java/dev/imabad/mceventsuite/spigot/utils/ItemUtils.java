package dev.imabad.mceventsuite.spigot.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
        lore = lore.stream().map(StringUtils::colorizeMessage).collect(Collectors.toList());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, int amount, List<String> lore) {
        ItemStack itemStack = createItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        lore = lore.stream().map(StringUtils::colorizeMessage).collect(Collectors.toList());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, String name, int amount, List<String> lore) {
        ItemStack itemStack = createItemStack(material, name, amount);
        ItemMeta meta = itemStack.getItemMeta();
        lore = lore.stream().map(StringUtils::colorizeMessage).collect(Collectors.toList());
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

    public static ItemStack createBook(String name, List<String> lore, List<String> pages) {
        ItemStack bookItemStack = createItemStack(Material.WRITTEN_BOOK, name, lore);
        BookMeta bookMeta = (BookMeta) bookItemStack.getItemMeta();
        bookMeta.setTitle(StringUtils.colorizeMessage(name));
        bookMeta.setAuthor("CubedCon");
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
        pages = pages.stream().map(StringUtils::colorizeMessage).collect(Collectors.toList());
        bookMeta.setPages(pages);
        bookItemStack.setItemMeta(bookMeta);
        return bookItemStack;
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
    public static ItemStack getSkull(String url, String name, List<String> lore) {
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
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static boolean equalsItemName(ItemStack itemStack, String name){
        return ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equalsIgnoreCase(name);
    }

    public static boolean equalsItemTexture(ItemStack itemStack1, ItemStack itemStack2)
    {
        return getIconTextureString(itemStack1).equalsIgnoreCase(getIconTextureString(itemStack2));
    }

    private static String getIconTextureString(ItemStack itemStack)
    {
        net.minecraft.world.item.ItemStack nmsHead2 = CraftItemStack.asNMSCopy(itemStack);
        CompoundTag rootCompound = (nmsHead2.hasTag()) ? nmsHead2.getTag() : new CompoundTag();
        assert rootCompound != null;
        CompoundTag ownerCompound = rootCompound.getCompound("SkullOwner");
        CompoundTag iconProperties = ownerCompound.getCompound("Properties");
        ListTag iconTextures = iconProperties.getList("textures", 10);
        CompoundTag iconTexture = (CompoundTag) iconTextures.get(0);
        return iconTexture.getString("Value");
    }
}
