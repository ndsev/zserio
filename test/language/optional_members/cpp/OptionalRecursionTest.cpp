#include <array>

#include "gtest/gtest.h"
#include "optional_members/optional_recursion/Block.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/RebindAlloc.h"

namespace optional_members
{
namespace optional_recursion
{

using allocator_type = Block::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class OptionalRecursionTest : public ::testing::Test
{
protected:
    template <size_t SIZE>
    void fillBlock(Block& block, const std::array<uint8_t, SIZE>& blockData)
    {
        vector_type<uint8_t> dataBytes;
        dataBytes.reserve(blockData.size());
        dataBytes.assign(blockData.begin(), blockData.end());
        block.setDataBytes(dataBytes);

        block.setBlockTerminator(0);

        block.initialize(static_cast<uint8_t>(blockData.size()));
    }

    template <size_t SIZE1, size_t SIZE2>
    void fillBlock(Block& block1, const std::array<uint8_t, SIZE1>& block1Data,
            const std::array<uint8_t, SIZE2>& block2Data)
    {
        Block block2;
        fillBlock(block2, block2Data);

        vector_type<uint8_t> dataBytes;
        dataBytes.reserve(block1Data.size());
        dataBytes.assign(block1Data.begin(), block1Data.end());
        block1.setDataBytes(dataBytes);

        block1.setBlockTerminator(static_cast<uint8_t>(block2Data.size()));
        block1.setNextData(block2);

        block1.initialize(static_cast<uint8_t>(block1Data.size()));
    }

    size_t getBlockBitSize(size_t blockDataSize)
    {
        return 8 * blockDataSize + 8;
    }

    size_t getBlockBitSize(size_t block1DataSize, size_t block2DataSize)
    {
        return getBlockBitSize(block1DataSize) + getBlockBitSize(block2DataSize);
    }

    template <size_t SIZE>
    void checkBlockInBitStream(zserio::BitStreamReader& reader, const std::array<uint8_t, SIZE>& blockData)
    {
        for (uint8_t data : blockData)
            ASSERT_EQ(data, reader.readBits(8));

        ASSERT_EQ(0, reader.readBits(8));
    }

    template <size_t SIZE1, size_t SIZE2>
    void checkBlockInBitStream(zserio::BitStreamReader& reader, const std::array<uint8_t, SIZE1>& block1Data,
            const std::array<uint8_t, SIZE2>& block2Data)
    {
        for (uint8_t data1 : block1Data)
            ASSERT_EQ(data1, reader.readBits(8));
        ASSERT_EQ(block2Data.size(), reader.readBits(8));

        checkBlockInBitStream(reader, block2Data);
    }

    static const std::array<uint8_t, 6> BLOCK1_DATA;
    static const std::array<uint8_t, 4> BLOCK2_DATA;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::array<uint8_t, 6> OptionalRecursionTest::BLOCK1_DATA = {1, 2, 3, 4, 5, 6};
const std::array<uint8_t, 4> OptionalRecursionTest::BLOCK2_DATA = {10, 9, 8, 7};

TEST_F(OptionalRecursionTest, isNextDataSetAndUsed)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);
    ASSERT_FALSE(block1.isNextDataSet());
    ASSERT_FALSE(block1.isNextDataUsed());

    block1.setBlockTerminator(1); // used but not set
    ASSERT_FALSE(block1.isNextDataSet());
    ASSERT_TRUE(block1.isNextDataUsed());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_TRUE(block12.isNextDataSet());
    ASSERT_TRUE(block12.isNextDataUsed());

    block12.setBlockTerminator(0); // set but not used
    ASSERT_TRUE(block12.isNextDataSet());
    ASSERT_FALSE(block12.isNextDataUsed());
}

TEST_F(OptionalRecursionTest, resetNextData)
{
    Block block12;
    fillBlock(block12, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_TRUE(block12.isNextDataSet());
    ASSERT_TRUE(block12.isNextDataUsed());

    block12.resetNextData(); // used but not set
    ASSERT_FALSE(block12.isNextDataSet());
    ASSERT_TRUE(block12.isNextDataUsed());
    ASSERT_THROW(block12.getNextData(), zserio::CppRuntimeException);
}

TEST_F(OptionalRecursionTest, bitSizeOf)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);
    ASSERT_EQ(getBlockBitSize(BLOCK1_DATA.size()), block1.bitSizeOf());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_EQ(getBlockBitSize(BLOCK1_DATA.size(), BLOCK2_DATA.size()), block12.bitSizeOf());

