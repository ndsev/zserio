#include "gtest/gtest.h"

#include "templates/choice_templated_enum_selector/ChoiceTemplatedEnumSelector.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace choice_templated_enum_selector
{

using allocator_type = ChoiceTemplatedEnumSelector::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;

TEST(ChoiceTemplatedEnumSelectorTest, readWrite)
{
    ChoiceTemplatedEnumSelector choiceTemplatedEnumSelector;
    choiceTemplatedEnumSelector.setSelectorFromZero(EnumFromZero::ONE);
    choiceTemplatedEnumSelector.setSelectorFromOne(EnumFromOne::THREE);
    choiceTemplatedEnumSelector.setFromZeroChoice(TemplatedChoice_EnumFromZero{static_cast<uint16_t>(42)});
    choiceTemplatedEnumSelector.setFromOneChoice(TemplatedChoice_EnumFromOne{string_type{"string"}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    choiceTemplatedEnumSelector.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ChoiceTemplatedEnumSelector readChoiceTemplatedEnumSelector(reader);

    ASSERT_TRUE(choiceTemplatedEnumSelector == readChoiceTemplatedEnumSelector);
}

} // namespace choice_templated_enum_selector
} // namespace templates
