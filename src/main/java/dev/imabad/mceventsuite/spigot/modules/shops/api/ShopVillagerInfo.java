package dev.imabad.mceventsuite.spigot.modules.shops.api;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.npc.NPC;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCModule;
import dev.imabad.mceventsuite.spigot.modules.shops.sponsor.SponsorShackInteraction;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.StarblocksInteraction;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class ShopVillagerInfo
{
    private IShop shop;
    private String name;
    private String displayName;
    private Location spawnLocation;
    private Location moveToLocation;
    private NPC shopNPC;

    public ShopVillagerInfo(IShop shop, String name, String displayName, Location spawnLocation, Location moveToLocation)
    {
        this.shop = shop;
        this.name = name;
        this.displayName = displayName;
        this.spawnLocation = spawnLocation;
        this.moveToLocation = moveToLocation;
        spawn();
    }

    public ShopVillagerInfo(IShop shop, String name, String displayName, Location spawnLocation)
    {
        this.shop = shop;
        this.name = name;
        this.displayName = displayName;
        this.spawnLocation = spawnLocation;
        this.moveToLocation = null;
        spawn();
    }

    public void spawn() {
        if(shop.getName().equalsIgnoreCase("DK Snacks")) {
            this.shopNPC = EventCore.getInstance().getModuleRegistry().getModule(NPCModule.class).getNpcManager().createNpc(
                    StringUtils.colorizeMessage(displayName),
                    EntityType.PLAYER,
                    new SponsorShackInteraction(shop, this),
                    spawnLocation
            );
        }else{
            this.shopNPC = EventCore.getInstance().getModuleRegistry().getModule(NPCModule.class).getNpcManager().createNpc(
                    StringUtils.colorizeMessage(displayName),
                    EntityType.VILLAGER,
                    new StarblocksInteraction(shop, this),
                    spawnLocation
            );
        }
    }

    public void close(){

    }

    public String getName() {
        return name;
    }

    public NPC getShopNPC() {
        return shopNPC;
    }

    private void gotoMoveLocation() {
        //shopNPC.getNavigator().setTarget(moveToLocation);
    }

    private void moveToSpawnLocation() {
        //shopNPC.getNavigator().setTarget(spawnLocation.clone().add(0, 0, 1));
    }

    private void setBusy(boolean busy){
       /* StarblocksInteraction starblocksTrait = this.getShopNPC().getTraitNullable(StarblocksInteraction.class);
        if(starblocksTrait == null){
            return;
        }
        starblocksTrait.setBusy(busy);
        LookClose lookClose = shopNPC.getOrAddTrait(LookClose.class);
        if(starblocksTrait.isBusy()){
            lookClose.lookClose(false);
        } else {
            lookClose.lookClose(true);
        }*/
    }

    public void doMoving(Runnable task) {
        /*setBusy(true);
        gotoMoveLocation();


        EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () ->
        {
            this.moveToSpawnLocation();
            EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () ->
            {
                task.run();
                setBusy(false);
            }, 20 * 2);
        }, 20 * 5);*/
    }

    public boolean isBusy() {
        /*StarblocksInteraction starblocksTrait = this.getShopNPC().getTraitNullable(StarblocksInteraction.class);
        if(starblocksTrait == null){
            return false;
        }
        return starblocksTrait.isBusy();*/
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setSkin(String value, String signature){
        this.shopNPC.setSkin(value, signature);
    }
}
