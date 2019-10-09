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
    TemplatedChoice_uint32_uint16& choice1 = choiceTemplatedField.getChoice1();
    choice1.setTemplatedField1(42);
    TemplatedChoice_Compound_uint32_uint16& choice2 = choiceTemplatedField.getChoice2();
    Compound_uint32 compoundUint32;
    compoundUint32.setValue(42);
    choice2.setTemplatedField1(compoundUint32);

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
