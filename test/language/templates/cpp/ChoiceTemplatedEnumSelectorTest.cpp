#include "gtest/gtest.h"

#include "templates/choice_templated_enum_selector/ChoiceTemplatedEnumSelector.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace choice_templated_enum_selector
{

using allocator_type = ChoiceTemplatedEnumSelector::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(ChoiceTemplatedEnumSelectorTest, readWrite)
{
    ChoiceTemplatedEnumSelector choiceTemplatedEnumSelector;
    choiceTemplatedEnumSelector.setSelectorFromZero(EnumFromZero::ONE);
    choiceTemplatedEnumSelector.setSelectorFromOne(EnumFromOne::THREE);
    {
        TemplatedChoice_EnumFromZero fromZeroChoice;
        fromZeroChoice.setUint16Field(42);
        choiceTemplatedEnumSelector.setFromZeroChoice(std::move(fromZeroChoice));
    }
    {
        TemplatedChoice_EnumFromOne fromOneChoice;
        fromOneChoice.setStringField("string");
        choiceTemplatedEnumSelector.setFromOneChoice(fromOneChoice); // copy
    }
    choiceTemplatedEnumSelector.initializeChildren();

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    choiceTemplatedEnumSelector.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ChoiceTemplatedEnumSelector readChoiceTemplatedEnumSelector(reader);

    ASSERT_TRUE(choiceTemplatedEnumSelector == readChoiceTemplatedEnumSelector);
}

} // namespace choice_templated_enum_selector
} // namespace templates
