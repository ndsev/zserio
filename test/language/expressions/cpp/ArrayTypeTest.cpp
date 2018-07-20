#include "math.h"

#include "gtest/gtest.h"

#include "expressions/array_type/ArrayTypeExpression.h"

namespace expressions
{
namespace array_type
{

TEST(ArrayTypeTest, bitSizeOfWithOptional)
{
    ArrayTypeExpression arrayTypeExpression;
    const size_t arrayLength = 2;
    zserio::Int8Array array(arrayLength);
    arrayTypeExpression.setArray(array);
    arrayTypeExpression.setIsZerosArrayValid(true);

    const size_t arrayTypeExpressionBitSizeWithOptional = 17;
    ASSERT_EQ(arrayTypeExpressionBitSizeWithOptional, arrayTypeExpression.bitSizeOf());
}

TEST(ArrayTypeTest, bitSizeOfWithoutOptional)
{
    ArrayTypeExpression arrayTypeExpression;
    const size_t arrayLength = 2;
    zserio::Int8Array array;
    array.reserve(arrayLength);
    for (size_t i = 1; i <= arrayLength; ++i)
        array.push_back(i);
    arrayTypeExpression.setArray(array);

    const size_t arrayTypeExpressionBitSizeWithoutOptional = 16;
    ASSERT_EQ(arrayTypeExpressionBitSizeWithoutOptional, arrayTypeExpression.bitSizeOf());
}

} // namespace array_type
} // namespace expressions
