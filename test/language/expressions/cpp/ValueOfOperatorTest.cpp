#include "math.h"

#include "gtest/gtest.h"

#include "expressions/valueof_operator/ValueOfFunctions.h"

namespace expressions
{
namespace valueof_operator
{

TEST(ValueOfOperatorTest, GetValueOfWhiteColor)
{
    ValueOfFunctions valueOfFunctions;
    valueOfFunctions.setColor(Color::WHITE);
    const uint8_t whiteColor = 1;
    ASSERT_EQ(whiteColor, valueOfFunctions.funcGetValueOfColor());
    ASSERT_EQ(whiteColor, valueOfFunctions.funcGetValueOfWhiteColor());
}

TEST(ValueOfOperatorTest, GetValueOfBlackColor)
{
    ValueOfFunctions valueOfFunctions;
    valueOfFunctions.setColor(Color::BLACK);
    const uint8_t blackColor = 2;
    ASSERT_EQ(blackColor, valueOfFunctions.funcGetValueOfColor());
    ASSERT_EQ(blackColor, valueOfFunctions.funcGetValueOfBlackColor());
}

} // namespace valueof_operator
} // namespace expressions
