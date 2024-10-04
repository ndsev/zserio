#include "gtest/gtest.h"
#include "templates/struct_templated_field/StructTemplatedField.h"
#include "zserio/RebindAlloc.h"

namespace templates
{
namespace struct_templated_field
{

using allocator_type = StructTemplatedField::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(StructTemplatedFieldTest, readWrite)
{
    StructTemplatedField structTemplatedField;
    structTemplatedField.setUint32Field(Field_uint32{42});
    structTemplatedField.setCompoundField(Field_Compound{Compound{42}});
    structTemplatedField.setStringField(Field_string{string_type{"string"}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structTemplatedField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructTemplatedField readStructTemplatedField(reader);

    ASSERT_TRUE(structTemplatedField == readStructTemplatedField);
}

} // namespace struct_templated_field
} // namespace templates
