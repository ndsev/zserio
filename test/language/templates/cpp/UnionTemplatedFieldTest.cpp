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

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    unionTemplatedField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UnionTemplatedField readUnionTemplatedField(reader);

    ASSERT_TRUE(unionTemplatedField == readUnionTemplatedField);
}

} // namespace union_templated_field
} // namespace templates
