#include "expressions/division_operator/DivisionFunction.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace division_operator
{

TEST(DivisionOperatorTest, divideFloatByInt)
{
    const DivisionFunction fun(10, 2);
    ASSERT_NEAR(fun.funcDivideFloatByInt(), 3.33333, 1e-5);
}

TEST(DivisionOperatorTest, divideIntByFloat)
{
    const DivisionFunction fun(10, 2);
    ASSERT_NEAR(fun.funcDivideIntByFloat(), 3.33333, 1e-5);
}

TEST(DivisionOperatorTest, divideFloatByFloat)
{
    const DivisionFunction fun(10, 2);
    ASSERT_NEAR(fun.funcDivideFloatByFloat(), 3.33333, 1e-5);
}

TEST(DivisionOperatorTest, divideIntByInt)
{
    const DivisionFunction fun(10, 2);
    ASSERT_EQ(fun.funcDivideIntByInt(), 3);
}

} // namespace division_operator
} // namespace expressions
