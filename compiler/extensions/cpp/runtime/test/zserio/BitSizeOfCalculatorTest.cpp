#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarInt16)
{
    EXPECT_EQ(1*8, getBitSizeOfVarInt16(0));

    EXPECT_EQ(1*8, getBitSizeOfVarInt16(   INT64_C(1) << (0) ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt16( -(INT64_C(1) << (0)) ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt16(  (INT64_C(1) << (6)) - 1 ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt16( -((INT64_C(1) << (6)) - 1) ));

    EXPECT_EQ(2*8, getBitSizeOfVarInt16(   INT64_C(1) << (6) ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt16( -(INT64_C(1) << (6)) ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt16(  (INT64_C(1) << (6 + 8)) - 1 ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt16( -((INT64_C(1) << (6 + 8)) - 1) ));

    const int16_t outOfRangeValue = INT16_C(1) << (6 + 8);
    ASSERT_THROW(getBitSizeOfVarInt16(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarInt32)
{
    EXPECT_EQ(1*8, getBitSizeOfVarInt32(0));

    EXPECT_EQ(1*8, getBitSizeOfVarInt32(   INT64_C(1) << (0) ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt32( -(INT64_C(1) << (0)) ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt32(  (INT64_C(1) << (6)) - 1 ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt32( -((INT64_C(1) << (6)) - 1) ));

    EXPECT_EQ(2*8, getBitSizeOfVarInt32(   INT64_C(1) << (6) ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt32( -(INT64_C(1) << (6)) ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt32(  (INT64_C(1) << (6 + 7)) - 1 ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt32( -((INT64_C(1) << (6 + 7)) - 1) ));

    EXPECT_EQ(3*8, getBitSizeOfVarInt32(   INT64_C(1) << (6 + 7) ));
    EXPECT_EQ(3*8, getBitSizeOfVarInt32( -(INT64_C(1) << (6 + 7)) ));
    EXPECT_EQ(3*8, getBitSizeOfVarInt32(  (INT64_C(1) << (6 + 7 + 7)) - 1 ));
    EXPECT_EQ(3*8, getBitSizeOfVarInt32( -((INT64_C(1) << (6 + 7 + 7)) - 1) ));

    EXPECT_EQ(4*8, getBitSizeOfVarInt32(   INT64_C(1) << (6 + 7 + 7) ));
    EXPECT_EQ(4*8, getBitSizeOfVarInt32( -(INT64_C(1) << (6 + 7 + 7)) ));
    EXPECT_EQ(4*8, getBitSizeOfVarInt32(  (INT64_C(1) << (6 + 7 + 7 + 8)) - 1 ));
    EXPECT_EQ(4*8, getBitSizeOfVarInt32( -((INT64_C(1) << (6 + 7 + 7 + 8)) - 1) ));

    const int32_t outOfRangeValue = INT32_C(1) << (6 + 7 + 7 + 8);
    ASSERT_THROW(getBitSizeOfVarInt32(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarInt64)
{
    EXPECT_EQ(1*8, getBitSizeOfVarInt64(0));

    EXPECT_EQ(1*8, getBitSizeOfVarInt64(   INT64_C(1) << (0) ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt64( -(INT64_C(1) << (0)) ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6)) - 1 ));
    EXPECT_EQ(1*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6)) - 1) ));

    EXPECT_EQ(2*8, getBitSizeOfVarInt64(   INT64_C(1) << (6) ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6)) ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7)) - 1 ));
    EXPECT_EQ(2*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7)) - 1) ));

    EXPECT_EQ(3*8, getBitSizeOfVarInt64(   INT64_C(1) << (6 + 7) ));
    EXPECT_EQ(3*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6 + 7)) ));
    EXPECT_EQ(3*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7)) - 1 ));
    EXPECT_EQ(3*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7)) - 1) ));

    EXPECT_EQ(4*8, getBitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7) ));
    EXPECT_EQ(4*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7)) ));
    EXPECT_EQ(4*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(4*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(5*8, getBitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7) ));
    EXPECT_EQ(5*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7)) ));
    EXPECT_EQ(5*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(5*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(6*8, getBitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(6*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7 + 7)) ));
    EXPECT_EQ(6*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(6*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(7*8, getBitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(7*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) ));
    EXPECT_EQ(7*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(7*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(8*8, getBitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(8*8, getBitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) ));
    EXPECT_EQ(8*8, getBitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1 ));
    EXPECT_EQ(8*8, getBitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1) ));

    const int64_t outOfRangeValue = INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8);
    ASSERT_THROW(getBitSizeOfVarInt64(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarUInt16)
{
    EXPECT_EQ(1*8, getBitSizeOfVarUInt16(0));

    EXPECT_EQ(1*8, getBitSizeOfVarUInt16(  INT64_C(1) << (0) ));
    EXPECT_EQ(1*8, getBitSizeOfVarUInt16( (INT64_C(1) << (7)) - 1 ));

    EXPECT_EQ(2*8, getBitSizeOfVarUInt16(  INT64_C(1) << (7) ));
    EXPECT_EQ(2*8, getBitSizeOfVarUInt16( (INT64_C(1) << (7 + 8)) - 1 ));

    const uint16_t outOfRangeValue = UINT16_C(1) << (7 + 8);
    ASSERT_THROW(getBitSizeOfVarUInt16(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarUInt32)
{
    EXPECT_EQ(1*8, getBitSizeOfVarUInt32(0));

    EXPECT_EQ(1*8, getBitSizeOfVarUInt32(  INT64_C(1) << (0) ));
    EXPECT_EQ(1*8, getBitSizeOfVarUInt32( (INT64_C(1) << (7)) - 1 ));

    EXPECT_EQ(2*8, getBitSizeOfVarUInt32(  INT64_C(1) << (7) ));
    EXPECT_EQ(2*8, getBitSizeOfVarUInt32( (INT64_C(1) << (7 + 7)) - 1 ));

    EXPECT_EQ(3*8, getBitSizeOfVarUInt32(  INT64_C(1) << (7 + 7) ));
    EXPECT_EQ(3*8, getBitSizeOfVarUInt32( (INT64_C(1) << (7 + 7 + 7)) - 1 ));

    EXPECT_EQ(4*8, getBitSizeOfVarUInt32(  INT64_C(1) << (7 + 7 + 7) ));
    EXPECT_EQ(4*8, getBitSizeOfVarUInt32( (INT64_C(1) << (7 + 7 + 7 + 8)) - 1 ));

    const uint32_t outOfRangeValue = UINT32_C(1) << (7 + 7 + 7 + 8);
    ASSERT_THROW(getBitSizeOfVarUInt32(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarUInt64)
{
    EXPECT_EQ(1*8, getBitSizeOfVarUInt64(0));

    EXPECT_EQ(1*8, getBitSizeOfVarUInt64(  INT64_C(1) << (0) ));
    EXPECT_EQ(1*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7)) - 1 ));

    EXPECT_EQ(2*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7) ));
    EXPECT_EQ(2*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7)) - 1 ));

    EXPECT_EQ(3*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7 + 7) ));
    EXPECT_EQ(3*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7 + 7)) - 1 ));

    EXPECT_EQ(4*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7 + 7 + 7) ));
    EXPECT_EQ(4*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(5*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7 + 7 + 7 + 7) ));
    EXPECT_EQ(5*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(6*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(6*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(7*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(7*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(8*8, getBitSizeOfVarUInt64(  INT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(8*8, getBitSizeOfVarUInt64( (INT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1 ));

    const uint64_t outOfRangeValue = UINT64_C(1) << (7 + 7 + 7  + 7 + 7 + 7 + 7 + 8);
    ASSERT_THROW(getBitSizeOfVarUInt64(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarInt)
{
    EXPECT_EQ(8, getBitSizeOfVarInt(INT64_C(0)));
    EXPECT_EQ(8, getBitSizeOfVarInt(-(INT64_C(1) << 6) + 1));
    EXPECT_EQ(8, getBitSizeOfVarInt((INT64_C(1) << 6) - 1));
    EXPECT_EQ(16, getBitSizeOfVarInt(-(INT64_C(1) << 6)));
    EXPECT_EQ(16, getBitSizeOfVarInt((INT64_C(1) << 6)));
    EXPECT_EQ(16, getBitSizeOfVarInt(-(INT64_C(1) << 13) + 1));
    EXPECT_EQ(16, getBitSizeOfVarInt((INT64_C(1) << 13) - 1));
    EXPECT_EQ(24, getBitSizeOfVarInt(-(INT64_C(1) << 13)));
    EXPECT_EQ(24, getBitSizeOfVarInt((INT64_C(1) << 13)));
    EXPECT_EQ(24, getBitSizeOfVarInt(-(INT64_C(1) << 20) + 1));
    EXPECT_EQ(24, getBitSizeOfVarInt((INT64_C(1) << 20) - 1));
    EXPECT_EQ(32, getBitSizeOfVarInt(-(INT64_C(1) << 20)));
    EXPECT_EQ(32, getBitSizeOfVarInt((INT64_C(1) << 20)));
    EXPECT_EQ(32, getBitSizeOfVarInt(-(INT64_C(1) << 27) + 1));
    EXPECT_EQ(32, getBitSizeOfVarInt((INT64_C(1) << 27) - 1));
    EXPECT_EQ(40, getBitSizeOfVarInt(-(INT64_C(1) << 27)));
    EXPECT_EQ(40, getBitSizeOfVarInt((INT64_C(1) << 27)));
    EXPECT_EQ(40, getBitSizeOfVarInt(-(INT64_C(1) << 34) + 1));
    EXPECT_EQ(40, getBitSizeOfVarInt((INT64_C(1) << 34) - 1));
    EXPECT_EQ(48, getBitSizeOfVarInt(-(INT64_C(1) << 34)));
    EXPECT_EQ(48, getBitSizeOfVarInt((INT64_C(1) << 34)));
    EXPECT_EQ(48, getBitSizeOfVarInt(-(INT64_C(1) << 41) + 1));
    EXPECT_EQ(48, getBitSizeOfVarInt((INT64_C(1) << 41) - 1));
    EXPECT_EQ(56, getBitSizeOfVarInt(-(INT64_C(1) << 41)));
    EXPECT_EQ(56, getBitSizeOfVarInt((INT64_C(1) << 41)));
    EXPECT_EQ(56, getBitSizeOfVarInt(-(INT64_C(1) << 48) + 1));
    EXPECT_EQ(56, getBitSizeOfVarInt((INT64_C(1) << 48) - 1));
    EXPECT_EQ(64, getBitSizeOfVarInt(-(INT64_C(1) << 48)));
    EXPECT_EQ(64, getBitSizeOfVarInt((INT64_C(1) << 48)));
    EXPECT_EQ(64, getBitSizeOfVarInt(-(INT64_C(1) << 55) + 1));
    EXPECT_EQ(64, getBitSizeOfVarInt((INT64_C(1) << 55) - 1));
    EXPECT_EQ(72, getBitSizeOfVarInt(-(INT64_C(1) << 55)));
    EXPECT_EQ(72, getBitSizeOfVarInt((INT64_C(1) << 55)));
    EXPECT_EQ(72, getBitSizeOfVarInt(INT64_MIN + 1));
    EXPECT_EQ(72, getBitSizeOfVarInt(INT64_MAX));

    // special case, INT64_MIN is stored as -0
    EXPECT_EQ(8, getBitSizeOfVarInt(INT64_MIN));
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfVarUInt)
{
    EXPECT_EQ(8, getBitSizeOfVarUInt(UINT64_C(0)));
    EXPECT_EQ(8, getBitSizeOfVarUInt((UINT64_C(1) << 7) - 1 ));
    EXPECT_EQ(16, getBitSizeOfVarUInt((UINT64_C(1) << 7)));
    EXPECT_EQ(16, getBitSizeOfVarUInt((UINT64_C(1) << 14) - 1 ));
    EXPECT_EQ(24, getBitSizeOfVarUInt((UINT64_C(1) << 14)));
    EXPECT_EQ(24, getBitSizeOfVarUInt((UINT64_C(1) << 21) - 1 ));
    EXPECT_EQ(32, getBitSizeOfVarUInt((UINT64_C(1) << 21)));
    EXPECT_EQ(32, getBitSizeOfVarUInt((UINT64_C(1) << 28) - 1 ));
    EXPECT_EQ(40, getBitSizeOfVarUInt((UINT64_C(1) << 28)));
    EXPECT_EQ(40, getBitSizeOfVarUInt((UINT64_C(1) << 35) - 1 ));
    EXPECT_EQ(48, getBitSizeOfVarUInt((UINT64_C(1) << 35)));
    EXPECT_EQ(48, getBitSizeOfVarUInt((UINT64_C(1) << 42) - 1 ));
    EXPECT_EQ(56, getBitSizeOfVarUInt((UINT64_C(1) << 42)));
    EXPECT_EQ(56, getBitSizeOfVarUInt((UINT64_C(1) << 49) - 1 ));
    EXPECT_EQ(64, getBitSizeOfVarUInt((UINT64_C(1) << 49)));
    EXPECT_EQ(64, getBitSizeOfVarUInt((UINT64_C(1) << 56) - 1 ));
    EXPECT_EQ(72, getBitSizeOfVarUInt((UINT64_C(1) << 56)));
    EXPECT_EQ(72, getBitSizeOfVarUInt(UINT64_MAX));
}

TEST(BitSizeOfCalculatorTest, GetBitSizeOfString)
{
    EXPECT_EQ((1 + 1) * 8, getBitSizeOfString("T"));
    EXPECT_EQ((1 + 4) * 8, getBitSizeOfString("Test"));

    const size_t testStringLength = static_cast<size_t>(1) << 7;
    std::string testString(testStringLength, '\xAB');
    EXPECT_EQ((2 + testStringLength) * 8, getBitSizeOfString(testString));
}

} // namespace zserio
