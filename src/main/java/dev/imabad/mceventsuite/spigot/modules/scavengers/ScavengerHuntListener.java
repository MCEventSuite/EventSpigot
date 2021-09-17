package dev.imabad.mceventsuite.spigot.modules.scavengers;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLDatabase;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.scavenger.ScavengerModule;
import dev.imabad.mceventsuite.core.modules.scavenger.db.ScavengerDAO;
import dev.imabad.mceventsuite.core.modules.scavenger.db.ScavengerHuntPlayer;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class ScavengerHuntListener implements Listener {

    private final ScavengerHuntSpigotModule module;
    public ScavengerHuntListener(ScavengerHuntSpigotModule module){
        this.module = module;
    }

    @EventHandler
    public void interactHead(PlayerInteractEvent playerInteractEvent){
        if(playerInteractEvent.getClickedBlock() != null && playerInteractEvent.getClickedBlock().getType() == Material.PLAYER_HEAD) {
            Location blockLocation = playerInteractEvent.getClickedBlock().getLocation();
            Player player = playerInteractEvent.getPlayer();
            if(playerInteractEvent.getHand() == EquipmentSlot.OFF_HAND) return;
            module.getFromLocation(blockLocation).ifPresent(scavengerLocation -> EventCore.getInstance().getModuleRegistry().getModule(ScavengerModule.class).findCard(scavengerLocation, player.getUniqueId(), EventSpigot.getInstance().getAudiences().player(player)));
            playerInteractEvent.setCancelled(true);
        }
    }
    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent) {

    }
}
