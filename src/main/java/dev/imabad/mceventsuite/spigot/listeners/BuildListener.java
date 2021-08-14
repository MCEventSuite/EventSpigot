package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class BuildListener implements Listener {

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent explosionPrimeEvent){
        explosionPrimeEvent.setCancelled(true);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent){
        worldLoadEvent.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_INSOMNIA, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.FALL_DAMAGE, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.FIRE_DAMAGE, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DROWNING_DAMAGE, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.DISABLE_RAIDS, true);
        worldLoadEvent.getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        worldLoadEvent.getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
    }

    @EventHandler
    public void onPlantGrow(BlockGrowEvent blockGrowEvent){
        World world = blockGrowEvent.getBlock().getWorld();
        if (BoothModule.BOOTH_WORLDS.contains(world.getName())) {
            blockGrowEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent structureGrowEvent){
        if(!structureGrowEvent.isFromBonemeal())
            structureGrowEvent.setCancelled(true);
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent blockRedstoneEvent) {
        World world = blockRedstoneEvent.getBlock().getWorld();
        if (BoothModule.BOOTH_WORLDS.contains(world.getName())) {
            blockRedstoneEvent.setNewCurrent(blockRedstoneEvent.getOldCurrent());
        }
    }

    @EventHandler
    public void onBlockFormEvent(BlockFormEvent blockFormEvent){
        if(blockFormEvent.getBlock().getType().toString().toLowerCase().contains("concrete_powder")){
            blockFormEvent.setCancelled(true);
        }
    }
}
