#include "gtest/gtest.h"

#include "array_types/fixed_array/FixedArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace fixed_array
{

class FixedArrayTest : public ::testing::Test
{
protected:
    void writeFixedArrayToByteArray(zserio::BitStreamWriter& writer)
    {
        for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }

    static const size_t FIXED_ARRAY_LENGTH = 5;
};

TEST_F(FixedArrayTest, bitSizeOf)
{
    zserio::UInt8Array uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    const size_t bitPosition = 2;
    ASSERT_EQ(FIXED_ARRAY_LENGTH * 8, fixedArray.bitSizeOf(bitPosition));
}

TEST_F(FixedArrayTest, initializeOffsets)
{
    zserio::UInt8Array uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + FIXED_ARRAY_LENGTH * 8, fixedArray.initializeOffsets(bitPosition));
}

TEST_F(FixedArrayTest, read)
{
    zserio::BitStreamWriter writer;
    writeFixedArrayToByteArray(writer);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    FixedArray fixedArray(reader);

    const zserio::UInt8Array& uint8Array = fixedArray.getUint8Array();
    const size_t numElements = FIXED_ARRAY_LENGTH;
    ASSERT_EQ(numElements, uint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, uint8Array[i]);
}

TEST_F(FixedArrayTest, write)
{
    zserio::UInt8Array uint8Array;
    uint8Array.reserve(FIXED_ARRAY_LENGTH);
    for (size_t i = 0; i < FIXED_ARRAY_LENGTH; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    zserio::BitStreamWriter writer;
    fixedArray.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    FixedArray readFixedArray(reader);
    const zserio::UInt8Array& readUint8Array = readFixedArray.getUint8Array();
    const size_t numElements = FIXED_ARRAY_LENGTH;
    ASSERT_EQ(numElements, readUint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, readUint8Array[i]);
}

TEST_F(FixedArrayTest, writeWrongArray)
{
    zserio::UInt8Array uint8Array;
    const size_t wrongArrayLength = FIXED_ARRAY_LENGTH + 1;
    uint8Array.reserve(wrongArrayLength);
    for (size_t i = 0; i < wrongArrayLength; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    FixedArray fixedArray;
    fixedArray.setUint8Array(uint8Array);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(fixedArray.write(writer), zserio::CppRuntimeException);
}

} // namespace fixed_array
} // namespace array_types
