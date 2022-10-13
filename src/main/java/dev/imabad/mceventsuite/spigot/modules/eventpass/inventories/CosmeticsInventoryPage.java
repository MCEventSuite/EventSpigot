package dev.imabad.mceventsuite.spigot.modules.eventpass.inventories;

import com.cubedcon.cosmetics.CosmeticItemCategory;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

public class CosmeticsInventoryPage extends EventInventory {

    private static final ItemStack BLANK = ItemUtils.createItemStack(Material.BLUE_STAINED_GLASS_PANE, "&c");
    private static final ItemStack HEADS = ItemUtils.createItemStack(Material.ZOMBIE_HEAD, "&aHeads");
    private static final ItemStack CLOTHING = ItemUtils.createItemStack(Material.LEATHER_CHESTPLATE, "&aClothing");
    private static final ItemStack PARTICLE_EFFECTS = ItemUtils.createItemStack(Material.NETHER_STAR, "&aParticle Effects");
    private static final ItemStack PARTICLE_TRAILS = ItemUtils.createItemStack(Material.BLAZE_POWDER, "&aTrails");
    private static final ItemStack GADGETS = ItemUtils.createItemStack(Material.FIREWORK_ROCKET, "&aGadgets");
    private static final ItemStack BALLOONS = ItemUtils.createItemStack(Material.LEAD, "&aBalloons");
    private static final ItemStack INFO = ItemUtils.createItemStack(Material.PAPER, "&9Cubed! &eEvent Pass", Arrays.asList("&7Unlock cosmetics by earning XP around", "&7the event or by purchasing a VIP rank.", "&c\n", "&7For more info and to view the cosmetics,", "&7visit &apass.cubedcon.com"));

    public CosmeticsInventoryPage(Player player) {
        super(player, "Cosmetics", 54);
    }

    @Override
    protected void populate() {
        for(int i = 45; i < 53; i++){
            inventory.setItem(i, BLANK);
        }
        inventory.setItem(53, INFO);
        inventory.setItem(10, HEADS);
        inventory.setItem(13, CLOTHING);
        inventory.setItem(16, PARTICLE_EFFECTS);
        inventory.setItem(28, PARTICLE_TRAILS);
        inventory.setItem(31, GADGETS);
        inventory.setItem(34, BALLOONS);
    }

    @Override
    public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory, @Nullable ItemStack clickItem, InventoryType.SlotType slotType, ClickType clickType) {
        Player player = (Player) whoClicked;
        EventPlayer player1 = EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).get();
        if(slot == 10){
            new CosmeticCategoryInventoryPage(player, player1, "Head", Collections.singletonList(CosmeticItemCategory.HATS)).open(player, this);
        } else if (slot == 13){
            new CosmeticCategoryInventoryPage(player, player1, "Clothing", Arrays.asList(CosmeticItemCategory.BOOTS, CosmeticItemCategory.CHESTPLATES, CosmeticItemCategory.LEGGINGS)).open(player, this);
        } else if (slot == 16){
            new CosmeticCategoryInventoryPage(player, player1, "Particle Effect", Collections.singletonList(CosmeticItemCategory.PARTICLES)).open(player, this);
        } else if (slot == 28){
            new CosmeticCategoryInventoryPage(player, player1, "Trail", Collections.singletonList(CosmeticItemCategory.TRAILS)).open(player, this);
        } else if (slot == 31){
            new CosmeticCategoryInventoryPage(player, player1, "Gadget", Collections.singletonList(CosmeticItemCategory.GADGETS)).open(player, this);
        } else if (slot == 34){
            new CosmeticCategoryInventoryPage(player, player1, "Balloon", Collections.singletonList(CosmeticItemCategory.BALLOONS)).open(player, this);
        }
        return true;
    }
}