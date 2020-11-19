package dev.imabad.mceventsuite.spigot.modules.shops.squareways;

import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.shops.api.*;
import dev.imabad.mceventsuite.spigot.modules.shops.squareways.products.SandwichProduct;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.products.CoffeeProduct;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SquarewayShop implements IMovingVillagerShop {

    private World world;
    private ShopArea area;
    private ShopState shopState;
    private ShopVillagerInfo sally;

    public SquarewayShop(ShopsModule module){
        world = module.getMainWorld();
        area = new ShopArea(new Location(world, 881, 70, 553), new Location(world, 895, 75, 549));
        shopState = ShopState.CLOSE;
    }

    @Override
    public ShopArea getShopArea() {
        return area;
    }

    @Override
    public String getName() {
        return "SquareWay";
    }

    @Override
    public ShopState getShopState() {
        return shopState;
    }

    @Override
    public boolean setShopState(ShopState enumShopState) {
        if (this.shopState == enumShopState) {
            return false;
        } else {
            this.shopState = enumShopState;
            return true;
        }
    }

    @Override
    public ShopDoor getDoor() {
        return null;
    }

    @Override
    public void registerEntities() {
        Location spawnLocation = new Location(world, 882.5, 71, 551.5, 180, 0);
        Location moveToBlock = new Location(world,  894.5, 71, 552.5, 180, 0);
        this.sally = new ShopVillagerInfo(this, "Nigel", "&3&lNigel", spawnLocation, moveToBlock);
        this.sally.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYwNTgxOTU1ODQ3MSwKICAicHJvZmlsZUlkIiA6ICJhNzFjNTQ5MmQwNTE0ZDg3OGFiOTEwZmRmZmRmYzgyZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcHBsZTU0NDciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQxMjk1NmFiMmQxMzlmM2M2OWE3ODkyYjgyNTE2YTI2ODdmYzU3ZDNhMDdlYWJlZDA3MDZkOTI2MjcxNTkzZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "R9cUhhtOWk569JyZrkuao36cnvZjHUcOds6LdWtbb6D9L9+OBApatr2cnkaAYNgr2Anfk4rTkZSwAYpzl0lUeo48DXNkTsZwphs+ro1nI6RpPUMn555wNN0ZjSRkyY6UWbZM1DcuQ/hFXI3Afg5MWReXfRTIeF0C2EmuBbrgmRvK3OB3rTfIezEPubdt9zBUhJRsC0Vu3NmD35MxB70SnKziohRIeNnolvPNTHQumQ8dNz8HQG/EwyVAKRfLLZ2LNudq0niuoJpdLMZfp+yrrVNyYULILhn8CKTmOCOdQ1tuWe+S4kjsRksQOPCvVqWN+xZTCRRuLCgnkBUcu3KDoGoZvQChqP2sYFu912NW6xIIErxOumk75idvokd4SIFkFnWWAHoNLVB+1pxhMhR13TYkoiBeOg6o+Z5fMTmczQXb1/y5lK5bcK5MAqncSehEyJXXcZuNrd2IFmeGUQw9BD0/Susq4nmJgPpF2QgMqjJWk8ZP+d51xACKlMxfh9fe0ZAJNQEb050m10lYm8KOZ4h5BjwSFbX04oBpIMsDJYyMaVT/YH3SwFSZCWRNU63cdIb35PwyRc5HpbJgIfD6zO+z7usNC3DvF+sMmgVjVERASSeiUG9j6gkbVPTcPmNFDV+6wTLryWvuctK4jzmW3+MjOe1jch387ExT6KY6CNc=");
    }

    @Override
    public void removeEntities() {
        if(this.sally != null){
            this.sally.close();
        }
    }

    @Override
    public void onPlayerEnter(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<IProduct> getProducts() {
        SandwichProduct blt = new SandwichProduct("&r&lBLT", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ5NjU4OWZiNWMxZjY5Mzg3YjdmYjE3ZDkyMzEyMDU4ZmY2ZThlYmViM2ViODllNGY3M2U3ODE5NjExM2IifX19",  this);
        SandwichProduct hamAndCheese = new SandwichProduct("&r&lHam & Cheese", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGEyMTJmNzBmYjY2NTQ4ZmRmMmFiY2RiY2U4ZjMxNzA0MzRiODk5NWUyMWQxNGY5YjFmZjE0OTdjNjQ4ZCJ9fX0=", this);
        SandwichProduct totallyCheesy = new SandwichProduct("&r&lTotally Cheesy", 4, "eeyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVmYWZkODk3MmI2Yjc2OTBmYjEzMWRjM2Y5MTdjNTU5OTkzOGY4N2I1ODRjMmY1ZTdkNDBhMGRlNDFlNTJmIn19fQ==", this);
        SandwichProduct justBread = new SandwichProduct("&r&lBread", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhlZGEzMTEyODM0NjI3NzQ5NDlkODM5MzA4OGZiNWQ1OGFkZjczZmVlZDQ5ZjBiY2NiOTYxZWIxMzExOSJ9fX0=", this);
        return Arrays.asList(blt, hamAndCheese, totallyCheesy, justBread);
    }
}
