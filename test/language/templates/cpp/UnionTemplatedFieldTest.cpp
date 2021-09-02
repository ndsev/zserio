#include "gtest/gtest.h"

#include "templates/union_templated_field/UnionTemplatedField.h"

namespace templates
{
namespace union_templated_field
{

TEST(UnionTemplatedFieldTest, readWrite)
{
    UnionTemplatedField unionTemplatedField;
    {
        TemplatedUnion_uint16_uint32 uintUnion;
        uintUnion.setField2(42);
        unionTemplatedField.setUintUnion(uintUnion);
    }
    {
        TemplatedUnion_float32_float64 floatUnion;
        floatUnion.setField2(42.0);
        unionTemplatedField.setFloatUnion(floatUnion);
    }
    {
        TemplatedUnion_Compound_uint16_Compound_uint32 compoundUnion;
        compoundUnion.setField3(Compound_Compound_uint16{Compound_uint16{13}});
        unionTemplatedField.setCompoundUnion(std::move(compoundUnion));
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    unionTemplatedField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UnionTemplatedField readUnionTemplatedField(reader);

    ASSERT_TRUE(unionTemplatedField == readUnionTemplatedField);
}

} // namespace union_templated_field
} // namespace templates
