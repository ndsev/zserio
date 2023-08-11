#include "gtest/gtest.h"

#include "array_types_warning/packed_array_struct_has_no_packable_field/PackedArrayStructHasNoPackableField.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_struct_has_no_packable_field
{

using allocator_type = PackedArrayStructHasNoPackableField::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<allocator_type>;

class PackedArrayStructHasNoPackableFieldTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayStructHasNoPackableFieldTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_struct_has_no_packable_field.blob";

TEST_F(PackedArrayStructHasNoPackableFieldTest, writeRead)
{
    PackedArrayStructHasNoPackableField packedArrayStructHasNoPackableField;

    packedArrayStructHasNoPackableField.setArray1({{
        StructWithPackable("A", 65),
        StructWithPackable("B", 66),
        StructWithPackable("C", 67)
    }});

    auto& array2 = packedArrayStructHasNoPackableField.getArray2();
    array2.resize(3);
    array2[0].setField2(TestEnum::ONE);
    array2[1].setField2(TestEnum::TWO);
    array2[2].setField2(TestEnum::ONE);

    packedArrayStructHasNoPackableField.setArray3({{
        StructWithPackableArray("ABC", {{ 65, 66, 67 }}),
        StructWithPackableArray("DEF", {{ 68, 69, 70 }}),
        StructWithPackableArray("GHI", {{ 71, 72, 73 }})
    }});

    packedArrayStructHasNoPackableField.setArray4({{
        StructWithoutPackable(4.0f, BitBuffer(vector_type<uint8_t>({0xF0}), 5), 0, "A",
                {{0, 0, 0}}, {{true, false, true}}),
        StructWithoutPackable(1.0f, BitBuffer(vector_type<uint8_t>({0xE0}), 5), 0, "B",
                {{0, 0, 0}}, {{true, false, true}}),
        StructWithoutPackable(0.0f, BitBuffer(vector_type<uint8_t>({0xD0}), 5), 0, "C",
                {{0, 0, 0}}, {{true, false, true}})
    }});

    packedArrayStructHasNoPackableField.getArray5().resize(3);

    zserio::serializeToFile(packedArrayStructHasNoPackableField, BLOB_NAME);
    auto readPackedArrayStructHasNoPackableField =
            zserio::deserializeFromFile<PackedArrayStructHasNoPackableField>(BLOB_NAME);
    ASSERT_EQ(packedArrayStructHasNoPackableField, readPackedArrayStructHasNoPackableField);
}

} // namespace packed_array_struct_has_no_packable_field
} // namespace array_types_warning
