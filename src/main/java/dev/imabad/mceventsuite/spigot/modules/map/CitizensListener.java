package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.shops.squareways.SquarewayShop;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.StarblocksShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    @EventHandler
    public void onCitizensLoad(net.citizensnpcs.api.event.CitizensEnableEvent event){
        EventCore.getInstance().getModuleRegistry().getModule(MapModule.class).initKevins();
    }

}
