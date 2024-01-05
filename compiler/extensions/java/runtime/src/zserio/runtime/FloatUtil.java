package zserio.runtime;

/**
 * The class provides help methods for manipulation with float numbers.
 *
 * The following float formats defined by IEEE 754 standard are supported:
 *
 * - half precision float point format (https://en.wikipedia.org/wiki/Half-precision_floating-point_format)
 * - single precision float point format (https://en.wikipedia.org/wiki/Single-precision_floating-point_format)
 * - double precision float point format (https://en.wikipedia.org/wiki/Double-precision_floating-point_format)
 */
public final class FloatUtil
{
    /**
     * Converts 16-bit float stored in short value to 32-bit float.
     *
     * @param float16Value Half precision float value stored in short to convert.
     *
     * @return Converted single precision float.
     */
    public static float convertShortToFloat(short float16Value)
    {
        // decompose half precision float (float16)
        final short sign16Shifted = (short)(float16Value & FLOAT16_SIGN_MASK);
        final short exponent16 =
                (short)((float16Value & FLOAT16_EXPONENT_MASK) >> FLOAT16_EXPONENT_BIT_POSITION);
        final short significand16 = (short)(float16Value & FLOAT16_SIGNIFICAND_MASK);

        // calculate significand for single precision float (float32)
        int significand32 = ((int)significand16)
                << (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS);

        // calculate exponent for single precision float (float32)
        int exponent32;
        if (exponent16 == 0)
        {
            if (significand32 != 0)
            {
                // subnormal (denormal) number will be normalized
                exponent32 = 1 - FLOAT16_EXPONENT_BIAS + FLOAT32_EXPONENT_BIAS; // exp is initialized by -14
                // shift significand until leading bit overflows into exponent bit
                while ((significand32 & (FLOAT32_SIGNIFICAND_MASK + 1)) == 0)
                {
                    exponent32--;
                    significand32 <<= 1;
                }
                // mask out overflowed leading bit from significand (normalized has implicit leading bit 1)
                significand32 &= FLOAT32_SIGNIFICAND_MASK;
            }
            else
            {
                // zero
                exponent32 = 0;
            }
        }
        else if (exponent16 == FLOAT16_EXPONENT_INFINITY_NAN)
        {
            // infinity or NaN
            exponent32 = FLOAT32_EXPONENT_INFINITY_NAN;
        }
        else
        {
            // normal number
            exponent32 = exponent16 - FLOAT16_EXPONENT_BIAS + FLOAT32_EXPONENT_BIAS;
        }

        // compose single precision float (float32)
        final int sign32Shifted = (int)(sign16Shifted)
                << (FLOAT32_SIGN_BIT_POSITION - FLOAT16_SIGN_BIT_POSITION);
        final int exponent32Shifted = exponent32 << FLOAT32_EXPONENT_BIT_POSITION;
        final int float32Value = sign32Shifted | exponent32Shifted | significand32;

        // convert it to float
        return convertIntToFloat(float32Value);
    }

    /**
     * Converts 32-bit float to 16-bit float stored in short value.
     *
     * @param float32 Single precision float to convert.
     *
     * @return Converted half precision float value stored in short.
     */
    public static short convertFloatToShort(float float32)
    {
        final int float32Value = convertFloatToInt(float32);

        // decompose single precision float (float32)
        final int sign32Shifted = (float32Value & FLOAT32_SIGN_MASK);
        final int exponent32 = (float32Value & FLOAT32_EXPONENT_MASK) >> FLOAT32_EXPONENT_BIT_POSITION;
        final int significand32 = (float32Value & FLOAT32_SIGNIFICAND_MASK);

        // calculate significand for half precision float (float16)
        short significand16 =
                (short)((significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS)));

