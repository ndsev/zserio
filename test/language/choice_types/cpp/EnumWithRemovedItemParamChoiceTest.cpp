#include "gtest/gtest.h"

#include "choice_types/enum_with_removed_item_param_choice/EnumWithRemovedItemParamChoice.h"

#include "zserio/SerializeUtil.h"

namespace choice_types
{
namespace enum_with_removed_item_param_choice
{

TEST(EnumWithRemovedItemParamChoiceTest, writeRead)
{
    EnumWithRemovedItemParamChoice::ParameterExpressions parameterExpressions = {
            nullptr, 0, [](void*, size_t) { return Selector::ZSERIO_REMOVED_GREY; } };

    EnumWithRemovedItemParamChoice enumWithRemovedItemParamChoice;
    enumWithRemovedItemParamChoice.setGreyData(0xCAFE);

    auto bitBuffer = zserio::serialize(enumWithRemovedItemParamChoice, parameterExpressions);

    const auto readEnumWithRemovedItemParamChoice =
            zserio::deserialize<EnumWithRemovedItemParamChoice>(bitBuffer, parameterExpressions);
    ASSERT_EQ(enumWithRemovedItemParamChoice, readEnumWithRemovedItemParamChoice);
}

} // namespace enum_with_removed_item_param_choice
} // namespace choice_types
