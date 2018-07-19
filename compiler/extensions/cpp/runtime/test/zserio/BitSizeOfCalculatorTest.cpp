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

TEST(BitSizeOfCalculatorTest, GetBitSizeOfString)
{
    EXPECT_EQ((1 + 1) * 8, getBitSizeOfString("T"));
    EXPECT_EQ((1 + 4) * 8, getBitSizeOfString("Test"));

    const size_t testStringLength = static_cast<size_t>(1) << 7;
    std::string testString(testStringLength, '\xAB');
    EXPECT_EQ((2 + testStringLength) * 8, getBitSizeOfString(testString));
}

} // namespace zserio
