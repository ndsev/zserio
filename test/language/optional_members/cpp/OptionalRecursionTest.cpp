#include "gtest/gtest.h"

#include "optional_members/optional_recursion/Block.h"

#include "zserio/RebindAlloc.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

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
    void fillBlock(Block& block, const uint8_t* blockData, size_t blockDataSize)
    {
        vector_type<uint8_t> dataBytes;
        dataBytes.reserve(blockDataSize);
        for (size_t i = 0; i < blockDataSize; ++i)
            dataBytes.push_back(static_cast<uint8_t>(blockData[i]));
        block.setDataBytes(dataBytes);

        block.setBlockTerminator(0);

        block.initialize(static_cast<uint8_t>(blockDataSize));
    }

    void fillBlock(Block& block1, const uint8_t* block1Data, size_t block1DataSize, const uint8_t* block2Data,
            size_t block2DataSize)
    {
        Block block2;
        fillBlock(block2, block2Data, block2DataSize);

        vector_type<uint8_t> dataBytes;
        dataBytes.reserve(block1DataSize);
        for (size_t i = 0; i < block1DataSize; ++i)
            dataBytes.push_back(static_cast<uint8_t>(block1Data[i]));
        block1.setDataBytes(dataBytes);

        block1.setBlockTerminator(static_cast<uint8_t>(block2DataSize));
        block1.setNextData(block2);

        block1.initialize(static_cast<uint8_t>(block1DataSize));
    }

    size_t getBlockBitSize(size_t blockDataSize)
    {
        return 8 * blockDataSize + 8;
    }

    size_t getBlockBitSize(size_t block1DataSize, size_t block2DataSize)
    {
        return getBlockBitSize(block1DataSize) + getBlockBitSize(block2DataSize);
    }

    void checkBlockInBitStream(zserio::BitStreamReader& reader, const uint8_t* blockData, size_t blockDataSize)
    {
        for (size_t i = 0; i < blockDataSize; ++i)
            ASSERT_EQ(blockData[i], reader.readBits(8));

        ASSERT_EQ(0, reader.readBits(8));
    }

    void checkBlockInBitStream(zserio::BitStreamReader& reader, const uint8_t* block1Data,
            size_t block1DataSize, const uint8_t* block2Data, size_t block2DataSize)
    {
        for (size_t i = 0; i < block1DataSize; ++i)
            ASSERT_EQ(block1Data[i], reader.readBits(8));
        ASSERT_EQ(block2DataSize, reader.readBits(8));

        checkBlockInBitStream(reader, block2Data, block2DataSize);
    }

    static const uint8_t BLOCK1_DATA[];
    static const uint8_t BLOCK2_DATA[];

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t OptionalRecursionTest::BLOCK1_DATA[] = {1, 2, 3, 4, 5, 6};
const uint8_t OptionalRecursionTest::BLOCK2_DATA[] = {10, 9, 8, 7};

TEST_F(OptionalRecursionTest, isNextDataSetAndUsed)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_FALSE(block1.isNextDataSet());
    ASSERT_FALSE(block1.isNextDataUsed());

    block1.setBlockTerminator(1); // used but not set
    ASSERT_FALSE(block1.isNextDataSet());
    ASSERT_TRUE(block1.isNextDataUsed());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_TRUE(block12.isNextDataSet());
    ASSERT_TRUE(block12.isNextDataUsed());

    block12.setBlockTerminator(0); // set but not used
    ASSERT_TRUE(block12.isNextDataSet());
    ASSERT_FALSE(block12.isNextDataUsed());
}

TEST_F(OptionalRecursionTest, resetNextData)
{
    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
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
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_EQ(getBlockBitSize(sizeof(BLOCK1_DATA)), block1.bitSizeOf());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_EQ(getBlockBitSize(sizeof(BLOCK1_DATA), sizeof(BLOCK2_DATA)), block12.bitSizeOf());

    block12.setBlockTerminator(0); // set but not used
    ASSERT_EQ(getBlockBitSize(sizeof(BLOCK1_DATA)), block12.bitSizeOf());
}

TEST_F(OptionalRecursionTest, initializeOffsets)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + getBlockBitSize(sizeof(BLOCK1_DATA)), block1.initializeOffsets(bitPosition));

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_EQ(bitPosition + getBlockBitSize(sizeof(BLOCK1_DATA), sizeof(BLOCK2_DATA)),
            block12.initializeOffsets(bitPosition));

    block12.setBlockTerminator(0); // set but not used
    ASSERT_EQ(bitPosition + getBlockBitSize(sizeof(BLOCK1_DATA)), block12.initializeOffsets(bitPosition));
}

TEST_F(OptionalRecursionTest, operatorEquality)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));

    Block block2;
    fillBlock(block2, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_TRUE(block2 == block1);

    Block block12_1;
    fillBlock(block12_1, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_FALSE(block12_1 == block1);

    Block block12_2;
    fillBlock(block12_2, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_TRUE(block12_1 == block12_2);

    block12_1.setBlockTerminator(0); // set but not used
    ASSERT_FALSE(block12_1 == block12_2);

    block12_2.setBlockTerminator(0); // set but not used
    ASSERT_TRUE(block12_1 == block12_2);
}

TEST_F(OptionalRecursionTest, hashCode)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));

    Block block2;
    fillBlock(block2, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block2.hashCode(), block1.hashCode());

    Block block12_1;
    fillBlock(block12_1, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_NE(block12_1.hashCode(), block1.hashCode());

    Block block12_2;
    fillBlock(block12_2, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
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
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));

    zserio::BitStreamWriter writer(bitBuffer);
    block1.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkBlockInBitStream(reader, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    reader.setBitPosition(0);

    Block readBlock1(reader, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block1, readBlock1);
}

TEST_F(OptionalRecursionTest, writeBlock12)
{
    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));

    zserio::BitStreamWriter writer(bitBuffer);
    block12.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkBlockInBitStream(reader, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    reader.setBitPosition(0);

    Block readBlock12(reader, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block12, readBlock12);

    block12.setBlockTerminator(0); // set but not used
    zserio::BitStreamWriter writer2(bitBuffer);
    block12.write(writer2);
    zserio::BitStreamReader reader2(writer2.getWriteBuffer(), writer2.getBitPosition(), zserio::BitsTag());
    checkBlockInBitStream(reader2, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    reader2.setBitPosition(0);

    Block readBlock12_2(reader2, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block12, readBlock12_2);
}

} // namespace optional_recursion
} // namespace optional_members
