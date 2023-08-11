#include "gtest/gtest.h"

#include "array_types_warning/packed_array_unpackable_bytes_element/PackedArrayUnpackableBytesElement.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_unpackable_bytes_element
{

class PackedArrayUnpackableBytesElementTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayUnpackableBytesElementTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_unpackable_bytes_element.blob";

TEST_F(PackedArrayUnpackableBytesElementTest, writeRead)
{
    PackedArrayUnpackableBytesElement packedArrayUnpackableBytesElement(
        {{10, 11, 12}},
        {{ {{0, 1, 2}}, {{11, 12, 13}}, {{100, 101, 102}} }}
    );

    zserio::serializeToFile(packedArrayUnpackableBytesElement, BLOB_NAME);
    auto readPackedArrayUnpackableBytesElement =
            zserio::deserializeFromFile<PackedArrayUnpackableBytesElement>(BLOB_NAME);
    ASSERT_EQ(packedArrayUnpackableBytesElement, readPackedArrayUnpackableBytesElement);
}

} // namespace packed_array_unpackable_bytes_element
} // namespace array_types_warning
