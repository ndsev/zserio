#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/bool_indexed_offset_array/BoolIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace bool_indexed_offset_array
{

class BoolIndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeBoolIndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
    {
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
        writer.writeBits(0, 7);

        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits(i & 0x01, ELEMENT_SIZE);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }
    }

    void checkOffsets(const BoolIndexedOffsetArray& boolIndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = boolIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (auto it = offsets.begin(); it != offsets.end(); ++it)
        {
            ASSERT_EQ(expectedOffset, *it);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    void checkBoolIndexedOffsetArray(const BoolIndexedOffsetArray& boolIndexedOffsetArray)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(boolIndexedOffsetArray, offsetShift);

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, boolIndexedOffsetArray.getSpacer());

        const auto& data = boolIndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, data.size());
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            ASSERT_EQ((i & 0x01) ? true : false, data[i] != 0);
    }

    void fillBoolIndexedOffsetArray(BoolIndexedOffsetArray& boolIndexedOffsetArray, bool createWrongOffsets)
    {
        auto& offsets = boolIndexedOffsetArray.getOffsets();
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
        boolIndexedOffsetArray.setSpacer(SPACER_VALUE);

        auto& data = boolIndexedOffsetArray.getData();
        data.reserve(NUM_ELEMENTS);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            data.push_back((i & 0x01) ? true : false);
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;

    static const uint32_t   ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);
    static const uint8_t    ELEMENT_SIZE = 1;
    static const uint8_t    ALIGNED_ELEMENT_SIZE = 8;
    static const uint8_t    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    static const uint8_t    SPACER_VALUE = 1;

    static const size_t     BOOL_indexed_offset_ARRAY_BIT_SIZE = ELEMENT0_OFFSET * 8 +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(BoolIndexedOffsetArrayTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBoolIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BoolIndexedOffsetArray boolIndexedOffsetArray(reader);
    checkBoolIndexedOffsetArray(boolIndexedOffsetArray);
}

TEST_F(BoolIndexedOffsetArrayTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBoolIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(BoolIndexedOffsetArray boolIndexedOffsetArray(reader), zserio::CppRuntimeException);
}

TEST_F(BoolIndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = BOOL_indexed_offset_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, boolIndexedOffsetArray.bitSizeOf());
}

TEST_F(BoolIndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = BOOL_indexed_offset_ARRAY_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, boolIndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(BoolIndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = BOOL_indexed_offset_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, boolIndexedOffsetArray.initializeOffsets(bitPosition));
    checkBoolIndexedOffsetArray(boolIndexedOffsetArray);
}

TEST_F(BoolIndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = BOOL_indexed_offset_ARRAY_BIT_SIZE + bitPosition - 1;
    ASSERT_EQ(expectedBitSize, boolIndexedOffsetArray.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(boolIndexedOffsetArray, offsetShift);
}

TEST_F(BoolIndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = true;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    boolIndexedOffsetArray.write(writer);
    checkBoolIndexedOffsetArray(boolIndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BoolIndexedOffsetArray readBoolIndexedOffsetArray(reader);
    checkBoolIndexedOffsetArray(readBoolIndexedOffsetArray);
    ASSERT_TRUE(boolIndexedOffsetArray == readBoolIndexedOffsetArray);
}

TEST_F(BoolIndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    const size_t bitPosition = 8;
    writer.writeBits(0, bitPosition);
    boolIndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(boolIndexedOffsetArray, offsetShift);
}

TEST_F(BoolIndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    BoolIndexedOffsetArray boolIndexedOffsetArray;
    fillBoolIndexedOffsetArray(boolIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(boolIndexedOffsetArray.write(writer, zserio::NO_PRE_WRITE_ACTION),
            zserio::CppRuntimeException);
}

} // namespace bool_indexed_offset_array
} // namespace indexed_offsets
