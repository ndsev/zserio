#include "array_types/packed_fixed_array_uint8/PackedFixedArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_fixed_array_uint8
{

using allocator_type = PackedFixedArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackedFixedArrayUInt8Test : public ::testing::Test
{
protected:
    void fillPackedFixedArray(PackedFixedArray& packedFixedArray)
    {
        auto& uint8Array = packedFixedArray.getUint8Array();
        uint8Array.reserve(FIXED_ARRAY_LENGTH);
        for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array.push_back(PACKED_ARRAY_ELEMENT);
    }

    size_t getPackedFixedArrayBitSize()
    {
        size_t bitSize = 1; // packing descriptor: isPacked
        bitSize += 6; // packing descriptor: maxBitNumber
        bitSize += 8; // firstElement

        return bitSize;
    }

    void checkPackedFixedArray(const PackedFixedArray& packedFixedArray)
    {
        const auto& uint8Array = packedFixedArray.getUint8Array();
        for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        {
            ASSERT_EQ(PACKED_ARRAY_ELEMENT, uint8Array.at(i));
        }
    }

    void writePackedFixedArrayToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(PACKED_ARRAY_ELEMENT, 8);
    }

    static const std::string BLOB_NAME;

    static const size_t FIXED_ARRAY_LENGTH;
    static const uint8_t PACKED_ARRAY_MAX_BIT_NUMBER;
    static const uint8_t PACKED_ARRAY_ELEMENT;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string PackedFixedArrayUInt8Test::BLOB_NAME = "language/array_types/packed_fixed_array_uint8.blob";

const size_t PackedFixedArrayUInt8Test::FIXED_ARRAY_LENGTH = 5;
const uint8_t PackedFixedArrayUInt8Test::PACKED_ARRAY_MAX_BIT_NUMBER = 0;
const uint8_t PackedFixedArrayUInt8Test::PACKED_ARRAY_ELEMENT = 100;

TEST_F(PackedFixedArrayUInt8Test, bitSizeOf)
{
    PackedFixedArray packedFixedArray;
    fillPackedFixedArray(packedFixedArray);

    const size_t bitPosition = 2;
    const size_t fixedArrayBitSize = getPackedFixedArrayBitSize();
    ASSERT_EQ(fixedArrayBitSize, packedFixedArray.bitSizeOf(bitPosition));
}

TEST_F(PackedFixedArrayUInt8Test, initializeOffsets)
{
    PackedFixedArray packedFixedArray;
    fillPackedFixedArray(packedFixedArray);

    const size_t bitPosition = 2;
    const size_t expectedEndBitPosition = bitPosition + getPackedFixedArrayBitSize();
    ASSERT_EQ(expectedEndBitPosition, packedFixedArray.initializeOffsets(bitPosition));
}

TEST_F(PackedFixedArrayUInt8Test, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writePackedFixedArrayToByteArray(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    PackedFixedArray packedFixedArray(reader);
    checkPackedFixedArray(packedFixedArray);
}

TEST_F(PackedFixedArrayUInt8Test, writeRead)
{
    PackedFixedArray packedFixedArray;
    fillPackedFixedArray(packedFixedArray);

    zserio::BitStreamWriter writer(bitBuffer);
    packedFixedArray.write(writer);

    ASSERT_EQ(packedFixedArray.bitSizeOf(), writer.getBitPosition());
    ASSERT_EQ(packedFixedArray.initializeOffsets(), writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    PackedFixedArray readPackedFixedArray(reader);
    checkPackedFixedArray(readPackedFixedArray);
}

TEST_F(PackedFixedArrayUInt8Test, writeReadFile)
{
    PackedFixedArray packedFixedArray;
    fillPackedFixedArray(packedFixedArray);

    zserio::serializeToFile(packedFixedArray, BLOB_NAME);

    PackedFixedArray readPackedFixedArray = zserio::deserializeFromFile<PackedFixedArray>(BLOB_NAME);
    checkPackedFixedArray(readPackedFixedArray);
}

TEST_F(PackedFixedArrayUInt8Test, writeWrongArray)
{
    vector_type<uint8_t> uint8Array;
    const size_t wrongArrayLength = FIXED_ARRAY_LENGTH + 1;
    uint8Array.reserve(wrongArrayLength);
    for (size_t i = 0; i < wrongArrayLength; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    PackedFixedArray packedFixedArray;
    packedFixedArray.setUint8Array(uint8Array);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(packedFixedArray.write(writer), zserio::CppRuntimeException);
}

} // namespace packed_fixed_array_uint8
} // namespace array_types
