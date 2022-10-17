package dev.imabad.mceventsuite.spigot.modules.shops.starblocks;

import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.shops.api.*;
import dev.imabad.mceventsuite.spigot.modules.shops.products.CakeProduct;
import dev.imabad.mceventsuite.spigot.modules.shops.products.CoffeeProduct;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StarblocksShop implements IMovingVillagerShop {

    private World world;
    private ShopArea area;
    private ShopState shopState;
    private ShopVillagerInfo sally, nathan, olivia;

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
        Location sallySpawnLocation = new Location(world, -94.5, 30.0, 199.5, 90, 0);
        Location sallyMoveLocation = new Location(world,  857.5, 71, 520.5, 180, 0); // TODO: Update
        this.sally = new ShopVillagerInfo(this, "Sally", "&3&lManager Sally", sallySpawnLocation, sallyMoveLocation);
        this.sally.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzMTM2MTI0ODY4MiwKICAicHJvZmlsZUlkIiA6ICJiNTM5NTkyMjMwY2I0MmE0OWY5YTRlYmYxNmRlOTYwYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJpYW5hZmFnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyOGI0NzI5YzNkOTY3MWU2Yzk5NTJmY2E0OWE3MTQ4MDJlYmNjYThmNTFhMDViNWM1YWUzNjE3NDlkZDM4MTciLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "moDohh37TrMNvWKjHjnIIn9xCTPkZZD0mcVI+3bNnU7rv1/lj3efKje+2ceQ5t46Kk9V/sVkmcn1qNfXRvBABe5FgbraXQFYDpc5O3JC6j8faqQZF4vm5rfKK2yT0PxR+KQnBKKe+WkiYVxeblaeY5oPLKtAD5GxDg+XbTPW0U8WNR3QEhV6AiE4RheXklEwkOi9gzzWnQ5iyCk8DlkQp2hnTJ4sZdnT2hECQRHjkPlevbYEPXZgIPysI1bkwxzSD1LqWK3aQPwDA5uaNoZn/nsjEgWO/rspwhc/5LMLuJbD8lG9WiZQm5Ra7S7C8hSunp3heurmWz5bjhwdHC+iq4XxWYsHx5A7QRiqJzFBrMo9zhe/E3Nm1F0xEh8DY9tp18UBy2FdijlJ+4KEHEHqgUoIearJYTO8zU4w1TJVbDiJS5DVK0mXkPrXIdNj2cgs+BclTjkU4O1jJzMnB3cJJgzdrrAcv27t7J93cnvOIrhQNFbCEjWApf60i+QnUMeM1DEE9OfTJ15vhOVJrmXq6+B2BSFaOz83DqRW+/wRzPRO6K6sW6CW75VckozFs8Ii9g8l7PpAqU6UW9V3c86nXBY6Donih45z/8Pgh8OPLjRbKN+7uuNUKbv4M2G9LH2qNo4St5e2S5ADHG8gFr0DRqtRZ9yKgHJvxNM8ZJmYPLI=");

        Location nathanSpawnPosition = new Location(world, 67.5, 30, -146.5, -90, 0);
        this.nathan = new ShopVillagerInfo(this, "Nathan", "&3&lNathan", nathanSpawnPosition, null);
        this.nathan.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzMTc0MjM5MTc2NiwKICAicHJvZmlsZUlkIiA6ICJiNjM2OWQ0MzMwNTU0NGIzOWE5OTBhODYyNWY5MmEwNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb2JpbmhvXyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODYxMmQwMzU3YmMyNzQ1MGRhZDg5YzNkMGQ4NjRlMjAyZTFkOTdhODc5ODAxMThkN2EzMDBmZmNkZGRmZjc0IgogICAgfQogIH0KfQ", "sEHwR6U75TQ7E63MqrkIyO3xxoHNn1sl5TFwsy6+0HbUaEPlOKQX23TnY88Z3jhbtSshjoBtf/TK3Y6pc4F6H48g+9rD9hqE1qAbspw7z3k5ZIdwotVqZ/uayFEyXWbJ7dCGkihjESWkHk9jJW3GeSH7/OoTej2eYg56ScsXDfNd0ud3nccZCw2sYjLmoVwqfoQE3wFE7ld+d8UZy7vmtcs0IFlGDKcwkpOhKtkjGVtGCrjPO0+CDMh0ALb0oj5S+DTcxmxrabzF00QXhdn+sPDbv/gbn8YfAYSqoSsJ3I/pknErkKmIQ9GdL7nRymoKWkbcHWfl7wLWFV495Ow0Vd+7M7963TI7uPhIqt8DI3/4ScqZs25ejihlyvUW2E/VXLzVeQsfFG0JpPpxzz6hmPI8zDCQBYYb/AJ8x6xOx5Se6ihoRJvRdF7yuOxLElJPQZErx+aZQCaYG67gknd5WKBJwGPQ3nV9zlvXX9lXzME7cHiG8vvmTOQ9GoERYwG/rvGvMdcpxWmpRtf+SB4Z/p06hdJ82zXrq7/G9GaMiMpYIiQ+L97OfurXO5AZLn1RnUjzWA1Sr3YojGDcCVwHKoYThepdm1ggwqnD167rDLm6XuPi7ZePTJEWtEROkFxL5gpJ9kJTq7PsAOoSh+9bT43jvV12JXKfunYGYZWWWgU");

        Location oliviaSpawnPosition = new Location(world, 64.5, 30, -147.5, -180, 0);
        this.olivia = new ShopVillagerInfo(this, "Olivia", "&3&lOlivia", oliviaSpawnPosition, null);
        this.olivia.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzMTc0MDg3MjEwOSwKICAicHJvZmlsZUlkIiA6ICJiMWMyNWQ0YjMwZDU0N2Y4YTk3NmZlYTllOGU1YzBjMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJvd29FbmRlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85Y2U4MWRhY2I5MmFmZjYwNjc2Nzg1NTdlMmJjYzI0ZWNmYTUyNjBhMjEyNDhkNjk1YzQzOWI1NzFlNjAxYzA0IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0", "pltau3bU3oRcLgnCsYWzgbRyBQX7QzRyC+m2l0ua/697YLDR4EVhkMUn1I6LvxseZZ0JQofeFUUIJBouo7iy03xPGRs8seUlkDGxLAgm9b1LcJM6ReidzzI05sAxrOCidoGCU0PAsGcxbQZwq4ysl/zv9QnYnnTEa8wmlaJGyIYga/YBPQ/vFcDZmxTJxaJr6ylqMgOEoxZwVwVLFXMdZI9qQBp2waCO39PvQHrXypZj8hFqGVrCfg72RBJFcvLXs2M0pcVAPnOmm53s5P7xj/f4TXhVuzofrQ+44mhn82iyqhasNcjLDNzFrn+hgciN4Ks2n0gkaitlap4rGkAqKgle1V97Di3QyiH/lEU1uEO6Fj15HfNIYe3h2mLQZgAw/ooX/BrXCBDjjGkLoJSKhRiJ2KsOVdbInpe+vdUQXm0aLVqIcGuStFTTYNorfCm9T50F4QzBHrPHwoKdzekMGV6+LWh+c6RhKGYBBtdI4zcvewrMQT8vEQJ33QadxBkm1CTIAD2yyzaOB94KofG/F7yih4xI8oZSiTsSVf/1IFrohEL+P7AHukqGOTx3SYQFdCzNRH3KOi1OD54qcsB/erJGEgptiEbN9F7pM7DydfPLZD8lY0VwTuE2yquIog6UxL5UNG5EigEYVS7grXmGmF9+Q80+y/3yCShvgXhzJnE");
    }

    @Override
    public void removeEntities() {
        if(this.sally != null){
            this.sally.close();
        }
        if(this.nathan != null) {
            this.nathan.close();
        }
        if(this.olivia != null) {
            this.olivia.close();
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
        CoffeeProduct americano = new CoffeeProduct("&r&lAmericano", 2, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3YzVhM2E5YzM3ZjcxNjljOTExNTQ5OTg5N2JmNmI5ZDFlMmY1Mjk1ZDk5OTYwYmNlMzQ5OGZjMGQ2ZmU2NSJ9fX0=", this, false);
        CoffeeProduct latte = new CoffeeProduct("&r&lLatte", 3, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ4NTc3NzM5NWMyMGM1NzNmMjRjNjUyZjliZWM3ZWI1OGU5ZDdlNWY5OWM1NzI1YTRkZmZlY2FhZDNlNzhiIn19fQ==",  this, false);
        CoffeeProduct cappuccino = new CoffeeProduct("&r&lCappuccino", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ4NTc3NzM5NWMyMGM1NzNmMjRjNjUyZjliZWM3ZWI1OGU5ZDdlNWY5OWM1NzI1YTRkZmZlY2FhZDNlNzhiIn19fQ==", this, false);
        CoffeeProduct hotChocolate = new CoffeeProduct("&r&lHot Chocolate", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc0ZjExOWU5NGZmOGI3NGQwYmUxNWNkMmFhNjg5NWIxM2FlODVhYjRlYWQwZWUzNDI5MTQ5MTY0NWY2ZDI4ZSJ9fX0=", this, false);
        CakeProduct cake = new CakeProduct("&r&lCake", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTg3MjI2ZmRhMDVhMWU2MjRlYmI3MmNmNzQwNjIzZTgxOTE4OWRmZWY1ODliNjgwNzdlNzVjMjQ4Y2U3OSJ9fX0=",  this);
        return Arrays.asList(americano, latte, cappuccino, hotChocolate, cake);
    }
}
