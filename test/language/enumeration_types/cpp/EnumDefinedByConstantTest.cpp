#include "gtest/gtest.h"

#include "enumeration_types/ConstType.h"

#include "enumeration_types/enum_defined_by_constant/Colors.h"

namespace enumeration_types
{
namespace enum_defined_by_constant
{

class EnumDefinedByConstant : public ::testing::Test
{
};

TEST_F(EnumDefinedByConstant, lightColor)
{
    ASSERT_EQ(1, ConstType::WHITE_COLOR);
    ASSERT_EQ(ConstType::WHITE_COLOR, Colors::White);
    ASSERT_EQ(Colors::White + 1, Colors::Black);
}

} // namespace enum_defined_by_constant
} // namespace enumeration_types
