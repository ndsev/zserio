#include "math.h"

#include "gtest/gtest.h"

#include "expressions/lengthof_operator/LengthOfFunctions.h"

#include "zserio/RebindAlloc.h"

namespace expressions
{
namespace lengthof_operator
{

using allocator_type = LengthOfFunctions::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(LengthOfOperatorTest, GetLengthOfFixedArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t fixedArrayLength = 10;
    vector_type<uint8_t> fixedArray(fixedArrayLength);
    lengthOfFunctions.setFixedArray(fixedArray);
    ASSERT_EQ(fixedArrayLength, lengthOfFunctions.funcGetLengthOfFixedArray());
}

TEST(LengthOfOperatorTest, GetLengthOfVariableArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t variableArrayLength = 11;
    vector_type<uint8_t> variableArray(variableArrayLength);
    lengthOfFunctions.setNumElements(static_cast<uint8_t>(variableArrayLength));
    lengthOfFunctions.setVariableArray(variableArray);
    ASSERT_EQ(variableArrayLength, lengthOfFunctions.funcGetLengthOfVariableArray());
}

} // namespace lengthof_operator
} // namespace expressions