        // calculate exponent for half precision float (float16)
        boolean needsRounding = false;
        short exponent16;
        if (exponent32 == 0)
        {
            if (significand32 != 0)
            {
                // subnormal (denormal) number will be zero
                significand16 = 0;
            }
            exponent16 = 0;
        }
        else if (exponent32 == FLOAT32_EXPONENT_INFINITY_NAN)
        {
            // infinity or NaN
            exponent16 = FLOAT16_EXPONENT_INFINITY_NAN;
        }
        else
        {
            // normal number
            final short signedExponent16 = (short)(exponent32 - FLOAT32_EXPONENT_BIAS + FLOAT16_EXPONENT_BIAS);
            if (signedExponent16 > FLOAT16_EXPONENT_INFINITY_NAN)
            {
                // exponent overflow, set infinity or NaN
                exponent16 = FLOAT16_EXPONENT_INFINITY_NAN;
            }
            else if (signedExponent16 <= 0)
            {
                // exponent underflow
                if (signedExponent16 <= (short)(-FLOAT16_SIGNIFICAND_NUM_BITS))
                {
                    // too big underflow, set to zero
                    exponent16 = 0;
                    significand16 = 0;
                }
                else
                {
                    // we can still use subnormal numbers
                    exponent16 = 0;
                    final int fullSignificand32 = significand32 | (FLOAT32_SIGNIFICAND_MASK + 1);
                    final int significandShift = 1 - signedExponent16;
                    significand16 = (short)(fullSignificand32 >>
                            (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS + significandShift));

                    needsRounding =
                            ((fullSignificand32 >>
                                     (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS +
                                             significandShift - 1)) &
                                    0x01) != 0;
                }
            }
            else
            {
                // exponent ok
                exponent16 = signedExponent16;
                needsRounding =
                        ((significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS - 1)) &
                                0x01) != 0;
            }
        }

        // compose half precision float (float16)
        final short sign16Shifted =
                (short)(sign32Shifted >>> (FLOAT32_SIGN_BIT_POSITION - FLOAT16_SIGN_BIT_POSITION));
        final short exponent16Shifted = (short)(exponent16 << FLOAT16_EXPONENT_BIT_POSITION);
        short float16Value = (short)(sign16Shifted | exponent16Shifted | significand16);

        // check rounding
        if (needsRounding)
            float16Value += (short)1; // might overflow to infinity

        return float16Value;
    }

    /**
     * Converts 32-bit float stored in int value to 32-bit float.
     *
     * @param float32Value Single precision float value stored in int to convert.
     *
     * @return Converted single precision float.
     */
    public static float convertIntToFloat(int float32Value)
    {
        return Float.intBitsToFloat(float32Value);
    }

    /**
     * Converts 32-bit float to 32-bit float stored in int value.
     *
     * @param float32 Single precision float to convert.
     *
     * @return Converted single precision float value stored in int.
     */
    public static int convertFloatToInt(float float32)
    {
        return Float.floatToIntBits(float32);
    }

    /**
     * Converts 64-bit float (double) stored in long value to 64-bit float (double).
     *
     * @param float64Value Double precision float value stored in long to convert.
     *
     * @return Converted double precision float.
     */
    public static double convertLongToDouble(long float64Value)
    {
        return Double.longBitsToDouble(float64Value);
    }

    /**
     * Converts 64-bit float (double) to 64-bit float (double) stored in long value.
     *
     * @param float64 Double precision float to convert.
     *
     * @return Converted double precision float value stored in long.
     */
    public static long convertDoubleToLong(double float64)
    {
        return Double.doubleToLongBits(float64);
    }

    private static final short FLOAT16_SIGN_MASK = (short)0x8000;
    private static final short FLOAT16_EXPONENT_MASK = (short)0x7C00;
    private static final short FLOAT16_SIGNIFICAND_MASK = (short)0x03FF;

    private static final short FLOAT16_SIGN_BIT_POSITION = 15;
    private static final short FLOAT16_EXPONENT_BIT_POSITION = 10;

    private static final short FLOAT16_SIGNIFICAND_NUM_BITS = FLOAT16_EXPONENT_BIT_POSITION;

    private static final short FLOAT16_EXPONENT_INFINITY_NAN = (short)0x001F;
    private static final short FLOAT16_EXPONENT_BIAS = 15;

    private static final int FLOAT32_SIGN_MASK = 0x80000000;
    private static final int FLOAT32_EXPONENT_MASK = 0x7F800000;
    private static final int FLOAT32_SIGNIFICAND_MASK = 0x007FFFFF;

    private static final int FLOAT32_SIGN_BIT_POSITION = 31;
    private static final int FLOAT32_EXPONENT_BIT_POSITION = 23;

    private static final int FLOAT32_SIGNIFICAND_NUM_BITS = FLOAT32_EXPONENT_BIT_POSITION;

    private static final int FLOAT32_EXPONENT_INFINITY_NAN = 0x00FF;
    private static final int FLOAT32_EXPONENT_BIAS = 127;
}
