#include "zserio/BitPositionUtil.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitPositionUtilTest, AlignTo)
{
    const size_t bitSize = 5;
    EXPECT_EQ(5, alignTo(0, bitSize));
    EXPECT_EQ(5, alignTo(1, bitSize));
    EXPECT_EQ(6, alignTo(2, bitSize));
    EXPECT_EQ(6, alignTo(3, bitSize));
    EXPECT_EQ(8, alignTo(4, bitSize));
    EXPECT_EQ(5, alignTo(5, bitSize));
    EXPECT_EQ(6, alignTo(6, bitSize));
    EXPECT_EQ(7, alignTo(7, bitSize));
    EXPECT_EQ(8, alignTo(8, bitSize));
}

TEST(BitPositionUtilTest, BitsToBytes)
{
    EXPECT_EQ(0, bitsToBytes(0));
    EXPECT_EQ(1, bitsToBytes(8));
    ASSERT_THROW(bitsToBytes(9), CppRuntimeException);
    EXPECT_EQ(2, bitsToBytes(16));
}

TEST(BitPositionUtilTest, BytesToBits)
{
    EXPECT_EQ(0, bytesToBits(0));
    EXPECT_EQ(8, bytesToBits(1));
    EXPECT_EQ(16, bytesToBits(2));
}

} // namespace zserio
