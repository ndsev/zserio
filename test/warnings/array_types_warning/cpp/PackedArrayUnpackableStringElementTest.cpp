#include "gtest/gtest.h"

#include "array_types_warning/packed_array_unpackable_string_element/PackedArrayUnpackableStringElement.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_unpackable_string_element
{

class PackedArrayUnpackableStringElementTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayUnpackableStringElementTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_unpackable_string_element.blob";

TEST_F(PackedArrayUnpackableStringElementTest, writeRead)
{
    PackedArrayUnpackableStringElement packedArrayUnpackableStringElement(
        {{10, 11, 12}},
        {{"A", "B", "C"}}
    );

    zserio::serializeToFile(packedArrayUnpackableStringElement, BLOB_NAME);
    auto readPackedArrayUnpackableStringElement =
            zserio::deserializeFromFile<PackedArrayUnpackableStringElement>(BLOB_NAME);
    ASSERT_EQ(packedArrayUnpackableStringElement, readPackedArrayUnpackableStringElement);
}

} // namespace packed_array_unpackable_string_element
} // namespace array_types_warning
