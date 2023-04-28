#include <limits>
#include <array>

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
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt8)
{
    using type = uint8_t;
    const type value = std::numeric_limits<type>::max();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertInt16)
{
    using type = int16_t;
    const type value = std::numeric_limits<type>::min();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt16)
{
    using type = uint16_t;
    const type value = std::numeric_limits<type>::max();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertInt32)
{
    using type = int32_t;
    const type value = std::numeric_limits<type>::min();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt32)
{
    using type = uint32_t;
    const type value = std::numeric_limits<type>::max();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertInt64)
{
    using type = int64_t;
    const type value = std::numeric_limits<type>::min();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertUInt64)
{
    using type = uint64_t;
    const type value = std::numeric_limits<type>::max();
    std::array<char, 24> buffer;
    const char* valueInString = convertIntToString(buffer, value);
    EXPECT_EQ(std::to_string(value), valueInString);

    EXPECT_EQ(std::to_string(value), toString(value));
}

TEST(StringConvertUtilTest, convertFloat)
{
    std::array<char, 24> integerBuffer;
    std::array<char, 24> floatingBuffer;
    const char* integerString = "";
    const char* floatingString = "";
    convertFloatToString(integerBuffer, floatingBuffer, 13579.2468F, integerString, floatingString);
    EXPECT_EQ(std::string("13579"), integerString);
    EXPECT_EQ(std::string("247"), floatingString);

    convertFloatToString(integerBuffer, floatingBuffer, -2468.123456789F, integerString, floatingString);
    EXPECT_EQ(std::string("-2468"), integerString);
    EXPECT_EQ(std::string("123"), floatingString);

    convertFloatToString(integerBuffer, floatingBuffer, 1.0F, integerString, floatingString);
    EXPECT_EQ(std::string("1"), integerString);
    EXPECT_EQ(std::string("0"), floatingString);

    convertFloatToString(integerBuffer, floatingBuffer, 1E10F, integerString, floatingString);
    EXPECT_EQ(std::string("10000000000"), integerString);
    EXPECT_EQ(std::string("0"), floatingString);

    convertFloatToString(integerBuffer, floatingBuffer, -1E10F, integerString, floatingString);
    EXPECT_EQ(std::string("-10000000000"), integerString);
    EXPECT_EQ(std::string("0"), floatingString);

    convertFloatToString(integerBuffer, floatingBuffer, 1E20F, integerString, floatingString);
    EXPECT_EQ(std::string("+Inf"), integerString);
    EXPECT_EQ(nullptr, floatingString);

    convertFloatToString(integerBuffer, floatingBuffer, -1E20F, integerString, floatingString);
    EXPECT_EQ(std::string("-Inf"), integerString);
    EXPECT_EQ(nullptr, floatingString);
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
    const std::array<char, 4> buffer = { 't', 'e', 's', 't' };
    EXPECT_EQ("test", toString(StringView(buffer.data(), buffer.size())));
}

} // namespace zserio
