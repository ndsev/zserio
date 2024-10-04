#include <cstdio>
#include <fstream>
#include <string>

#include "gtest/gtest.h"
#include "subtypes/enum_subtype/CONST_BLACK.h"
#include "subtypes/enum_subtype/Color.h"

namespace subtypes
{
namespace enum_subtype
{

TEST(EnumSubtypeTest, testSubtype)
{
    ASSERT_EQ(Color::BLACK, CONST_BLACK);
}

} // namespace enum_subtype
} // namespace subtypes
