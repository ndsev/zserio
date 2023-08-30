#include "gtest/gtest.h"

#include "array_types/packed_auto_array_union_has_no_packable_field/PackedAutoArrayUnionHasNoPackableField.h"

#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_auto_array_union_has_no_packable_field
{

class PackedAutoArrayUnionHasNoPackableFieldTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedAutoArrayUnionHasNoPackableFieldTest::BLOB_NAME =
        "language/array_types/packed_auto_array_union_has_no_packable_field.blob";

TEST_F(PackedAutoArrayUnionHasNoPackableFieldTest, writeRead)
{
    PackedAutoArrayUnionHasNoPackableField packedAutoArrayUnionHasNoPackableField;

    packedAutoArrayUnionHasNoPackableField.setArray1({{
        StructWithPackable("A", 65),
        StructWithPackable("B", 66),
        StructWithPackable("C", 67)
    }});

    packedAutoArrayUnionHasNoPackableField.setArray2({{
        StructWithPackableArray("ABC", {{ 65, 66, 67 }}),
        StructWithPackableArray("DEF", {{ 68, 69, 70 }}),
        StructWithPackableArray("GHI", {{ 71, 72, 73 }})
    }});

    auto& array3 = packedAutoArrayUnionHasNoPackableField.getArray3();
    array3.resize(3);
    array3[0].setField1(4.0F);
    array3[1].setField1(1.0F);
    array3[2].setField1(0.0F);

    zserio::serializeToFile(packedAutoArrayUnionHasNoPackableField, BLOB_NAME);
    auto readPackedAutoArrayUnionHasNoPackableField =
            zserio::deserializeFromFile<PackedAutoArrayUnionHasNoPackableField>(BLOB_NAME);
    ASSERT_EQ(packedAutoArrayUnionHasNoPackableField, readPackedAutoArrayUnionHasNoPackableField);
}

} // namespace packed_auto_array_union_has_no_packable_field
} // namespace array_types
