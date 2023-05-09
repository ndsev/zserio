#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/bit5_indexed_offset_array/Bit5IndexedOffsetArray.h"

namespace indexed_offsets
{
namespace bit5_indexed_offset_array
{

class Bit5IndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeBit5IndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
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
            writer.writeBits(i % 64, ELEMENT_SIZE);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }
    }

    void checkOffsets(const Bit5IndexedOffsetArray& bit5IndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = bit5IndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (auto offset : offsets)
        {
            ASSERT_EQ(expectedOffset, offset);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    void checkBit5IndexedOffsetArray(const Bit5IndexedOffsetArray& bit5IndexedOffsetArray)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(bit5IndexedOffsetArray, offsetShift);

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, bit5IndexedOffsetArray.getSpacer());

        const auto& data = bit5IndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, data.size());
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            ASSERT_EQ(i % 64, data[i]);
    }

    void fillBit5IndexedOffsetArray(Bit5IndexedOffsetArray& bit5IndexedOffsetArray, bool createWrongOffsets)
    {
        auto& offsets = bit5IndexedOffsetArray.getOffsets();
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
        bit5IndexedOffsetArray.setSpacer(SPACER_VALUE);

        auto& data = bit5IndexedOffsetArray.getData();
        data.reserve(NUM_ELEMENTS);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            data.push_back(i % 64);
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;

    static const uint32_t   ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);
    static const uint8_t    ELEMENT_SIZE = 5;
    static const uint8_t    ALIGNED_ELEMENT_SIZE = 8;
    static const uint8_t    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    static const uint8_t    SPACER_VALUE = 1;

    static const size_t     BIT5_indexed_offset_ARRAY_BIT_SIZE = ELEMENT0_OFFSET * 8 +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(Bit5IndexedOffsetArrayTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBit5IndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Bit5IndexedOffsetArray bit5IndexedOffsetArray(reader);
    checkBit5IndexedOffsetArray(bit5IndexedOffsetArray);
}

TEST_F(Bit5IndexedOffsetArrayTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBit5IndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(Bit5IndexedOffsetArray bit5IndexedOffsetArray(reader), zserio::CppRuntimeException);
}

TEST_F(Bit5IndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = BIT5_indexed_offset_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, bit5IndexedOffsetArray.bitSizeOf());
}

TEST_F(Bit5IndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = BIT5_indexed_offset_ARRAY_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, bit5IndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(Bit5IndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = BIT5_indexed_offset_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, bit5IndexedOffsetArray.initializeOffsets(bitPosition));
    checkBit5IndexedOffsetArray(bit5IndexedOffsetArray);
}

TEST_F(Bit5IndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = BIT5_indexed_offset_ARRAY_BIT_SIZE + bitPosition - 1;
    ASSERT_EQ(expectedBitSize, bit5IndexedOffsetArray.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(bit5IndexedOffsetArray, offsetShift);
}

TEST_F(Bit5IndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = false;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    bit5IndexedOffsetArray.write(writer);
    checkBit5IndexedOffsetArray(bit5IndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Bit5IndexedOffsetArray readBit5IndexedOffsetArray(reader);
    checkBit5IndexedOffsetArray(readBit5IndexedOffsetArray);
    ASSERT_TRUE(bit5IndexedOffsetArray == readBit5IndexedOffsetArray);
}

TEST_F(Bit5IndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 8;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    bit5IndexedOffsetArray.initializeOffsets(writer.getBitPosition());
    bit5IndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(bit5IndexedOffsetArray, offsetShift);
}

TEST_F(Bit5IndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    Bit5IndexedOffsetArray bit5IndexedOffsetArray;
    fillBit5IndexedOffsetArray(bit5IndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(bit5IndexedOffsetArray.write(writer), zserio::CppRuntimeException);
}

} // namespace bit5_indexed_offset_array
} // namespace indexed_offsets
