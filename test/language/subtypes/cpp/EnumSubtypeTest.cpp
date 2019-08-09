#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "subtypes/enum_subtype/Color.h"
#include "subtypes/enum_subtype/CONST_BLACK.h"

namespace subtypes
{
namespace enum_subtype
{

TEST(EnumSubtypeTest, TestSubtype)
{
    ASSERT_EQ(Color::BLACK, CONST_BLACK);
}

} // namespace enum_subtype
} // namespace subtypes
