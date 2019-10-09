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
    choiceTemplatedEnumSelector.setFromZeroChoice(TemplatedChoice_EnumFromZero{static_cast<uint16_t>(42)});
    choiceTemplatedEnumSelector.setFromOneChoice(TemplatedChoice_EnumFromOne{std::string{"string"}});

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
