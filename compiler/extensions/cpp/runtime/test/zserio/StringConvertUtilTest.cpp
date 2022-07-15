#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"
#include "zserio/StringView.h"

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

TEST(StringConvertUtilTest, convertFloat)
{
    char buffer[48];
    EXPECT_EQ(std::string("13579.247"), convertFloatToString(buffer, 13579.2468f));
    EXPECT_EQ(std::string("-2468.123"), convertFloatToString(buffer, -2468.123456789f));
    EXPECT_EQ(std::string("1.0"), convertFloatToString(buffer, 1.0f));
    EXPECT_EQ(std::string("10000000000.0"), convertFloatToString(buffer, 1E10f));
    EXPECT_EQ(std::string("-10000000000.0"), convertFloatToString(buffer, -1E10f));
    EXPECT_EQ(std::string("+Inf"), convertFloatToString(buffer, 1E20f));
    EXPECT_EQ(std::string("-Inf"), convertFloatToString(buffer, -1E20f));
}

TEST(StringConvertUtilTest, convertBool)
{
    EXPECT_EQ(std::string("true"), convertBoolToString(true));
    EXPECT_EQ(std::string("false"), convertBoolToString(false));

    EXPECT_EQ("true", toString(true));
    EXPECT_EQ("false", toString(false));
}

TEST(StringConvertUtilTest, convertStringView)
{
    EXPECT_EQ(std::string(), toString(StringView()));
    EXPECT_EQ("test", toString(StringView("test")));
    const char buffer[4] = { 't', 'e', 's', 't' };
    EXPECT_EQ("test", toString(StringView(buffer, sizeof(buffer))));
}

} // namespace zserio
