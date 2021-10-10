package mrghastien.thermocraft.util.math;

import com.google.common.net.HostSpecifier;

public class FloatLong extends Number {

    private final long number;
    private final short decimal;

    //TODO
    public FloatLong(long number, short decimal) {
        this.number = number;
        this.decimal = decimal;
    }

    @Override
    public int intValue() {
        return (int) number;
    }

    @Override
    public long longValue() {
        return number;
    }

    @Override
    public float floatValue() {
        return (float) number + ((float) decimal) / ((float) (1 << 16));
    }

    @Override
    public double doubleValue() {
        return (double) number + ((double) decimal) / ((double) (1 << 16));
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(number) + "." + ((double) decimal) / ((double) (1 << 16));
    }

    public FloatLong add(FloatLong n) {
        int sum = decimal + n.decimal;
        int offset = sum >> 16;
        return new FloatLong(number + n.number + offset > 0 ? 1 : 0, (short) (decimal + n.decimal));
    }

    public FloatLong sub(FloatLong n) {
        int sum = decimal - n.decimal;
        return new FloatLong(number - n.number - sum < 0 ? 1 : 0, (short) Math.abs(decimal - n.decimal));
    }
}
