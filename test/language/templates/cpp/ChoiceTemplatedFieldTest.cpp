#include "gtest/gtest.h"

#include "templates/choice_templated_field/ChoiceTemplatedField.h"

namespace templates
{
namespace choice_templated_field
{

TEST(ChoiceTemplatedFieldTest, readWrite)
{
    ChoiceTemplatedField choiceTemplatedField;
    choiceTemplatedField.setSelector(0);
    choiceTemplatedField.setChoice1(TemplatedChoice_uint32_uint16{static_cast<uint32_t>(42)});
    choiceTemplatedField.setChoice2(TemplatedChoice_Compound_uint32_uint16{Compound_uint32{42}});

    zserio::BitStreamWriter writer;
    choiceTemplatedField.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    ChoiceTemplatedField readChoiceTemplatedField(reader);

    ASSERT_TRUE(choiceTemplatedField == readChoiceTemplatedField);
}

} // namespace choice_templated_field
} // namespace templates
