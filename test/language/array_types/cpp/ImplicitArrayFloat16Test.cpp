#include "gtest/gtest.h"

#include "array_types/implicit_array_float16/ImplicitArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace implicit_array_float16
{

class ImplicitArrayFloat16Test : public ::testing::Test
{
protected:
    void writeImplicitArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        for (size_t i = 0; i < numElements; ++i)
            writer.writeFloat16(static_cast<float>(i));
    }
};

TEST_F(ImplicitArrayFloat16Test, bitSizeOf)
{
    const size_t numElements = 55;
    std::vector<float> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<float>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(numElements * 16, implicitArray.bitSizeOf(bitPosition));
}

TEST_F(ImplicitArrayFloat16Test, initializeOffsets)
{
    const size_t numElements = 55;
    std::vector<float> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<float>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + numElements * 16, implicitArray.initializeOffsets(bitPosition));
}

TEST_F(ImplicitArrayFloat16Test, read)
{
    const size_t numElements = 99;
    zserio::BitStreamWriter writer;
    writeImplicitArrayToByteArray(writer, numElements);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ImplicitArray implicitArray(reader);

    const std::vector<float>& array = implicitArray.getArray();
    ASSERT_EQ(numElements, array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<float>(i), array[i]);
}

TEST_F(ImplicitArrayFloat16Test, write)
{
    const size_t numElements = 55;
    std::vector<float> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<float>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::BitStreamWriter writer;
    implicitArray.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ImplicitArray readImplicitArray(reader);
    const std::vector<float>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<float>(i), readArray[i]);
}

} // namespace implicit_array_float16
} // namespace array_types
