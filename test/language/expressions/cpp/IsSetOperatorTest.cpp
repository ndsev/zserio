#include "expressions/isset_operator/IsSetOperator.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace isset_operator
{

TEST(IsSetOperatorTest, hasNone)
{
    IsSetOperator isSetOperator;
    isSetOperator.initializeChildren();

    ASSERT_FALSE(isSetOperator.funcHasInt());
    ASSERT_FALSE(isSetOperator.funcHasString());
    ASSERT_FALSE(isSetOperator.funcHasBoth());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasInt());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasString());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasBoth());

    ASSERT_FALSE(isSetOperator.getParameterized().isIntFieldUsed());
    ASSERT_FALSE(isSetOperator.getParameterized().isIntFieldSet());
    ASSERT_FALSE(isSetOperator.getParameterized().isStringFieldUsed());
    ASSERT_FALSE(isSetOperator.getParameterized().isStringFieldSet());
}

TEST(IsSetOperatorTest, hasInt)
{
    IsSetOperator isSetOperator;
    isSetOperator.setTestBitmask(TestBitmask::Values::INT);
    isSetOperator.getParameterized().setIntField(13);
    isSetOperator.initializeChildren();

    ASSERT_TRUE(isSetOperator.funcHasInt());
    ASSERT_FALSE(isSetOperator.funcHasString());
    ASSERT_FALSE(isSetOperator.funcHasBoth());
    ASSERT_TRUE(isSetOperator.getParameterized().funcHasInt());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasString());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasBoth());

    ASSERT_TRUE(isSetOperator.getParameterized().isIntFieldUsed());
    ASSERT_TRUE(isSetOperator.getParameterized().isIntFieldSet());
    ASSERT_FALSE(isSetOperator.getParameterized().isStringFieldUsed());
    ASSERT_FALSE(isSetOperator.getParameterized().isStringFieldSet());
}

TEST(IsSetOperatorTest, hasString)
{
    IsSetOperator isSetOperator;
    isSetOperator.setTestBitmask(TestBitmask::Values::STRING);
    isSetOperator.getParameterized().setStringField("test");
    isSetOperator.initializeChildren();

    ASSERT_FALSE(isSetOperator.funcHasInt());
    ASSERT_TRUE(isSetOperator.funcHasString());
    ASSERT_FALSE(isSetOperator.funcHasBoth());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasInt());
    ASSERT_TRUE(isSetOperator.getParameterized().funcHasString());
    ASSERT_FALSE(isSetOperator.getParameterized().funcHasBoth());

    ASSERT_FALSE(isSetOperator.getParameterized().isIntFieldUsed());
    ASSERT_FALSE(isSetOperator.getParameterized().isIntFieldSet());
    ASSERT_TRUE(isSetOperator.getParameterized().isStringFieldUsed());
    ASSERT_TRUE(isSetOperator.getParameterized().isStringFieldSet());
}

TEST(IsSetOperatorTest, hasBoth)
{
    IsSetOperator isSetOperator;
    isSetOperator.setTestBitmask(TestBitmask::Values::BOTH);
    isSetOperator.getParameterized().setIntField(13);
    isSetOperator.getParameterized().setStringField("test");
    isSetOperator.initializeChildren();

    ASSERT_TRUE(isSetOperator.funcHasInt());
    ASSERT_TRUE(isSetOperator.funcHasString());
    ASSERT_TRUE(isSetOperator.funcHasBoth());
    ASSERT_TRUE(isSetOperator.getParameterized().funcHasInt());
    ASSERT_TRUE(isSetOperator.getParameterized().funcHasString());
    ASSERT_TRUE(isSetOperator.getParameterized().funcHasBoth());

    ASSERT_TRUE(isSetOperator.getParameterized().isIntFieldUsed());
    ASSERT_TRUE(isSetOperator.getParameterized().isIntFieldSet());
    ASSERT_TRUE(isSetOperator.getParameterized().isStringFieldUsed());
    ASSERT_TRUE(isSetOperator.getParameterized().isStringFieldSet());
}

} // namespace isset_operator
} // namespace expressions
