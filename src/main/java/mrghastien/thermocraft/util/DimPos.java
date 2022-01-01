package mrghastien.thermocraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;


public class DimPos {

    protected final BlockPos pos;
    protected final ResourceKey<Level> dimension;

    public DimPos(BlockPos pos, ResourceKey<Level> dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    public DimPos(BlockPos pos, Level world) {
        this(pos.immutable(), world.dimension());
    }

    public DimPos(int x, int y, int z, ResourceKey<Level> dimension) {
        this(new BlockPos(x, y, z), dimension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DimPos dimPos = (DimPos) o;

        if (getX() != dimPos.getX() || getY() != dimPos.getY() || getZ() != dimPos.getZ()) return false;
        return dimension.equals(dimPos.dimension);
    }

    @Override
    public int hashCode() {
        int result = pos.hashCode();
        result = 31 * result + dimension.hashCode();
        return result;
    }

    public DimPos relative(Direction dir) {
        return new DimPos(pos.relative(dir), dimension);
    }

    public int getX() {
        return blockPos().getX();
    }

    public int getY() {
        return blockPos().getY();
    }

    public int getZ() {
        return blockPos().getZ();
    }

    public BlockPos blockPos() {
        return pos;
    }

    public ResourceKey<Level> dimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return "DimPos{x=" + pos.getX() + ", y=" + pos.getY() + ", z=" + pos.getZ() + ", dim=" + dimension.toString() + "}";
    }

    public boolean isDimensionEqual(DimPos pos) {
        return pos.dimension == dimension;
    }

    public int distSqr(DimPos pos) {
        if(!isDimensionEqual(pos)) return Integer.MAX_VALUE;
        return (getX() - pos.getX()) * (getX() - pos.getX()) + (getY() - pos.getY()) * (getY() - pos.getY()) + (getZ() - pos.getZ()) * (getZ() - pos.getZ());
    }
}
