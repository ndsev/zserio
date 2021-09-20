#include <limits>

#include "gtest/gtest.h"

#include "builtin_types/bitfield_uint64_length/Container.h"

#include "zserio/SerializeUtil.h"

namespace builtin_types
{
namespace bitfield_uint64_length
{

TEST(BitFieldUIn64LengthTest, bitSizeOf)
{
    Container container;
    const uint64_t bitFieldLength = 33;
    container.setLength(bitFieldLength);
    container.setUnsignedBitField(std::numeric_limits<uint32_t>::max() + static_cast<uint64_t>(1));
    container.setSignedBitField(std::numeric_limits<int32_t>::max() + static_cast<int64_t>(1));
    const size_t expectedBitSizeOfContainer = 64 + 33 + 33;
    ASSERT_EQ(expectedBitSizeOfContainer, container.bitSizeOf());
}

TEST(BitFieldUIn64LengthTest, readWrite)
{
    Container container;
    const uint64_t bitFieldLength = 33;
    container.setLength(bitFieldLength);
    container.setUnsignedBitField(std::numeric_limits<uint32_t>::max() + static_cast<uint64_t>(1));
    container.setSignedBitField(std::numeric_limits<int32_t>::max() + static_cast<int64_t>(1));

    const std::string fileName = "language/builtin_types/bit_field_uint64_length.blob";
    zserio::serializeToFile(container, fileName);

    const Container readContainer = zserio::deserializeFromFile<Container>(fileName);
    ASSERT_TRUE(container == readContainer);
}

} // namespace bitfield_uint64_length
} // namespace builtin_types
