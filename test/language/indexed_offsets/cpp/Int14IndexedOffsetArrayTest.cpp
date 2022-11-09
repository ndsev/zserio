#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/int14_indexed_offset_array/Int14IndexedOffsetArray.h"

namespace indexed_offsets
{
namespace int14_indexed_offset_array
{

class Int14IndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeInt14IndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
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
            writer.writeBits(i, ELEMENT_SIZE);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }
    }

    void checkOffsets(const Int14IndexedOffsetArray& int14IndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = int14IndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (auto it = offsets.begin(); it != offsets.end(); ++it)
        {
            ASSERT_EQ(expectedOffset, *it);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    void checkInt14IndexedOffsetArray(const Int14IndexedOffsetArray& int14IndexedOffsetArray)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(int14IndexedOffsetArray, offsetShift);

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, int14IndexedOffsetArray.getSpacer());

        const auto& data = int14IndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, data.size());
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            ASSERT_EQ(i, data[i]);
    }

    void fillInt14IndexedOffsetArray(Int14IndexedOffsetArray& int14IndexedOffsetArray, bool createWrongOffsets)
    {
        auto& offsets = int14IndexedOffsetArray.getOffsets();
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
        int14IndexedOffsetArray.setSpacer(SPACER_VALUE);

        auto& data = int14IndexedOffsetArray.getData();
        data.reserve(NUM_ELEMENTS);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            data.push_back(i);
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;

    static const uint32_t   ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);
    static const uint8_t    ELEMENT_SIZE = 14;
    static const uint8_t    ALIGNED_ELEMENT_SIZE = 2 * 8;
    static const uint8_t    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    static const uint8_t    SPACER_VALUE = 1;

    static const size_t     INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE = ELEMENT0_OFFSET * 8 +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(Int14IndexedOffsetArrayTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeInt14IndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Int14IndexedOffsetArray int14IndexedOffsetArray(reader);
    checkInt14IndexedOffsetArray(int14IndexedOffsetArray);
}

TEST_F(Int14IndexedOffsetArrayTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeInt14IndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(Int14IndexedOffsetArray int14IndexedOffsetArray(reader), zserio::CppRuntimeException);
}

TEST_F(Int14IndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, int14IndexedOffsetArray.bitSizeOf());
}

TEST_F(Int14IndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, int14IndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(Int14IndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, int14IndexedOffsetArray.initializeOffsets(bitPosition));
    checkInt14IndexedOffsetArray(int14IndexedOffsetArray);
}

TEST_F(Int14IndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1;
    ASSERT_EQ(expectedBitSize, int14IndexedOffsetArray.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(int14IndexedOffsetArray, offsetShift);
}

TEST_F(Int14IndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = false;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    int14IndexedOffsetArray.write(writer);
    checkInt14IndexedOffsetArray(int14IndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Int14IndexedOffsetArray readInt14IndexedOffsetArray(reader);
    checkInt14IndexedOffsetArray(readInt14IndexedOffsetArray);
    ASSERT_TRUE(int14IndexedOffsetArray == readInt14IndexedOffsetArray);
}

TEST_F(Int14IndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 8;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    int14IndexedOffsetArray.initializeOffsets(writer.getBitPosition());
    int14IndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(int14IndexedOffsetArray, offsetShift);
}

TEST_F(Int14IndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    Int14IndexedOffsetArray int14IndexedOffsetArray;
    fillInt14IndexedOffsetArray(int14IndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(int14IndexedOffsetArray.write(writer), zserio::CppRuntimeException);
}

} // namespace int14_indexed_offset_array
} // namespace indexed_offsets
