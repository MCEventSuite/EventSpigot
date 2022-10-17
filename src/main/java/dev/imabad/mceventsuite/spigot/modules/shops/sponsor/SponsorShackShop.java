package dev.imabad.mceventsuite.spigot.modules.shops.sponsor;

import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.shops.api.*;
import dev.imabad.mceventsuite.spigot.modules.shops.products.CakeProduct;
import dev.imabad.mceventsuite.spigot.modules.shops.products.CoffeeProduct;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SponsorShackShop implements IMovingVillagerShop {

    private World world;
    private ShopArea area;
    private ShopState shopState;
    private ShopVillagerInfo sponsorNpc;

    public SponsorShackShop(ShopsModule module){
        world = module.getMainWorld();
        area = new ShopArea(new Location(world, -146, 29, -94), new Location(world, -155, 33, -89));
        shopState = ShopState.OPEN;
    }

    @Override
    public ShopArea getShopArea() {
        return area;
    }

    @Override
    public String getName() {
        return "DK Snacks";
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
        Location sponsorNPCPosition = new Location(world, -151.5, 31, -90.5, 180, 0);
        this.sponsorNpc = new ShopVillagerInfo(this, "dksnacks", "&3&lDK Snacks", sponsorNPCPosition, null);
        this.sponsorNpc.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzMTc0MjM5MTc2NiwKICAicHJvZmlsZUlkIiA6ICJiNjM2OWQ0MzMwNTU0NGIzOWE5OTBhODYyNWY5MmEwNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb2JpbmhvXyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODYxMmQwMzU3YmMyNzQ1MGRhZDg5YzNkMGQ4NjRlMjAyZTFkOTdhODc5ODAxMThkN2EzMDBmZmNkZGRmZjc0IgogICAgfQogIH0KfQ", "sEHwR6U75TQ7E63MqrkIyO3xxoHNn1sl5TFwsy6+0HbUaEPlOKQX23TnY88Z3jhbtSshjoBtf/TK3Y6pc4F6H48g+9rD9hqE1qAbspw7z3k5ZIdwotVqZ/uayFEyXWbJ7dCGkihjESWkHk9jJW3GeSH7/OoTej2eYg56ScsXDfNd0ud3nccZCw2sYjLmoVwqfoQE3wFE7ld+d8UZy7vmtcs0IFlGDKcwkpOhKtkjGVtGCrjPO0+CDMh0ALb0oj5S+DTcxmxrabzF00QXhdn+sPDbv/gbn8YfAYSqoSsJ3I/pknErkKmIQ9GdL7nRymoKWkbcHWfl7wLWFV495Ow0Vd+7M7963TI7uPhIqt8DI3/4ScqZs25ejihlyvUW2E/VXLzVeQsfFG0JpPpxzz6hmPI8zDCQBYYb/AJ8x6xOx5Se6ihoRJvRdF7yuOxLElJPQZErx+aZQCaYG67gknd5WKBJwGPQ3nV9zlvXX9lXzME7cHiG8vvmTOQ9GoERYwG/rvGvMdcpxWmpRtf+SB4Z/p06hdJ82zXrq7/G9GaMiMpYIiQ+L97OfurXO5AZLn1RnUjzWA1Sr3YojGDcCVwHKoYThepdm1ggwqnD167rDLm6XuPi7ZePTJEWtEROkFxL5gpJ9kJTq7PsAOoSh+9bT43jvV12JXKfunYGYZWWWgU");}

    @Override
    public void removeEntities() {
        if(this.sponsorNpc != null) {
            this.sponsorNpc.close();
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
        CakeProduct fish = new CakeProduct("&r&lFish", 2, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVjYzc3ODE5Yzk5M2MxOTZjM2E1NzhkZGNlMGVmNmIyMGVhMTc1ZDk5ZTYxNTRmNmI1NTRjMTFiODRmZDU0MiJ9fX0=", this);
        CakeProduct fries = new CakeProduct("&r&lFries", 3, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE4MmUxMWQ1YTY0YzZkNjUzZjNhMWM1M2M5NDIwYTE1MzgxOGU2N2RiZDhlOTM0NDJjMzVjNjI3ZTBjNjMxIn19fQ==",  this);
        CakeProduct pizza = new CakeProduct("&r&lPizza", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2NTc0MGE3OWY1ZmQ1MjljMmFlOTM3OTNiYzI1NThjYzQ0ODZkODdhODEyNTNmYTkwMmUxYWQzZjk1NzVjNSJ9fX0=", this);
        CakeProduct fried_chicken = new CakeProduct("&r&lFried Chicken", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhmYjA1MDJhM2FhNWY4YmQzMmE1ZWE1ZTUxOWMzZGQzNTMyMzQxNzBkZmVmOTU5ZWU4YWRiOTQ4N2ZlYSJ9fX0=", this);
        CoffeeProduct tea = new CoffeeProduct("&r&lTea", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk2OTk0NDliZTMxYjAyMGJhZmEwZDJkOWIzNjYyMmE5ZDMyMzI2ZDU3OGQ0YTIzNmZiMDg3NTRjODY3NzI5In19fQ==",  this, false);
        CoffeeProduct fizzyPop = new CoffeeProduct("&r&lTea", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRkNTdmNjNjMmM5NDgyZGE1MjhmYzUzOWNjOGUzZTA2MzViNjMxMzBhN2FhZTg4YjlhYzBhMGNlMjA5ZDFlMyJ9fX0=",  this, false);
        return Arrays.asList(fish, fries, pizza, fried_chicken, tea, fizzyPop);
    }
}
