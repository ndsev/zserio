#include "allow_implicit_arrays/implicit_array_uint64/ImplicitArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace allow_implicit_arrays
{
namespace implicit_array_uint64
{

using allocator_type = ImplicitArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ImplicitArrayUInt64Test : public ::testing::Test
{
protected:
    void writeImplicitArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        for (size_t i = 0; i < numElements; ++i)
            writer.writeBits64(static_cast<uint64_t>(i), 64);
    }

    static const std::string BLOB_NAME;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string ImplicitArrayUInt64Test::BLOB_NAME =
        "arguments/allow_implicit_arrays/implicit_array_uint64.blob";

TEST_F(ImplicitArrayUInt64Test, bitSizeOf)
{
    const size_t numElements = 55;
    vector_type<uint64_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint64_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(numElements * 64, implicitArray.bitSizeOf(bitPosition));
}

TEST_F(ImplicitArrayUInt64Test, initializeOffsets)
{
    const size_t numElements = 55;
    vector_type<uint64_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint64_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + numElements * 64, implicitArray.initializeOffsets(bitPosition));
}

TEST_F(ImplicitArrayUInt64Test, readConstructor)
{
    const size_t numElements = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeImplicitArrayToByteArray(writer, numElements);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ImplicitArray implicitArray(reader);

    const vector_type<uint64_t>& array = implicitArray.getArray();
    ASSERT_EQ(numElements, array.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint64_t>(i), array[i]);
}

TEST_F(ImplicitArrayUInt64Test, writeRead)
{
    const size_t numElements = 55;
    vector_type<uint64_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint64_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::BitStreamWriter writer(bitBuffer);
    implicitArray.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ImplicitArray readImplicitArray(reader);
    const vector_type<uint64_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint64_t>(i), readArray[i]);
}

TEST_F(ImplicitArrayUInt64Test, writeReadFile)
{
    const size_t numElements = 55;
    vector_type<uint64_t> array;
    array.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
        array.push_back(static_cast<uint64_t>(i));
    ImplicitArray implicitArray;
    implicitArray.setArray(array);

    zserio::serializeToFile(implicitArray, BLOB_NAME);

    ImplicitArray readImplicitArray = zserio::deserializeFromFile<ImplicitArray>(BLOB_NAME);
    const vector_type<uint64_t>& readArray = readImplicitArray.getArray();
    ASSERT_EQ(numElements, readArray.size());
    for (size_t i = 0; i < numElements; ++i)
        ASSERT_EQ(static_cast<uint64_t>(i), readArray[i]);
}

} // namespace implicit_array_uint64
} // namespace allow_implicit_arrays
