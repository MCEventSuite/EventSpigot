package dev.imabad.mceventsuite.spigot.modules.npc.nms;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.npc.NPC;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCManager;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PacketListener implements Listener {

    private final NPCManager npcManager;

    public PacketListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent) {
        this.npcManager.setWorld(worldLoadEvent.getWorld());
    }

    private void sendPlayerPacket(Connection connection, NPC npc, boolean respawn) {
        final ServerPlayer npcPlayer = (ServerPlayer) npc.getEntity();

        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npcPlayer));
        connection.send(new ClientboundAddPlayerPacket(npcPlayer));
        EventSpigot.getInstance().getServer().getScheduler().runTaskLater(EventSpigot.getInstance(), () -> {
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npcPlayer));
        }, 20 * 2);

        if(respawn) {
            connection.send(new ClientboundSetEntityDataPacket(npc.getEntity().getId(), npc.getEntity().getEntityData(), true));
            connection.send(new ClientboundRotateHeadPacket(npc.getEntity(), (byte)(npc.getEntity().getYHeadRot() * 256 / 360)));
            connection.send(new ClientboundMoveEntityPacket.Rot(
                    npc.getEntity().getId(),
                    (byte)(npc.getEntity().getYRot() * 256 / 360),
                    (byte)(npc.getEntity().getXRot() * 256 / 360), true));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            long lastClick = 0;
            @Override
            public void channelRead(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull Object packet) throws Exception {
                if(packet instanceof ServerboundInteractPacket interactPacket) {
                    if(System.currentTimeMillis() - lastClick >= 1000)
                        npcManager.handleInteract(player, interactPacket.getEntityId());
                    this.lastClick = System.currentTimeMillis();
                }
                super.channelRead(channelHandlerContext, packet);
            }
        };

        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final Connection connection = serverPlayer.connection.connection;

        for(NPC npc : npcManager.getNpcList()) {
            if(npc.getEntity() instanceof ServerPlayer npcPlayer) {
                this.sendPlayerPacket(connection, npc, false);
            } else {
                connection.send(new ClientboundAddEntityPacket(npc.getEntity()));
            }

            connection.send(new ClientboundSetEntityDataPacket(npc.getEntity().getId(), npc.getEntity().getEntityData(), true));
            connection.send(new ClientboundRotateHeadPacket(npc.getEntity(), (byte)(npc.getEntity().getYHeadRot() * 256 / 360)));
            connection.send(new ClientboundMoveEntityPacket.Rot(
                    npc.getEntity().getId(),
                    (byte)(npc.getEntity().getYRot() * 256 / 360),
                    (byte)(npc.getEntity().getXRot() * 256 / 360), true));
        }

        ChannelPipeline pipeline = serverPlayer.connection.connection.channel.pipeline();
        if(pipeline.get("eventspigot_npc") != null)
            pipeline.remove("eventspigot_npc");
        pipeline.addBefore("packet_handler", "eventspigot_npc", channelDuplexHandler);
    }

    private void handlePlayerMove(Player player, Location from, Location to) {
        if(player.isLocalPlayer()) {
            final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            final Connection connection = serverPlayer.connection.connection;

            for(NPC npc : npcManager.getNpcList()) {
                if(npc.getEntity() instanceof ServerPlayer) {
                    final Location npcLocation = new Location(player.getWorld(),
                            npc.getEntity().getX(), npc.getEntity().getY(), npc.getEntity().getZ());
                    if (from.distance(npcLocation) > (16 * 5) && to.distance(npcLocation) <= (16 * 5)) {
                        connection.send(new ClientboundRemoveEntitiesPacket(npc.getEntity().getId()));
                        this.sendPlayerPacket(connection, npc, true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        this.handlePlayerMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.handlePlayerMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("eventspigot_npc");
            return null;
        });
    }
}
