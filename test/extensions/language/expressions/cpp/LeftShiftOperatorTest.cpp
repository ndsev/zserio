#include "expressions/left_shift_operator/LeftShiftOperator.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace left_shift_operator
{

TEST(LeftShiftOperatorTest, defaultValues)
{
    LeftShiftOperator leftShiftOperator;
    EXPECT_EQ(40, leftShiftOperator.getU32());
    EXPECT_EQ(-40, leftShiftOperator.getI32());
    EXPECT_EQ(32, leftShiftOperator.getU32Complex());
    EXPECT_EQ(-32, leftShiftOperator.getI32Complex());
    EXPECT_EQ(24, leftShiftOperator.getU32Plus());
    EXPECT_EQ(-64, leftShiftOperator.getI32Minus());
    EXPECT_EQ(12, leftShiftOperator.getU32PlusRhsExpr());
    EXPECT_EQ(-24, leftShiftOperator.getI32MinusRhsExpr());
    EXPECT_EQ(11534336, leftShiftOperator.getU63Complex());
    EXPECT_EQ(-9216, leftShiftOperator.getI64Complex());
}

TEST(LeftShiftOperatorTest, getU63LShift3)
{
    LeftShiftOperator leftShiftOperator;
    ASSERT_EQ(104, leftShiftOperator.funcGetU63LShift3());
}

TEST(LeftShiftOperatorTest, getI64LShift4)
{
    LeftShiftOperator leftShiftOperator;
    ASSERT_EQ(-208, leftShiftOperator.funcGetI64LShift4());
}

TEST(LeftShiftOperatorTest, getU63LShift)
{
    LeftShiftOperator leftShiftOperator;
    ASSERT_EQ(13312, leftShiftOperator.funcGetU63LShift());
}

TEST(LeftShiftOperatorTest, getI64LShift)
{
    LeftShiftOperator leftShiftOperator;
    ASSERT_EQ(-13312, leftShiftOperator.funcGetI64LShift());
}

TEST(LeftShiftOperatorTest, getPositiveI32LShift)
{
    LeftShiftOperator leftShiftOperator;
    ASSERT_EQ(13312, leftShiftOperator.funcGetPositiveI32LShift());
}

TEST(LeftShiftOperatorTest, getI64ComplexLShift)
{
    LeftShiftOperator leftShiftOperator;
    ASSERT_EQ(-3072, leftShiftOperator.funcGetI64ComplexLShift());
}

} // namespace left_shift_operator
} // namespace expressions
