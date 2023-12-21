#include "gtest/gtest.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/CppRuntimeException.h"

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

} // namespace zserio
