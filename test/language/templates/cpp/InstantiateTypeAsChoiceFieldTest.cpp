#include "gtest/gtest.h"

#include "templates/instantiate_type_as_choice_field/InstantiateTypeAsChoiceField.h"

namespace templates
{
namespace instantiate_type_as_choice_field
{

TEST(InstantiateTypeAsChoiceFieldTest, readWrite)
{
    InstantiateTypeAsChoiceField instantiateTypeAsChoiceField;
    instantiateTypeAsChoiceField.initialize(true);
    instantiateTypeAsChoiceField.setTest(Test32{13});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeAsChoiceField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeAsChoiceField readInstantiateTypeAsChoiceField(reader, true);

    ASSERT_TRUE(instantiateTypeAsChoiceField == readInstantiateTypeAsChoiceField);
}

} // namespace instantiate_type_as_choice_field
} // namespace templates
