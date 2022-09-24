package dev.imabad.mceventsuite.spigot.modules.map.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.time.LocalDate;

public class Tree {
    private int x;
    private int y;
    private int z;
    private int rotation;

    public Tree() {

    }

    public Tree(Vector vector, int rotation) {
        this.x = vector.getBlockX();
        this.y = vector.getBlockY();
        this.z = vector.getBlockZ();
        this.rotation = rotation;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getRotation() {
        return this.rotation;
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }
}
