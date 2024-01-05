package zserio.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FloatUtilTest
{
    @Test
    public void convertShortToFloat()
    {
        // plus zero
        final short float16ValuePlusZero = createFloat16Value((short)0, (short)0, (short)0); // +0.0
        checkFloat16ToFloat32Conversion(float16ValuePlusZero, 0.0f);

        // minus zero
        final short float16ValueMinusZero = createFloat16Value((short)1, (short)0, (short)0); // -0.0
        checkFloat16ToFloat32Conversion(float16ValueMinusZero, -0.0f);

        // plus infinity
        final short float16ValuePlusInfinity = createFloat16Value((short)0, (short)0x1F, (short)0); // +INF
        final int float32ValuePlusInfinity = createFloat32Value(0, 0xFF, 0); // +INF
        checkFloat16ToFloat32Conversion(float16ValuePlusInfinity, float32ValuePlusInfinity);

        // minus infinity
        final short float16ValueMinusInfinity = createFloat16Value((short)1, (short)0x1F, (short)0); // -INF
        final int float32ValueMinusInfinity = createFloat32Value(1, 0xFF, 0); // -INF
        checkFloat16ToFloat32Conversion(float16ValueMinusInfinity, float32ValueMinusInfinity);

        // quiet NaN (Java uses only the 1st significand bit in NaN)
        final short float16ValueQuietNan = createFloat16Value((short)0, (short)0x1F, (short)0x3FF); // +NaN
        final int float32ValueQuietNan = createFloat32Value(0, 0xFF, 0x400000); // +NaN
        checkFloat16ToFloat32Conversion(float16ValueQuietNan, float32ValueQuietNan);

        // signaling NaN (Java uses only quiet NaN)
        // -NaN
        final short float16ValueSignalingNan = createFloat16Value((short)1, (short)0x1F, (short)0x3FF);
        checkFloat16ToFloat32Conversion(float16ValueSignalingNan, float32ValueQuietNan);

        // normal numbers
        final short float16ValueOne = createFloat16Value((short)0, (short)15, (short)0); // 1.0
        checkFloat16ToFloat32Conversion(float16ValueOne, 1.0f);

        // 1.0 + 2^-10
        final short float16ValueOnePlus = createFloat16Value((short)0, (short)15, (short)0x01);
        // 1.0 + 2^-10
        final int float32ValueOnePlus = createFloat32Value(0, 127, 0x2000);
        checkFloat16ToFloat32Conversion(float16ValueOnePlus, float32ValueOnePlus);

        // 2^15 (1 + 2^-1 + ... + 2^-10)
        final short float16ValueMax = createFloat16Value((short)0, (short)30, (short)0x3FF);
        checkFloat16ToFloat32Conversion(float16ValueMax, 65504.0f);

        // subnormal numbers
        // 2^-14 (2^-10)
        final short float16ValueMinSubnormal = createFloat16Value((short)0, (short)0, (short)1);
        // 2^-24
        final int float32ValueMinSubnormal = createFloat32Value(0, 103, 0);
        checkFloat16ToFloat32Conversion(float16ValueMinSubnormal, float32ValueMinSubnormal);

        // 2^-14 (2^-1 + ... + 2^-10)
        final short float16ValueMaxSubnormal = createFloat16Value((short)0, (short)0, (short)0x3FF);
        // 2^-15 (1 + 2^-1 + ... + 2^-9)
        final int float32ValueMaxSubnormal = createFloat32Value(0, 112, 0x7FC000);
        checkFloat16ToFloat32Conversion(float16ValueMaxSubnormal, float32ValueMaxSubnormal);
    }

    @Test
    public void convertFloatToShort()
    {
        // plus zero
        final short float16ValuePlusZero = createFloat16Value((short)0, (short)0, (short)0); // +0.0
        checkFloat32ToFloat16Conversion(0.0f, float16ValuePlusZero);

        // minus zero
        final short float16ValueMinusZero = createFloat16Value((short)1, (short)0, (short)0); // -0.0
        checkFloat32ToFloat16Conversion(-0.0f, float16ValueMinusZero);

        // plus infinity
        final int float32ValuePlusInfinity = createFloat32Value(0, 0xFF, 0); // +INF
        final short float16ValuePlusInfinity = createFloat16Value((short)0, (short)0x1F, (short)0); // +INF
        checkFloat32ToFloat16Conversion(float32ValuePlusInfinity, float16ValuePlusInfinity);

        // minus infinity
        final int float32ValueMinusInfinity = createFloat32Value(1, 0xFF, 0); // -INF
        final short float16ValueMinusInfinity = createFloat16Value((short)1, (short)0x1F, (short)0); // -INF
        checkFloat32ToFloat16Conversion(float32ValueMinusInfinity, float16ValueMinusInfinity);

        // quiet NaN (Java uses only the 1st significand bit in NaN)
        final int float32ValueQuietNan = createFloat32Value(0, 0xFF, 0x7FE000); // +NaN
        final short float16ValueQuietNan = createFloat16Value((short)0, (short)0x1F, (short)0x200); // +NaN
        checkFloat32ToFloat16Conversion(float32ValueQuietNan, float16ValueQuietNan);

        // signaling NaN (Java uses only quiet NaN)
        final int float32ValueSignalingNan = createFloat32Value(1, 0xFF, 0x7FE000); // -NaN
        checkFloat32ToFloat16Conversion(float32ValueSignalingNan, float16ValueQuietNan);

        // normal numbers
        final short float16ValueOne = createFloat16Value((short)0, (short)15, (short)0); // 1.0
        checkFloat32ToFloat16Conversion(1.0f, float16ValueOne);

        final int float32ValueOnePlus = createFloat32Value(0, 127, 0x2000); // 1.0 + 2^-10
        final short float16ValueOnePlus = createFloat16Value((short)0, (short)15, (short)0x01); // 1.0 + 2^-10
        checkFloat32ToFloat16Conversion(float32ValueOnePlus, float16ValueOnePlus);

        final short float16ValueMax =
                createFloat16Value((short)0, (short)30, (short)0x3FF); // 2^15 (1 + 2^-1 + ... + 2^-10)
        checkFloat32ToFloat16Conversion(65504.0f, float16ValueMax);

        // normal numbers converted to zero
        final int float32ValueUnderflow = createFloat32Value(0, 102, 0); // 2^-25
        checkFloat32ToFloat16Conversion(float32ValueUnderflow, float16ValuePlusZero);

        // normal numbers converted to subnormal numbers
        final int float32ValueMinUnderflow = createFloat32Value(0, 103, 1); // 2^-24 (1 + 2^-23)
        final short float16ValueMinSubnormal = createFloat16Value((short)0, (short)0, (short)1); // 2^-24
        checkFloat32ToFloat16Conversion(float32ValueMinUnderflow, float16ValueMinSubnormal);

        // normal numbers converted to subnormal numbers with rounding
        final int float32ValueMinUnderflowRounding = createFloat32Value(0, 104, 0x200000); // 2^-23 (1 + 2^-2)
        final short float16ValueMinSubnormalRounding =
                createFloat16Value((short)0, (short)0, (short)0x3); // 2^-14 (2^-9 + 2^-10)
        checkFloat32ToFloat16Conversion(float32ValueMinUnderflowRounding, float16ValueMinSubnormalRounding);

        // normal numbers converted to infinity
        final int float32ValueOverflow = createFloat32Value(0, 144, 0); // 2^17
        checkFloat32ToFloat16Conversion(float32ValueOverflow, float16ValuePlusInfinity);

        // normal numbers converted with rounding
        final int float32ValueRounding = createFloat32Value(0, 127, 0x401000); // 1 + 2^-1 + 2^-11
        final short float16ValueRounding =
                createFloat16Value((short)0, (short)15, (short)0x201); // 1 + 2^-1 + 2^-10
        checkFloat32ToFloat16Conversion(float32ValueRounding, float16ValueRounding);

        // subnormal numbers
        final int float32ValueMinSubnormal = createFloat32Value(0, 0, 1); // 2^-126 (2^-23)
        checkFloat32ToFloat16Conversion(float32ValueMinSubnormal, float16ValuePlusZero);

        // 2^-126 (2^-1 + ... + 2^-23)
        final int float32ValueMaxSubnormal = createFloat32Value(0, 0, 0x007FFFFF);
        checkFloat32ToFloat16Conversion(float32ValueMaxSubnormal, float16ValuePlusZero);
    }

    @Test
    public void convertIntToFloat()
    {
        for (TestFloat32Element testElement : TEST_FLOAT32_DATA)
        {
            final int float32Value =
                    createFloat32Value(testElement.sign, testElement.exponent, testElement.significand);
            final float convertedFloat = FloatUtil.convertIntToFloat(float32Value);

            assertEquals(Float.toString(testElement.expectedFloat), Float.toString(convertedFloat));
        }
    }

    @Test
    public void convertFloatToInt()
    {
        for (TestFloat32Element testElement : TEST_FLOAT32_DATA)
        {
            final int convertedFloatValue = FloatUtil.convertFloatToInt(testElement.expectedFloat);
            final int expectedFloatValue =
                    createFloat32Value(testElement.sign, testElement.exponent, testElement.significand);

            assertEquals(expectedFloatValue, convertedFloatValue);
        }
    }

    @Test
    public void convertLongToDouble()
    {
        for (TestFloat64Element testElement : TEST_FLOAT64_DATA)
        {
            final long float64Value =
                    createFloat64Value(testElement.sign, testElement.exponent, testElement.significand);
            final double convertedDouble = FloatUtil.convertLongToDouble(float64Value);

            assertEquals(Double.toString(testElement.expectedDouble), Double.toString(convertedDouble));
        }
    }

    @Test
    public void convertDoubleToLong()
    {
        for (TestFloat64Element testElement : TEST_FLOAT64_DATA)
        {
            final long convertedDoubleValue = FloatUtil.convertDoubleToLong(testElement.expectedDouble);
            final long expectedDoubleValue =
                    createFloat64Value(testElement.sign, testElement.exponent, testElement.significand);

            assertEquals(expectedDoubleValue, convertedDoubleValue);
        }
    }

    private static short createFloat16Value(short sign, short exponent, short significand)
    {
        return (short)((sign << FLOAT16_SIGN_BIT_POSITION) | (exponent << FLOAT16_EXPONENT_BIT_POSITION) |
                significand);
    }

    private static int createFloat32Value(int sign, int exponent, int significand)
    {

        return (sign << FLOAT32_SIGN_BIT_POSITION) | (exponent << FLOAT32_EXPONENT_BIT_POSITION) | significand;
    }

    private static long createFloat64Value(long sign, long exponent, long significand)
    {

        return (sign << FLOAT64_SIGN_BIT_POSITION) | (exponent << FLOAT64_EXPONENT_BIT_POSITION) | significand;
    }

    private static void checkFloat16ToFloat32Conversion(short float16Value, int expectedFloat32Value)
    {
        final float float32 = FloatUtil.convertShortToFloat(float16Value);
        assertEquals(expectedFloat32Value, Float.floatToIntBits(float32));
    }

    private static void checkFloat16ToFloat32Conversion(short float16Value, float expectedFloat32)
    {
        assertEquals(
                Float.toString(expectedFloat32), Float.toString(FloatUtil.convertShortToFloat(float16Value)));
    }

    private static void checkFloat32ToFloat16Conversion(int float32Value, short expectedFloat16Value)
    {
        final float float32 = Float.intBitsToFloat(float32Value);
        assertEquals(expectedFloat16Value, FloatUtil.convertFloatToShort(float32));
    }

    private static void checkFloat32ToFloat16Conversion(float float32, short expectedFloat16Value)
    {
        assertEquals(expectedFloat16Value, FloatUtil.convertFloatToShort(float32));
    }

    private static class TestFloat32Element
    {
        public TestFloat32Element(int sign, int exponent, int significand, float expectedFloat)
        {
            this.sign = sign;
            this.exponent = exponent;
            this.significand = significand;
            this.expectedFloat = expectedFloat;
        }

        public int sign;
        public int exponent;
        public int significand;
        public float expectedFloat;
    };

    private static class TestFloat64Element
    {
        public TestFloat64Element(long sign, long exponent, long significand, double expectedDouble)
        {
            this.sign = sign;
            this.exponent = exponent;
            this.significand = significand;
            this.expectedDouble = expectedDouble;
        }

        public long sign;
        public long exponent;
        public long significand;
        public double expectedDouble;
    };

    private static final TestFloat32Element TEST_FLOAT32_DATA[] = {
            new TestFloat32Element(0, 0, 0, 0.0f),
            new TestFloat32Element(1, 0, 0, -0.0f),
            new TestFloat32Element(0, 127, 0, +1.0f),
            new TestFloat32Element(1, 127, 0, -1.0f),
            // 2^1 (1 + 2^-1 + 2^-2)
            new TestFloat32Element(0, 128, 0x600000, 3.5f),
            // 2^-1 (1 + 2^-1 + 2^-2)
            new TestFloat32Element(0, 126, 0x600000, 0.875f),
            // 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
            new TestFloat32Element(0, 130, 0x1E0000, 9.875f),
            // 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
            new TestFloat32Element(0, 126, 0x1E0000, 0.6171875f),
    };

    private static final TestFloat64Element TEST_FLOAT64_DATA[] = {
            new TestFloat64Element(0, 0, 0, 0.0),
            new TestFloat64Element(1, 0, 0, -0.0),
            new TestFloat64Element(0, 1023, 0, +1.0f),
            new TestFloat64Element(1, 1023, 0, -1.0f),
            // 2^1 (1 + 2^-1 + 2^-2)
            new TestFloat64Element(0, 1024, 0xC000000000000L, 3.5f),
            // 2^-1 (1 + 2^-1 + 2^-2)
            new TestFloat64Element(0, 1022, 0xC000000000000L, 0.875f),
            // 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
            new TestFloat64Element(0, 1026, 0x3C00000000000L, 9.875f),
            // 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
            new TestFloat64Element(0, 1022, 0x3C00000000000L, 0.6171875f),
    };

    private static final short FLOAT16_SIGN_BIT_POSITION = 15;
    private static final short FLOAT16_EXPONENT_BIT_POSITION = 10;

    private static final int FLOAT32_SIGN_BIT_POSITION = 31;
    private static final int FLOAT32_EXPONENT_BIT_POSITION = 23;

    private static final long FLOAT64_SIGN_BIT_POSITION = 63;
    private static final long FLOAT64_EXPONENT_BIT_POSITION = 52;
};
