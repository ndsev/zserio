#include <array>

#include "gtest/gtest.h"
#include "zserio/FloatUtil.h"

namespace zserio
{

class FloatUtilTest : public ::testing::Test
{
protected:
    uint16_t createFloat16Value(uint16_t sign, uint16_t exponent, uint16_t significand)
    {
        return static_cast<uint16_t>((static_cast<uint32_t>(sign) << FLOAT16_SIGN_BIT_POSITION) |
                (static_cast<uint32_t>(exponent) << FLOAT16_EXPONENT_BIT_POSITION) | significand);
    }

    uint32_t createFloat32Value(uint32_t sign, uint32_t exponent, uint32_t significand)
    {
        return (sign << FLOAT32_SIGN_BIT_POSITION) | (exponent << FLOAT32_EXPONENT_BIT_POSITION) | significand;
    }

    uint64_t createFloat64Value(uint64_t sign, uint64_t exponent, uint64_t significand)
    {
        return (sign << FLOAT64_SIGN_BIT_POSITION) | (exponent << FLOAT64_EXPONENT_BIT_POSITION) | significand;
    }

    void checkFloat16ToFloat32Conversion(uint16_t float16Value, uint32_t expectedFloat32Value)
    {
        const float float32 = convertUInt16ToFloat(float16Value);
        ASSERT_EQ(expectedFloat32Value, convertFloatToUInt32(float32));
    }

    void checkFloat16ToFloat32Conversion(uint16_t float16Value, float expectedFloat32)
    {
        ASSERT_EQ(expectedFloat32, convertUInt16ToFloat(float16Value));
    }

    void checkFloat32ToFloat16Conversion(uint32_t float32Value, uint16_t expectedFloat16Value)
    {
        const float float32 = convertUInt32ToFloat(float32Value);
        ASSERT_EQ(expectedFloat16Value, convertFloatToUInt16(float32));
    }

    void checkFloat32ToFloat16Conversion(float float32, uint16_t expectedFloat16Value)
    {
        ASSERT_EQ(expectedFloat16Value, convertFloatToUInt16(float32));
    }

    struct TestFloat32Element
    {
        uint32_t sign;
        uint32_t exponent;
        uint32_t significand;
        float expectedFloat;
    };

    struct TestFloat64Element
    {
        uint64_t sign;
        uint64_t exponent;
        uint64_t significand;
        double expectedDouble;
    };

    static const std::array<TestFloat32Element, 8> TEST_FLOAT32_DATA;
    static const std::array<TestFloat64Element, 8> TEST_FLOAT64_DATA;

private:
    static const uint16_t FLOAT16_SIGN_BIT_POSITION;
    static const uint16_t FLOAT16_EXPONENT_BIT_POSITION;

    static const uint32_t FLOAT32_SIGN_BIT_POSITION;
    static const uint32_t FLOAT32_EXPONENT_BIT_POSITION;

