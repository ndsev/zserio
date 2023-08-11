#include "gtest/gtest.h"

#include "array_types_warning/packed_array_choice_has_no_packable_field/PackedArrayChoiceHasNoPackableField.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_choice_has_no_packable_field
{

class PackedArrayChoiceHasNoPackableFieldTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayChoiceHasNoPackableFieldTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_choice_has_no_packable_field.blob";

TEST_F(PackedArrayChoiceHasNoPackableFieldTest, writeRead)
{
    PackedArrayChoiceHasNoPackableField packedArrayChoiceHasNoPackableField;

    packedArrayChoiceHasNoPackableField.setArray1({{
        StructWithPackable("A", 65),
        StructWithPackable("B", 66),
        StructWithPackable("C", 67)
    }});

    auto& array2 = packedArrayChoiceHasNoPackableField.getArray2();
    array2.resize(3);
    array2[0].setField2(TestEnum::ONE);
    array2[1].setField2(TestEnum::TWO);
    array2[2].setField2(TestEnum::ONE);

    packedArrayChoiceHasNoPackableField.setArray3({{
        StructWithPackableArray("ABC", {{ 65, 66, 67 }}),
        StructWithPackableArray("DEF", {{ 68, 69, 70 }}),
        StructWithPackableArray("GHI", {{ 71, 72, 73 }})
    }});

    auto& array4 = packedArrayChoiceHasNoPackableField.getArray4();
    array4.resize(3);
    array4[0].initialize(true);
    array4[0].setField1(4.0f);
    array4[1].initialize(true);
    array4[1].setField1(1.0f);
    array4[2].initialize(true);
    array4[2].setField1(0.0f);

    zserio::serializeToFile(packedArrayChoiceHasNoPackableField, BLOB_NAME);
    auto readPackedArrayChoiceHasNoPackableField =
            zserio::deserializeFromFile<PackedArrayChoiceHasNoPackableField>(BLOB_NAME);
    ASSERT_EQ(packedArrayChoiceHasNoPackableField, readPackedArrayChoiceHasNoPackableField);
}

} // namespace packed_array_choice_has_no_packable_field
} // namespace array_types_warning
