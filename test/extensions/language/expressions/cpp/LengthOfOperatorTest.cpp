#include "expressions/lengthof_operator/LengthOfFunctions.h"
#include "expressions/lengthof_operator/STR_CONSTANT.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace expressions
{
namespace lengthof_operator
{

using allocator_type = LengthOfFunctions::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using string_type = zserio::string<allocator_type>;

TEST(LengthOfOperatorTest, getLengthOfFixedArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t fixedArrayLength = 10;
    vector_type<uint8_t> fixedArray(fixedArrayLength);
    lengthOfFunctions.setFixedArray(fixedArray);
    ASSERT_EQ(fixedArrayLength, lengthOfFunctions.funcGetLengthOfFixedArray());
}

TEST(LengthOfOperatorTest, getLengthOfVariableArray)
{
    LengthOfFunctions lengthOfFunctions;
    const size_t variableArrayLength = 11;
    vector_type<uint8_t> variableArray(variableArrayLength);
    lengthOfFunctions.setNumElements(static_cast<uint8_t>(variableArrayLength));
    lengthOfFunctions.setVariableArray(variableArray);
    ASSERT_EQ(variableArrayLength, lengthOfFunctions.funcGetLengthOfVariableArray());
}

TEST(LengthOfOperatorTest, getLengthOfStrConstant)
{
    LengthOfFunctions lengthOfFunctions;
    ASSERT_EQ(11, STR_CONSTANT.size()); // check that it's length in bytes (UTF-8)
    ASSERT_EQ(STR_CONSTANT.size(), lengthOfFunctions.funcGetLengthOfStrConstant());
}

TEST(LengthOfOperatorTest, getLengthOfLiteral)
{
    LengthOfFunctions lengthOfFunctions;
    ASSERT_EQ(10, zserio::makeStringView("€literal").size()); // check that it's length in bytes (UTF-8)
    ASSERT_EQ(zserio::makeStringView("€literal").size(), lengthOfFunctions.funcGetLengthOfLiteral());
}

TEST(LengthOfOperatorTest, literalLengthFieldDefault)
{
    LengthOfFunctions lengthOfFunctions;
    ASSERT_EQ(10, zserio::makeStringView("€literal").size()); // check that it's length in bytes (UTF-8)
    ASSERT_EQ(zserio::makeStringView("€literal").size(), lengthOfFunctions.getLiteralLengthField());
}

TEST(LengthOfOperatorTest, getLengthOfString)
{
    auto strField = string_type("€test");
    LengthOfFunctions lengthOfFunctions;
    lengthOfFunctions.setStrField(strField);
    ASSERT_EQ(7, strField.size()); // check that it's length in bytes (UTF-8)
    ASSERT_EQ(strField.size(), lengthOfFunctions.funcGetLengthOfString());
}

TEST(LengthOfOperatorTest, getLengthOfBytes)
{
    auto bytesField = vector_type<uint8_t>{{0x00, 0x01, 0x02}};
    LengthOfFunctions lengthOfFunctions;
    lengthOfFunctions.setBytesField(bytesField);
    ASSERT_EQ(bytesField.size(), lengthOfFunctions.funcGetLengthOfBytes());
}

TEST(LengthOfOperatorTest, getLengthOfFirstStrInArray)
{
    auto strArray = vector_type<string_type>{{string_type("€"), string_type("$")}};
    LengthOfFunctions lengthOfFunctions;
    lengthOfFunctions.setStrArray(strArray);
    ASSERT_EQ(3, strArray.at(0).size()); // check that it's length in bytes (UTF-8)
    ASSERT_EQ(strArray.at(0).size(), lengthOfFunctions.funcGetLengthOfFirstStrInArray());
}

TEST(LengthOfOperatorTest, getLengthOfFirstBytesInArray)
{
    auto bytesArray = vector_type<vector_type<uint8_t>>{{{{0x00, 0x01}}, {{}}}};
    LengthOfFunctions lengthOfFunctions;
    lengthOfFunctions.setBytesArray(bytesArray);
    ASSERT_EQ(bytesArray.at(0).size(), lengthOfFunctions.funcGetLengthOfFirstBytesInArray());
}

TEST(LengthOfOperatorTest, writeRead)
{
    LengthOfFunctions lengthOfFunctions;
    lengthOfFunctions.setFixedArray(
            vector_type<uint8_t>{{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09}});
    lengthOfFunctions.setNumElements(3);
    lengthOfFunctions.setVariableArray(vector_type<uint8_t>{{0x03, 0x02, 0x01}});
    lengthOfFunctions.setStrField("longer than constant");
    lengthOfFunctions.setBytesField(vector_type<uint8_t>{{0x00, 0x01, 0x02}});
    lengthOfFunctions.setStrArray(vector_type<string_type>());
    lengthOfFunctions.setBytesArray(vector_type<vector_type<uint8_t>>());

    auto bitBuffer = zserio::serialize(lengthOfFunctions);
    auto readLengthOfFunctions = zserio::deserialize<LengthOfFunctions>(bitBuffer);
    ASSERT_EQ(lengthOfFunctions, readLengthOfFunctions);
}

} // namespace lengthof_operator
} // namespace expressions