    static const uint64_t FLOAT64_SIGN_BIT_POSITION;
    static const uint64_t FLOAT64_EXPONENT_BIT_POSITION;
};

const std::array<FloatUtilTest::TestFloat32Element, 8> FloatUtilTest::TEST_FLOAT32_DATA = {
        TestFloat32Element{0, 0, UINT32_C(0), 0.0F}, //
        TestFloat32Element{1, 0, UINT32_C(0), -0.0F}, //
        TestFloat32Element{0, 127, UINT32_C(0), +1.0F}, //
        TestFloat32Element{1, 127, UINT32_C(0), -1.0F}, //
        TestFloat32Element{0, 128, UINT32_C(0x600000), 3.5F}, // 2^1 (1 + 2^-1 + 2^-2)
        TestFloat32Element{0, 126, UINT32_C(0x600000), 0.875F}, // 2^-1 (1 + 2^-1 + 2^-2)
        TestFloat32Element{0, 130, UINT32_C(0x1E0000), 9.875F}, // 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
        TestFloat32Element{0, 126, UINT32_C(0x1E0000), 0.6171875F} // 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
};

const std::array<FloatUtilTest::TestFloat64Element, 8> FloatUtilTest::TEST_FLOAT64_DATA = {
        TestFloat64Element{0, 0, UINT64_C(0), 0.0}, //
        TestFloat64Element{1, 0, UINT64_C(0), -0.0}, //
        TestFloat64Element{0, 1023, UINT64_C(0), +1.0}, //
        TestFloat64Element{1, 1023, UINT64_C(0), -1.0}, //
        TestFloat64Element{0, 1024, UINT64_C(0xC000000000000), 3.5}, // 2^1 (1 + 2^-1 + 2^-2)
        TestFloat64Element{0, 1022, UINT64_C(0xC000000000000), 0.875}, // 2^-1 (1 + 2^-1 + 2^-2)
        TestFloat64Element{0, 1026, UINT64_C(0x3C00000000000), 9.875}, // 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
        TestFloat64Element{
                0, 1022, UINT64_C(0x3C00000000000), 0.6171875} // 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
};

const uint16_t FloatUtilTest::FLOAT16_SIGN_BIT_POSITION = UINT16_C(15);
const uint16_t FloatUtilTest::FLOAT16_EXPONENT_BIT_POSITION = UINT16_C(10);

const uint32_t FloatUtilTest::FLOAT32_SIGN_BIT_POSITION = UINT16_C(31);
const uint32_t FloatUtilTest::FLOAT32_EXPONENT_BIT_POSITION = UINT16_C(23);

const uint64_t FloatUtilTest::FLOAT64_SIGN_BIT_POSITION = UINT16_C(63);
const uint64_t FloatUtilTest::FLOAT64_EXPONENT_BIT_POSITION = UINT16_C(52);

TEST_F(FloatUtilTest, convertUInt16ToFloat)
{
    // plus zero
    const uint16_t float16ValuePlusZero = createFloat16Value(0, 0, 0); // +0.0
    checkFloat16ToFloat32Conversion(float16ValuePlusZero, 0.0F);

    // minus zero
    const uint16_t float16ValueMinusZero = createFloat16Value(1, 0, 0); // -0.0
    checkFloat16ToFloat32Conversion(float16ValueMinusZero, -0.0F);

    // plus infinity
    const uint16_t float16ValuePlusInfinity = createFloat16Value(0, 0x1F, 0); // +INF
    const uint32_t float32ValuePlusInfinity = createFloat32Value(0, 0xFF, 0); // +INF
    checkFloat16ToFloat32Conversion(float16ValuePlusInfinity, float32ValuePlusInfinity);

    // minus infinity
    const uint16_t float16ValueMinusInfinity = createFloat16Value(1, 0x1F, 0); // -INF
    const uint32_t float32ValueMinusInfinity = createFloat32Value(1, 0xFF, 0); // -INF
    checkFloat16ToFloat32Conversion(float16ValueMinusInfinity, float32ValueMinusInfinity);

    // quiet NaN
    const uint16_t float16ValueQuietNan = createFloat16Value(0, 0x1F, 0x3FF); // +NaN
    const uint32_t float32ValueQuietNan = createFloat32Value(0, 0xFF, 0x7FE000); // +NaN
    checkFloat16ToFloat32Conversion(float16ValueQuietNan, float32ValueQuietNan);

    // signaling NaN
    const uint16_t float16ValueSignalingNan = createFloat16Value(1, 0x1F, 0x3FF); // -NaN
    const uint32_t float32ValueSignalingNan = createFloat32Value(1, 0xFF, 0x7FE000); // -NaN
    checkFloat16ToFloat32Conversion(float16ValueSignalingNan, float32ValueSignalingNan);

    // normal numbers
    const uint16_t float16ValueOne = createFloat16Value(0, 15, 0); // 1.0
    checkFloat16ToFloat32Conversion(float16ValueOne, 1.0F);

    const uint16_t float16ValueOnePlus = createFloat16Value(0, 15, 0x01); // 1.0 + 2^-10
    const uint32_t float32ValueOnePlus = createFloat32Value(0, 127, 0x2000); // 1.0 + 2^-10
    checkFloat16ToFloat32Conversion(float16ValueOnePlus, float32ValueOnePlus);

    const uint16_t float16ValueMax = createFloat16Value(0, 30, 0x3FF); // 2^15 (1 + 2^-1 + ... + 2^-10)
    checkFloat16ToFloat32Conversion(float16ValueMax, 65504.0F);

    // subnormal numbers
    const uint16_t float16ValueMinSubnormal = createFloat16Value(0, 0, 1); // 2^-14 (2^-10)
    const uint32_t float32ValueMinSubnormal = createFloat32Value(0, 103, 0); // 2^-24
    checkFloat16ToFloat32Conversion(float16ValueMinSubnormal, float32ValueMinSubnormal);

    const uint16_t float16ValueMaxSubnormal = createFloat16Value(0, 0, 0x3FF); // 2^-14 (2^-1 + ... + 2^-10)
                                                                               // 2^-15 (1 + 2^-1 + ... + 2^-9)
    const uint32_t float32ValueMaxSubnormal = createFloat32Value(0, 112, 0x7FC000);
    checkFloat16ToFloat32Conversion(float16ValueMaxSubnormal, float32ValueMaxSubnormal);
}

TEST_F(FloatUtilTest, convertFloatToUInt16)
{
    // plus zero
    const uint16_t float16ValuePlusZero = createFloat16Value(0, 0, 0); // +0.0
    checkFloat32ToFloat16Conversion(0.0F, float16ValuePlusZero);

    // minus zero
    const uint16_t float16ValueMinusZero = createFloat16Value(1, 0, 0); // -0.0
    checkFloat32ToFloat16Conversion(-0.0F, float16ValueMinusZero);

    // plus infinity
    const uint32_t float32ValuePlusInfinity = createFloat32Value(0, 0xFF, 0); // +INF
    const uint16_t float16ValuePlusInfinity = createFloat16Value(0, 0x1F, 0); // +INF
    checkFloat32ToFloat16Conversion(float32ValuePlusInfinity, float16ValuePlusInfinity);

    // minus infinity
    const uint32_t float32ValueMinusInfinity = createFloat32Value(1, 0xFF, 0); // -INF
    const uint16_t float16ValueMinusInfinity = createFloat16Value(1, 0x1F, 0); // -INF
    checkFloat32ToFloat16Conversion(float32ValueMinusInfinity, float16ValueMinusInfinity);

    // quiet NaN
    const uint32_t float32ValueQuietNan = createFloat32Value(0, 0xFF, 0x7FE000); // +NaN
    const uint16_t float16ValueQuietNan = createFloat16Value(0, 0x1F, 0x3FF); // +NaN
    checkFloat32ToFloat16Conversion(float32ValueQuietNan, float16ValueQuietNan);

    // signaling NaN
    const uint32_t float32ValueSignalingNan = createFloat32Value(1, 0xFF, 0x7FE000); // -NaN
    const uint16_t float16ValueSignalingNan = createFloat16Value(1, 0x1F, 0x3FF); // -NaN
    checkFloat32ToFloat16Conversion(float32ValueSignalingNan, float16ValueSignalingNan);

    // normal numbers
    const uint16_t float16ValueOne = createFloat16Value(0, 15, 0); // 1.0
    checkFloat32ToFloat16Conversion(1.0F, float16ValueOne);

    const uint32_t float32ValueOnePlus = createFloat32Value(0, 127, 0x2000); // 1.0 + 2^-10
    const uint16_t float16ValueOnePlus = createFloat16Value(0, 15, 0x01); // 1.0 + 2^-10
    checkFloat32ToFloat16Conversion(float32ValueOnePlus, float16ValueOnePlus);

    const uint16_t float16ValueMax = createFloat16Value(0, 30, 0x3FF); // 2^15 (1 + 2^-1 + ... + 2^-10)
    checkFloat32ToFloat16Conversion(65504.0F, float16ValueMax);

    // normal numbers converted to zero
    const uint32_t float32ValueUnderflow = createFloat32Value(0, 102, 0); // 2^-25
    checkFloat32ToFloat16Conversion(float32ValueUnderflow, float16ValuePlusZero);

    // normal numbers converted to subnormal numbers
    const uint32_t float32ValueMinUnderflow = createFloat32Value(0, 103, 1); // 2^-24 (1 + 2^-23)
    const uint16_t float16ValueMinSubnormal = createFloat16Value(0, 0, 1); // 2^-24
    checkFloat32ToFloat16Conversion(float32ValueMinUnderflow, float16ValueMinSubnormal);

    // normal numbers converted to subnormal numbers with rounding
    const uint32_t float32ValueMinUnderflowRounding = createFloat32Value(0, 104, 0x200000); // 2^-23 (1 + 2^-2)
    const uint16_t float16ValueMinSubnormalRounding = createFloat16Value(0, 0, 0x3); // 2^-14 (2^-9 + 2^-10)
    checkFloat32ToFloat16Conversion(float32ValueMinUnderflowRounding, float16ValueMinSubnormalRounding);

    // normal numbers converted to infinity
    const uint32_t float32ValueOverflow = createFloat32Value(0, 144, 0); // 2^17
    checkFloat32ToFloat16Conversion(float32ValueOverflow, float16ValuePlusInfinity);

    // normal numbers converted with rounding
    const uint32_t float32ValueRounding = createFloat32Value(0, 127, 0x401000); // 1 + 2^-1 + 2^-11
    const uint16_t float16ValueRounding = createFloat16Value(0, 15, 0x201); // 1 + 2^-1 + 2^-10
    checkFloat32ToFloat16Conversion(float32ValueRounding, float16ValueRounding);

    // subnormal numbers
    const uint32_t float32ValueMinSubnormal = createFloat32Value(0, 0, 1); // 2^-126 (2^-23)
    checkFloat32ToFloat16Conversion(float32ValueMinSubnormal, float16ValuePlusZero);

    // 2^-126 (2^-1 + ... + 2^-23)
    const uint32_t float32ValueMaxSubnormal = createFloat32Value(0, 0, 0x007FFFFF);
    checkFloat32ToFloat16Conversion(float32ValueMaxSubnormal, float16ValuePlusZero);
}

TEST_F(FloatUtilTest, convertUInt32ToFloat)
{
    for (TestFloat32Element testElement : TEST_FLOAT32_DATA)
    {
        const uint32_t float32Value =
                createFloat32Value(testElement.sign, testElement.exponent, testElement.significand);
        const float convertedFloat = convertUInt32ToFloat(float32Value);

        ASSERT_EQ(testElement.expectedFloat, convertedFloat);
    }
}

TEST_F(FloatUtilTest, convertFloatToUInt32)
{
    for (TestFloat32Element testElement : TEST_FLOAT32_DATA)
    {
        const uint32_t convertedFloatValue = convertFloatToUInt32(testElement.expectedFloat);
        const uint32_t expectedFloatValue =
                createFloat32Value(testElement.sign, testElement.exponent, testElement.significand);

        ASSERT_EQ(expectedFloatValue, convertedFloatValue);
    }
}

TEST_F(FloatUtilTest, convertUInt64ToDouble)
{
    for (TestFloat64Element testElement : TEST_FLOAT64_DATA)
    {
        const uint64_t float64Value =
                createFloat64Value(testElement.sign, testElement.exponent, testElement.significand);
        const double convertedDouble = convertUInt64ToDouble(float64Value);

        ASSERT_EQ(testElement.expectedDouble, convertedDouble);
    }
}

TEST_F(FloatUtilTest, convertDoubleToUInt64)
{
    for (TestFloat64Element testElement : TEST_FLOAT64_DATA)
    {
        const uint64_t convertedDoubleValue = convertDoubleToUInt64(testElement.expectedDouble);
        const uint64_t expectedDoubleValue =
                createFloat64Value(testElement.sign, testElement.exponent, testElement.significand);

        ASSERT_EQ(expectedDoubleValue, convertedDoubleValue);
    }
}

} // namespace zserio
