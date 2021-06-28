#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

namespace zserio
{

TEST(StringConvertUtilTest, convertInt8)
{
    using type = int8_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertUInt8)
{
    using type = uint8_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertInt16)
{
    using type = int16_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertUInt16)
{
    using type = uint16_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertInt32)
{
    using type = int32_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertUInt32)
{
    using type = uint32_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertInt64)
{
    using type = int64_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

TEST(StringConvertUtilTest, convertUInt64)
{
    using type = uint64_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);
}

} // namespace zserio
