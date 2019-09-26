#include "gtest/gtest.h"

#include "templates/choice_templated_selector/ChoiceTemplatedSelector.h"

namespace templates
{
namespace choice_templated_selector
{

TEST(ChoiceTemplatedSelectorTest, readWrite)
{
    ChoiceTemplatedSelector choiceTemplatedSelector;
    choiceTemplatedSelector.setSelector16(0);
    choiceTemplatedSelector.setSelector32(2);
    TemplatedChoice_uint16& uint16Choice = choiceTemplatedSelector.getUint16Choice();
    uint16Choice.setUint16Field(42);
    TemplatedChoice_uint32& uint32Choice = choiceTemplatedSelector.getUint32Choice();
    uint32Choice.setStringField("string");

    zserio::BitStreamWriter writer;
    choiceTemplatedSelector.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    ChoiceTemplatedSelector readChoiceTemplatedSelector(reader);

    ASSERT_TRUE(choiceTemplatedSelector == readChoiceTemplatedSelector);
}

} // namespace choice_templated_selector
} // namespace templates
