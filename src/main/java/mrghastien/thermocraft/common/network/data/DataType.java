package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeFriendlyByteBuf;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a type of data that can be handled by {@link DataReference data references}.
 * <p>
 * A DataType encapsulates a class to provide decode and encode functions.
 *
 * @param <T> The type (class) of the data
 */
public class DataType<T> {

    private static final Map<String, DataType<?>> TYPES = new HashMap<>(9);

    public static final DataType<Integer> INT = builder(Integer.TYPE)
            .defaultValue(0)
            .toBytes(FriendlyByteBuf::writeInt)
            .fromBytes(FriendlyByteBuf::readInt)
            .serializeNBT(IntTag::valueOf)
            .deserializeNBT(nbt -> ((IntTag)nbt).getAsInt())
            .build();

    public static final DataType<Long> LONG = builder(Long.TYPE)
            .defaultValue(0L)
            .toBytes(FriendlyByteBuf::writeLong)
            .fromBytes(FriendlyByteBuf::readLong)
            .serializeNBT(LongTag::valueOf)
            .deserializeNBT(nbt -> ((LongTag)nbt).getAsLong())
            .build();

    public static final DataType<Float> FLOAT = builder(Float.TYPE)
            .defaultValue(0f)
            .toBytes(FriendlyByteBuf::writeFloat)
            .fromBytes(FriendlyByteBuf::readFloat)
            .serializeNBT(FloatTag::valueOf)
            .deserializeNBT(nbt -> ((FloatTag)nbt).getAsFloat())
            .build();

    public static final DataType<Double> DOUBLE = builder(Double.TYPE)
            .defaultValue(0d)
            .toBytes(FriendlyByteBuf::writeDouble)
            .fromBytes(FriendlyByteBuf::readDouble)
            .serializeNBT(DoubleTag::valueOf)
            .deserializeNBT(nbt -> ((DoubleTag)nbt).getAsDouble())
            .build();

    public static final DataType<Boolean> BOOL = builder(Boolean.TYPE)
            .defaultValue(false)
            .toBytes(FriendlyByteBuf::writeBoolean)
            .fromBytes(FriendlyByteBuf::readBoolean)
            .serializeNBT(ByteTag::valueOf)
            .deserializeNBT(nbt -> ((ByteTag)nbt).getAsByte() != 0)
            .build();

    public static final DataType<String> STRING = builder(String.class)
            .defaultValue("")
            .toBytes(FriendlyByteBuf::writeUtf)
            .fromBytes(FriendlyByteBuf::readUtf)
            .serializeNBT(StringTag::valueOf)
            .deserializeNBT(Tag::getAsString)
            .build();

    public static final DataType<ItemStack> ITEM_STACK = builder(ItemStack.class)
            .defaultValue(ItemStack.EMPTY)
            .equalityTest(ItemStack::matches)
            .toBytes(FriendlyByteBuf::writeItem)
            .fromBytes(FriendlyByteBuf::readItem)
            .serializeNBT(IForgeItemStack::serializeNBT)
            .deserializeNBT(nbt -> ItemStack.of((CompoundTag) nbt))
            .build();

    public static final DataType<FluidStack> FLUID_STACK = builder(FluidStack.class)
            .defaultValue(FluidStack.EMPTY)
            .equalityTest(FluidStack::isFluidStackIdentical)
            .toBytes(IForgeFriendlyByteBuf::writeFluidStack)
            .fromBytes(IForgeFriendlyByteBuf::readFluidStack)
            .serializeNBT(v -> v.writeToNBT(new CompoundTag()))
            .deserializeNBT(nbt -> FluidStack.loadFluidStackFromNBT((CompoundTag) nbt))
            .build();

    public static final DataType<FixedPointNumber> FIXED_POINT = builder(FixedPointNumber.class)
            .defaultValue(FixedPointNumber.valueOf(0))
            .toBytes((buf, value) -> value.toBuffer(buf))
            .fromBytes(FixedPointNumber::decodeFromBuffer)
            .serializeNBT(v -> {
                CompoundTag tag = new CompoundTag();
                tag.putLong("integral", v.longValue());
                tag.putShort("fractional", v.getFractionalBits());
                return tag;
            })
            .deserializeNBT(nbt -> {
                CompoundTag tag = (CompoundTag) nbt;
                return FixedPointNumber.deserializeNBT(tag);
            })
            .build();

    private final Class<T> clazz;
    private final T defaultValue;
    private final BiConsumer<FriendlyByteBuf, T> encoder;
    private final Function<FriendlyByteBuf, T> decoder;
    private final Function<T, Tag> nbtEncoder;
    private final Function<Tag, T> nbtDecoder;
    private final BiPredicate<T, T> equalityPredicate;

