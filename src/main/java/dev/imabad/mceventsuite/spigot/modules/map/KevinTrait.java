package dev.imabad.mceventsuite.spigot.modules.map;

import net.citizensnpcs.api.trait.Trait;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

public class KevinTrait extends Trait {

    private String speech;

    public KevinTrait(String speech) {
        super("kevin");
        this.speech = speech;
    }

    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        if(event.getNPC() == this.getNPC()){
            event.getClicker().sendMessage(event.getNPC().getName() + ": " + ChatColor.translateAlternateColorCodes('&', speech));
            event.setCancelled(true);
        }
    }

}
