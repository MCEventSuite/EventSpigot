package dev.imabad.mceventsuite.spigot.modules.shops.api;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.entities.VillagerNPC;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.StarblocksTrait;
import dev.imabad.mceventsuite.spigot.utils.EntityRegistry;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

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

    public void spawn(){
        if(EventSpigot.getInstance().getServer().getPluginManager().isPluginEnabled("Citizens")){
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, StringUtils.colorizeMessage(displayName));
            npc.addTrait(new StarblocksTrait(shop, this));
            LookClose lookClose = npc.getOrAddTrait(LookClose.class);
            lookClose.lookClose(true);
            this.shopNPC = npc;
            npc.spawn(spawnLocation);
        }
    }

    public void close(){
        System.out.println("Cleaning up NPC");
        shopNPC.despawn();
        shopNPC.destroy();
        CitizensAPI.getNPCRegistry().deregister(shopNPC);
    }

    public String getName() {
        return name;
    }

    public NPC getShopNPC() {
        return shopNPC;
    }

    private void gotoMoveLocation() {
        shopNPC.getNavigator().setTarget(moveToLocation);
    }

    private void moveToSpawnLocation() {
        shopNPC.getNavigator().setTarget(spawnLocation.clone().add(0, 0, 1));
    }

    private void setBusy(boolean busy){
        StarblocksTrait starblocksTrait = this.getShopNPC().getTraitNullable(StarblocksTrait.class);
        if(starblocksTrait == null){
            return;
        }
        starblocksTrait.setBusy(busy);
        LookClose lookClose = shopNPC.getOrAddTrait(LookClose.class);
        if(starblocksTrait.isBusy()){
            lookClose.lookClose(false);
        } else {
            lookClose.lookClose(true);
        }
    }

    public void doMoving(Runnable task) {
        setBusy(true);
        gotoMoveLocation();


        EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () ->
        {
            this.moveToSpawnLocation();
            EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () ->
            {
                task.run();
                setBusy(false);
            }, 20 * 2);
        }, 20 * 5);
    }

    public boolean isBusy() {
        StarblocksTrait starblocksTrait = this.getShopNPC().getTraitNullable(StarblocksTrait.class);
        if(starblocksTrait == null){
            return false;
        }
        return starblocksTrait.isBusy();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setSkin(String value, String signature){
        SkinTrait trait = this.shopNPC.getTrait(SkinTrait.class);
        if(trait == null){
            trait = new SkinTrait();
            this.shopNPC.addTrait(trait);
        }
        trait.setSkinPersistent(name, signature, value);
    }
}
