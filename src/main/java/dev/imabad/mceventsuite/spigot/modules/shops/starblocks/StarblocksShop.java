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
    private ShopVillagerInfo sally, jordan;

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
        Location sallySpawnLocation = new Location(world, 30.5, 32.0, 78.5, 0, 0);
        Location sallyMoveLocation = new Location(world,  857.5, 71, 520.5, 180, 0); // TODO: Update
        this.sally = new ShopVillagerInfo(this, "Sally", "&3&lManager Sally", sallySpawnLocation, sallyMoveLocation);
        this.sally.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzMTM2MTI0ODY4MiwKICAicHJvZmlsZUlkIiA6ICJiNTM5NTkyMjMwY2I0MmE0OWY5YTRlYmYxNmRlOTYwYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJpYW5hZmFnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyOGI0NzI5YzNkOTY3MWU2Yzk5NTJmY2E0OWE3MTQ4MDJlYmNjYThmNTFhMDViNWM1YWUzNjE3NDlkZDM4MTciLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "moDohh37TrMNvWKjHjnIIn9xCTPkZZD0mcVI+3bNnU7rv1/lj3efKje+2ceQ5t46Kk9V/sVkmcn1qNfXRvBABe5FgbraXQFYDpc5O3JC6j8faqQZF4vm5rfKK2yT0PxR+KQnBKKe+WkiYVxeblaeY5oPLKtAD5GxDg+XbTPW0U8WNR3QEhV6AiE4RheXklEwkOi9gzzWnQ5iyCk8DlkQp2hnTJ4sZdnT2hECQRHjkPlevbYEPXZgIPysI1bkwxzSD1LqWK3aQPwDA5uaNoZn/nsjEgWO/rspwhc/5LMLuJbD8lG9WiZQm5Ra7S7C8hSunp3heurmWz5bjhwdHC+iq4XxWYsHx5A7QRiqJzFBrMo9zhe/E3Nm1F0xEh8DY9tp18UBy2FdijlJ+4KEHEHqgUoIearJYTO8zU4w1TJVbDiJS5DVK0mXkPrXIdNj2cgs+BclTjkU4O1jJzMnB3cJJgzdrrAcv27t7J93cnvOIrhQNFbCEjWApf60i+QnUMeM1DEE9OfTJ15vhOVJrmXq6+B2BSFaOz83DqRW+/wRzPRO6K6sW6CW75VckozFs8Ii9g8l7PpAqU6UW9V3c86nXBY6Donih45z/8Pgh8OPLjRbKN+7uuNUKbv4M2G9LH2qNo4St5e2S5ADHG8gFr0DRqtRZ9yKgHJvxNM8ZJmYPLI=");

        Location jordanSpawnLocation = new Location(world, 30.5, 32.0, 82.5, 0, 0);
        Location jordanMoveLocation = new Location(world,  857.5, 71, 520.5, 180, 0); // TODO: Update
        this.jordan = new ShopVillagerInfo(this, "Jordan", "&3&lJordan", jordanSpawnLocation, jordanMoveLocation);
        this.jordan.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzMTM2NDU2NzQ5MiwKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jOWUyNTMyZWEwZTEzM2UzOWRlMjdkMDg2ODBmNWY1OGY0MzUzMjRkNWIwYjE3ZDA1OGRjZTllOTM5NTkyYjI5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "PfdbDHERgDN6QSV313GmoxE7uCaOpKqnP5b/pSl9rDkTw5kKjM5Gqa8d+MuGwRSYQikcz0JJOURqVbDom/42z4jlDnC5M6Awrb4cpxif2vEoRfTNoGWJu/chMmZbIcVBZAYx0zu/m/+2+RWLrjE0mlgPNULXABkgSVS61qp6hfzc17V5CvRN1zpllsPK3B2phmW0SBoOZeFn0E26ETwtNBLu1QbRLxNN1xJe+pVAhKqeILpetcg/4AWoEn77zwCIXqjFq0T+sgwN31Tt0Fv0/70d7vmJ/nbWinY8a/VM8bSpe/EXoo7OTclJX46BMg3XOXDyLtKARJvK94bBp7ajN5VjwINwaYz39lQr4VnK9J0EcQ0yM7clmFnoe87GQMVbLbCs71eFmawnwnss/HIgzZyP40SGPMOm3ef4vdM7aK6qPksZ4siBu3oqxE2NSsURBc8p8LyNZTMc5KHbBt9jExev8iyXBLLOe/Ih1rOV34IYj27bd4L1Qf0swRTH/CW/ahtXflumZp+FrTFOw451KSzhgTaEv+SGv3ZETzja+L5hWWbEtNPoQao3iXTxdbo5APWsHyoaCbvJJuCRqEY4w3tF7WDp9ZoDsNwfd3do2xT7OwdN5h0vVuXo0Pe5SFyUYnkCTGe9JfHGi55+caHWSGlX9ySdmdRW3/IS9bTmI9s=");
    }

    @Override
    public void removeEntities() {
        if(this.sally != null){
            this.sally.close();
        }
        if(this.jordan != null){
            this.jordan.close();
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
