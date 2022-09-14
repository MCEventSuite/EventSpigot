package dev.imabad.mceventsuite.spigot.modules.shops.starblocks;

import dev.imabad.mceventsuite.spigot.modules.npc.NPC;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCInteraction;
import dev.imabad.mceventsuite.spigot.modules.shops.api.IShop;
import dev.imabad.mceventsuite.spigot.modules.shops.api.ShopVillagerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class StarblocksInteraction implements NPCInteraction {

    private IShop shop;
    private ShopVillagerInfo shopVillagerInfo;

    public StarblocksInteraction(IShop shop, ShopVillagerInfo info) {
        //super("starblocks");
        this.shop = shop;
        this.shopVillagerInfo = info;
    }

    boolean isBusy = false;

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public boolean isBusy() {
        return isBusy;
    }

    @Override
    public void interact(Player player, NPC npc) {
        this.shop.openInventory(player, this.shopVillagerInfo);
    }
}
