#include <cstdio>
#include <fstream>
#include <string>

#include "gtest/gtest.h"
#include "subtypes/bitmask_subtype/CONST_READ.h"
#include "subtypes/bitmask_subtype/Permission.h"

namespace subtypes
{
namespace bitmask_subtype
{

TEST(BitmaskSubtypeTest, testSubtype)
{
    ASSERT_EQ(Permission::Values::READ, CONST_READ);
}

} // namespace bitmask_subtype
} // namespace subtypes
