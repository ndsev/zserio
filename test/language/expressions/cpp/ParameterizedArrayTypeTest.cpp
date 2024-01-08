#include "expressions/parameterized_array_type/ParameterizedArrayTypeExpression.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"

namespace expressions
{
namespace parameterized_array_type
{

using allocator_type = ParameterizedArrayTypeExpression::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(ParameterizedArrayTypeTest, bitSizeOfWithOptional)
{
    ParameterizedArrayHolder parameterizedArrayHolder;
    const size_t arrayLength = 2;
    vector_type<ParameterizedArrayElement> array;
    array.reserve(arrayLength);
    for (size_t i = 1; i <= arrayLength; ++i)
    {
        ParameterizedArrayElement element;
        element.setValue1(0);
        array.push_back(element);
    }
    parameterizedArrayHolder.setArray(array);

    ParameterizedArrayTypeExpression parameterizedArrayTypeExpression;
    parameterizedArrayTypeExpression.setHolder(parameterizedArrayHolder);
    parameterizedArrayTypeExpression.setIsValue1Zero(true);
    parameterizedArrayTypeExpression.initializeChildren();

    const size_t parameterizedArrayTypeExpressionBitSizeWithOptional = 33;
    ASSERT_EQ(
            parameterizedArrayTypeExpressionBitSizeWithOptional, parameterizedArrayTypeExpression.bitSizeOf());
}

TEST(ParameterizedArrayTypeTest, bitSizeOfWithoutOptional)
{
    ParameterizedArrayHolder parameterizedArrayHolder;
    const size_t arrayLength = 2;
    vector_type<ParameterizedArrayElement> array;
    array.reserve(arrayLength);
    for (size_t i = 1; i <= arrayLength; ++i)
    {
        ParameterizedArrayElement element;
        element.setValue1(1);
        array.push_back(element);
    }
    parameterizedArrayHolder.setArray(array);

    ParameterizedArrayTypeExpression parameterizedArrayTypeExpression;
    parameterizedArrayTypeExpression.setHolder(parameterizedArrayHolder);
    parameterizedArrayTypeExpression.setIsValue1Zero(false);
    parameterizedArrayTypeExpression.initializeChildren();

    const size_t parameterizedArrayTypeExpressionBitSizeWithOptional = 32;
    ASSERT_EQ(
            parameterizedArrayTypeExpressionBitSizeWithOptional, parameterizedArrayTypeExpression.bitSizeOf());
}

} // namespace parameterized_array_type
} // namespace expressions
