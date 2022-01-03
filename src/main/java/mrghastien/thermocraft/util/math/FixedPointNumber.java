package mrghastien.thermocraft.util.math;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
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
public class FixedPointNumber extends Number implements Comparable<FixedPointNumber> {

    public static final FixedPointNumber ZERO = new FixedPointNumber(0L, (short) 0);

    protected long integral;
    protected short fractional;

    /**Creates a FixedPointNumber from the integer and fractional parts.
     * @param integral The integer part
     * @param fractional The fractional part
     */
    protected FixedPointNumber(long integral, short fractional) {
        this.integral = integral;
        this.fractional = fractional;
    }

    public static FixedPointNumber valueOf(long l) {
        if(l == 0) return ZERO;
        return new FixedPointNumber(l, (short) 0);
    }

    /**Creates a FixedPointNumber from a single-precision-floating-point number.
     *
     * @param number The floating-point number to convert.
     */
    public static FixedPointNumber valueOf(float number) {
        if(number == 0) return ZERO;
        long[] v = convertFromFloat(number);
        return new FixedPointNumber(v[0], longToUnsignedShort(v[1]));
    }

    /**Creates a FixedPointNumber from a double-precision-floating-point number.
     *
     * @param number The double-precision-floating-point number to convert.
     */
    public static FixedPointNumber valueOf(double number) {
        if(number == 0) return ZERO;
        long[] v = convertFromDouble(number);
        return new FixedPointNumber(v[0], longToUnsignedShort(v[1]));
    }

    public FixedPointNumber copy() {
        return new FixedPointNumber(integral, fractional);
    }

    @Override
    public int intValue() {
        return (int)longValue();
    }

    @Override
    public long longValue() {
        return isNegative() ? -negate(integral, fractional)[0] : integral;
    }

    public short getFractionalBits() {
        return fractional;
    }

    public boolean isMutable() {
        return false;
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
        long integral = this.integral;
        short fractional = this.fractional;

        int bits = ((int) (integral >>> 63)) << 31;
        if(isNegative()) {
            long[] v = negate(integral, fractional);
            integral = v[0];
            fractional = longToUnsignedShort(v[1]);
        }

        int exponent = findExponent(integral, fractional);
        int mantissa = ((int)integral & (smartLeftShift(1, exponent) - 1)) & ((1 << 23) - 1);
        mantissa = smartLeftShift(mantissa, 23 - exponent);
        mantissa |= smartLeftShift(Short.toUnsignedInt(fractional), 7 - exponent);

        bits |= (exponent + (1 << 7) - 1) << 23;
        bits |= mantissa & ((1 << 23) - 1);
        return Float.intBitsToFloat(bits);
    }

    @Override
    public double doubleValue() {
        long integral = this.integral;
        short fractional = this.fractional;

        long bits = integral & (1L << 63); //Sign bit
        if(isNegative()) {
            long[] v = negate(integral, fractional);
            integral = v[0];
            fractional = longToUnsignedShort(v[1]);
        }

        long exponent = findExponent(integral, fractional);
        long mantissa = (integral & (smartLeftShift(1L, exponent) - 1));
        mantissa = smartLeftShift(mantissa, 52 - exponent);
        mantissa |= smartLeftShift(Short.toUnsignedLong(fractional), 36 - exponent);

        bits |= (exponent + (1 << 10) - 1) << 52; //Place the exponent bits
        bits |= mantissa & ((1L << 52) - 1);
        return Double.longBitsToDouble(bits);
    }

    @Override
    public String toString() {
        return  toString(16);
    }

    public String toString(int precision) {
        if(isNegative()) {
            long[] values = negate(integral, fractional);
            return "-" + values[0] + "." + fractionalToString(longToUnsignedShort(values[1]), precision);
        }
        return integral + "." + fractionalToString(fractional, precision);
    }

    public FixedPointNumber add(FixedPointNumber n) {
        return add(n.integral, n.fractional);
    }

    public FixedPointNumber add(long n) {
        return add(n, (short) 0);
    }

    public FixedPointNumber add(double n) {
        long[] v = convertFromDouble(n);
        return add(v[0], longToUnsignedShort(v[1]));
    }

    public FixedPointNumber add(float n) {
        long[] v = convertFromFloat(n);
        return add(v[0], longToUnsignedShort(v[1]));
    }

    public FixedPointNumber add(long n, short frac) {
        int sum = Short.toUnsignedInt(fractional) + Short.toUnsignedInt(frac);
        return new FixedPointNumber(integral + n + (sum >> 16), (short) (sum & 0xffff));
    }

    public FixedPointNumber sub(FixedPointNumber n) {
        return add(n.negate());
    }

