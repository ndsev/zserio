#include "gtest/gtest.h"
#include "indexed_offsets/varint32_indexed_offset_array/VarInt32IndexedOffsetArray.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace indexed_offsets
{
namespace varint32_indexed_offset_array
{

class VarInt32IndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeVarInt32IndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
    {
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && writeWrongOffsets)
            {
                writer.writeBits(wrongOffset, 32);
            }
            else
            {
                writer.writeBits(currentOffset, 32);
            }
            currentOffset += static_cast<uint32_t>(zserio::bitSizeOfVarInt32(i) / 8);
        }

        writer.writeBits(SPACER_VALUE, 1);
        writer.writeBits(0, 7);

        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeVarInt32(i);
        }
    }

    void checkOffsets(const VarInt32IndexedOffsetArray& varint32IndexedOffsetArray, uint16_t offsetShift)
    {
        const auto& offsets = varint32IndexedOffsetArray.getOffsets();
        const auto& data = varint32IndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (size_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            const uint32_t offset = offsets[i];
            ASSERT_EQ(expectedOffset, offset);
            expectedOffset += static_cast<uint32_t>(zserio::bitSizeOfVarInt32(data[i]) / 8);
        }
    }

    void checkVarInt32IndexedOffsetArray(const VarInt32IndexedOffsetArray& varint32IndexedOffsetArray)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(varint32IndexedOffsetArray, offsetShift);

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, varint32IndexedOffsetArray.getSpacer());

        const auto& data = varint32IndexedOffsetArray.getData();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, data.size());
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            ASSERT_EQ(i, data[i]);
        }
    }

    void fillVarInt32IndexedOffsetArray(
            VarInt32IndexedOffsetArray& varint32IndexedOffsetArray, bool createWrongOffsets)
    {
        auto& offsets = varint32IndexedOffsetArray.getOffsets();
        offsets.reserve(NUM_ELEMENTS);
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
            {
                offsets.push_back(wrongOffset);
            }
            else
            {
                offsets.push_back(currentOffset);
            }
            currentOffset += static_cast<uint32_t>(zserio::bitSizeOfVarInt32(i) / 8);
        }
        varint32IndexedOffsetArray.setSpacer(SPACER_VALUE);

        auto& data = varint32IndexedOffsetArray.getData();
        data.reserve(NUM_ELEMENTS);
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            data.push_back(i);
        }
    }

    size_t getVarInt32IndexedOffsetArrayBitSize()
    {
        size_t bitSize = ELEMENT0_OFFSET * 8;
        for (int16_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            bitSize += zserio::bitSizeOfVarInt32(i);
        }

        return bitSize;
    }

    static const uint8_t NUM_ELEMENTS = 5;
    static const uint32_t WRONG_OFFSET = 0;

    static const uint32_t ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);

    static const uint8_t SPACER_VALUE = 1;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(VarInt32IndexedOffsetArrayTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeVarInt32IndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray(reader);
    checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
}

TEST_F(VarInt32IndexedOffsetArrayTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeVarInt32IndexedOffsetArrayToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(VarInt32IndexedOffsetArray varint32IndexedOffsetArray(reader), zserio::CppRuntimeException);
}

TEST_F(VarInt32IndexedOffsetArrayTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    const size_t expectedBitSize = getVarInt32IndexedOffsetArrayBitSize();
    ASSERT_EQ(expectedBitSize, varint32IndexedOffsetArray.bitSizeOf());
}

TEST_F(VarInt32IndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = getVarInt32IndexedOffsetArrayBitSize() - bitPosition;
    ASSERT_EQ(expectedBitSize, varint32IndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(VarInt32IndexedOffsetArrayTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = getVarInt32IndexedOffsetArrayBitSize();
    ASSERT_EQ(expectedBitSize, varint32IndexedOffsetArray.initializeOffsets(bitPosition));
    checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
}

TEST_F(VarInt32IndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = getVarInt32IndexedOffsetArrayBitSize() + bitPosition - 1;
    ASSERT_EQ(expectedBitSize, varint32IndexedOffsetArray.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(varint32IndexedOffsetArray, offsetShift);
}

TEST_F(VarInt32IndexedOffsetArrayTest, write)
{
    const bool createWrongOffsets = false;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    varint32IndexedOffsetArray.write(writer);
    checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    VarInt32IndexedOffsetArray readVarInt32IndexedOffsetArray(reader);
    checkVarInt32IndexedOffsetArray(readVarInt32IndexedOffsetArray);
    ASSERT_TRUE(varint32IndexedOffsetArray == readVarInt32IndexedOffsetArray);
}

TEST_F(VarInt32IndexedOffsetArrayTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    const size_t bitPosition = 8;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    varint32IndexedOffsetArray.initializeOffsets(writer.getBitPosition());
    varint32IndexedOffsetArray.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(varint32IndexedOffsetArray, offsetShift);
}

TEST_F(VarInt32IndexedOffsetArrayTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    VarInt32IndexedOffsetArray varint32IndexedOffsetArray;
    fillVarInt32IndexedOffsetArray(varint32IndexedOffsetArray, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(varint32IndexedOffsetArray.write(writer), zserio::CppRuntimeException);
}

} // namespace varint32_indexed_offset_array
} // namespace indexed_offsets
