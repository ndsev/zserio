#include "math.h"

#include "gtest/gtest.h"

#include "expressions/lengthof_operator/LengthOfFunctions.h"

namespace expressions
{
namespace lengthof_operator
{

TEST(LengthOfOperatorTest, GetLengthOfFixedArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t fixedArrayLength = 10;
    zserio::UInt8Array fixedArray(fixedArrayLength);
    lengthOfFunctions.setFixedArray(fixedArray);
    ASSERT_EQ(fixedArrayLength, lengthOfFunctions.funcGetLengthOfFixedArray());
}

TEST(LengthOfOperatorTest, GetLengthOfVariableArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t variableArrayLength = 11;
    zserio::UInt8Array variableArray(variableArrayLength);
    lengthOfFunctions.setNumElements(static_cast<uint8_t>(variableArrayLength));
    lengthOfFunctions.setVariableArray(variableArray);
    ASSERT_EQ(variableArrayLength, lengthOfFunctions.funcGetLengthOfVariableArray());
}

TEST(LengthOfOperatorTest, GetLengthOfImplicitArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t implicitArrayLength = 12;
    zserio::UInt8Array implicitArray(implicitArrayLength);
    lengthOfFunctions.setImplicitArray(implicitArray);
    ASSERT_EQ(implicitArrayLength, lengthOfFunctions.funcGetLengthOfImplicitArray());
}

} // namespace lengthof_operator
} // namespace expressions
