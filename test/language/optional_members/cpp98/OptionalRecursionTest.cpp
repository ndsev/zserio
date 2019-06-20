#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#
#include "optional_members/optional_recursion/Block.h"

namespace optional_members
{
namespace optional_recursion
{

class OptionalRecursionTest : public ::testing::Test
{
protected:
    void fillBlock(Block& block, const uint8_t* blockData, size_t blockDataSize)
    {
        block.initialize(static_cast<uint8_t>(blockDataSize));

        zserio::UInt8Array dataBytes;
        dataBytes.reserve(blockDataSize);
        for (size_t i = 0; i < blockDataSize; ++i)
            dataBytes.push_back(static_cast<uint8_t>(blockData[i]));
        block.setDataBytes(dataBytes);

        block.setBlockTerminator(0);
    }

    void fillBlock(Block& block1, const uint8_t* block1Data, size_t block1DataSize, const uint8_t* block2Data,
            size_t block2DataSize)
    {
        Block block2;
        fillBlock(block2, block2Data, block2DataSize);

        block1.initialize(static_cast<uint8_t>(block1DataSize));

        zserio::UInt8Array dataBytes;
        dataBytes.reserve(block1DataSize);
        for (size_t i = 0; i < block1DataSize; ++i)
            dataBytes.push_back(static_cast<uint8_t>(block1Data[i]));
        block1.setDataBytes(dataBytes);

        block1.setBlockTerminator(static_cast<uint8_t>(block2DataSize));
        block1.setNextData(block2);
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
};

const uint8_t OptionalRecursionTest::BLOCK1_DATA[] = {1, 2, 3, 4, 5, 6};
const uint8_t OptionalRecursionTest::BLOCK2_DATA[] = {10, 9, 8, 7};

TEST_F(OptionalRecursionTest, hasNextData)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_FALSE(block1.hasNextData());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_TRUE(block12.hasNextData());
}

TEST_F(OptionalRecursionTest, bitSizeOf)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_EQ(getBlockBitSize(sizeof(BLOCK1_DATA)), block1.bitSizeOf());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_EQ(getBlockBitSize(sizeof(BLOCK1_DATA), sizeof(BLOCK2_DATA)), block12.bitSizeOf());
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
}

TEST_F(OptionalRecursionTest, operatorEquality)
{
    Block emptyBlock1;
    emptyBlock1.initialize(0);
    Block emptyBlock2;
    emptyBlock2.initialize(0);
    ASSERT_TRUE(emptyBlock1 == emptyBlock2);

    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_FALSE(block1 == emptyBlock1);

    Block block2;
    fillBlock(block2, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_TRUE(block2 == block1);

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_FALSE(block12 == block1);
}

TEST_F(OptionalRecursionTest, hashCode)
{
    Block emptyBlock1;
    emptyBlock1.initialize(0);
    Block emptyBlock2;
    emptyBlock2.initialize(0);
    ASSERT_EQ(emptyBlock1.hashCode(), emptyBlock2.hashCode());

    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_NE(block1.hashCode(), emptyBlock1.hashCode());

    Block block2;
    fillBlock(block2, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block2.hashCode(), block1.hashCode());

    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    ASSERT_NE(block12.hashCode(), block1.hashCode());
}

TEST_F(OptionalRecursionTest, writeBlock1)
{
    Block block1;
    fillBlock(block1, BLOCK1_DATA, sizeof(BLOCK1_DATA));

    zserio::BitStreamWriter writer;
    block1.write(writer);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkBlockInBitStream(reader, BLOCK1_DATA, sizeof(BLOCK1_DATA));
    reader.setBitPosition(0);

    Block readBlock1(reader, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block1, readBlock1);
}

TEST_F(OptionalRecursionTest, writeBlock12)
{
    Block block12;
    fillBlock(block12, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));

    zserio::BitStreamWriter writer;
    block12.write(writer);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkBlockInBitStream(reader, BLOCK1_DATA, sizeof(BLOCK1_DATA), BLOCK2_DATA, sizeof(BLOCK2_DATA));
    reader.setBitPosition(0);

    Block readBlock12(reader, sizeof(BLOCK1_DATA));
    ASSERT_EQ(block12, readBlock12);
}

} // namespace optional_recursion
} // namespace optional_members
