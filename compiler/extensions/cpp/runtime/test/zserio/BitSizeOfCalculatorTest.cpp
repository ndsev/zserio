#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt16)
{
    EXPECT_EQ(8, bitSizeOfVarInt16(0));

    EXPECT_EQ(8, bitSizeOfVarInt16(   INT16_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarInt16( -(INT16_C(1) << (0)) ));
    EXPECT_EQ(8, bitSizeOfVarInt16(  (INT16_C(1) << (6)) - 1 ));
    EXPECT_EQ(8, bitSizeOfVarInt16( -((INT16_C(1) << (6)) - 1) ));

    EXPECT_EQ(16, bitSizeOfVarInt16(  INT16_C(1) << (6) ));
    EXPECT_EQ(16, bitSizeOfVarInt16( -(INT16_C(1) << (6)) ));
    EXPECT_EQ(16, bitSizeOfVarInt16(  (INT16_C(1) << (6 + 8)) - 1 ));
    EXPECT_EQ(16, bitSizeOfVarInt16( -((INT16_C(1) << (6 + 8)) - 1) ));

    const int16_t outOfRangeValue = INT16_C(1) << (6 + 8);
    ASSERT_THROW(bitSizeOfVarInt16(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt32)
{
    EXPECT_EQ(8, bitSizeOfVarInt32(0));

    EXPECT_EQ(8, bitSizeOfVarInt32(   INT32_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarInt32( -(INT32_C(1) << (0)) ));
    EXPECT_EQ(8, bitSizeOfVarInt32(  (INT32_C(1) << (6)) - 1 ));
    EXPECT_EQ(8, bitSizeOfVarInt32( -((INT32_C(1) << (6)) - 1) ));

    EXPECT_EQ(16, bitSizeOfVarInt32(   INT32_C(1) << (6) ));
    EXPECT_EQ(16, bitSizeOfVarInt32( -(INT32_C(1) << (6)) ));
    EXPECT_EQ(16, bitSizeOfVarInt32(  (INT32_C(1) << (6 + 7)) - 1 ));
    EXPECT_EQ(16, bitSizeOfVarInt32( -((INT32_C(1) << (6 + 7)) - 1) ));

    EXPECT_EQ(24, bitSizeOfVarInt32(   INT32_C(1) << (6 + 7) ));
    EXPECT_EQ(24, bitSizeOfVarInt32( -(INT32_C(1) << (6 + 7)) ));
    EXPECT_EQ(24, bitSizeOfVarInt32(  (INT32_C(1) << (6 + 7 + 7)) - 1 ));
    EXPECT_EQ(24, bitSizeOfVarInt32( -((INT32_C(1) << (6 + 7 + 7)) - 1) ));

    EXPECT_EQ(32, bitSizeOfVarInt32(   INT32_C(1) << (6 + 7 + 7) ));
    EXPECT_EQ(32, bitSizeOfVarInt32( -(INT32_C(1) << (6 + 7 + 7)) ));
    EXPECT_EQ(32, bitSizeOfVarInt32(  (INT32_C(1) << (6 + 7 + 7 + 8)) - 1 ));
    EXPECT_EQ(32, bitSizeOfVarInt32( -((INT32_C(1) << (6 + 7 + 7 + 8)) - 1) ));

    const int32_t outOfRangeValue = INT32_C(1) << (6 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarInt32(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt64)
{
    EXPECT_EQ(8, bitSizeOfVarInt64(0));

    EXPECT_EQ(8, bitSizeOfVarInt64(   INT64_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarInt64( -(INT64_C(1) << (0)) ));
    EXPECT_EQ(8, bitSizeOfVarInt64(  (INT64_C(1) << (6)) - 1 ));
    EXPECT_EQ(8, bitSizeOfVarInt64( -((INT64_C(1) << (6)) - 1) ));

    EXPECT_EQ(16, bitSizeOfVarInt64(   INT64_C(1) << (6) ));
    EXPECT_EQ(16, bitSizeOfVarInt64( -(INT64_C(1) << (6)) ));
    EXPECT_EQ(16, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7)) - 1 ));
    EXPECT_EQ(16, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7)) - 1) ));

    EXPECT_EQ(24, bitSizeOfVarInt64(   INT64_C(1) << (6 + 7) ));
    EXPECT_EQ(24, bitSizeOfVarInt64( -(INT64_C(1) << (6 + 7)) ));
    EXPECT_EQ(24, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7)) - 1 ));
    EXPECT_EQ(24, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7)) - 1) ));

    EXPECT_EQ(32, bitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7) ));
    EXPECT_EQ(32, bitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7)) ));
    EXPECT_EQ(32, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(32, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(40, bitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7) ));
    EXPECT_EQ(40, bitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7)) ));
    EXPECT_EQ(40, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(40, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(48, bitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(48, bitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7 + 7)) ));
    EXPECT_EQ(48, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(48, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(56, bitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(56, bitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) ));
    EXPECT_EQ(56, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1 ));
    EXPECT_EQ(56, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1) ));

    EXPECT_EQ(64, bitSizeOfVarInt64(   INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(64, bitSizeOfVarInt64( -(INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) ));
    EXPECT_EQ(64, bitSizeOfVarInt64(  (INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1 ));
    EXPECT_EQ(64, bitSizeOfVarInt64( -((INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1) ));

    const int64_t outOfRangeValue = INT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarInt64(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt16)
{
    EXPECT_EQ(8, bitSizeOfVarUInt16(0));

    EXPECT_EQ(8, bitSizeOfVarUInt16(  UINT16_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarUInt16( (UINT16_C(1) << (7)) - 1 ));

    EXPECT_EQ(16, bitSizeOfVarUInt16(  UINT16_C(1) << (7) ));
    EXPECT_EQ(16, bitSizeOfVarUInt16( (UINT16_C(1) << (7 + 8)) - 1 ));

    const uint16_t outOfRangeValue = UINT16_C(1) << (7 + 8);
    ASSERT_THROW(bitSizeOfVarUInt16(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt32)
{
    EXPECT_EQ(8, bitSizeOfVarUInt32(0));

    EXPECT_EQ(8, bitSizeOfVarUInt32(  UINT32_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarUInt32( (UINT32_C(1) << (7)) - 1 ));

    EXPECT_EQ(16, bitSizeOfVarUInt32(  UINT32_C(1) << (7) ));
    EXPECT_EQ(16, bitSizeOfVarUInt32( (UINT32_C(1) << (7 + 7)) - 1 ));

    EXPECT_EQ(24, bitSizeOfVarUInt32(  UINT32_C(1) << (7 + 7) ));
    EXPECT_EQ(24, bitSizeOfVarUInt32( (UINT32_C(1) << (7 + 7 + 7)) - 1 ));

    EXPECT_EQ(32, bitSizeOfVarUInt32(  UINT32_C(1) << (7 + 7 + 7) ));
    EXPECT_EQ(32, bitSizeOfVarUInt32( (UINT32_C(1) << (7 + 7 + 7 + 8)) - 1 ));

    const uint32_t outOfRangeValue = UINT32_C(1) << (7 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarUInt32(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt64)
{
    EXPECT_EQ(8, bitSizeOfVarUInt64(0));

    EXPECT_EQ(8, bitSizeOfVarUInt64(  UINT64_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarUInt64( (UINT64_C(1) << (7)) - 1 ));

    EXPECT_EQ(16, bitSizeOfVarUInt64(  UINT64_C(1) << (7) ));
    EXPECT_EQ(16, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7)) - 1 ));

    EXPECT_EQ(24, bitSizeOfVarUInt64(  UINT64_C(1) << (7 + 7) ));
    EXPECT_EQ(24, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7 + 7)) - 1 ));

    EXPECT_EQ(32, bitSizeOfVarUInt64(  UINT64_C(1) << (7 + 7 + 7) ));
    EXPECT_EQ(32, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(40, bitSizeOfVarUInt64(  UINT64_C(1) << (7 + 7 + 7 + 7) ));
    EXPECT_EQ(40, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(48, bitSizeOfVarUInt64(  UINT64_C(1) << (7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(48, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(56, bitSizeOfVarUInt64(  UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(56, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(64, bitSizeOfVarUInt64(  UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7) ));
    EXPECT_EQ(64, bitSizeOfVarUInt64( (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1 ));

    const uint64_t outOfRangeValue = UINT64_C(1) << (7 + 7 + 7  + 7 + 7 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarUInt64(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt)
{
    EXPECT_EQ(8, bitSizeOfVarInt(INT64_C(0)));
    EXPECT_EQ(8, bitSizeOfVarInt(-(INT64_C(1) << 6) + 1));
    EXPECT_EQ(8, bitSizeOfVarInt((INT64_C(1) << 6) - 1));
    EXPECT_EQ(16, bitSizeOfVarInt(-(INT64_C(1) << 6)));
    EXPECT_EQ(16, bitSizeOfVarInt((INT64_C(1) << 6)));
    EXPECT_EQ(16, bitSizeOfVarInt(-(INT64_C(1) << 13) + 1));
    EXPECT_EQ(16, bitSizeOfVarInt((INT64_C(1) << 13) - 1));
    EXPECT_EQ(24, bitSizeOfVarInt(-(INT64_C(1) << 13)));
    EXPECT_EQ(24, bitSizeOfVarInt((INT64_C(1) << 13)));
    EXPECT_EQ(24, bitSizeOfVarInt(-(INT64_C(1) << 20) + 1));
    EXPECT_EQ(24, bitSizeOfVarInt((INT64_C(1) << 20) - 1));
    EXPECT_EQ(32, bitSizeOfVarInt(-(INT64_C(1) << 20)));
    EXPECT_EQ(32, bitSizeOfVarInt((INT64_C(1) << 20)));
    EXPECT_EQ(32, bitSizeOfVarInt(-(INT64_C(1) << 27) + 1));
    EXPECT_EQ(32, bitSizeOfVarInt((INT64_C(1) << 27) - 1));
    EXPECT_EQ(40, bitSizeOfVarInt(-(INT64_C(1) << 27)));
    EXPECT_EQ(40, bitSizeOfVarInt((INT64_C(1) << 27)));
    EXPECT_EQ(40, bitSizeOfVarInt(-(INT64_C(1) << 34) + 1));
    EXPECT_EQ(40, bitSizeOfVarInt((INT64_C(1) << 34) - 1));
    EXPECT_EQ(48, bitSizeOfVarInt(-(INT64_C(1) << 34)));
    EXPECT_EQ(48, bitSizeOfVarInt((INT64_C(1) << 34)));
    EXPECT_EQ(48, bitSizeOfVarInt(-(INT64_C(1) << 41) + 1));
    EXPECT_EQ(48, bitSizeOfVarInt((INT64_C(1) << 41) - 1));
    EXPECT_EQ(56, bitSizeOfVarInt(-(INT64_C(1) << 41)));
    EXPECT_EQ(56, bitSizeOfVarInt((INT64_C(1) << 41)));
    EXPECT_EQ(56, bitSizeOfVarInt(-(INT64_C(1) << 48) + 1));
    EXPECT_EQ(56, bitSizeOfVarInt((INT64_C(1) << 48) - 1));
    EXPECT_EQ(64, bitSizeOfVarInt(-(INT64_C(1) << 48)));
    EXPECT_EQ(64, bitSizeOfVarInt((INT64_C(1) << 48)));
    EXPECT_EQ(64, bitSizeOfVarInt(-(INT64_C(1) << 55) + 1));
    EXPECT_EQ(64, bitSizeOfVarInt((INT64_C(1) << 55) - 1));
    EXPECT_EQ(72, bitSizeOfVarInt(-(INT64_C(1) << 55)));
    EXPECT_EQ(72, bitSizeOfVarInt((INT64_C(1) << 55)));
    EXPECT_EQ(72, bitSizeOfVarInt(INT64_MIN + 1));
    EXPECT_EQ(72, bitSizeOfVarInt(INT64_MAX));

    // special case, INT64_MIN is stored as -0
    EXPECT_EQ(8, bitSizeOfVarInt(INT64_MIN));
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt)
{
    EXPECT_EQ(8, bitSizeOfVarUInt(UINT64_C(0)));
    EXPECT_EQ(8, bitSizeOfVarUInt((UINT64_C(1) << 7) - 1 ));
    EXPECT_EQ(16, bitSizeOfVarUInt((UINT64_C(1) << 7)));
    EXPECT_EQ(16, bitSizeOfVarUInt((UINT64_C(1) << 14) - 1 ));
    EXPECT_EQ(24, bitSizeOfVarUInt((UINT64_C(1) << 14)));
    EXPECT_EQ(24, bitSizeOfVarUInt((UINT64_C(1) << 21) - 1 ));
    EXPECT_EQ(32, bitSizeOfVarUInt((UINT64_C(1) << 21)));
    EXPECT_EQ(32, bitSizeOfVarUInt((UINT64_C(1) << 28) - 1 ));
    EXPECT_EQ(40, bitSizeOfVarUInt((UINT64_C(1) << 28)));
    EXPECT_EQ(40, bitSizeOfVarUInt((UINT64_C(1) << 35) - 1 ));
    EXPECT_EQ(48, bitSizeOfVarUInt((UINT64_C(1) << 35)));
    EXPECT_EQ(48, bitSizeOfVarUInt((UINT64_C(1) << 42) - 1 ));
    EXPECT_EQ(56, bitSizeOfVarUInt((UINT64_C(1) << 42)));
    EXPECT_EQ(56, bitSizeOfVarUInt((UINT64_C(1) << 49) - 1 ));
    EXPECT_EQ(64, bitSizeOfVarUInt((UINT64_C(1) << 49)));
    EXPECT_EQ(64, bitSizeOfVarUInt((UINT64_C(1) << 56) - 1 ));
    EXPECT_EQ(72, bitSizeOfVarUInt((UINT64_C(1) << 56)));
    EXPECT_EQ(72, bitSizeOfVarUInt(UINT64_MAX));
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarSize)
{
    EXPECT_EQ(8, bitSizeOfVarSize(0));

    EXPECT_EQ(8, bitSizeOfVarSize(  UINT32_C(1) << (0) ));
    EXPECT_EQ(8, bitSizeOfVarSize( (UINT32_C(1) << (7)) - 1 ));

    EXPECT_EQ(16, bitSizeOfVarSize(  UINT32_C(1) << (7) ));
    EXPECT_EQ(16, bitSizeOfVarSize( (UINT32_C(1) << (7 + 7)) - 1 ));

    EXPECT_EQ(24, bitSizeOfVarSize(  UINT32_C(1) << (7 + 7) ));
    EXPECT_EQ(24, bitSizeOfVarSize( (UINT32_C(1) << (7 + 7 + 7)) - 1 ));

    EXPECT_EQ(32, bitSizeOfVarSize(  UINT32_C(1) << (7 + 7 + 7) ));
    EXPECT_EQ(32, bitSizeOfVarSize( (UINT32_C(1) << (7 + 7 + 7 + 7)) - 1 ));

    EXPECT_EQ(40, bitSizeOfVarSize(  UINT32_C(1) << (7 + 7 + 7 + 7) ));
    EXPECT_EQ(40, bitSizeOfVarSize( (UINT32_C(1) << (2 + 7 + 7 + 7 + 8)) - 1 ));

    const uint32_t outOfRangeValue = UINT32_C(1) << (2 + 7 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarSize(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfString)
{
    EXPECT_EQ((1 + 1) * 8, bitSizeOfString(std::string("T")));
    EXPECT_EQ((1 + 4) * 8, bitSizeOfString(std::string("Test")));

    const size_t testStringLength = static_cast<size_t>(1) << 7;
    std::string testString(testStringLength, '\xAB');
    EXPECT_EQ((2 + testStringLength) * 8, bitSizeOfString(testString));
}

TEST(BitSizeOfCalculatorTest, bitSizeOfBitBuffer)
{
    EXPECT_EQ(8 + 8, bitSizeOfBitBuffer(BitBuffer(std::vector<uint8_t>({0xAB, 0xC0}), 8)));
    EXPECT_EQ(8 + 11, bitSizeOfBitBuffer(BitBuffer(std::vector<uint8_t>({0xAB, 0xC0}), 11)));
    EXPECT_EQ(8 + 16, bitSizeOfBitBuffer(BitBuffer(std::vector<uint8_t>({0xAB, 0xCD}), 16)));
    EXPECT_EQ(8 + 16, bitSizeOfBitBuffer(BitBuffer(std::vector<uint8_t>({0xAB, 0xCD}))));

    EXPECT_EQ(8 + 15 * 8 + 7, bitSizeOfBitBuffer(BitBuffer(std::vector<uint8_t>(16), 127)));
    EXPECT_EQ(16 + 16 * 8, bitSizeOfBitBuffer(BitBuffer(std::vector<uint8_t>(16), 128)));
}

} // namespace zserio
