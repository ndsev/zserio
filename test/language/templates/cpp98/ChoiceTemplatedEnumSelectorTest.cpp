#include "gtest/gtest.h"

#include "templates/choice_templated_enum_selector/ChoiceTemplatedEnumSelector.h"

namespace templates
{
namespace choice_templated_enum_selector
{

TEST(ChoiceTemplatedEnumSelectorTest, readWrite)
{
    ChoiceTemplatedEnumSelector choiceTemplatedEnumSelector;
    choiceTemplatedEnumSelector.setSelectorFromZero(EnumFromZero::ONE);
    choiceTemplatedEnumSelector.setSelectorFromOne(EnumFromOne::THREE);
    TemplatedChoice_EnumFromZero& fromZeroChoice = choiceTemplatedEnumSelector.getFromZeroChoice();
    fromZeroChoice.setUint16Field(42);
    TemplatedChoice_EnumFromOne& fromOneChoice = choiceTemplatedEnumSelector.getFromOneChoice();
    fromOneChoice.setStringField("string");

    zserio::BitStreamWriter writer;
    choiceTemplatedEnumSelector.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    ChoiceTemplatedEnumSelector readChoiceTemplatedEnumSelector(reader);

    ASSERT_TRUE(choiceTemplatedEnumSelector == readChoiceTemplatedEnumSelector);
}

} // namespace choice_templated_enum_selector
} // namespace templates
