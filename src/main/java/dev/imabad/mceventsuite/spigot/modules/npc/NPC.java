package dev.imabad.mceventsuite.spigot.modules.npc;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Arrow;

public class NPC {
    private final String displayName;
    private final LivingEntity entity;
    private final Entity armorStand;
    private final NPCInteraction interactEvent;

    public NPC(String displayName, LivingEntity entity, NPCInteraction runnable, Character icon) {
        this.displayName = displayName;
        this.entity = entity;

        if(icon != null) {
            this.armorStand = new ArmorStand(EntityType.ARMOR_STAND, this.getEntity().level);
            armorStand.setInvisible(true);
            SynchedEntityData data = armorStand.getEntityData();
            data.set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0x20);
            data.set(new EntityDataAccessor<>(5, EntityDataSerializers.BOOLEAN), true);
            data.set(new EntityDataAccessor<>(15, EntityDataSerializers.BYTE), (byte) 0x01);

            double modifier = 1.1;
            if (entity instanceof Villager)
                modifier = 1.25;
            else if (entity instanceof IronGolem)
                modifier = 2.0;

            this.armorStand.moveTo(entity.getX(), entity.getY() + modifier, entity.getZ());
        } else {
            this.armorStand = null;
        }

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

    public Entity getArmorStand() {
        return this.armorStand;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public NPCInteraction getInteractEvent() {
        return this.interactEvent;
    }
}
