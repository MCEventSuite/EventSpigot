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
        this.sally.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYwNTYyMTg5MzczMiwKICAicHJvZmlsZUlkIiA6ICJjNGU1MGFhOWZmYzI0ODkzOWI3NWQwZTRhMDljNTZjMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSX0thcmJhbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hNDEyOTU2YWIyZDEzOWYzYzY5YTc4OTJiODI1MTZhMjY4N2ZjNTdkM2EwN2VhYmVkMDcwNmQ5MjYyNzE1OTNmIgogICAgfQogIH0KfQ==", "J/Rgo8sSlVVBrLQNpfRuaCDRMNdcyvnv65bR077k1Xbc5oQEL1PhyL9SUFgmrcPTzeKLlp8EyepOveupGS5Ud8nVd75yedfNTMsarGNXlABEvBX7ZoPwkja9Vez5vaPob6gr+50a6G0QzyRR/hmpaOwkeIRCD027t+eyF7ofqeSv+jiQkeaaJdhK0pktTXJR70nln39RTB7eCTYDMtLUSUTcVdOvT9dPgoHkgPhDP3JX2UXzlHgdPYozvD1gADy/nMIgS8p4JTHrkGkiXIqFhuiKRnJn7+77zfzMz9nI6VKx7pDed6y6yaCv08WLf+9GzGox14oJHpcM9oVNMcJlT41xPFMUy0hmga1pa80gkeijdCw8YdNlQkY/pbnd9r6sU0//adfPRZQ7OafQLG/1uu3OmJjtj1FoTlvMV8M5komufNsnmW+rjGjxKgjtbF/8x1PV8Vea+F3G2Ep9cYkZSZQrhDDlyNOm2h06iDQWfKuWzaKVS5GdRknB4NaERKXmn4VdC6mBWuRfcGADdzTZzogabiXGTNLvGj/WJfNyYCjhmGxW3jLHxekjYfWRtux4+++m31Y8y59f32ZNJ3jnPJK/YAKjMkJEGT8hz/8X82HGbUuc9tbrjbak1mC17yYL8QOWQpfKBfBZJihz7PXnmiDDlG2JYmE0VdlzdMuzkzE=");
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