    public FixedPointNumber sub(double d) {
        long[] v = convertFromDouble(d);
        return sub(v[0], longToUnsignedShort(v[1]));
    }

    public FixedPointNumber sub(long n, short frac) {
        long[] v = negate(n, frac);
        return add(v[0], longToUnsignedShort(v[1]));
    }

    public FixedPointNumber onesComplement() {
        return new FixedPointNumber(~integral, (short) ~fractional);
    }

    public FixedPointNumber negate() {
        long invertedInt = ~integral;
        int invertedFrac = ((~fractional) & 0xffff) + 1;
        if((invertedFrac >>> 16) == 1) invertedInt++;
        return new FixedPointNumber(invertedInt, (short) invertedFrac);
    }

    public boolean isNegative() {
        return (integral >>> 63) == 1;
    }

    public boolean isLessThan(long n) {
        return isLessThan(FixedPointNumber.valueOf(n));
    }

    public boolean isLessOrEqTo(long n) {
        return isLessOrEqTo(FixedPointNumber.valueOf(n));
    }

    public boolean isGreaterThan(long n) {
        return isGreaterThan(FixedPointNumber.valueOf(n));
    }

    public boolean isGreaterOrEqTo(long n) {
        return isGreaterOrEqTo(FixedPointNumber.valueOf(n));
    }

    public int getSignum() {
        return isNegative() ? -1 : this == ZERO ? 0 : 1;
    }

    @Override
    public int compareTo(@Nonnull FixedPointNumber o) {
        int signum = getSignum();
        int otherSignum = o.getSignum();
        if(signum == otherSignum) {
            if(integral == o.integral) {
                return fractional > o.fractional ? signum : fractional == o.fractional ? 0 : -signum;
            }
            return integral > o.integral ? 1 : -1;
        }
        return signum > otherSignum ? 1 : -1;
    }

    public boolean isLessThan(FixedPointNumber n) {
        return compareTo(n) < 0;
    }

    public boolean isLessOrEqTo(FixedPointNumber n) {
        return compareTo(n) <= 0;
    }

    public boolean isGreaterThan(FixedPointNumber n) {
        return compareTo(n) > 0;
    }

    public boolean isGreaterOrEqTo(FixedPointNumber n) {
        return compareTo(n) >= 0;
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

    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeLong(integral);
        buf.writeShort(fractional);
    }

    public FixedPointNumber toImmutable() {
        return this;
    }

    public Mutable toMutable() {
        return new Mutable(this);
    }

    private static byte findExponent(long integral, short fractional) {
        byte exponent = 0;
        if(integral == 0) {
            if(fractional == 0) return 0;
            int frac = Short.toUnsignedInt(fractional);
            while((frac >>> 16) != 1) {
                frac <<= 1;
                exponent--;
            }
        } else {
            while (integral > 1) {
                integral >>>= 1;
                exponent++;
            }
        }
        return exponent;
    }

    private static int smartRightShift(int bits, int shift) {
        if(shift < -31 || shift > 31) return 0;
        if(shift < 0) return bits << -shift;
        return bits >>> shift;
    }

    private static long smartRightShift(long bits, long shift) {
        if(shift < -63 || shift > 63) return 0L;
        if(shift < 0) return bits << -shift;
        return bits >>> shift;
    }

    private static int smartLeftShift(int bits, int shift) {
        if(shift < -31 || shift > 31) return 0;
        if(shift < 0) return bits >>> -shift;
        return bits << shift;
    }

    private static long smartLeftShift(long bits, long shift) {
        if(shift < -63 || shift > 63) return 0L;
        if(shift < 0) return bits >>> -shift;
        return bits << shift;
    }

    private static long[] convertFromFloat(float number) {
        if(number == 0) return new long[] {0L, (short)0};

        int bits = Float.floatToRawIntBits(number);
        int exponent = ((bits & 0x7f800000) >>> 23) - ((1 << 7) - 1);
        //Take the bits of the exponent, shift them to
        // make sure the first bit of the exponent is the first bit,
        // and subtract 127.
        int mantissa = (bits & ((1 << 23) - 1)) | (1 << 23);
        //The mantissa is always between 1 & 2. Thus a bit set to 1 is added prior to all the mantissa bits.
        //It isn't actually stored because it is always 1.
        //Now the integer part of the number is just the mantissa left-shifted by the exponent, equivalent to multiplying by 2^exponent.

        long integral = smartRightShift(mantissa, 23 - exponent); //Only take the integral part of the shifted mantissa
        short fractional = (short) (smartRightShift(mantissa, 7 - exponent) & ((1 << 16) - 1)); //Take the 16 first bits from the left from the

        if((bits >>> 31) == 1) return negate(integral, fractional);
        return new long[] {integral, fractional};
    }

