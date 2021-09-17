package dev.imabad.mceventsuite.spigot.modules.daylight;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class DaylightInventory extends EventInventory {

    private static final ItemStack SERVER_TIME = ItemUtils.createItemStack(Material.YELLOW_CONCRETE, "&eServer Time");
    private static final ItemStack ALWAYS_DAY = ItemUtils.createItemStack(Material.BLUE_CONCRETE, "&bAlways Day");
    private static final ItemStack ALWAYS_NIGHT = ItemUtils.createItemStack(Material.BLACK_CONCRETE, "&fAlways Night");
    private static final ItemStack YOUR_TIME = ItemUtils.createItemStack(Material.GREEN_CONCRETE, "&2Your Time");

    public DaylightInventory(Player player) {
        super(player, "Player Daylight", 9);
    }

    @Override
    protected void populate() {
        TimeType timeType = EventCore.getInstance().getModuleRegistry().getModule(DaylightModule.class).playerTime.get(getPlayer().getUniqueId());
        if(timeType == TimeType.UTC) {
            inventory.setItem(1, highlight(SERVER_TIME));
        } else {
            inventory.setItem(1, SERVER_TIME);
        }
        if(timeType == TimeType.ALWAYS_DAY) {
            inventory.setItem(3, highlight(ALWAYS_DAY));
        } else {
            inventory.setItem(3, ALWAYS_DAY);
        }
        if(timeType == TimeType.ALWAYS_NIGHT){
            inventory.setItem(5, highlight(ALWAYS_NIGHT));
        } else {
            inventory.setItem(5, ALWAYS_NIGHT);
        }
        if(timeType == TimeType.LOCAL) {
            inventory.setItem(7, highlight(YOUR_TIME));
        } else {
            inventory.setItem(7, YOUR_TIME);
        }
    }

    public ItemStack highlight(ItemStack itemStack){
        itemStack = itemStack.clone();
        itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        itemStack.lore(Collections.singletonList(Component.text("Selected").color(NamedTextColor.GREEN)));
        return itemStack;
    }

    @Override
    public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType) {
        TimeType timeType = null;
        if(slot == 1){
            timeType = TimeType.UTC;
        } else if(slot == 3) {
            timeType = TimeType.ALWAYS_DAY;
        } else if(slot == 5) {
            timeType = TimeType.ALWAYS_NIGHT;
        } else if(slot == 7) {
            timeType = TimeType.LOCAL;
        }
        if(timeType != null){
            TimeType currenType = EventCore.getInstance().getModuleRegistry().getModule(DaylightModule.class).playerTime.get(getPlayer().getUniqueId());
            if(currenType != timeType){
                EventCore.getInstance().getModuleRegistry().getModule(DaylightModule.class).setPlayerTime(getPlayer(), timeType);
                return true;
            }
        }
        return false;
    }
}
