package dev.imabad.mceventsuite.spigot.modules.stafftrack;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.UpdateStaffTrackMessage;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.hibernate.sql.Update;

public class StaffTrackModule extends Module {

    private List<UUID> hasMoved = new ArrayList<>();
    private BukkitTask staffTrackTask;

    @Override
    public String getName() {
        return "stafftrack";
    }

    @Override
    public void onEnable() {
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new StaffTrackListener(this), EventSpigot.getInstance());
        this.staffTrackTask = EventSpigot.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(EventSpigot.getInstance(), () -> {
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new UpdateStaffTrackMessage(hasMoved));
            hasMoved.clear();
        }, 0, 20 * 60);
    }

    @Override
    public void onDisable() {
        this.hasMoved.clear();
        this.staffTrackTask.cancel();
        this.staffTrackTask = null;
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }


    public void addMoved(UUID uuid){
        if(this.hasMoved.contains(uuid)){
            return;
        }
        this.hasMoved.add(uuid);
    }
}
