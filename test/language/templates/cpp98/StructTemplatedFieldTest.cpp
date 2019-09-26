#include "gtest/gtest.h"

#include "templates/struct_templated_field/StructTemplatedField.h"

namespace templates
{
namespace struct_templated_field
{

TEST(StructTemplatedFieldTest, readWrite)
{
    StructTemplatedField structTemplatedField;
    structTemplatedField.getUint32Field().setValue(42);
    structTemplatedField.getCompoundField().getValue().setValue(42);
    structTemplatedField.getStringField().setValue("string");

    zserio::BitStreamWriter writer;
    structTemplatedField.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructTemplatedField readStructTemplatedField(reader);

    ASSERT_TRUE(structTemplatedField == readStructTemplatedField);
}

} // namespace struct_templated_field
} // namespace templates
