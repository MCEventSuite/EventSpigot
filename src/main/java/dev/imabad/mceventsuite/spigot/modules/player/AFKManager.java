package dev.imabad.mceventsuite.spigot.modules.player;

import com.github.puregero.multilib.MultiLib;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.util.SpecialTag;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class AFKManager implements Listener {
    private static final Long AFK_TIME = 60L * 1000L * 1L;

    private final Map<UUID, Long> lastAction;
    private final Map<UUID, Entity> afk;

    public AFKManager() {
        this.lastAction = new HashMap<>();
        this.afk = new HashMap<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(EventSpigot.getInstance(), () -> {
            for(final Player player : Bukkit.getLocalOnlinePlayers()) {
                final long lastAction = this.lastAction.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
                if(System.currentTimeMillis() - lastAction >= AFK_TIME && !afk.containsKey(player.getUniqueId())) {
                    this.markAfk(player);
                    player.sendMessage(net.kyori.adventure.text.Component.text("You are now AFK.").color(NamedTextColor.GRAY));
                    MultiLib.notify("eventspigot:afk", "start:" + player.getUniqueId());
                }
            }
        }, 0L, 20L * 5L);

        MultiLib.onString(EventSpigot.getInstance(), "eventspigot:afk", (data) -> {
            final String[] parts = data.split(":");
            final UUID uuid = UUID.fromString(parts[1]);

            if(parts[0].equalsIgnoreCase("start")) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null)
                    return;

                this.markAfk(player);
            } else {
                this.unmarkAfk(uuid);
            }
        });

        Bukkit.getPluginManager().registerEvents(this, EventSpigot.getInstance());
    }

    public void markAfk(Player player) {
        ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, ((CraftPlayer) player).getHandle().getLevel());
        armorStand.teleportTo(player.getLocation().getX(), player.getLocation().getY() + 1.1, player.getLocation().getZ());
        SynchedEntityData data = armorStand.getEntityData();

        data.set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0x20);
        data.set(new EntityDataAccessor<>(5, EntityDataSerializers.BOOLEAN), true);
        data.set(new EntityDataAccessor<>(15, EntityDataSerializers.BYTE), (byte) 0x01);

        for(Player player1 : Bukkit.getLocalOnlinePlayers()) {
            this.sendAfkPacket(player1, armorStand);
        }
        this.afk.put(player.getUniqueId(), armorStand);
    }

    public void unmarkAfk(UUID player) {
        final Entity entity = this.afk.remove(player);
        if(entity == null)
            return;

        for(Player player1 : Bukkit.getLocalOnlinePlayers()) {
            ((CraftPlayer) player1).getHandle().connection.connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
        }
    }

    public void sendAfkPacket(Player player, Entity armorStand) {
        final String tag = FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId()) ? SpecialTag.AFK.getBedrockString()
                : SpecialTag.AFK.getJavaString();
        final Optional<Component> componentOptional = Optional.of(Component.literal(tag));
        SynchedEntityData data = armorStand.getEntityData();
        data.set(new EntityDataAccessor<>(2, EntityDataSerializers.OPTIONAL_COMPONENT), componentOptional);
        data.set(new EntityDataAccessor<>(3, EntityDataSerializers.BOOLEAN), true);

        ((CraftPlayer) player).getHandle().connection.connection.send(new ClientboundAddEntityPacket(armorStand));
        ((CraftPlayer) player).getHandle().connection.connection.send(new ClientboundSetEntityDataPacket(armorStand.getId(),
                data, true));
    }

    public void recordAction(Player player) {
        if(!player.isLocalPlayer())
            return;

        this.lastAction.put(player.getUniqueId(), System.currentTimeMillis());

        if (this.afk.containsKey(player.getUniqueId())) {
            this.unmarkAfk(player.getUniqueId());
            player.sendMessage(net.kyori.adventure.text.Component.text("You are no longer AFK.").color(NamedTextColor.GRAY));
            MultiLib.notify("eventspigot:afk", "end:" + player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.recordAction(event.getPlayer());
        for(Entity entity : this.afk.values()) {
            this.sendAfkPacket(event.getPlayer(), entity);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.unmarkAfk(event.getPlayer().getUniqueId());
        MultiLib.notify("eventspigot:afk", "end:" + event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        this.recordAction(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        this.recordAction(event.getPlayer());
    }
}
