package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class FireworkAction extends ShowAction{

    private Location location;
    private List<FireworkEffect> effects;
    private int power;
    private Vector direction;
    private double directionPower;

    public FireworkAction(Show show, long time, Location actionLocation, List<FireworkEffect> effects, int power, Vector direction, double directionPower) {
        super(show, time);
        this.location = actionLocation;
        this.effects = effects;
        this.power = power;
        this.direction = direction;
        this.directionPower = directionPower;
    }

    @Override
    public void execute() {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta metaData = firework.getFireworkMeta();
        metaData.clearEffects();
        metaData.addEffects(effects);
        metaData.setPower(Math.min(1, power));
        firework.setFireworkMeta(metaData);
        if(direction.length() > 0) {
            firework.setVelocity(direction.normalize().multiply(this.directionPower * 0.05D));
        }
        if(power == 0){
            final ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
            final FireworkRocketEntity fwEntity = ((CraftFirework) firework).getHandle();
            level.broadcastEntityEvent(fwEntity, (byte) 17);
            firework.remove();
        }
    }
}
