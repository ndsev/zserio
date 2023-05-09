#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt16)
{
    EXPECT_EQ(8, bitSizeOfVarInt16(0));

    EXPECT_EQ(8, bitSizeOfVarInt16(static_cast<int16_t>(1U << (0U))));
    EXPECT_EQ(8, bitSizeOfVarInt16(-static_cast<int16_t>(1U << (0U))));
    EXPECT_EQ(8, bitSizeOfVarInt16(static_cast<int16_t>((1U << (6U)) - 1)));
    EXPECT_EQ(8, bitSizeOfVarInt16(-static_cast<int16_t>((1U << (6U)) - 1)));

    EXPECT_EQ(16, bitSizeOfVarInt16(static_cast<int16_t>(1U << (6U))));
    EXPECT_EQ(16, bitSizeOfVarInt16(-static_cast<int16_t>(1U << (6U))));
    EXPECT_EQ(16, bitSizeOfVarInt16(static_cast<int16_t>((1U << (6U + 8)) - 1)));
    EXPECT_EQ(16, bitSizeOfVarInt16(-static_cast<int16_t>((1U << (6U + 8)) - 1)));

    const int16_t outOfRangeValue = static_cast<int16_t>(1U << (6U + 8));
    ASSERT_THROW(bitSizeOfVarInt16(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt32)
{
    EXPECT_EQ(8, bitSizeOfVarInt32(0));

    EXPECT_EQ(8, bitSizeOfVarInt32(static_cast<int32_t>(1U << (0U))));
    EXPECT_EQ(8, bitSizeOfVarInt32(-static_cast<int32_t>(1U << (0U))));
    EXPECT_EQ(8, bitSizeOfVarInt32(static_cast<int32_t>((1U << (6U)) - 1)));
    EXPECT_EQ(8, bitSizeOfVarInt32(-static_cast<int32_t>(((1U << (6U)) - 1))));

    EXPECT_EQ(16, bitSizeOfVarInt32(static_cast<int32_t>(1U << (6U))));
    EXPECT_EQ(16, bitSizeOfVarInt32(-static_cast<int32_t>(1U << (6U))));
    EXPECT_EQ(16, bitSizeOfVarInt32(static_cast<int32_t>((1U << (6U + 7)) - 1)));
    EXPECT_EQ(16, bitSizeOfVarInt32(-static_cast<int32_t>(((1U << (6U + 7)) - 1))));

    EXPECT_EQ(24, bitSizeOfVarInt32(static_cast<int32_t>(1U << (6U + 7))));
    EXPECT_EQ(24, bitSizeOfVarInt32(-static_cast<int32_t>(1U << (6U + 7))));
    EXPECT_EQ(24, bitSizeOfVarInt32(static_cast<int32_t>((1U << (6U + 7 + 7)) - 1)));
    EXPECT_EQ(24, bitSizeOfVarInt32(-static_cast<int32_t>((1U << (6U + 7 + 7)) - 1)));

    EXPECT_EQ(32, bitSizeOfVarInt32(static_cast<int32_t>(1U << (6U + 7 + 7))));
    EXPECT_EQ(32, bitSizeOfVarInt32(-static_cast<int32_t>(1U << (6U + 7 + 7))));
    EXPECT_EQ(32, bitSizeOfVarInt32(static_cast<int32_t>((1U << (6U + 7 + 7 + 8)) - 1)));
    EXPECT_EQ(32, bitSizeOfVarInt32(-static_cast<int32_t>((1U << (6U + 7 + 7 + 8)) - 1)));

    const int32_t outOfRangeValue = static_cast<int32_t>(1U << (6U + 7 + 7 + 8));
    ASSERT_THROW(bitSizeOfVarInt32(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt64)
{
    EXPECT_EQ(8, bitSizeOfVarInt64(0));

    EXPECT_EQ(8, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (0U))));
    EXPECT_EQ(8, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (0U))));
    EXPECT_EQ(8, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U)) - 1)));
    EXPECT_EQ(8, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U)) - 1)));

    EXPECT_EQ(16, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U))));
    EXPECT_EQ(16, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U))));
    EXPECT_EQ(16, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U + 7)) - 1)));
    EXPECT_EQ(16, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U + 7)) - 1)));

    EXPECT_EQ(24, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U + 7))));
    EXPECT_EQ(24, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U + 7))));
    EXPECT_EQ(24, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7)) - 1)));
    EXPECT_EQ(24, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7)) - 1)));

    EXPECT_EQ(32, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7))));
    EXPECT_EQ(32, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7))));
    EXPECT_EQ(32, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7)) - 1)));
    EXPECT_EQ(32, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7)) - 1)));

    EXPECT_EQ(40, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7))));
    EXPECT_EQ(40, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7))));
    EXPECT_EQ(40, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7)) - 1)));
    EXPECT_EQ(40, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7)) - 1)));

    EXPECT_EQ(48, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7))));
    EXPECT_EQ(48, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7))));
    EXPECT_EQ(48, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7)) - 1)));
    EXPECT_EQ(48, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7)) - 1)));

    EXPECT_EQ(56, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7))));
    EXPECT_EQ(56, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7))));
    EXPECT_EQ(56, bitSizeOfVarInt64(static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7)) - 1)));
    EXPECT_EQ(56, bitSizeOfVarInt64(-static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7)) - 1)));

    EXPECT_EQ(64, bitSizeOfVarInt64(static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7))));
    EXPECT_EQ(64, bitSizeOfVarInt64(-static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7))));
    EXPECT_EQ(64, bitSizeOfVarInt64(
            static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1)));
    EXPECT_EQ(64, bitSizeOfVarInt64(
            -static_cast<int64_t>((UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1)));

    const int64_t outOfRangeValue = static_cast<int64_t>(UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8));
    ASSERT_THROW(bitSizeOfVarInt64(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt16)
{
    EXPECT_EQ(8, bitSizeOfVarUInt16(0));

    EXPECT_EQ(8, bitSizeOfVarUInt16(1U << (0U)));
    EXPECT_EQ(8, bitSizeOfVarUInt16((1U << (7U)) - 1));

    EXPECT_EQ(16, bitSizeOfVarUInt16(1U << (7U)));
    EXPECT_EQ(16, bitSizeOfVarUInt16((1U << (7U + 8)) - 1));

    const uint16_t outOfRangeValue = 1U << (7U + 8);
    ASSERT_THROW(bitSizeOfVarUInt16(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt32)
{
    EXPECT_EQ(8, bitSizeOfVarUInt32(0));

    EXPECT_EQ(8, bitSizeOfVarUInt32(1U << (0U)));
    EXPECT_EQ(8, bitSizeOfVarUInt32((1U << (7U)) - 1));

    EXPECT_EQ(16, bitSizeOfVarUInt32(1U << (7U)));
    EXPECT_EQ(16, bitSizeOfVarUInt32((1U << (7U + 7)) - 1));

    EXPECT_EQ(24, bitSizeOfVarUInt32(1U << (7U + 7)));
    EXPECT_EQ(24, bitSizeOfVarUInt32((1U << (7U + 7 + 7)) - 1));

    EXPECT_EQ(32, bitSizeOfVarUInt32(1U << (7U + 7 + 7)));
    EXPECT_EQ(32, bitSizeOfVarUInt32((1U << (7U + 7 + 7 + 8)) - 1));

    const uint32_t outOfRangeValue = 1U << (7U + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarUInt32(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt64)
{
    EXPECT_EQ(8, bitSizeOfVarUInt64(0));

    EXPECT_EQ(8, bitSizeOfVarUInt64(UINT64_C(1) << (0U)));
    EXPECT_EQ(8, bitSizeOfVarUInt64((UINT64_C(1) << (7U)) - 1));

    EXPECT_EQ(16, bitSizeOfVarUInt64(UINT64_C(1) << (7U)));
    EXPECT_EQ(16, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7)) - 1));

    EXPECT_EQ(24, bitSizeOfVarUInt64(UINT64_C(1) << (7U + 7)));
    EXPECT_EQ(24, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7 + 7)) - 1));

    EXPECT_EQ(32, bitSizeOfVarUInt64(UINT64_C(1) << (7U + 7 + 7)));
    EXPECT_EQ(32, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7 + 7 + 7)) - 1));

    EXPECT_EQ(40, bitSizeOfVarUInt64(UINT64_C(1) << (7U + 7 + 7 + 7)));
    EXPECT_EQ(40, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7 + 7 + 7 + 7)) - 1));

    EXPECT_EQ(48, bitSizeOfVarUInt64(UINT64_C(1) << (7U + 7 + 7 + 7 + 7)));
    EXPECT_EQ(48, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7 + 7 + 7 + 7 + 7)) - 1));

    EXPECT_EQ(56, bitSizeOfVarUInt64(UINT64_C(1) << (7U + 7 + 7 + 7 + 7 + 7)));
    EXPECT_EQ(56, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7 + 7 + 7 + 7 + 7 + 7)) - 1));

    EXPECT_EQ(64, bitSizeOfVarUInt64(UINT64_C(1) << (7U + 7 + 7 + 7 + 7 + 7 + 7)));
    EXPECT_EQ(64, bitSizeOfVarUInt64((UINT64_C(1) << (7U + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1));

    const uint64_t outOfRangeValue = UINT64_C(1) << (7U + 7 + 7  + 7 + 7 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarUInt64(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarInt)
{
    EXPECT_EQ(8, bitSizeOfVarInt(INT64_C(0)));
    EXPECT_EQ(8, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 6U) + 1));
    EXPECT_EQ(8, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 6U) - 1));
    EXPECT_EQ(16, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 6U)));
    EXPECT_EQ(16, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 6U)));
    EXPECT_EQ(16, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 13U) + 1));
    EXPECT_EQ(16, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 13U) - 1));
    EXPECT_EQ(24, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 13U)));
    EXPECT_EQ(24, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 13U)));
    EXPECT_EQ(24, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 20U) + 1));
    EXPECT_EQ(24, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 20U) - 1));
    EXPECT_EQ(32, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 20U)));
    EXPECT_EQ(32, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 20U)));
    EXPECT_EQ(32, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 27U) + 1));
    EXPECT_EQ(32, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 27U) - 1));
    EXPECT_EQ(40, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 27U)));
    EXPECT_EQ(40, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 27U)));
    EXPECT_EQ(40, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 34U) + 1));
    EXPECT_EQ(40, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 34U) - 1));
    EXPECT_EQ(48, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 34U)));
    EXPECT_EQ(48, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 34U)));
    EXPECT_EQ(48, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 41U) + 1));
    EXPECT_EQ(48, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 41U) - 1));
    EXPECT_EQ(56, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 41U)));
    EXPECT_EQ(56, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 41U)));
    EXPECT_EQ(56, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 48U) + 1));
    EXPECT_EQ(56, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 48U) - 1));
    EXPECT_EQ(64, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 48U)));
    EXPECT_EQ(64, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 48U)));
    EXPECT_EQ(64, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 55U) + 1));
    EXPECT_EQ(64, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 55U) - 1));
    EXPECT_EQ(72, bitSizeOfVarInt(-static_cast<int64_t>(UINT64_C(1) << 55U)));
    EXPECT_EQ(72, bitSizeOfVarInt(static_cast<int64_t>(UINT64_C(1) << 55U)));
    EXPECT_EQ(72, bitSizeOfVarInt(INT64_MIN + 1));
    EXPECT_EQ(72, bitSizeOfVarInt(INT64_MAX));

    // special case, INT64_MIN is stored as -0
    EXPECT_EQ(8, bitSizeOfVarInt(INT64_MIN));
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarUInt)
{
    EXPECT_EQ(8, bitSizeOfVarUInt(UINT64_C(0)));
    EXPECT_EQ(8, bitSizeOfVarUInt((UINT64_C(1) << 7U) - 1));
    EXPECT_EQ(16, bitSizeOfVarUInt((UINT64_C(1) << 7U)));
    EXPECT_EQ(16, bitSizeOfVarUInt((UINT64_C(1) << 14U) - 1));
    EXPECT_EQ(24, bitSizeOfVarUInt((UINT64_C(1) << 14U)));
    EXPECT_EQ(24, bitSizeOfVarUInt((UINT64_C(1) << 21U) - 1));
    EXPECT_EQ(32, bitSizeOfVarUInt((UINT64_C(1) << 21U)));
    EXPECT_EQ(32, bitSizeOfVarUInt((UINT64_C(1) << 28U) - 1));
    EXPECT_EQ(40, bitSizeOfVarUInt((UINT64_C(1) << 28U)));
    EXPECT_EQ(40, bitSizeOfVarUInt((UINT64_C(1) << 35U) - 1));
    EXPECT_EQ(48, bitSizeOfVarUInt((UINT64_C(1) << 35U)));
    EXPECT_EQ(48, bitSizeOfVarUInt((UINT64_C(1) << 42U) - 1));
    EXPECT_EQ(56, bitSizeOfVarUInt((UINT64_C(1) << 42U)));
    EXPECT_EQ(56, bitSizeOfVarUInt((UINT64_C(1) << 49U) - 1));
    EXPECT_EQ(64, bitSizeOfVarUInt((UINT64_C(1) << 49U)));
    EXPECT_EQ(64, bitSizeOfVarUInt((UINT64_C(1) << 56U) - 1));
    EXPECT_EQ(72, bitSizeOfVarUInt((UINT64_C(1) << 56U)));
    EXPECT_EQ(72, bitSizeOfVarUInt(UINT64_MAX));
}

