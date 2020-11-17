package dev.imabad.mceventsuite.spigot.modules.shops.squareways;

import dev.imabad.mceventsuite.spigot.modules.shops.api.IShop;
import dev.imabad.mceventsuite.spigot.modules.shops.api.ShopVillagerInfo;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class SquareWayTrait extends Trait {

    private IShop shop;
    private ShopVillagerInfo shopVillagerInfo;

    public SquareWayTrait(IShop shop, ShopVillagerInfo info) {
        super("squareway");
        this.shop = shop;
        this.shopVillagerInfo = info;
    }

    boolean isBusy = false;

    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        if(event.getNPC() == this.getNPC()){
            if(!isBusy){
                this.shop.openInventory(event.getClicker(), this.shopVillagerInfo);
            }
        }
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public boolean isBusy() {
        return isBusy;
    }

}