    private DataType(Builder<T> builder) {
        this.clazz = builder.clazz;
        this.defaultValue = builder.defaultValue;
        this.encoder = builder.encoder;
        this.decoder = builder.decoder;
        this.nbtEncoder = builder.nbtEncoder;
        this.nbtDecoder = builder.nbtDecoder;
        this.equalityPredicate = builder.equalityPredicate;
        TYPES.put(clazz.getName(), this);
    }

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }

    public static DataType<?> fromString(String name) {
        return TYPES.get(name);
    }

    @Override
    public String toString() {
        return "DataType{" + clazz.getSimpleName() + "}";
    }

    public String getFullName() {
        return clazz.getName();
    }

    //ouh weird black magic here
    public DataReference<T> cast(DataReference<?> reference) {
        if(reference.getType() != this)
            throw new ClassCastException("DataReference type cannot be cast to" + clazz);
        return (DataReference<T>) reference;
    }

    public void encode(FriendlyByteBuf buf, T value) {
        encoder.accept(buf, value);
    }

    public Tag serializeNBT(T value) {
        return nbtEncoder.apply(value);
    }

    public T deserializeNBT(Tag tag) {
        return nbtDecoder.apply(tag);
    }

    public T decode(FriendlyByteBuf buf) {
        return decoder.apply(buf);
    }

    public boolean canCreateIntArray() {
        return this == INT || this == LONG || this == FLOAT || this == DOUBLE;
    }

    public ContainerData toIntArray(DataReference<T> reference) {
        if(!canCreateIntArray())
            throw new IllegalArgumentException("Cannot create IntArray from " + clazz);

        return new ExtendedContainerData(reference);
    }

    public boolean equals(T first, Object second) {
        if(!clazz.isAssignableFrom(second.getClass())) return false;
        return equalityPredicate.test(first, (T) second);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public class ExtendedContainerData implements ContainerData {

        private static final long MASK = 0xffffffffffffL;

        private final DataReference<?> ref;

        private final Supplier<Long> longSupplier;

        private final int length;

        private ExtendedContainerData(DataReference<?> ref) {
            this.ref = ref;
            if(ref.getType() != DataType.this) throw new IllegalStateException();
            longSupplier = () -> {
                ref.update();
                if (ref.getType() == FLOAT) return (long) Float.floatToRawIntBits(FLOAT.cast(ref).get());
                if (ref.getType() == DOUBLE) return Double.doubleToRawLongBits(DOUBLE.cast(ref).get());
                if (ref.getType() == INT) return (long) INT.cast(ref).get();
                if (ref.getType() == LONG) return LONG.cast(ref).get();
                throw new IllegalStateException("The data reference type changed magically in a ShortArray");
            };
            length = ref.getType() == INT || ref.getType() == FLOAT ? 2 : 4;
        }

        @Override
        public int get(int index) {
            return (int) ((longSupplier.get() >>> (index << 4)) & 0xffff);
        }

        @Override
        public void set(int index, int value) {
            long initialValue = longSupplier.get();
            long staticValue = initialValue & ((MASK >>> ((3 - index) << 4)) | initialValue & (MASK << ((index + 1) << 4)));
            initialValue = staticValue | ((long) value << (index << 4));
            if(ref.getType() == INT) {
                INT.cast(ref).accept((int) initialValue);
            } else if(ref.getType() == FLOAT) {
                FLOAT.cast(ref).accept(Float.intBitsToFloat((int) initialValue));
            } else if(ref.getType() == LONG) {
                LONG.cast(ref).accept(initialValue);
            } else if(ref.getType() == DOUBLE) {
                DOUBLE.cast(ref).accept(Double.longBitsToDouble(initialValue));
            } else throw new IllegalStateException("The data reference type changed magically in a ReferenceIntArray");
        }

        @Override
        public int getCount() {
            return length;
        }
    }

    private static class Builder<T> {

        private boolean built;

        private Class<T> clazz;
        private T defaultValue;
        private BiConsumer<FriendlyByteBuf, T> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private Function<T, Tag> nbtEncoder;
        private Function<Tag, T> nbtDecoder;
        private BiPredicate<T, T> equalityPredicate;

        private Builder(Class<T> type) {
            this.clazz = type;
            defaultValue = null;
            encoder = null;
            decoder = null;
            nbtEncoder = null;
            nbtDecoder = null;
            equalityPredicate = (a, b) -> a == b;
            built = false;
        }

        public Builder<T> defaultValue(T value) {
            checkNotBuilt();
            defaultValue = value;
            return this;
        }

        public Builder<T> fromBytes(@Nonnull Function<FriendlyByteBuf, T> decoder) {
            checkNotBuilt();
            this.decoder = decoder;
            return this;
        }

        public Builder<T> toBytes(@Nonnull BiConsumer<FriendlyByteBuf, T> encoder) {
            checkNotBuilt();
            this.encoder = encoder;
            return this;
        }

        public Builder<T> serializeNBT(@Nonnull Function<T, Tag> encoder) {
            checkNotBuilt();
            this.nbtEncoder = encoder;
            return this;
        }

        public Builder<T> deserializeNBT(@Nonnull Function<Tag, T> decoder) {
            checkNotBuilt();
            this.nbtDecoder = decoder;
            return this;
        }

        public Builder<T> equalityTest(@Nonnull BiPredicate<T, T> equalityPredicate) {
            checkNotBuilt();
            this.equalityPredicate = equalityPredicate;
            return this;
        }

        public DataType<T> build() {
            checkNotBuilt();

            if(encoder == null ^ decoder == null)
                throw new IllegalStateException("A DataType must have either an encoder and a decoder or none.");

            if(nbtEncoder == null ^ nbtDecoder == null)
                throw new IllegalStateException("A DataType must have either a NBT serializer and a deserializer or none.");

            built = true;
            return new DataType<>(this);
        }

        private void checkNotBuilt() {
            if(built) throw new UnsupportedOperationException("This builder has already built a DataType");
        }
    }
}
