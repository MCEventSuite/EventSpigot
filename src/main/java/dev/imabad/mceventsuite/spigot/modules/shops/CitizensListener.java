package dev.imabad.mceventsuite.spigot.modules.shops;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.shops.squareways.SquarewayShop;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.StarblocksShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    @EventHandler
    public void onCitizensLoad(net.citizensnpcs.api.event.CitizensEnableEvent event){
        EventSpigot.getInstance().getLogger().info("Citizens Enabled! Registering shops!");
        ShopsModule module = EventCore.getInstance().getModuleRegistry().getModule(ShopsModule.class);
        module.registerShop(new StarblocksShop(module));
        module.registerShop(new SquarewayShop(module));
    }

}
