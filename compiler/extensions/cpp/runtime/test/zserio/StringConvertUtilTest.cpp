#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

namespace zserio
{

using allocator_type = std::allocator<uint8_t>;

TEST(StringConvertUtilTest, convertInt8)
{
    using type = int8_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt8)
{
    using type = uint8_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertInt16)
{
    using type = int16_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt16)
{
    using type = uint16_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertInt32)
{
    using type = int32_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt32)
{
    using type = uint32_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertInt64)
{
    using type = int64_t;
    const type value = std::numeric_limits<type>::min();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt64)
{
    using type = uint64_t;
    const type value = std::numeric_limits<type>::max();
    char buffer[24];
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertBool)
{
    EXPECT_EQ("true", convertBoolToString(true));
    EXPECT_EQ("false", convertBoolToString(false));

    EXPECT_EQ("true", toString(true));
    EXPECT_EQ("false", toString(false));
}

} // namespace zserio
