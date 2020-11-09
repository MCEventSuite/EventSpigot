package dev.imabad.mceventsuite.spigot.modules.stafftrack;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.UpdateStaffTrackMessage;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StaffTrackModule extends Module {

    private List<UUID> hasMoved = new ArrayList<>();
    private HashMap<UUID, Integer> timeOnline = new HashMap<>();
    private BukkitTask staffTrackTask;

    @Override
    public String getName() {
        return "stafftrack";
    }

    @Override
    public void onEnable() {
        EventSpigot.getInstance().getServer().getPluginManager().registerEvents(new StaffTrackListener(this), EventSpigot.getInstance());
        this.staffTrackTask = EventSpigot.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(EventSpigot.getInstance(), () -> {
            hasMoved.forEach(this::addMinute);
            hasMoved.clear();
        }, 0, 20 * 60);
    }

    @Override
    public void onDisable() {
        this.staffTrackTask.cancel();
        this.staffTrackTask = null;
        this.timeOnline.keySet().forEach(uuid -> {
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new UpdateStaffTrackMessage(uuid, this.timeOnline.get(uuid)));
        });
        this.timeOnline.clear();
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }

    public void addMinute(UUID uuid){
        int time = 1;
        if(this.timeOnline.containsKey(uuid)){
            time = this.timeOnline.get(uuid) + 1;
        }
        this.timeOnline.put(uuid, time);
    }

    public void addMoved(UUID uuid){
        if(this.hasMoved.contains(uuid)){
            return;
        }
        this.hasMoved.add(uuid);
    }

    public int getAndRemovePlayerTime(UUID uuid){
        int time = timeOnline.get(uuid);
        timeOnline.remove(uuid);
        return time;
    }
}
