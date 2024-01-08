#include "array_types/fixed_array_uint8/FixedArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace fixed_array_uint8
{

using allocator_type = FixedArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class FixedArrayUInt8Test : public ::testing::Test
{
protected:
    void writeFixedArrayToByteArray(zserio::BitStreamWriter& writer)
    {
        for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }

    static const std::string BLOB_NAME;
    static const size_t FIXED_ARRAY_LENGTH = 5;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string FixedArrayUInt8Test::BLOB_NAME = "language/array_types/fixed_array_uint8.blob";

TEST_F(FixedArrayUInt8Test, bitSizeOf)
{
    vector_type<uint8_t> uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    const size_t bitPosition = 2;
    ASSERT_EQ(FIXED_ARRAY_LENGTH * 8, fixedArray.bitSizeOf(bitPosition));
}

TEST_F(FixedArrayUInt8Test, initializeOffsets)
{
    vector_type<uint8_t> uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + FIXED_ARRAY_LENGTH * 8, fixedArray.initializeOffsets(bitPosition));
}

TEST_F(FixedArrayUInt8Test, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeFixedArrayToByteArray(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    FixedArray fixedArray(reader);

    const vector_type<uint8_t>& uint8Array = fixedArray.getUint8Array();
    const size_t numElements = FIXED_ARRAY_LENGTH;
    ASSERT_EQ(numElements, uint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, uint8Array[i]);
}

TEST_F(FixedArrayUInt8Test, writeRead)
{
    vector_type<uint8_t> uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    zserio::BitStreamWriter writer(bitBuffer);
    fixedArray.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    FixedArray readFixedArray(reader);
    const vector_type<uint8_t>& readUint8Array = readFixedArray.getUint8Array();
    const size_t numElements = FIXED_ARRAY_LENGTH;
    ASSERT_EQ(numElements, readUint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, readUint8Array[i]);
}

TEST_F(FixedArrayUInt8Test, writeReadFile)
{
    vector_type<uint8_t> uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    zserio::serializeToFile(fixedArray, BLOB_NAME);

    FixedArray readFixedArray = zserio::deserializeFromFile<FixedArray>(BLOB_NAME);
    const vector_type<uint8_t>& readUint8Array = readFixedArray.getUint8Array();
    const size_t numElements = FIXED_ARRAY_LENGTH;
    ASSERT_EQ(numElements, readUint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, readUint8Array[i]);
}

TEST_F(FixedArrayUInt8Test, writeWrongArray)
{
    vector_type<uint8_t> uint8Array;
    const size_t wrongArrayLength = FIXED_ARRAY_LENGTH + 1;
    uint8Array.reserve(wrongArrayLength);
    for (size_t i = 0; i < wrongArrayLength; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(fixedArray.write(writer), zserio::CppRuntimeException);
}

} // namespace fixed_array_uint8
} // namespace array_types
