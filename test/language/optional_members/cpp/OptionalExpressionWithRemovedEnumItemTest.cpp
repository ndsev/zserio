#include "gtest/gtest.h"
#include "optional_members/optional_expression_with_removed_enum_item/Compound.h"
#include "zserio/SerializeUtil.h"

namespace optional_members
{
namespace optional_expression_with_removed_enum_item
{

TEST(OptionalExpressionWithRemovedEnumItemTest, writeRead)
{
    Compound compound(12, {{1, 2}});
    auto bitBuffer = zserio::serialize(compound);

    const Compound readCompound = zserio::deserialize<Compound>(bitBuffer);
    ASSERT_EQ(compound, readCompound);
}

} // namespace optional_expression_with_removed_enum_item
} // namespace optional_members
