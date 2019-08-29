#include "zserio/StringConvertUtil.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(StringConvertUtilTest, simpleTypes)
{
    const bool boolValue = true;
    const std::string boolValueInString = convertToString(boolValue);
    EXPECT_EQ("true", boolValueInString);

    const char charValue = 10;
    const std::string charValueInString = convertToString(charValue);
    EXPECT_EQ("10", charValueInString);

    const signed char signedCharValue = -10;
    const std::string signedCharValueInString = convertToString(signedCharValue);
    EXPECT_EQ("-10", signedCharValueInString);

    const unsigned char unsignedCharValue = 10;
    const std::string unsignedCharValueInString = convertToString(unsignedCharValue);
    EXPECT_EQ("10", unsignedCharValueInString);

    const int intValue = 10;
    const std::string intValueInString = convertToString(intValue);
    EXPECT_EQ("10", intValueInString);

    const long longValue = 123456789L;
    const std::string longValueInString = convertToString(longValue);
    EXPECT_EQ("123456789", longValueInString);

    const float floatValue = 9.9f;
    const std::string floatValueInString = convertToString(floatValue);
    EXPECT_EQ("9.9", floatValueInString);

    const std::string stringValue = "string";
    const std::string stringValueInString = convertToString(stringValue);
    EXPECT_EQ("string", stringValueInString);

    const char* rawStringValue = "raw string";
    const std::string rawStringValueInString = convertToString(rawStringValue);
    EXPECT_EQ("raw string", rawStringValueInString);
}

} // namespace zserio
