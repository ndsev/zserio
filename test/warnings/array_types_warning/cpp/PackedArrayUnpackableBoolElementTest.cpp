#include "gtest/gtest.h"

#include "array_types_warning/packed_array_unpackable_bool_element/PackedArrayUnpackableBoolElement.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_unpackable_bool_element
{

class PackedArrayUnpackableBoolElementTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayUnpackableBoolElementTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_unpackable_bool_element.blob";

TEST_F(PackedArrayUnpackableBoolElementTest, writeRead)
{
    PackedArrayUnpackableBoolElement packedArrayUnpackableBoolElement(
        {{0, 1, 2}},
        {{TestEnum::ONE, TestEnum::TWO, TestEnum::ONE}},
        {{TestBitmask::Values::BLACK, TestBitmask::Values::BLACK, TestBitmask::Values::BLACK}},
        {{0, 1, 2}},
        5,
        {{0, -1, -2}},
        {{true, false, true}}
    );

    zserio::serializeToFile(packedArrayUnpackableBoolElement, BLOB_NAME);
    auto readPackedArrayUnpackableBoolElement =
            zserio::deserializeFromFile<PackedArrayUnpackableBoolElement>(BLOB_NAME);
    ASSERT_EQ(packedArrayUnpackableBoolElement, readPackedArrayUnpackableBoolElement);
}

} // namespace packed_array_unpackable_bool_element
} // namespace array_types_warning
