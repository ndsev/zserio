#include "expressions/array_type/ArrayTypeExpression.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"

namespace expressions
{
namespace array_type
{

using allocator_type = ArrayTypeExpression::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(ArrayTypeTest, bitSizeOfWithOptional)
{
    ArrayTypeExpression arrayTypeExpression;
    const size_t arrayLength = 2;
    vector_type<int8_t> array(arrayLength);
    arrayTypeExpression.setArray(array);
    arrayTypeExpression.setIsZerosArrayValid(true);

    const size_t arrayTypeExpressionBitSizeWithOptional = 17;
    ASSERT_EQ(arrayTypeExpressionBitSizeWithOptional, arrayTypeExpression.bitSizeOf());
}

TEST(ArrayTypeTest, bitSizeOfWithoutOptional)
{
    ArrayTypeExpression arrayTypeExpression;
    const size_t arrayLength = 2;
    vector_type<int8_t> array;
    array.reserve(arrayLength);
    for (size_t i = 1; i <= arrayLength; ++i)
        array.push_back(static_cast<int8_t>(i));
    arrayTypeExpression.setArray(array);

    const size_t arrayTypeExpressionBitSizeWithoutOptional = 16;
    ASSERT_EQ(arrayTypeExpressionBitSizeWithoutOptional, arrayTypeExpression.bitSizeOf());
}

} // namespace array_type
} // namespace expressions
