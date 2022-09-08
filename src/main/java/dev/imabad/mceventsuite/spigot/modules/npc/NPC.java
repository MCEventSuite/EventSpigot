package dev.imabad.mceventsuite.spigot.modules.npc;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class NPC {
    private final String displayName;
    private final LivingEntity entity;
    private final NPCInteraction interactEvent;

    public NPC(String displayName, LivingEntity entity, NPCInteraction runnable) {
        this.displayName = displayName;
        this.entity = entity;
        this.interactEvent = runnable;
    }

    public void setSkin(String texture, String signature) {
        if(this.entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public NPCInteraction getInteractEvent() {
        return this.interactEvent;
    }
}
