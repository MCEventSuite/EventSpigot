package dev.imabad.mceventsuite.spigot.modules.shops.api;


import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

public interface IMovingVillagerShop extends IShop {

    @Override
    default void purchaseAction(ShopVillagerInfo villagerNPC, Player player, IProduct product) {
        TextComponent comingUp =
                LegacyComponentSerializer.legacy('&').deserialize(villagerNPC.getDisplayName()).append(Component.text(": One ").color(NamedTextColor.WHITE)).append(Component.text(product.getDisplayName()).decoration(TextDecoration.BOLD, true).color(NamedTextColor.RED)).append(Component.text(" coming right up!").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false));
        EventSpigot.getInstance().getAudiences().player(player).sendMessage(comingUp);
        Runnable runnable = () -> {
            ItemStack itemStack;
            if(FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())){
                itemStack = product.getBedrockItemStack();
            } else {
                itemStack = product.getItemStack();
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(product.getDisplayName());
            List<String> newLore = product.getLore();
            itemMeta.setLore(newLore);
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(2, itemStack);
            TextComponent comp =
                    LegacyComponentSerializer.legacy('&').deserialize(villagerNPC.getDisplayName()).append(Component.text(": ").color(NamedTextColor.WHITE)).append(Component.text(product.getDisplayName()).decoration(TextDecoration.BOLD, true).color(NamedTextColor.RED)).append(Component.text(" for ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)).append(Component.text(player.getName()).color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD));
            EventSpigot.getInstance().getAudiences().player(player).sendMessage(comp);
        };
        villagerNPC.doMoving(runnable);
    }

    @Override
    default void openInventory(Player player, ShopVillagerInfo shopNPC)
    {
        new ShopInventory(StringUtils.colorizeMessage(shopNPC.getName()), this, false, player).setVillagerNPC(shopNPC).open(player, null);
    }
}

