package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.spigot.modules.map.objects.Kevin;
import dev.imabad.mceventsuite.spigot.modules.npc.NPC;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCInteraction;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;

public class KevinInteraction implements NPCInteraction {

    private Kevin kevin;

    public KevinInteraction(Kevin kevin) {
        this.kevin = kevin;
    }

    @Override
    public void interact(Player player, NPC npc) {
        if(this.kevin.getVoiceLines().size() > 0) {
            String textLine = this.kevin.getVoiceLines().get(new Random().nextInt(this.kevin.getVoiceLines().size()));
            if(textLine.isBlank())
                return;
            player.sendMessage(StringUtils.colorizeMessage(npc.getDisplayName() + "&r: " + textLine));
        }
    }
}