TEST(BitSizeOfCalculatorTest, bitSizeOfVarSize)
{
    EXPECT_EQ(8, bitSizeOfVarSize(0));

    EXPECT_EQ(8, bitSizeOfVarSize(1U << (0U)));
    EXPECT_EQ(8, bitSizeOfVarSize((1U << (7U)) - 1));

    EXPECT_EQ(16, bitSizeOfVarSize(1U << (7U)));
    EXPECT_EQ(16, bitSizeOfVarSize((1U << (7U + 7)) - 1));

    EXPECT_EQ(24, bitSizeOfVarSize(1U << (7U + 7)));
    EXPECT_EQ(24, bitSizeOfVarSize((1U << (7U + 7 + 7)) - 1));

    EXPECT_EQ(32, bitSizeOfVarSize(1U << (7U + 7 + 7)));
    EXPECT_EQ(32, bitSizeOfVarSize((1U << (7U + 7 + 7 + 7)) - 1));

    EXPECT_EQ(40, bitSizeOfVarSize(1U << (7U + 7 + 7 + 7)));
    EXPECT_EQ(40, bitSizeOfVarSize((1U << (2U + 7 + 7 + 7 + 8)) - 1));

    const uint32_t outOfRangeValue = 1U << (2U + 7 + 7 + 7 + 8);
    ASSERT_THROW(bitSizeOfVarSize(outOfRangeValue), CppRuntimeException);
}

TEST(BitSizeOfCalculatorTest, bitSizeOfString)
{
    EXPECT_EQ((1 + 1) * 8, bitSizeOfString(std::string("T")));
    EXPECT_EQ((1 + 4) * 8, bitSizeOfString(std::string("Test")));

    const size_t testStringLength = static_cast<size_t>(1U << 7U);
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
