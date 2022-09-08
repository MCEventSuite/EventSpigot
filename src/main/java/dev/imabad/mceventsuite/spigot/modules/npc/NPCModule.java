package dev.imabad.mceventsuite.spigot.modules.npc;

import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.npc.nms.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class NPCModule extends Module {

    private NPCManager npcManager;

    @Override
    public String getName() {
        return "npc";
    }

    @Override
    public void onEnable() {
        this.npcManager = new NPCManager(this);
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new PacketListener(this.npcManager),
                EventSpigot.getInstance());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    public NPCManager getNpcManager() {
        return this.npcManager;
    }
}
