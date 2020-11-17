package dev.imabad.mceventsuite.spigot.modules.shops.starblocks;

import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.shops.api.*;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.products.CakeProduct;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.products.CoffeeProduct;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StarblocksShop implements IMovingVillagerShop {

    private World world;
    private ShopArea area;
    private ShopState shopState;
    private ShopVillagerInfo sally;

    public StarblocksShop(ShopsModule module){
        world = module.getMainWorld();
        area = new ShopArea(new Location(world, 846, 70, 519), new Location(world, 866, 76, 524));
        shopState = ShopState.CLOSE;
    }

    @Override
    public ShopArea getShopArea() {
        return area;
    }

    @Override
    public String getName() {
        return "StarBlocks";
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
        Location spawnLocation = new Location(world, 856.5, 71, 523.5, 0, 0);
        Location moveToBlock = new Location(world,  857.5, 71, 520.5, 180, 0);
        this.sally = new ShopVillagerInfo(this, "Sally", "&3&lSally", spawnLocation, moveToBlock);
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
        CoffeeProduct americano = new CoffeeProduct("&r&lAmericano", 2, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFjZWRkNTdlYzFiM2FjMTQ1NDQ2MjZjYzZiNGJjZGJkYzM1MTNmMzlhOTFjYzM3YTA0OGE5ZmQyNDRkNGQifX19", this, false);
        CoffeeProduct latte = new CoffeeProduct("&r&lLatte", 3, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2VhMGY3NzU3MTg1YmU5ZGY1YjJmYzlkODVkNDA2NDJlYTRmZGI0NTE1ZjMxNGRhMThmNTljNjk2ZTViZTkifX19",  this, false);
        CoffeeProduct iced = new CoffeeProduct("&r&lIced Latte", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQxYTY5ZTE2NmMzYmI1ZGI4OWUyNzQzZDczZGE1Y2QwNjE5ZGE1ZTJlOTIzZGE5OWMyZTU1YmE4NTNkOSJ9fX0=", this, false);
        CakeProduct coffeeCake = new CakeProduct("&r&lCoffee Cake", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWM2ZWI4ZjE1YmEwZDc5OTNiZjg3MDhmYTFkZDg2YzFlOGZkZTc0MWE3ZGRlOTE5NWYyMjg5MWUwMjE1MyJ9fX0=",  this);
        CakeProduct sprinkleCake = new CakeProduct("&r&lSprinkle Cake", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19", this);
        return Arrays.asList(americano, latte, iced, coffeeCake, sprinkleCake);
    }
}
