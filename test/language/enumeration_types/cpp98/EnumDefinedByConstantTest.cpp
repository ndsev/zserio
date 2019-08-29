#include "gtest/gtest.h"

#include "enumeration_types/enum_defined_by_constant/Colors.h"
#include "enumeration_types/enum_defined_by_constant/WHITE_COLOR.h"

namespace enumeration_types
{
namespace enum_defined_by_constant
{

class EnumDefinedByConstant : public ::testing::Test
{
};

TEST_F(EnumDefinedByConstant, lightColor)
{
    ASSERT_EQ(1, WHITE_COLOR);
    ASSERT_EQ(WHITE_COLOR, Colors::White);
    ASSERT_EQ(Colors::White + 1, Colors::Black);
}

} // namespace enum_defined_by_constant
} // namespace enumeration_types
