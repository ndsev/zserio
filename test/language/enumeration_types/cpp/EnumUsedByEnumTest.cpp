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
    static const uint8_t VALUE_NONE          = 0x00;
    static const uint8_t VALUE_LIGHT_RED     = 0x01;
    static const uint8_t VALUE_LIGHT_GREEN   = 0x02;
    static const uint8_t VALUE_LIGHT_BLUE    = 0x03;
    static const uint8_t VALUE_LIGHT_PINK    = 0x04;
    static const uint8_t VALUE_DARK_RED      = 0x11;
    static const uint8_t VALUE_DARK_GREEN    = 0x12;
    static const uint8_t VALUE_DARK_BLUE     = 0x13;
    static const uint8_t VALUE_DARK_PINK     = 0x14;

};

TEST_F(EnumUsedByEnumTest, lightColor)
{
    uint8_t expectedEnumValue = VALUE_LIGHT_RED;
    ASSERT_EQ(expectedEnumValue, LightColor::LIGHT_RED);

    expectedEnumValue = VALUE_LIGHT_GREEN;
    ASSERT_EQ(expectedEnumValue, LightColor::LIGHT_GREEN);

    expectedEnumValue = VALUE_LIGHT_BLUE;
    ASSERT_EQ(expectedEnumValue, LightColor::LIGHT_BLUE);
}

TEST_F(EnumUsedByEnumTest, darkColor)
{
    uint8_t expectedEnumValue = VALUE_DARK_RED;
    ASSERT_EQ(expectedEnumValue, DarkColor::DARK_RED);

    expectedEnumValue = VALUE_DARK_GREEN;
    ASSERT_EQ(expectedEnumValue, DarkColor::DARK_GREEN);

    expectedEnumValue = VALUE_DARK_BLUE;
    ASSERT_EQ(expectedEnumValue, DarkColor::DARK_BLUE);
}

TEST_F(EnumUsedByEnumTest, color)
{
    uint8_t expectedEnumValue = VALUE_NONE;
    ASSERT_EQ(expectedEnumValue, Color::NONE);

    expectedEnumValue = VALUE_LIGHT_RED;
    ASSERT_EQ(expectedEnumValue, Color::LIGHT_RED);

    expectedEnumValue = VALUE_LIGHT_GREEN;
    ASSERT_EQ(expectedEnumValue, Color::LIGHT_GREEN);

    expectedEnumValue = VALUE_LIGHT_BLUE;
    ASSERT_EQ(expectedEnumValue, Color::LIGHT_BLUE);

    expectedEnumValue = VALUE_LIGHT_PINK;
    ASSERT_EQ(expectedEnumValue, Color::LIGHT_PINK);

    expectedEnumValue = VALUE_DARK_RED;
    ASSERT_EQ(expectedEnumValue, Color::DARK_RED);

    expectedEnumValue = VALUE_DARK_GREEN;
    ASSERT_EQ(expectedEnumValue, Color::DARK_GREEN);

    expectedEnumValue = VALUE_DARK_BLUE;
    ASSERT_EQ(expectedEnumValue, Color::DARK_BLUE);

    expectedEnumValue = VALUE_DARK_PINK;
    ASSERT_EQ(expectedEnumValue, Color::DARK_PINK);
}

} // namespace enum_used_by_enum
} // namespace enumeration_types
