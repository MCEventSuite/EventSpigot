package dev.imabad.mceventsuite.spigot.modules.eventpass.inventories;

import com.cubedcon.cosmetics.CosmeticItem;
import com.cubedcon.cosmetics.CosmeticItemCategory;
import com.cubedcon.cosmetics.CosmeticItemType;
import com.cubedcon.cosmetics.managers.CosmeticManager;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassDAO;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassReward;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassUnlockedReward;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.modules.eventpass.EventPassSpigotModule;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CosmeticCategoryInventoryPage extends EventInventory {

    private String category;
    private List<EventPassReward> rewardList;

    private static final NamespacedKey COSMETIC_ID = new NamespacedKey(EventSpigot.getInstance(), "cosmetic-id");
    private static final ItemStack LEFT_ARROW = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNzg0NTFiZjI2Y2Y0OWZkNWY1NGNkOGYyYjM3Y2QyNWM5MmU1Y2E3NjI5OGIzNjM0Y2I1NDFlOWFkODkifX19", "&cBack");
    private static final ItemStack BLANK = ItemUtils.createItemStack(Material.BLUE_STAINED_GLASS_PANE, "&c");
    private static final ItemStack CLEAR = ItemUtils.createItemStack(Material.BARRIER, "&cRemove");
    private static final ItemStack INFO = ItemUtils.createItemStack(Material.PAPER, "&9Cubed! &eEvent Pass", Arrays.asList("&7Unlock cosmetics by earning XP around", "&7the event or by purchasing a VIP rank.", "&c\n", "&7For more info and to view the cosmetics,", "&7visit &apass.cubedcon.com"));;
    private static final ItemStack LOCKED = ItemUtils.createItemStack(Material.GHAST_TEAR, "&a???");

    private EventPlayer eventPlayer;
    private EventPassPlayer eventPassPlayer;
    private List<EventPassUnlockedReward> unlockedRewards;
    private List<CosmeticItemCategory> cosmeticItemCategory;

    public CosmeticCategoryInventoryPage(Player player, EventPlayer eventPlayer, String category, List<CosmeticItemCategory> ciC) {
        super(player, "Cosmetics - " + category, 54);
        this.category = category;
        this.rewardList = EventCore.getInstance().getModuleRegistry().getModule(EventPassSpigotModule.class).getRewards().stream().filter(eventPassReward -> eventPassReward.getDescription().equalsIgnoreCase(category)).sorted(Comparator.comparingInt(EventPassReward::getRequiredLevel)).collect(Collectors.toList());
        this.eventPlayer = eventPlayer;
        this.eventPassPlayer = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(
                EventPassDAO.class).getOrCreateEventPass(eventPlayer);
        this.unlockedRewards = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(
                EventPassDAO.class).getUnlockedRewards(eventPlayer);
        this.cosmeticItemCategory = ciC;
    }

    public boolean hasUnlocked(EventPassReward reward) {
        return unlockedRewards.stream().anyMatch(reward1 -> reward1.getUnlockedReward().getId().equals(reward.getId()));
    }

    @Override
    protected void populate() {
        for(int i = 46; i < 53; i++){
            inventory.setItem(i, BLANK);
        }
        inventory.setItem(45, LEFT_ARROW);
        inventory.setItem(49, CLEAR);
        inventory.setItem(53, INFO);
        int rows = (int) Math.ceil(rewardList.size() / 7f);
        for(int i = 0; i < rows; i++){
            int rowStart = (category.equals("Clothing") ? 1 : 10) + (i * 9);
            for(int x = 0; x < 7; x++){
                int rewardIndex = (i*7) + x;
                if(rewardIndex >= rewardList.size()){
                    break;
                }
                EventPassReward eventPassReward = rewardList.get(rewardIndex);
                if(hasUnlocked(eventPassReward)){
                    CosmeticItemType cosmeticItemType = CosmeticItemType.valueOf(eventPassReward.getId());
                    ItemStack icon = cosmeticItemType.getIcon().clone();
                    ItemMeta meta = icon.getItemMeta();
                    meta.getPersistentDataContainer().set(COSMETIC_ID, PersistentDataType.STRING, eventPassReward.getId());
                    icon.setItemMeta(meta);
                    inventory.setItem(rowStart + x, icon);
                } else {
                    inventory.setItem(rowStart + x, LOCKED);
                }
            }
        }
    }

    @Override
    public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType) {
        Player player = (Player) whoClicked;
        EventPlayer eventPlayer = EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).get();
        if(slot == 45){
            backInventory.open(player, null);
        } else if(slot == 49) {
            cosmeticItemCategory.forEach(cosmeticItemCategory -> CosmeticManager.getInstance().getCurrentCosmeticItemByCategory(player.getUniqueId(), cosmeticItemCategory).ifPresent(cosmeticItem -> CosmeticManager.getInstance().removeCosmeticItem(cosmeticItem)));
        } else {
            if(clickItem != null && clickItem.hasItemMeta() && clickItem.getItemMeta().getPersistentDataContainer().has(COSMETIC_ID, PersistentDataType.STRING)){
                String id = clickItem.getItemMeta().getPersistentDataContainer().get(COSMETIC_ID, PersistentDataType.STRING);
                CosmeticItemType cosmeticItemType = CosmeticItemType.valueOf(id);
                CosmeticManager.getInstance().addCosmeticItem(player, cosmeticItemType);
                player.closeInventory();
            }
        }
        return true;
    }
}