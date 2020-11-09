package dev.imabad.mceventsuite.spigot.modules.shops.api;

import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public interface ISkullProduct extends IProduct
{
    String getTextureID();

    @Override
    default ItemStack getItemStack() {
        return ItemUtils.getSkull(getTextureID(), getDisplayName(), getLore());
    }
}
