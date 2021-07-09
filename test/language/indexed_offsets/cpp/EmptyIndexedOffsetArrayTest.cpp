#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/empty_indexed_offset_array/EmptyIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace empty_indexed_offset_array
{

class EmptyIndexedOffsetArrayTest : public ::testing::Test
{
protected:
    void writeEmptyIndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(SPACER_VALUE, 1);
        writer.writeBits(FIELD_VALUE, 6);
    }

    void checkEmptyIndexedOffsetArray(const EmptyIndexedOffsetArray& emptyIndexedOffsetArray)
    {
        const auto& offsets = emptyIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());

        const uint8_t expectedSpacer = SPACER_VALUE;
        ASSERT_EQ(expectedSpacer, emptyIndexedOffsetArray.getSpacer());

        const uint8_t expectedField = FIELD_VALUE;
        ASSERT_EQ(expectedField, emptyIndexedOffsetArray.getField());

        const auto& data = emptyIndexedOffsetArray.getData();
        ASSERT_EQ(expectedNumElements, data.size());
    }

    void fillEmptyIndexedOffsetArray(EmptyIndexedOffsetArray& emptyIndexedOffsetArray)
    {
        emptyIndexedOffsetArray.setSpacer(SPACER_VALUE);
        emptyIndexedOffsetArray.setField(FIELD_VALUE);
    }

    static const uint8_t    NUM_ELEMENTS = 0;

    static const uint8_t    SPACER_VALUE = 1;
    static const uint8_t    FIELD_VALUE = 63;

    static const size_t     EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE = 1 + 6;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(EmptyIndexedOffsetArrayTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeEmptyIndexedOffsetArrayToByteArray(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EmptyIndexedOffsetArray emptyIndexedOffsetArray(reader);
    checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
}

TEST_F(EmptyIndexedOffsetArrayTest, bitSizeOf)
{
    EmptyIndexedOffsetArray emptyIndexedOffsetArray;
    fillEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

    const size_t expectedBitSize = EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, emptyIndexedOffsetArray.bitSizeOf());
}

TEST_F(EmptyIndexedOffsetArrayTest, bitSizeOfWithPosition)
{
    EmptyIndexedOffsetArray emptyIndexedOffsetArray;
    fillEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

    const size_t bitPosition = 1;
    const size_t expectedBitSize = EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, emptyIndexedOffsetArray.bitSizeOf(bitPosition));
}

TEST_F(EmptyIndexedOffsetArrayTest, initializeOffsets)
{
    EmptyIndexedOffsetArray emptyIndexedOffsetArray;
    fillEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, emptyIndexedOffsetArray.initializeOffsets(bitPosition));
    checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
}

TEST_F(EmptyIndexedOffsetArrayTest, initializeOffsetsWithPosition)
{
    EmptyIndexedOffsetArray emptyIndexedOffsetArray;
    fillEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

    const size_t bitPosition = 9;
    const size_t expectedBitSize = EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition;
    ASSERT_EQ(expectedBitSize, emptyIndexedOffsetArray.initializeOffsets(bitPosition));
    checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
}

TEST_F(EmptyIndexedOffsetArrayTest, write)
{
    EmptyIndexedOffsetArray emptyIndexedOffsetArray;
    fillEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

    zserio::BitStreamWriter writer(bitBuffer);
    emptyIndexedOffsetArray.write(writer);
    checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EmptyIndexedOffsetArray readEmptyIndexedOffsetArray(reader);
    checkEmptyIndexedOffsetArray(readEmptyIndexedOffsetArray);
    ASSERT_TRUE(emptyIndexedOffsetArray == readEmptyIndexedOffsetArray);
}

} // namespace empty_indexed_offset_array
} // namespace indexed_offsets
