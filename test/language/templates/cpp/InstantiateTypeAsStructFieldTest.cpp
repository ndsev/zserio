#include "gtest/gtest.h"
#include "templates/instantiate_type_as_struct_field/InstantiateTypeAsStructField.h"

namespace templates
{
namespace instantiate_type_as_struct_field
{

TEST(InstantiateTypeAsStructFieldTest, readWrite)
{
    InstantiateTypeAsStructField instantiateTypeAsStructField;
    instantiateTypeAsStructField.setTest(Test32{13});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeAsStructField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeAsStructField readInstantiateTypeAsStructField(reader);

    ASSERT_TRUE(instantiateTypeAsStructField == readInstantiateTypeAsStructField);
}

} // namespace instantiate_type_as_struct_field
} // namespace templates
