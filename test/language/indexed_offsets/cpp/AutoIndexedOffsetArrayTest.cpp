#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/auto_indexed_offset_array/AutoIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace auto_indexed_offset_array
{

class AutoIndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeAutoIndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
    {
        writer.writeVarSize(NUM_ELEMENTS);
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && writeWrongOffsets)
                writer.writeBits(wrongOffset, 32);
            else
                writer.writeBits(currentOffset, 32);
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }

        writer.writeBits(SPACER_VALUE, 1);

        writer.writeVarSize(NUM_ELEMENTS);
        writer.writeBits(0, 7);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits(i % 64, ELEMENT_SIZE);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }
    }

    void checkOffsets(const AutoIndexedOffsetArray& autoIndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = autoIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (auto offset : offsets)
        {
            ASSERT_EQ(expectedOffset, offset);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    void checkAutoIndexedOffsetArray(const AutoIndexedOffsetArray& autoIndexedOffsetArray)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(autoIndexedOffsetArray, offsetShift);

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, autoIndexedOffsetArray.getSpacer());

        const auto& data = autoIndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, data.size());
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            ASSERT_EQ(i % 64, data[i]);
    }

    void fillAutoIndexedOffsetArray(AutoIndexedOffsetArray& autoIndexedOffsetArray, bool createWrongOffsets)
    {
        auto& offsets = autoIndexedOffsetArray.getOffsets();
        offsets.reserve(NUM_ELEMENTS);
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets.push_back(wrongOffset);
            else
                offsets.push_back(currentOffset);
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
        autoIndexedOffsetArray.setSpacer(SPACER_VALUE);

        auto& data = autoIndexedOffsetArray.getData();
        data.reserve(NUM_ELEMENTS);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            data.push_back(i % 64);
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;

    static const size_t     AUTO_ARRAY_LENGTH_BYTE_SIZE = 1;
    static const uint32_t   ELEMENT0_OFFSET = AUTO_ARRAY_LENGTH_BYTE_SIZE + NUM_ELEMENTS * sizeof(uint32_t) +
            sizeof(uint8_t) + AUTO_ARRAY_LENGTH_BYTE_SIZE;
    static const uint8_t    ELEMENT_SIZE = 5;
    static const uint8_t    ALIGNED_ELEMENT_SIZE = 8;
    static const uint8_t    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    static const uint8_t    SPACER_VALUE = 1;

    static const size_t     AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE = ELEMENT0_OFFSET * 8 +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(AutoIndexedOffsetArrayTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoIndexedOffsetArray autoIndexedOffsetArray(reader);
    checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
}

TEST_F(AutoIndexedOffsetArrayTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(AutoIndexedOffsetArray autoIndexedOffsetArray(reader), zserio::CppRuntimeException);
}

TEST_F(AutoIndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.bitSizeOf());
}

TEST_F(AutoIndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(AutoIndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.initializeOffsets(bitPosition));
    checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
}

TEST_F(AutoIndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(autoIndexedOffsetArray, offsetShift);
}

TEST_F(AutoIndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = false;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    autoIndexedOffsetArray.write(writer);
    checkAutoIndexedOffsetArray(autoIndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoIndexedOffsetArray readAutoIndexedOffsetArray(reader);
    checkAutoIndexedOffsetArray(readAutoIndexedOffsetArray);
    ASSERT_TRUE(autoIndexedOffsetArray == readAutoIndexedOffsetArray);
}

TEST_F(AutoIndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 8;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    autoIndexedOffsetArray.initializeOffsets(writer.getBitPosition());
    autoIndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(autoIndexedOffsetArray, offsetShift);
}

TEST_F(AutoIndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(autoIndexedOffsetArray.write(writer), zserio::CppRuntimeException);
}

} // namespace auto_indexed_offset_array
} // namespace indexed_offsets
