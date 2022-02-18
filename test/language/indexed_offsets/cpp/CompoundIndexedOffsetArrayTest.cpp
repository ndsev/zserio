#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/compound_indexed_offset_array/CompoundIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace compound_indexed_offset_array
{

class CompoundIndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeCompoundIndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
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
            writer.writeBits(i, 32);
            writer.writeBits(i % 8, 3);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }
    }

    void checkOffsets(const CompoundIndexedOffsetArray& compoundIndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = compoundIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (auto it = offsets.begin(); it != offsets.end(); ++it)
        {
            ASSERT_EQ(expectedOffset, *it);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    void checkCompoundIndexedOffsetArray(const CompoundIndexedOffsetArray& compoundIndexedOffsetArray)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(compoundIndexedOffsetArray, offsetShift);

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, compoundIndexedOffsetArray.getSpacer());

        const auto& data = compoundIndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, data.size());
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            const Compound& compound = data[i];
            ASSERT_EQ(i, compound.getId());
            ASSERT_EQ(i % 8, compound.getValue());
        }
    }

    void fillCompoundIndexedOffsetArray(CompoundIndexedOffsetArray& compoundIndexedOffsetArray,
            bool createWrongOffsets)
    {
        auto& offsets = compoundIndexedOffsetArray.getOffsets();
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
        compoundIndexedOffsetArray.setSpacer(SPACER_VALUE);

        auto& data = compoundIndexedOffsetArray.getData();
        data.reserve(NUM_ELEMENTS);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            Compound compound;
            compound.setId(i);
            compound.setValue(i % 8);
            data.push_back(compound);
        }
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;

    static const uint32_t   ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);
    static const uint8_t    ELEMENT_SIZE = 35;
    static const uint8_t    ALIGNED_ELEMENT_SIZE = 5 * 8;
    static const uint8_t    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    static const uint8_t    SPACER_VALUE = 1;

    static const size_t     COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE = ELEMENT0_OFFSET * 8 +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(CompoundIndexedOffsetArrayTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeCompoundIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    CompoundIndexedOffsetArray compoundIndexedOffsetArray(reader);
    checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray);
}

TEST_F(CompoundIndexedOffsetArrayTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeCompoundIndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(CompoundIndexedOffsetArray compoundIndexedOffsetArray(reader),
            zserio::CppRuntimeException);
}

TEST_F(CompoundIndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, compoundIndexedOffsetArray.bitSizeOf());
}

TEST_F(CompoundIndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, compoundIndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(CompoundIndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, compoundIndexedOffsetArray.initializeOffsets(bitPosition));
    checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray);
}

TEST_F(CompoundIndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1;
    ASSERT_EQ(expectedBitSize, compoundIndexedOffsetArray.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(compoundIndexedOffsetArray, offsetShift);
}

TEST_F(CompoundIndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = false;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    compoundIndexedOffsetArray.write(writer);
    checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    CompoundIndexedOffsetArray readCompoundIndexedOffsetArray(reader);
    checkCompoundIndexedOffsetArray(readCompoundIndexedOffsetArray);
    ASSERT_TRUE(compoundIndexedOffsetArray == readCompoundIndexedOffsetArray);
}

TEST_F(CompoundIndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 8;
    compoundIndexedOffsetArray.initializeOffsets(bitPosition);
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    compoundIndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(compoundIndexedOffsetArray, offsetShift);
}

TEST_F(CompoundIndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    CompoundIndexedOffsetArray compoundIndexedOffsetArray;
    fillCompoundIndexedOffsetArray(compoundIndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(compoundIndexedOffsetArray.write(writer), zserio::CppRuntimeException);
}

} // namespace compound_indexed_offset_array
} // namespace indexed_offsets
