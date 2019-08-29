#include "gtest/gtest.h"

#include "array_types/implicit_array/ImplicitArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace implicit_array
{

class ImplicitArrayTest : public ::testing::Test
{
protected:
    void writeImplicitArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        for (size_t i = 0; i < numElements; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }
};

TEST_F(ImplicitArrayTest, bitSizeOf)
{
    const size_t numElements = 55;
    std::vector<uint8_t> uint8Array;
    uint8Array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setUint8Array(uint8Array);

    const size_t bitPosition = 2;
    ASSERT_EQ(numElements * 8, implicitArray.bitSizeOf(bitPosition));
}

TEST_F(ImplicitArrayTest, initializeOffsets)
{
    const size_t numElements = 55;
    std::vector<uint8_t> uint8Array;
    uint8Array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setUint8Array(uint8Array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + numElements * 8, implicitArray.initializeOffsets(bitPosition));
}

TEST_F(ImplicitArrayTest, read)
{
    const size_t numElements = 99;
    zserio::BitStreamWriter writer;
    writeImplicitArrayToByteArray(writer, numElements);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ImplicitArray implicitArray(reader);

    const std::vector<uint8_t>& uint8Array = implicitArray.getUint8Array();
    ASSERT_EQ(numElements, uint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, uint8Array[i]);
}

TEST_F(ImplicitArrayTest, write)
{
    const size_t numElements = 55;
    std::vector<uint8_t> uint8Array;
    uint8Array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        uint8Array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setUint8Array(uint8Array);

    zserio::BitStreamWriter writer;
    implicitArray.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ImplicitArray readImplicitArray(reader);
    const std::vector<uint8_t>& readUint8Array = readImplicitArray.getUint8Array();
    ASSERT_EQ(numElements, readUint8Array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(i, readUint8Array[i]);
}

} // namespace implicit_array
} // namespace array_types
