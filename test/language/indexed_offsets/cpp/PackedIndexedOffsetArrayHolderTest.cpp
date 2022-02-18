#include "gtest/gtest.h"

#include "zserio/SerializeUtil.h"

#include "indexed_offsets/packed_indexed_offset_array_holder/AutoIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace packed_indexed_offset_array_holder
{

using allocator_type = AutoIndexedOffsetArray::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class PackedIndexedOffsetArrayHolderTest : public ::testing::Test
{
protected:
    void fillAutoIndexedOffsetArray(AutoIndexedOffsetArray& autoIndexedOffsetArray, size_t numElements)
    {
        auto& offsetArray = autoIndexedOffsetArray.getOffsetArray();
        auto& offsetHolders = offsetArray.getOffsetHolders();
        offsetHolders.reserve(numElements + 1);
        for (size_t i = 0; i < numElements + 1; ++i)
            offsetHolders.emplace_back(UINT32_C(0), vector_type<uint32_t>{0}, static_cast<uint32_t>(i));

        auto& data1 = autoIndexedOffsetArray.getData1();
        data1.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            data1.push_back(static_cast<int32_t>(i));

        auto& data2 = autoIndexedOffsetArray.getData2();
        data2.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            data2.push_back(static_cast<int32_t>(i * 2));

        autoIndexedOffsetArray.initializeOffsets();
    }

    size_t getAutoIndexedOffsetArrayBitSize(size_t numElements)
    {
        size_t bitSize = 0;
        for (size_t i = 0; i < numElements + 1; ++i)
        {
            bitSize += 32; // offset[i]
            bitSize += 32; // offsets[1]
            bitSize += 32; // value[i]
        }

        for (size_t i = 0; i < numElements; ++i)
            bitSize += 32; // data1[i]

        for (size_t i = 0; i < numElements; ++i)
            bitSize += 32; // data2[i]

        return bitSize;
    }

    void checkBitSizeOf(size_t numElements)
    {
        AutoIndexedOffsetArray autoIndexedOffsetArray;
        fillAutoIndexedOffsetArray(autoIndexedOffsetArray, numElements);

        const double unpackedBitSize = static_cast<double>(getAutoIndexedOffsetArrayBitSize(numElements));
        const double packedBitSize = static_cast<double>(autoIndexedOffsetArray.bitSizeOf());
        const double minCompressionRation = 0.82;
        ASSERT_GT(unpackedBitSize * minCompressionRation, packedBitSize)
                << "Unpacked array has " << std::to_string(unpackedBitSize) << " bits, "
                << "packed array has " << std::to_string(packedBitSize) << " bits, "
                << "compression ratio is "
                << std::to_string(packedBitSize / unpackedBitSize * 100) << "%!";
    }

    void checkWriteRead(size_t numElements)
    {
        AutoIndexedOffsetArray autoIndexedOffsetArray;
        fillAutoIndexedOffsetArray(autoIndexedOffsetArray, numElements);

        zserio::BitStreamWriter writer(bitBuffer);
        autoIndexedOffsetArray.write(writer);

        ASSERT_EQ(autoIndexedOffsetArray.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(autoIndexedOffsetArray.initializeOffsets(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        AutoIndexedOffsetArray readAutoIndexedOffsetArray(reader);
        ASSERT_EQ(autoIndexedOffsetArray, readAutoIndexedOffsetArray);
    }

    void checkWriteReadFile(size_t numElements)
    {
        AutoIndexedOffsetArray autoIndexedOffsetArray;
        fillAutoIndexedOffsetArray(autoIndexedOffsetArray, numElements);
        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(autoIndexedOffsetArray, fileName);

        const auto readAutoIndexedOffsetArray = zserio::deserializeFromFile<AutoIndexedOffsetArray>(fileName);
        ASSERT_EQ(autoIndexedOffsetArray, readAutoIndexedOffsetArray);
    }

    static const std::string BLOB_NAME_BASE;

    static const size_t NUM_ELEMENTS1 = 50;
    static const size_t NUM_ELEMENTS2 = 100;
    static const size_t NUM_ELEMENTS3 = 1000;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(20 * 1024 * 8);
};

const std::string PackedIndexedOffsetArrayHolderTest::BLOB_NAME_BASE =
        "language/indexed_offsets/packed_indexed_offset_array_holder_";

const size_t PackedIndexedOffsetArrayHolderTest::NUM_ELEMENTS1;
const size_t PackedIndexedOffsetArrayHolderTest::NUM_ELEMENTS2;
const size_t PackedIndexedOffsetArrayHolderTest::NUM_ELEMENTS3;

TEST_F(PackedIndexedOffsetArrayHolderTest, bitSizeOfLength1)
{
    checkBitSizeOf(NUM_ELEMENTS1);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, bitSizeOfLength2)
{
    checkBitSizeOf(NUM_ELEMENTS2);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, bitSizeOfLength3)
{
    checkBitSizeOf(NUM_ELEMENTS3);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, writeReadLength1)
{
    checkWriteRead(NUM_ELEMENTS1);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, writeReadLength2)
{
    checkWriteRead(NUM_ELEMENTS2);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, writeReadLength3)
{
    checkWriteRead(NUM_ELEMENTS3);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, writeReadFileLength1)
{
    checkWriteReadFile(NUM_ELEMENTS1);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, writeReadFileLength2)
{
    checkWriteReadFile(NUM_ELEMENTS2);
}

TEST_F(PackedIndexedOffsetArrayHolderTest, writeReadFileLength3)
{
    checkWriteReadFile(NUM_ELEMENTS3);
}

} // namespace packed_indexed_offset_array_holder
} // namespace indexed_offsets
