#include "gtest/gtest.h"

#include "choice_types/enum_with_removed_item_param_choice/EnumWithRemovedItemParamChoice.h"

#include "zserio/SerializeUtil.h"

namespace choice_types
{
namespace enum_with_removed_item_param_choice
{

TEST(EnumWithRemovedItemParamChoiceTest, writeRead)
{
    EnumWithRemovedItemParamChoice enumWithRemovedItemParamChoice;
    enumWithRemovedItemParamChoice.setGreyData(0xCAFE);

    auto bitBuffer = zserio::serialize(enumWithRemovedItemParamChoice, Selector::ZSERIO_REMOVED_GREY);

    const auto readEnumWithRemovedItemParamChoice =
            zserio::deserialize<EnumWithRemovedItemParamChoice>(bitBuffer, Selector::ZSERIO_REMOVED_GREY);
    ASSERT_EQ(enumWithRemovedItemParamChoice, readEnumWithRemovedItemParamChoice);
}

} // namespace enum_with_removed_item_param_choice
} // namespace choice_types
