#include "expressions/bitmask_type/BitmaskTypeExpression.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace bitmask_type
{

TEST(BitmaskTypeTest, bitSizeOfNoColor)
{
    BitmaskTypeExpression bitmaskTypeExpression;
    bitmaskTypeExpression.setColors(Colors());
    bitmaskTypeExpression.setHasNotColorRed(true);

    ASSERT_EQ(9, bitmaskTypeExpression.bitSizeOf());
}

TEST(BitmaskTypeTest, bitSizeOfRed)
{
    BitmaskTypeExpression bitmaskTypeExpression;
    bitmaskTypeExpression.setColors(Colors::Values::RED);
    bitmaskTypeExpression.setHasColorRed(true);

    ASSERT_EQ(9, bitmaskTypeExpression.bitSizeOf());
}

TEST(BitmaskTypeTest, bitSizeOfGreen)
{
    BitmaskTypeExpression bitmaskTypeExpression;
    bitmaskTypeExpression.setColors(Colors::Values::GREEN);
    bitmaskTypeExpression.setHasColorGreen(true);
    bitmaskTypeExpression.setHasNotColorRed(true);
    bitmaskTypeExpression.setHasOtherColorThanRed(true);

    ASSERT_EQ(11, bitmaskTypeExpression.bitSizeOf());
}

TEST(BitmaskTypeTest, bitSizeOfBlue)
{
    BitmaskTypeExpression bitmaskTypeExpression;
    bitmaskTypeExpression.setColors(Colors::Values::BLUE);
    bitmaskTypeExpression.setHasColorBlue(true);
    bitmaskTypeExpression.setHasNotColorRed(true);
    bitmaskTypeExpression.setHasOtherColorThanRed(true);

    ASSERT_EQ(11, bitmaskTypeExpression.bitSizeOf());
}

TEST(BitmaskTypeTest, bitSizeOfBlueGreen)
{
    BitmaskTypeExpression bitmaskTypeExpression;
    bitmaskTypeExpression.setColors(Colors::Values::BLUE | Colors::Values::GREEN);
    bitmaskTypeExpression.setHasColorGreen(true);
    bitmaskTypeExpression.setHasColorBlue(true);
    bitmaskTypeExpression.setHasNotColorRed(true);
    bitmaskTypeExpression.setHasOtherColorThanRed(true);

    ASSERT_EQ(12, bitmaskTypeExpression.bitSizeOf());
}

TEST(BitmaskTypeTest, bitSizeOfAllColors)
{
    BitmaskTypeExpression bitmaskTypeExpression;
    bitmaskTypeExpression.setColors(Colors::Values::RED | Colors::Values::GREEN | Colors::Values::BLUE);
    bitmaskTypeExpression.setHasColorRed(true);
    bitmaskTypeExpression.setHasColorGreen(true);
    bitmaskTypeExpression.setHasColorBlue(true);
    bitmaskTypeExpression.setHasAllColors(true);
    bitmaskTypeExpression.setHasOtherColorThanRed(true);

    ASSERT_EQ(13, bitmaskTypeExpression.bitSizeOf());
}

} // namespace bitmask_type
} // namespace expressions
