package dev.imabad.mceventsuite.spigot;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.api.objects.EventRank;
import dev.imabad.mceventsuite.core.modules.join.JoinModule;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.RankDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisMessageListener;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.events.RedisConnectionEvent;
import dev.imabad.mceventsuite.core.modules.redis.messages.NewBoothMessage;
import dev.imabad.mceventsuite.core.util.GsonUtils;
import dev.imabad.mceventsuite.spigot.impl.EventPermission;
import dev.imabad.mceventsuite.spigot.impl.SpigotActionExecutor;
import dev.imabad.mceventsuite.spigot.listeners.PlayerListener;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EventSpigot extends JavaPlugin {

    private static EventSpigot eventSpigot;

    public static EventSpigot getInstance(){
        return eventSpigot;
    }


    private List<EventRank> ranks;

    @Override
    public void onEnable() {
        new EventCore(getDataFolder());
        EventCore.getInstance().getEventRegistry().registerListener(RedisConnectionEvent.class, redisConnectionEvent -> {
            System.out.println("[EventSpigot] Connected to Redis");
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(TestRedisMessage.class, new RedisMessageListener<>((msg) -> {
                System.out.println("[EventSpigot] Received Hello " + msg.getHello());
            }));
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).publishMessage(RedisChannel.GLOBAL, new TestRedisMessage());
        });
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventSpigotModule());
        EventCore.getInstance().setActionExecutor(new SpigotActionExecutor());
        eventSpigot = this;
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new JoinModule());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, (event) -> {
            ranks = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRanks();
            System.out.println(GsonUtils.getGson().toJson(ranks));
        });
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if(getServer().getPluginManager().isPluginEnabled("Vault")){
            getServer().getServicesManager().register(Permission.class, new EventPermission(), this, ServicePriority.Highest);
        }
        if(getServer().getPluginManager().isPluginEnabled("PlotSquared")){
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new BoothModule());
        }
    }

    @Override
    public void onDisable() {
        EventCore.getInstance().shutdown();
    }
}
