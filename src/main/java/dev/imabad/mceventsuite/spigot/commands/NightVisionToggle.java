package dev.imabad.mceventsuite.spigot.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

public class NightVisionToggle extends BukkitCommand {
    public NightVisionToggle() {
        super("nv");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!commandLabel.equals("nv")){
            return false;
        }
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        Collection<PotionEffect> potionEffectList = player.getActivePotionEffects();
        boolean hasPotionEffect = potionEffectList.stream().anyMatch(potionEffect -> potionEffect.getType().equals(PotionEffectType.NIGHT_VISION));
        if(hasPotionEffect){
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            sender.sendMessage(ChatColor.RED + "Toggled night vision off");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true, false, false));
            sender.sendMessage(ChatColor.GREEN + "Toggled night vision on");
        }
        return true;
    }
}