    private static long[] convertFromDouble(double number) {
        if(number == 0) return new long[] {0L, (short)0};

        //Gathering the bit groups
        long bits = Double.doubleToRawLongBits(number);
        long exponent = ((bits & 0x7ff0000000000000L) >>> 52) - ((1 << 10) - 1);
        long mantissa = (bits & ((1L << 52) - 1) ) | (1L << 52);

        //Separating the integer part from the fractional part
        long integral = smartRightShift(mantissa, 52 - exponent);
        short fractional = (short) (smartRightShift(mantissa, 36 - exponent) & ((1 << 16) - 1));

        //Negating if negative
        if((bits >>> 63) == 1) return negate(integral, fractional);
        return new long[] {integral, fractional};
    }

    private static String fractionalToString(short fractional, int precision) {
        int frac = Short.toUnsignedInt(fractional);
        if(frac == 0) return "0";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < precision && frac != 0; i++) {
            frac *= 10;
            int n = frac >> 16;
            builder.append(n);
            frac &= 0xffff; //Only keep the first 16 bits
        }
        return builder.toString();
    }

    private static long[] negate(long integral, short fractional) {
        long invertedInt = ~integral;
        int invertedFrac = ((~fractional) & 0xffff) + 1;
        if((invertedFrac >>> 16) == 1) invertedInt++;
        return new long[] {invertedInt, (short) invertedFrac};
    }

    private static short longToUnsignedShort(long l) {
        return (short) (l & 0xffff);
    }

    public static FixedPointNumber decodeFromBuffer(FriendlyByteBuf buf) {
        return new FixedPointNumber(buf.readLong(), buf.readShort());
    }

    public static FixedPointNumber.Mutable decodeFromBuffer(FriendlyByteBuf buf, FixedPointNumber.Mutable number) {
       number.set(buf.readLong(), buf.readShort());
       return number;
    }

    public static FixedPointNumber deserializeNBT(CompoundTag tag) {
        return new FixedPointNumber(tag.getLong("integral"), tag.getShort("fractional"));
    }

    public static class Mutable extends FixedPointNumber {

        private Mutable(FixedPointNumber number) {
            super(number.integral, number.fractional);
        }

        private Mutable(long integral, short fractional) {
            super(integral, fractional);
        }

        public static Mutable valueOf(long l) {
            return new Mutable(l, (short) 0);
        }

        /**Creates a FixedPointNumber from a single-precision-floating-point number.
         *
         * @param number The floating-point number to convert.
         */
        public static Mutable valueOf(float number) {
            long[] v = convertFromFloat(number);
            return new Mutable(v[0], longToUnsignedShort(v[1]));
        }

        /**Creates a FixedPointNumber from a double-precision-floating-point number.
         *
         * @param number The double-precision-floating-point number to convert.
         */
        public static Mutable valueOf(double number) {
            long[] v = convertFromDouble(number);
            return new Mutable(v[0], longToUnsignedShort(v[1]));
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        public void set(long integer) {
            set(integer, (short) 0);
        }

        public void set(FixedPointNumber number) {
            set(number.integral, number.fractional);
        }

        public void set(long integer, short fraction) {
            this.integral = integer;
            this.fractional = fraction;
        }

        //TODO: make proper conversion
        public void set(double number) {
            long[] v = FixedPointNumber.convertFromDouble(number);
            set(v[0], FixedPointNumber.longToUnsignedShort(v[1]));
        }

        public void set(float number) {
            long[] v = FixedPointNumber.convertFromFloat(number);
            set(v[0], FixedPointNumber.longToUnsignedShort(v[1]));
        }

        public FixedPointNumber add(long n, short frac) {
            int sum = Short.toUnsignedInt(fractional) + Short.toUnsignedInt(frac);
            this.integral = integral + n + ((sum >> 16) & 1);
            this.fractional = (short) (sum & 0xffff);
            return this;
        }

        public FixedPointNumber onesComplement() {
            this.integral = ~integral;
            this.fractional = (short) ~fractional;
            return this;
        }

        public FixedPointNumber negate() {
            long invertedInt = ~integral;
            int invertedFrac = ((short)~fractional) + 1;
            if((invertedFrac >>> 16) == 1) invertedInt++;
            invertedInt = (invertedInt & ((1L << 63) - 1)) | ~(invertedInt >>> 63);
            this.integral = invertedInt;
            this.fractional = (short) invertedFrac;
            return this;
        }

        @Override
        public FixedPointNumber copy() {
            return new Mutable(integral, fractional);
        }

        @Override
        public FixedPointNumber toImmutable() {
            return new FixedPointNumber(integral, fractional);
        }

        @Override
        public Mutable toMutable() {
            return this;
        }
    }
}
