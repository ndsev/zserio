#include "math.h"

#include "gtest/gtest.h"

#include "expressions/parametrized_array_type/ParametrizedArrayTypeExpression.h"

namespace expressions
{
namespace parametrized_array_type
{

TEST(ParametrizedArrayTypeTest, bitSizeOfWithOptional)
{
    ParametrizedArrayHolder parametrizedArrayHolder;
    const size_t arrayLength = 2;
    zserio::ObjectArray<ParametrizedArrayElement> array;
    array.reserve(arrayLength);
    for (size_t i = 1; i <= arrayLength; ++i)
    {
        ParametrizedArrayElement element;
        element.setValue1(0);
        array.push_back(element);
    }
    parametrizedArrayHolder.setArray(array);

    ParametrizedArrayTypeExpression parametrizedArrayTypeExpression;
    parametrizedArrayTypeExpression.setHolder(parametrizedArrayHolder);
    parametrizedArrayTypeExpression.setIsValue1Zero(true);
    parametrizedArrayTypeExpression.initializeChildren();

    const size_t parametrizedArrayTypeExpressionBitSizeWithOptional = 33;
    ASSERT_EQ(parametrizedArrayTypeExpressionBitSizeWithOptional, parametrizedArrayTypeExpression.bitSizeOf());
}

TEST(ParametrizedArrayTypeTest, bitSizeOfWithoutOptional)
{
    ParametrizedArrayHolder parametrizedArrayHolder;
    const size_t arrayLength = 2;
    zserio::ObjectArray<ParametrizedArrayElement> array;
    array.reserve(arrayLength);
    for (size_t i = 1; i <= arrayLength; ++i)
    {
        ParametrizedArrayElement element;
        element.setValue1(1);
        array.push_back(element);
    }
    parametrizedArrayHolder.setArray(array);

    ParametrizedArrayTypeExpression parametrizedArrayTypeExpression;
    parametrizedArrayTypeExpression.setHolder(parametrizedArrayHolder);
    parametrizedArrayTypeExpression.setIsValue1Zero(false);
    parametrizedArrayTypeExpression.initializeChildren();

    const size_t parametrizedArrayTypeExpressionBitSizeWithOptional = 32;
    ASSERT_EQ(parametrizedArrayTypeExpressionBitSizeWithOptional, parametrizedArrayTypeExpression.bitSizeOf());
}

} // namespace parametrized_array_type
} // namespace expressions
