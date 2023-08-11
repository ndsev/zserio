#include "gtest/gtest.h"

#include "array_types_warning/packed_array_union_has_no_packable_field/PackedArrayUnionHasNoPackableField.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_union_has_no_packable_field
{

class PackedArrayUnionHasNoPackableFieldTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayUnionHasNoPackableFieldTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_union_has_no_packable_field.blob";

TEST_F(PackedArrayUnionHasNoPackableFieldTest, writeRead)
{
    PackedArrayUnionHasNoPackableField packedArrayUnionHasNoPackableField;

    packedArrayUnionHasNoPackableField.setArray1({{
        StructWithPackable("A", 65),
        StructWithPackable("B", 66),
        StructWithPackable("C", 67)
    }});

    packedArrayUnionHasNoPackableField.setArray2({{
        StructWithPackableArray("ABC", {{ 65, 66, 67 }}),
        StructWithPackableArray("DEF", {{ 68, 69, 70 }}),
        StructWithPackableArray("GHI", {{ 71, 72, 73 }})
    }});

    auto& array3 = packedArrayUnionHasNoPackableField.getArray3();
    array3.resize(3);
    array3[0].setField1(4.0f);
    array3[1].setField1(1.0f);
    array3[2].setField1(0.0f);

    zserio::serializeToFile(packedArrayUnionHasNoPackableField, BLOB_NAME);
    auto readPackedArrayUnionHasNoPackableField =
            zserio::deserializeFromFile<PackedArrayUnionHasNoPackableField>(BLOB_NAME);
    ASSERT_EQ(packedArrayUnionHasNoPackableField, readPackedArrayUnionHasNoPackableField);
}

} // namespace packed_array_union_has_no_packable_field
} // namespace array_types_warning
