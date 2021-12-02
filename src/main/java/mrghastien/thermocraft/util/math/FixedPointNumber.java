package mrghastien.thermocraft.util.math;

import mrghastien.thermocraft.common.capabilities.heat.SidedHeatHandler;

import java.util.Objects;

/**This represents a number with 64 bits for the integer part and 16 bits for the fractional part, thus taking 80 bits in memory.
 *
 * This number format is supposed to be used when integer precision is important, and only a few decimal places are needed.
 * Floating point numbers (floats and doubles), which use the IEEE 754 standard, are represented similarly to scientific notation :
 * M * 2^e
 * with M being the mantissa and e the exponent.
 * This allows floating point numbers to have minuscule or enormous values. But this comes at a cost.
 * In fact, because there are only a limited amount of bits representing the mantissa, a floating point number cannot have both huge integer parts
 * and an infinitely small fractional part. As an added bonus, most operations can cause imprecisions (i.e. 2.5 * 2 = 5.00000000000...1)
 * and equality between floating point numbers is very chaotic.
 *
 * On the other hand, fixed point numbers allows for more precise operations and also support bitwise operations (for most implementations,
 * including this one). Fixed-point numbers are also very light in terms of computing power; operations are very fast with integers, and so
 * with fixed-point numbers.
 * There are still some downsides. The most significant one is the range of possible values being very limited compared to floating-point numbers.
 * Solving this problem requires more space in memory.
 *
 * Fixed point numbers are typically used when dealing with economics, because we need unit precision, but don't need very small fractions of a unit
 * of money (in general not less than one 100th / a cent).
 */
public class FixedPointNumber extends Number {

    private final long integral;
    private final short fractional;

    /**Creates a FixedPointNumber from the integer and fractional parts.
     * @param integral The integer part
     * @param fractional The fractional part
     */
    public FixedPointNumber(long integral, short fractional) {
        this.integral = integral;
        this.fractional = fractional;
    }

    /**Creates a FixedPointNumber from a double-precision-floating-point number.
     *
     * @param number The double-precision-floating-point number to convert.
     */
    public FixedPointNumber(double number) {
        this.integral = (long) number;
        this.fractional = mirrorBits(getDoubleMantissa((number % 1.0) + 1.0), 52);
    }

    /**Creates a FixedPointNumber from a single-precision-floating-point number.
     *
     * @param number The floating-point number to convert.
     */
    public FixedPointNumber(float number) {
        this.integral = (long) number;
        this.fractional = mirrorBits(getFloatMantissa((number % 1.0f) + 1.0f), 23);
    }

    private static long getDoubleMantissa(double d) {
        return Double.doubleToRawLongBits(d) & 0x000fffffffffffffL;
    }

    private static int getFloatMantissa(float d) {
        return Float.floatToRawIntBits(d) & 0x007fffff;
    }

    private static short mirrorBits(long bits, int mantissaSize) {
        short result = 0;
        bits >>>= mantissaSize - 16; //Only take the first 16 bits
        for(int i = 0; i < 16; i++) {
            result |= bits & (1L << 16 - i);
        }
        return result;
    }

    @Override
    public int intValue() {
        return (int) integral;
    }

    @Override
    public long longValue() {
        return integral;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedPointNumber that = (FixedPointNumber) o;
        return integral == that.integral && fractional == that.fractional;
    }

    @Override
    public int hashCode() {
        return Objects.hash(integral, fractional);
    }

    //TODO: Fixed to floating point conversion
    @Override
    public float floatValue() {
        return 0.0f;
    }

    @Override
    public double doubleValue() {
        return 0.0;
    }

    private String fractionalToString() {
        StringBuilder builder = new StringBuilder();
        int frac = Short.toUnsignedInt(fractional);
        while(frac != 0) {
            frac *= 10;
            int n = frac >> 16;
            builder.append(n);
            frac &= 0xffff; //Only keep the first 16 bits
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(integral) + "." + fractionalToString();
    }

    //TODO: Operations
    public FixedPointNumber add(FixedPointNumber n) {
        int sum = Short.toUnsignedInt(fractional) + Short.toUnsignedInt(n.fractional);
        return new FixedPointNumber(integral + n.integral + (sum >> 16) & 1, (short) (sum & 0xffff));
    }

    public FixedPointNumber sub(FixedPointNumber n) {
        int sum = fractional - n.fractional;
        return new FixedPointNumber(integral - n.integral - sum < 0 ? 1 : 0, (short) Math.abs(fractional - n.fractional));
    }

    public FixedPointNumber onesComplement() {
        return new FixedPointNumber(~integral, (short) ~fractional);
    }

    //TODO: Signed
    public FixedPointNumber twoComplement() {

        return null;
    }

    public boolean isNegative() {
        return (integral & 1L << 63) == 1;
    }

    public byte[] toByteArray() {
        byte[] array = new byte[10];
        for(int o = 0; o < 8; o++) {
            array[o] = (byte) (integral & 0xff << (o * 0xff));
        }

        for(int o = 0; o < 2; o++) {
            array[8 + o] = (byte) (fractional & 0xff << (o * 0xff));
        }
        return array;
    }

    public static class Mutable extends FixedPointNumber {

        public Mutable(long integral, short fractional) {
            super(integral, fractional);
        }

        public Mutable(double number) {
            super(number);
        }

        public Mutable(float number) {
            super(number);
        }
    }
}
