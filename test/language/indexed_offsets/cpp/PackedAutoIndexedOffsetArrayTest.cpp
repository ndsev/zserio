#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/packed_auto_indexed_offset_array/AutoIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace packed_auto_indexed_offset_array
{

class PackedAutoIndexedOffsetArrayTest : public ::testing::Test
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

        writer.writeBits(SPACER_VALUE, 3);

        writer.writeVarSize(NUM_ELEMENTS);

        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        writer.alignTo(8);
        writer.writeBits(0, ELEMENT_SIZE);
        for (uint8_t i = 0; i < NUM_ELEMENTS - 1; ++i)
        {
            writer.alignTo(8);
            writer.writeSignedBits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
        }
    }

    void checkOffsets(const AutoIndexedOffsetArray& autoIndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = autoIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (auto it = offsets.begin(); it != offsets.end(); ++it)
        {
            ASSERT_EQ(expectedOffset, *it);
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
        {
            ASSERT_EQ(i, data[i]);
        }
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
            data.push_back(i);
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;

    static const size_t     AUTO_ARRAY_LENGTH_BYTE_SIZE = 1;
    static const uint32_t   ELEMENT0_OFFSET = AUTO_ARRAY_LENGTH_BYTE_SIZE + NUM_ELEMENTS * sizeof(uint32_t) +
            AUTO_ARRAY_LENGTH_BYTE_SIZE + sizeof(uint8_t) + 1;
    static const uint8_t    ELEMENT_SIZE = 5;
    static const uint8_t    ALIGNED_ELEMENT_SIZE = 8;
    static const uint8_t    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    static const uint8_t    SPACER_VALUE = 7;

    static const int8_t     PACKED_ARRAY_DELTA = 1;
    static const uint8_t    PACKED_ARRAY_MAX_BIT_NUMBER = 1;

    static const size_t     AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE = ELEMENT0_OFFSET * 8 +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + PACKED_ARRAY_MAX_BIT_NUMBER + 1;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(PackedAutoIndexedOffsetArrayTest, readConstructor)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoIndexedOffsetArray autoIndexedOffsetArray(reader);
    checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
}

TEST_F(PackedAutoIndexedOffsetArrayTest, readConstructorWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(AutoIndexedOffsetArray autoIndexedOffsetArray(reader), zserio::CppRuntimeException);
}

TEST_F(PackedAutoIndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.bitSizeOf());
}

TEST_F(PackedAutoIndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(PackedAutoIndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, autoIndexedOffsetArray.initializeOffsets(bitPosition));
    checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
}

TEST_F(PackedAutoIndexedOffsetArrayTest, initializeOffsetsWithPosition)
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

TEST_F(PackedAutoIndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    autoIndexedOffsetArray.write(writer);
    checkAutoIndexedOffsetArray(autoIndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoIndexedOffsetArray readPackedAutoIndexedOffsetArray(reader);
    checkAutoIndexedOffsetArray(readPackedAutoIndexedOffsetArray);
    ASSERT_TRUE(autoIndexedOffsetArray == readPackedAutoIndexedOffsetArray);
}

TEST_F(PackedAutoIndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    const size_t bitPosition = 8;
    writer.writeBits(0, bitPosition);
    autoIndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(autoIndexedOffsetArray, offsetShift);
}

TEST_F(PackedAutoIndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    AutoIndexedOffsetArray autoIndexedOffsetArray;
    fillAutoIndexedOffsetArray(autoIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(autoIndexedOffsetArray.write(writer, zserio::NO_PRE_WRITE_ACTION),
            zserio::CppRuntimeException);
}

} // namespace packed_auto_indexed_offset_array
} // namespace indexed_offsets
