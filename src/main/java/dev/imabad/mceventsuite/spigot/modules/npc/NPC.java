package dev.imabad.mceventsuite.spigot.modules.npc;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NPC {
    private final String displayName;
    private final Entity entity;
    private final BiConsumer<Player, NPC> interactEvent;

    public NPC(String displayName, Entity entity, BiConsumer<Player, NPC> runnable) {
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

    public Entity getEntity() {
        return this.entity;
    }

    public BiConsumer<Player, NPC> getInteractEvent() {
        return this.interactEvent;
    }
}
