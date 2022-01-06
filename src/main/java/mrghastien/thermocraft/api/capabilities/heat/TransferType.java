package mrghastien.thermocraft.api.capabilities.heat;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

//Neutral is for connections between cables.
public enum TransferType implements StringRepresentable {
    NONE, INPUT, OUTPUT, BOTH,
    /**
     * For connections between cables only.
     */
    NEUTRAL;

    public static final TransferType[] VALUES = TransferType.values();

    public boolean canReceive() {
        return this == INPUT || this == BOTH;
    }

    public boolean canExtract() {
        return this == OUTPUT || this == BOTH;
    }

    public static TransferType get(boolean canReceive, boolean canExtract) {
        return canExtract && canReceive ? BOTH : canReceive ? INPUT : canExtract ? OUTPUT : NONE;
    }

    public TransferType or(TransferType other) {
        return get(canReceive() || other.canReceive(), canExtract() || other.canExtract());
    }

    public TransferType and(TransferType other) {
        return get(canReceive() && other.canReceive(), canExtract() && other.canExtract());
    }

    public TransferType opposite() {
        return this == INPUT ? OUTPUT : this == OUTPUT ? INPUT : this;
    }

    public boolean canTransfer() {
        return this == INPUT || this == OUTPUT || this == BOTH;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static TransferType fromString(String s) {
        for(TransferType t : VALUES) {
            if(t.getSerializedName().equals(s))
                return t;
        }
        return null;
    }
}
