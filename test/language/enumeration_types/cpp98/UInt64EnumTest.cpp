#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "enumeration_types/uint64_enum/DarkColor.h"

namespace enumeration_types
{
namespace uint64_enum
{

TEST(UInt64EnumTest, emptyConstructor)
{
    const DarkColor darkColor;
    ASSERT_EQ(0, darkColor.getValue());
}

TEST(UInt64EnumTest, valueConstructor)
{
    const DarkColor darkColor(DarkColor::DARK_RED);
    ASSERT_EQ(1, darkColor.getValue());
}

TEST(UInt64EnumTest, toEnum)
{
    ASSERT_EQ(DarkColor::NONE, DarkColor::toEnum(0));
    ASSERT_EQ(DarkColor::DARK_RED, DarkColor::toEnum(1));
    ASSERT_EQ(DarkColor::DARK_BLUE, DarkColor::toEnum(2));
    ASSERT_EQ(DarkColor::DARK_GREEN, DarkColor::toEnum(7));
}

TEST(UInt64EnumTest, toEnumFailure)
{
    ASSERT_THROW(DarkColor::toEnum(3), zserio::CppRuntimeException);
}

} // namespace uint64_enum
} // namespace enumeration_types
