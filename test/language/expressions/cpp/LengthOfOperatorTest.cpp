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
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

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

TEST(LengthOfOperatorTest, GetLengthOfImplicitArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t implicitArrayLength = 12;
    vector_type<uint8_t> implicitArray(implicitArrayLength);
    lengthOfFunctions.setImplicitArray(implicitArray);
    ASSERT_EQ(implicitArrayLength, lengthOfFunctions.funcGetLengthOfImplicitArray());
}

} // namespace lengthof_operator
} // namespace expressions