    block12.setBlockTerminator(0); // set but not used
    ASSERT_EQ(getBlockBitSize(BLOCK1_DATA.size()), block12.bitSizeOf());
}

TEST_F(OptionalRecursionTest, initializeOffsets)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + getBlockBitSize(BLOCK1_DATA.size()), block1.initializeOffsets(bitPosition));

    Block block12;
    fillBlock(block12, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_EQ(bitPosition + getBlockBitSize(BLOCK1_DATA.size(), BLOCK2_DATA.size()),
            block12.initializeOffsets(bitPosition));

    block12.setBlockTerminator(0); // set but not used
    ASSERT_EQ(bitPosition + getBlockBitSize(BLOCK1_DATA.size()), block12.initializeOffsets(bitPosition));
}

TEST_F(OptionalRecursionTest, operatorEquality)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);

    Block block2;
    fillBlock(block2, BLOCK1_DATA);
    ASSERT_TRUE(block2 == block1);

    Block block12_1;
    fillBlock(block12_1, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_FALSE(block12_1 == block1);

    Block block12_2;
    fillBlock(block12_2, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_TRUE(block12_1 == block12_2);

    block12_1.setBlockTerminator(0); // set but not used
    ASSERT_FALSE(block12_1 == block12_2);

    block12_2.setBlockTerminator(0); // set but not used
    ASSERT_TRUE(block12_1 == block12_2);
}

TEST_F(OptionalRecursionTest, operatorLessThan)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);
    Block block2;
    fillBlock(block2, BLOCK1_DATA);
    ASSERT_FALSE(block1 < block2);
    ASSERT_FALSE(block2 < block1);

    Block block12_1;
    fillBlock(block12_1, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_TRUE(block1 < block12_1);
    ASSERT_FALSE(block12_1 < block1);

    Block block12_2;
    fillBlock(block12_2, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_FALSE(block12_1 < block12_2);
    ASSERT_FALSE(block12_2 < block12_1);

    block12_1.setBlockTerminator(0); // set but not used
    ASSERT_TRUE(block12_1 < block12_2);
    ASSERT_FALSE(block12_2 < block12_1);

    block12_2.setBlockTerminator(0); // set but not used
    ASSERT_FALSE(block12_1 < block12_2);
    ASSERT_FALSE(block12_2 < block12_1);
}

TEST_F(OptionalRecursionTest, hashCode)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);

    Block block2;
    fillBlock(block2, BLOCK1_DATA);
    ASSERT_EQ(block2.hashCode(), block1.hashCode());

    Block block12_1;
    fillBlock(block12_1, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_NE(block12_1.hashCode(), block1.hashCode());

    Block block12_2;
    fillBlock(block12_2, BLOCK1_DATA, BLOCK2_DATA);
    ASSERT_EQ(block12_1.hashCode(), block12_2.hashCode());

    block12_1.setBlockTerminator(0); // set but not used
    ASSERT_NE(block12_1.hashCode(), block12_2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(6240113, block12_1.hashCode());
    ASSERT_EQ(1846174533, block12_2.hashCode());

    block12_2.setBlockTerminator(0); // set but not used
    ASSERT_EQ(block12_1.hashCode(), block12_2.hashCode());
}

TEST_F(OptionalRecursionTest, writeBlock1)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA);

    zserio::BitStreamWriter writer(bitBuffer);
    block1.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkBlockInBitStream(reader, BLOCK1_DATA);
    reader.setBitPosition(0);

    Block readBlock1(reader, BLOCK1_DATA.size());
    ASSERT_EQ(block1, readBlock1);
}

TEST_F(OptionalRecursionTest, writeBlock12)
{
    Block block12;
    fillBlock(block12, BLOCK1_DATA, BLOCK2_DATA);

    zserio::BitStreamWriter writer(bitBuffer);
    block12.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkBlockInBitStream(reader, BLOCK1_DATA, BLOCK2_DATA);
    reader.setBitPosition(0);

    Block readBlock12(reader, BLOCK1_DATA.size());
    ASSERT_EQ(block12, readBlock12);

    block12.setBlockTerminator(0); // set but not used
    zserio::BitStreamWriter writer2(bitBuffer);
    block12.write(writer2);
    zserio::BitStreamReader reader2(writer2.getWriteBuffer(), writer2.getBitPosition(), zserio::BitsTag());
    checkBlockInBitStream(reader2, BLOCK1_DATA);
    reader2.setBitPosition(0);

    Block readBlock12_2(reader2, BLOCK1_DATA.size());
    ASSERT_EQ(block12, readBlock12_2);
}

} // namespace optional_recursion
} // namespace optional_members
