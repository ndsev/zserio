#include "gtest/gtest.h"

#include "array_types_warning/packed_array_unpackable_extern_element/PackedArrayUnpackableExternElement.h"

#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_unpackable_extern_element
{

using allocator_type = PackedArrayUnpackableExternElement::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<allocator_type>;

class PackedArrayUnpackableExternElementTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedArrayUnpackableExternElementTest::BLOB_NAME =
        "warnings/array_types_warning/packed_array_unpackable_extern_element.blob";

TEST_F(PackedArrayUnpackableExternElementTest, writeRead)
{
    PackedArrayUnpackableExternElement packedArrayUnpackableExternElement(
        {{10, 11, 12}},
        {{
            BitBuffer(vector_type<uint8_t>{{0xFF, 0xC0}}, 10),
            BitBuffer(vector_type<uint8_t>{{0xFF, 0x80}}, 10),
            BitBuffer(vector_type<uint8_t>{{0xFF, 0x40}}, 10)
        }}
    );

    zserio::serializeToFile(packedArrayUnpackableExternElement, BLOB_NAME);
    auto readPackedArrayUnpackableExternElement =
            zserio::deserializeFromFile<PackedArrayUnpackableExternElement>(BLOB_NAME);
    ASSERT_EQ(packedArrayUnpackableExternElement, readPackedArrayUnpackableExternElement);
}

} // namespace packed_array_unpackable_extern_element
} // namespace array_types_warning
