#include "gtest/gtest.h"

#include "allow_implicit_arrays/implicit_array_bit8/ImplicitArray.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace allow_implicit_arrays
{
namespace implicit_array_bit8
{

using allocator_type = ImplicitArray::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class ImplicitArrayBit8Test : public ::testing::Test
{
protected:
    void writeImplicitArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        for (size_t i = 0; i < numElements; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }

    static const std::string BLOB_NAME;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string ImplicitArrayBit8Test::BLOB_NAME = "arguments/allow_implicit_arrays/implicit_array_bit8.blob";

TEST_F(ImplicitArrayBit8Test, bitSizeOf)
{
    const size_t numElements = 55;
    vector_type<uint8_t> array;
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
    vector_type<uint8_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + numElements * 8, implicitArray.initializeOffsets(bitPosition));
}

TEST_F(ImplicitArrayBit8Test, readConstructor)
{
    const size_t numElements = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeImplicitArrayToByteArray(writer, numElements);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ImplicitArray implicitArray(reader);

    const vector_type<uint8_t>& array = implicitArray.getArray();
    ASSERT_EQ(numElements, array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint8_t>(i), array[i]);
}

TEST_F(ImplicitArrayBit8Test, writeRead)
{
    const size_t numElements = 55;
    vector_type<uint8_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::BitStreamWriter writer(bitBuffer);
    implicitArray.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ImplicitArray readImplicitArray(reader);
    const vector_type<uint8_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint8_t>(i), readArray[i]);
}

TEST_F(ImplicitArrayBit8Test, writeReadFile)
{
    const size_t numElements = 55;
    vector_type<uint8_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint8_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::serializeToFile(implicitArray, BLOB_NAME);

    ImplicitArray readImplicitArray = zserio::deserializeFromFile<ImplicitArray>(BLOB_NAME);
    const vector_type<uint8_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint8_t>(i), readArray[i]);
}

} // namespace implicit_array_bit8
} // namespace allow_implicit_arrays
