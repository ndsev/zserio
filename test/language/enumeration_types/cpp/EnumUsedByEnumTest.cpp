#include "gtest/gtest.h"

#include "enumeration_types/enum_used_by_enum/LightColor.h"
#include "enumeration_types/enum_used_by_enum/DarkColor.h"
#include "enumeration_types/enum_used_by_enum/Color.h"

namespace enumeration_types
{
namespace enum_used_by_enum
{

class EnumUsedByEnumTest : public ::testing::Test
{
protected:
    static const uint8_t VALUE_NONE;
    static const uint8_t VALUE_LIGHT_RED;
    static const uint8_t VALUE_LIGHT_GREEN;
    static const uint8_t VALUE_LIGHT_BLUE;
    static const uint8_t VALUE_LIGHT_PINK;
    static const uint8_t VALUE_DARK_RED;
    static const uint8_t VALUE_DARK_GREEN;
    static const uint8_t VALUE_DARK_BLUE;
    static const uint8_t VALUE_DARK_PINK;
};

const uint8_t EnumUsedByEnumTest::VALUE_NONE          = 0x00;
const uint8_t EnumUsedByEnumTest::VALUE_LIGHT_RED     = 0x01;
const uint8_t EnumUsedByEnumTest::VALUE_LIGHT_GREEN   = 0x02;
const uint8_t EnumUsedByEnumTest::VALUE_LIGHT_BLUE    = 0x03;
const uint8_t EnumUsedByEnumTest::VALUE_LIGHT_PINK    = 0x04;
const uint8_t EnumUsedByEnumTest::VALUE_DARK_RED      = 0x11;
const uint8_t EnumUsedByEnumTest::VALUE_DARK_GREEN    = 0x12;
const uint8_t EnumUsedByEnumTest::VALUE_DARK_BLUE     = 0x13;
const uint8_t EnumUsedByEnumTest::VALUE_DARK_PINK     = 0x14;

TEST_F(EnumUsedByEnumTest, lightColor)
{
    ASSERT_EQ(VALUE_LIGHT_RED, zserio::enumToValue(LightColor::LIGHT_RED));
    ASSERT_EQ(VALUE_LIGHT_GREEN, zserio::enumToValue(LightColor::LIGHT_GREEN));
    ASSERT_EQ(VALUE_LIGHT_BLUE, zserio::enumToValue(LightColor::LIGHT_BLUE));
}

TEST_F(EnumUsedByEnumTest, darkColor)
{
    ASSERT_EQ(VALUE_DARK_RED, zserio::enumToValue(DarkColor::DARK_RED));
    ASSERT_EQ(VALUE_DARK_GREEN, zserio::enumToValue(DarkColor::DARK_GREEN));
    ASSERT_EQ(VALUE_DARK_BLUE, zserio::enumToValue(DarkColor::DARK_BLUE));
}

TEST_F(EnumUsedByEnumTest, color)
{
    ASSERT_EQ(VALUE_NONE, zserio::enumToValue(Color::NONE));
    ASSERT_EQ(VALUE_LIGHT_RED, zserio::enumToValue(Color::LIGHT_RED));
    ASSERT_EQ(VALUE_LIGHT_GREEN, zserio::enumToValue(Color::LIGHT_GREEN));
    ASSERT_EQ(VALUE_LIGHT_BLUE, zserio::enumToValue(Color::LIGHT_BLUE));
    ASSERT_EQ(VALUE_LIGHT_PINK, zserio::enumToValue(Color::LIGHT_PINK));
    ASSERT_EQ(VALUE_DARK_RED, zserio::enumToValue(Color::DARK_RED));
    ASSERT_EQ(VALUE_DARK_GREEN, zserio::enumToValue(Color::DARK_GREEN));
    ASSERT_EQ(VALUE_DARK_BLUE, zserio::enumToValue(Color::DARK_BLUE));
    ASSERT_EQ(VALUE_DARK_PINK, zserio::enumToValue(Color::DARK_PINK));
}

} // namespace enum_used_by_enum
} // namespace enumeration_types
