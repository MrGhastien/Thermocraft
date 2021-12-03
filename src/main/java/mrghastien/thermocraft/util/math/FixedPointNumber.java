package mrghastien.thermocraft.util.math;

import mrghastien.thermocraft.common.capabilities.heat.SidedHeatHandler;
import mrghastien.thermocraft.util.Pair;

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

    protected long integral;
    protected short fractional;

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
        Pair<Long, Short> values = convertFromDouble(number);
        this.integral = values.getFirst();
        this.fractional = values.getSecond();
    }

    /**Creates a FixedPointNumber from a single-precision-floating-point number.
     *
     * @param number The floating-point number to convert.
     */
    public FixedPointNumber(float number) {
        Pair<Long, Short> values = convertFromFloat(number);
        this.integral = values.getFirst();
        this.fractional = values.getSecond();
    }

    private static Pair<Long, Short> convertFromFloat(float number) {
        int bits = Float.floatToRawIntBits(number);
        if(bits == 0) return new Pair<>(0L, (short)0);
        int exponent = ((bits & 0x7f800000) >>> 23) - ((1 << 7) - 1);
        //Take the bits of the exponent, shift them to
        // make sure the first bit of the exponent is the first bit,
        // and subtract 127.
        int mantissa = bits & ((1 << 23) - 1);
        //The mantissa is always between 1 & 2. Thus a bit set to 1 is added prior to all the mantissa bits.
        //It isn't actually stored because it is always 1.
        //Now the integer part of the number is just the mantissa left-shifted by the exponent, equivalent to multiplying by 2^exponent.
        long integral = (mantissa | (1 << 23)) >>> (23- exponent); //Only take the integral part of the shifted mantissa
        short fractional = (short) (relativeRightShift(mantissa, 7 - exponent) & ((1 << 16) - 1)); //Take the 16 first bits from the left from the
        return new Pair<>(integral, fractional);
    }

    private static int relativeRightShift(int bits, int shift) {
        if(shift < 0) return bits << -shift;
        return bits >>> shift;
    }

    private static long relativeRightShift(long bits, long shift) {
        if(shift < 0) return bits << -shift;
        return bits >>> shift;
    }

    private static Pair<Long, Short> convertFromDouble(double number) {
        long bits = Double.doubleToRawLongBits(number);
        if(bits == 0) return new Pair<>(0L, (short)0);
        long exponent = ((bits & 0x7ff0000000000000L) >>> 52) - ((1 << 10) - 1);
        long mantissa = bits & ((1L << 52) - 1);
        long integral = (mantissa | (1L << 52)) >>> (52 - exponent);
        short fractional = (short) (relativeRightShift(mantissa, 36 - exponent) & ((1 << 16) - 1));
        return new Pair<>(integral, fractional);
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
        int frac = Short.toUnsignedInt(fractional);
        if(frac == 0) return "0";
        StringBuilder builder = new StringBuilder();
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
        return integral + "." + fractionalToString();
    }

    //TODO: Operations
    public FixedPointNumber add(FixedPointNumber n) {
        return add(n.integral, n.fractional);
    }

    public FixedPointNumber add(long n, short frac) {
        int sum = Short.toUnsignedInt(fractional) + Short.toUnsignedInt(frac);
        return new FixedPointNumber(integral + n + ((sum >> 16) & 1), (short) (sum & 0xffff));
    }

    public FixedPointNumber sub(FixedPointNumber n) {
        return add(n.negate());
    }

    public FixedPointNumber onesComplement() {
        return new FixedPointNumber(~integral, (short) ~fractional);
    }

    //TODO: Signed
    public FixedPointNumber negate() {
        long invertedInt = ~integral;
        int invertedFrac = ((short)~fractional) + 1;
        if((invertedFrac >>> 16) == 1) invertedInt++;
        //invertedInt = (invertedInt & ((1L << 63) - 1)) | ~(invertedInt >>> 63);
        return new FixedPointNumber(invertedInt, (short) invertedFrac);
    }

    public boolean isNegative() {
        return (integral >>> 63) == 1;
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

        public void set(long integer) {
            this.integral = integer;
        }

        public void set(long integer, short fraction) {
            set(integer);
            this.fractional = fraction;
        }

        //TODO: make proper conversion
        public void set(double number) {
            Pair<Long, Short> values = FixedPointNumber.convertFromDouble(number);
            set(values.getFirst(), values.getSecond());
        }

        public void set(float number) {
            Pair<Long, Short> values = FixedPointNumber.convertFromFloat(number);
            set(values.getFirst(), values.getSecond());
        }

        public FixedPointNumber add(long n, short frac) {
            int sum = Short.toUnsignedInt(fractional) + Short.toUnsignedInt(frac);
            this.integral = integral + n + (sum >> 16) & 1;
            this.fractional = (short) (sum & 0xffff);
            return this;
        }

        public FixedPointNumber onesComplement() {
            this.integral = ~integral;
            this.fractional = (short) ~fractional;
            return this;
        }

        //TODO: Signed
        public FixedPointNumber negate() {
            long invertedInt = ~integral;
            int invertedFrac = ((short)~fractional) + 1;
            if((invertedFrac >>> 16) == 1) invertedInt++;
            invertedInt = (invertedInt & ((1L << 63) - 1)) | ~(invertedInt >>> 63);
            this.integral = invertedInt;
            this.fractional = (short) invertedFrac;
            return this;
        }
    }
}
