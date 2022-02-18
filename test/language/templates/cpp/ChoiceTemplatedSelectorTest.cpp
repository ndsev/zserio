#include "gtest/gtest.h"

#include "templates/choice_templated_selector/ChoiceTemplatedSelector.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace choice_templated_selector
{

using allocator_type = ChoiceTemplatedSelector::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;

TEST(ChoiceTemplatedSelectorTest, readWrite)
{
    ChoiceTemplatedSelector choiceTemplatedSelector;
    choiceTemplatedSelector.setSelector16(0);
    choiceTemplatedSelector.setSelector32(1);
    {
        TemplatedChoice_uint16_Shift16 uint16Choice;
        uint16Choice.setUint16Field(42);
        choiceTemplatedSelector.setUint16Choice(std::move(uint16Choice));
    }
    {
        TemplatedChoice_uint32_Shift32 uint32Choice;
        uint32Choice.setStringField("string");
        choiceTemplatedSelector.setUint32Choice(uint32Choice); // copy
    }
    choiceTemplatedSelector.initializeChildren();

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    choiceTemplatedSelector.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ChoiceTemplatedSelector readChoiceTemplatedSelector(reader);

    ASSERT_TRUE(choiceTemplatedSelector == readChoiceTemplatedSelector);
}

} // namespace choice_templated_selector
} // namespace templates
