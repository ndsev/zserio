#include "gtest/gtest.h"

#include "templates/union_templated_field/UnionTemplatedField.h"

namespace templates
{
namespace union_templated_field
{

TEST(UnionTemplatedFieldTest, readWrite)
{
    UnionTemplatedField unionTemplatedField;
    TemplatedUnion_uint16_uint32& uintUnion = unionTemplatedField.getUintUnion();
    uintUnion.setField1(42);

    TemplatedUnion_float32_float64& floatUnion = unionTemplatedField.getFloatUnion();
    floatUnion.setField2(42.0);

    TemplatedUnion_Compound_uint16_Compound_uint32& compoundUnion = unionTemplatedField.getCompoundUnion();
    Compound_Compound_uint16 compoundCompoundUint16;
    compoundCompoundUint16.getValue().setValue(13);
    compoundUnion.setField3(compoundCompoundUint16);

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
