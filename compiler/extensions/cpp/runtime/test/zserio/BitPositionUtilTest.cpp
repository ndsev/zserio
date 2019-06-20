#include "zserio/BitPositionUtil.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitPositionUtilTest, alignTo)
{
    const size_t bitPosition = 5;
    EXPECT_EQ(5, alignTo(0, bitPosition));
    EXPECT_EQ(5, alignTo(1, bitPosition));
    EXPECT_EQ(6, alignTo(2, bitPosition));
    EXPECT_EQ(6, alignTo(3, bitPosition));
    EXPECT_EQ(8, alignTo(4, bitPosition));
    EXPECT_EQ(5, alignTo(5, bitPosition));
    EXPECT_EQ(6, alignTo(6, bitPosition));
    EXPECT_EQ(7, alignTo(7, bitPosition));
    EXPECT_EQ(8, alignTo(8, bitPosition));
}

TEST(BitPositionUtilTest, bitsToBytes)
{
    EXPECT_EQ(0, bitsToBytes(0));
    EXPECT_EQ(1, bitsToBytes(8));
    ASSERT_THROW(bitsToBytes(9), CppRuntimeException);
    EXPECT_EQ(2, bitsToBytes(16));
}

TEST(BitPositionUtilTest, bytesToBits)
{
    EXPECT_EQ(0, bytesToBits(0));
    EXPECT_EQ(8, bytesToBits(1));
    EXPECT_EQ(16, bytesToBits(2));
}

} // namespace zserio
