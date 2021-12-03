#include "gtest/gtest.h"

#include "templates/instantiate_type_as_struct_array_field/InstantiateTypeAsStructArrayField.h"

namespace templates
{
namespace instantiate_type_as_struct_array_field
{

TEST(InstantiateTypeAsStructArrayFieldTest, readWrite)
{
    InstantiateTypeAsStructArrayField instantiateTypeAsStructArrayField;
    instantiateTypeAsStructArrayField.setTest({Test32{13}, Test32{17}, Test32{23}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeAsStructArrayField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeAsStructArrayField readInstantiateTypeAsStructArrayField(reader);

    ASSERT_TRUE(instantiateTypeAsStructArrayField == readInstantiateTypeAsStructArrayField);
}

} // namespace instantiate_type_as_struct_array_field
} // namespace templates
