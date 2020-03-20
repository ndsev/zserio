#include "gtest/gtest.h"

#include "array_types/implicit_array_bit8/ImplicitArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace implicit_array_bit8
{

class ImplicitArrayBit8Test : public ::testing::Test
{
protected:
    void writeImplicitArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        for (size_t i = 0; i < numElements; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }
};

TEST_F(ImplicitArrayBit8Test, bitSizeOf)
{
    const size_t numElements = 55;
    std::vector<uint8_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(numElements * 8, implicitArray.bitSizeOf(bitPosition));
}

TEST_F(ImplicitArrayBit8Test, initializeOffsets)
{
    const size_t numElements = 55;
    std::vector<uint8_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + numElements * 8, implicitArray.initializeOffsets(bitPosition));
}

TEST_F(ImplicitArrayBit8Test, read)
{
    const size_t numElements = 99;
    zserio::BitStreamWriter writer;
    writeImplicitArrayToByteArray(writer, numElements);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ImplicitArray implicitArray(reader);

    const std::vector<uint8_t>& array = implicitArray.getArray();
    ASSERT_EQ(numElements, array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint8_t>(i), array[i]);
}

TEST_F(ImplicitArrayBit8Test, write)
{
    const size_t numElements = 55;
    std::vector<uint8_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::BitStreamWriter writer;
    implicitArray.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ImplicitArray readImplicitArray(reader);
    const std::vector<uint8_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint8_t>(i), readArray[i]);
}

} // namespace implicit_array_bit8
} // namespace array_types
