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
    choiceTemplatedSelector.setUint16Choice(TemplatedChoice_uint16{static_cast<uint16_t>(42)});
    choiceTemplatedSelector.setUint32Choice(TemplatedChoice_uint32{std::string{"string"}});

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
