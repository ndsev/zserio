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
    choiceTemplatedSelector.setUint16Choice(TemplatedChoice_uint16_Shift16{static_cast<uint16_t>(42)});
    choiceTemplatedSelector.setUint32Choice(TemplatedChoice_uint32_Shift32{string_type{"string"}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    choiceTemplatedSelector.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ChoiceTemplatedSelector readChoiceTemplatedSelector(reader);

    ASSERT_TRUE(choiceTemplatedSelector == readChoiceTemplatedSelector);
}

} // namespace choice_templated_selector
} // namespace templates
