#include "gtest/gtest.h"

#include "templates/union_templated_field/UnionTemplatedField.h"

namespace templates
{
namespace union_templated_field
{

TEST(UnionTemplatedFieldTest, readWrite)
{
    UnionTemplatedField unionTemplatedField;
    unionTemplatedField.setUintUnion(TemplatedUnion_uint16_uint32{static_cast<uint32_t>(42)});
    unionTemplatedField.setFloatUnion(TemplatedUnion_float32_float64{42.0});
    unionTemplatedField.setCompoundUnion(TemplatedUnion_Compound_uint16_Compound_uint32{
            Compound_Compound_uint16{Compound_uint16{13}}});

    zserio::BitStreamWriter writer;
    unionTemplatedField.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    UnionTemplatedField readUnionTemplatedField(reader);

    ASSERT_TRUE(unionTemplatedField == readUnionTemplatedField);
}

} // namespace union_templated_field
} // namespace templates
