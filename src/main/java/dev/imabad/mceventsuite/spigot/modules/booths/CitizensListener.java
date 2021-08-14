package dev.imabad.mceventsuite.spigot.modules.booths;

import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CitizensListener implements Listener {

    private static final String VILLAGER_NAME = "Teleporter Tim";
    private List<NPC> villagers = new ArrayList<>();

    @EventHandler
    public void onCitizensLoad(CitizensEnableEvent event) {
        // Remove any existing NPCs
        for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
            npc.despawn();
            npc.destroy();
            CitizensAPI.getNPCRegistry().deregister(npc);
        }

        for (String worldName : Arrays.asList("contest", "creative", "stream")) {
            addNPCs(worldName);
        }

        for (String worldName : BoothModule.BOOTH_WORLDS) {
            addNPCs(worldName);
        }
    }

    public void addNPCs(String worldName) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, StringUtils.colorizeMessage(VILLAGER_NAME));
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.lookClose(true);
        villagers.add(npc);

        World world = Bukkit.getWorld(worldName);

        if (BoothModule.BOOTH_WORLDS.contains(worldName)) {
            npc.spawn(new Location(world, 1, 65, 0.5, 270, 0));
        } else {
            npc.spawn(worldName.equals("creative")
                    ? new Location(world, 6, 52, 6, 270, 0)
                    : new Location(world, 0.5, 51, 0.5, 270, 0));
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        String name = event.getRightClicked().getCustomName();

        if (name == null || !name.equals(VILLAGER_NAME)) {
            return;
        }

        event.setCancelled(true);
        new TeleportInventory(event.getPlayer()).open(event.getPlayer(), null);
    }

}
