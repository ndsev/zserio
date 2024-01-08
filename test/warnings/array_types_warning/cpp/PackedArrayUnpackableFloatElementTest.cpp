#include "array_types_warning/packed_array_unpackable_float_element/PackedArrayUnpackableFloatElement.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_unpackable_float_element
{

class PackedArrayUnpackableFloatElementTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayUnpackableFloatElementTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_unpackable_float_element.blob";

TEST_F(PackedArrayUnpackableFloatElementTest, writeRead)
{
    PackedArrayUnpackableFloatElement packedArrayUnpackableFloatElement({{10, 11, 12}}, {{4.0, 1.0, 0.0}});

    zserio::serializeToFile(packedArrayUnpackableFloatElement, BLOB_NAME);
    auto readPackedArrayUnpackableFloatElement =
            zserio::deserializeFromFile<PackedArrayUnpackableFloatElement>(BLOB_NAME);
    ASSERT_EQ(packedArrayUnpackableFloatElement, readPackedArrayUnpackableFloatElement);
}

} // namespace packed_array_unpackable_float_element
} // namespace array_types_warning
