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
    {
        TemplatedChoice_uint32_uint16 choice1;
        choice1.setTemplatedField1(42);
        choiceTemplatedField.setChoice1(std::move(choice1));
    }
    {
        TemplatedChoice_Compound_uint32_uint16 choice2;
        choice2.setTemplatedField1(Compound_uint32{42});
        choiceTemplatedField.setChoice2(choice2); // copy
    }
    choiceTemplatedField.initializeChildren();

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    choiceTemplatedField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ChoiceTemplatedField readChoiceTemplatedField(reader);

    ASSERT_TRUE(choiceTemplatedField == readChoiceTemplatedField);
}

} // namespace choice_templated_field
} // namespace templates
