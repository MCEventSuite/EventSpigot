package dev.imabad.mceventsuite.spigot.modules.map;

import net.citizensnpcs.api.trait.Trait;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Random;

public class KevinTrait extends Trait {

    private List<String> voiceLines;

    public KevinTrait(List<String> voiceLines) {
        super("kevin");
        this.voiceLines = voiceLines;
    }

    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        if(event.getNPC() == this.getNPC()){
            if(voiceLines.size() > 0) {
                String textLine = voiceLines.get(new Random().nextInt(voiceLines.size()));
                event.getClicker().sendMessage(event.getNPC().getName() + ": " + ChatColor.translateAlternateColorCodes('&', textLine));
            }
            event.setCancelled(true);
        }
    }

}
