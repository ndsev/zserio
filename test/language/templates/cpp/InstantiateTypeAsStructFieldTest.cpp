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

    zserio::BitStreamWriter writer;
    instantiateTypeAsStructField.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateTypeAsStructField readInstantiateTypeAsStructField(reader);

    ASSERT_TRUE(instantiateTypeAsStructField == readInstantiateTypeAsStructField);
}

} // namespace instantiate_type_as_struct_field
} // namespace templates
