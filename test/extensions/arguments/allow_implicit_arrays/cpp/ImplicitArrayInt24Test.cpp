#include "allow_implicit_arrays/implicit_array_int24/ImplicitArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace allow_implicit_arrays
{
namespace implicit_array_int24
{

using allocator_type = ImplicitArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ImplicitArrayInt24Test : public ::testing::Test
{
protected:
    void writeImplicitArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        for (size_t i = 0; i < numElements; ++i)
        {
            writer.writeSignedBits(static_cast<int32_t>(i), 24);
        }
    }

    static const std::string BLOB_NAME;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string ImplicitArrayInt24Test::BLOB_NAME =
        "arguments/allow_implicit_arrays/implicit_array_int24.blob";

TEST_F(ImplicitArrayInt24Test, bitSizeOf)
{
    const size_t numElements = 55;
    vector_type<int32_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        array.push_back(static_cast<uint8_t>(i));
    }
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(numElements * 24, implicitArray.bitSizeOf(bitPosition));
}

TEST_F(ImplicitArrayInt24Test, initializeOffsets)
{
    const size_t numElements = 55;
    vector_type<int32_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        array.push_back(static_cast<uint8_t>(i));
    }
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + numElements * 24, implicitArray.initializeOffsets(bitPosition));
}

TEST_F(ImplicitArrayInt24Test, readConstructor)
{
    const size_t numElements = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeImplicitArrayToByteArray(writer, numElements);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ImplicitArray implicitArray(reader);

    const vector_type<int32_t>& array = implicitArray.getArray();
    ASSERT_EQ(numElements, array.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(static_cast<int32_t>(i), array[i]);
    }
}

TEST_F(ImplicitArrayInt24Test, writeRead)
{
    const size_t numElements = 55;
    vector_type<int32_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        array.push_back(static_cast<uint8_t>(i));
    }
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::BitStreamWriter writer(bitBuffer);
    implicitArray.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ImplicitArray readImplicitArray(reader);
    const vector_type<int32_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(static_cast<int32_t>(i), readArray[i]);
    }
}

TEST_F(ImplicitArrayInt24Test, writeReadFile)
{
    const size_t numElements = 55;
    vector_type<int32_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        array.push_back(static_cast<uint8_t>(i));
    }
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::serializeToFile(implicitArray, BLOB_NAME);

    ImplicitArray readImplicitArray = zserio::deserializeFromFile<ImplicitArray>(BLOB_NAME);
    const vector_type<int32_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(static_cast<int32_t>(i), readArray[i]);
    }
}

} // namespace implicit_array_int24
} // namespace allow_implicit_arrays
