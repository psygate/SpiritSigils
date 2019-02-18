package net.civex4.spiritsigils.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.UUID;

/**
 * An abstracted block location key to avoid storing full blocks.
 */
public class BlockKey {
    private final int x, y, z;
    private final UUID worldUUID;

    public BlockKey(int x, int y, int z, UUID world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldUUID = Objects.requireNonNull(world, "WorldUUID is not nullable.");
    }

    public BlockKey(Block block) {
        this(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public Block toBlock() {
        return Bukkit.getWorld(worldUUID).getBlockAt(x, y, z);
    }

    public Location toLocation() {
        return new Location(getWorld(), x, y, z);
    }

    @Override
    public String toString() {
        return "BlockKey{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", worldUUID=" + worldUUID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockKey blockKey = (BlockKey) o;
        return x == blockKey.x &&
                y == blockKey.y &&
                z == blockKey.z &&
                worldUUID.equals(blockKey.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldUUID);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldUUID);
    }
}
