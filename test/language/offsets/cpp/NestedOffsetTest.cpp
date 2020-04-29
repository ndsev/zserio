#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "offsets/nested_offset/NestedOffset.h"

namespace offsets
{
namespace nested_offset
{

class NestedOffsetTest : public ::testing::Test
{
protected:
    void writeNestedOffsetToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
    {
        const uint32_t wrongTerminatorOffset = WRONG_TERMINATOR_OFFSET;
        const uint32_t correctTerminatorOffset = TERMINATOR_OFFSET;
        writer.writeBits((writeWrongOffsets) ? wrongTerminatorOffset : correctTerminatorOffset, 32);
        writer.writeBool(BOOL_VALUE);
        writer.writeVarUInt64(NestedOffsetUnion::CHOICE_nestedOffsetArrayStructure); // union's choice tag
        writer.writeBits(NUM_ELEMENTS, 8);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits((writeWrongOffsets) ? WRONG_DATA_OFFSET : FIRST_DATA_OFFSET + i * 8, 32);
            writer.writeBits(0, (i == 0) ? 7 : 1);
            writer.writeBits(i, 31);
        }

        writer.writeBits(TERMINATOR_VALUE, 7);
    }

    void checkNestedOffset(const NestedOffset& nestedOffset)
    {
        const uint32_t expectedTerminatorOffset = TERMINATOR_OFFSET;
        ASSERT_EQ(expectedTerminatorOffset, nestedOffset.getTerminatorOffset());
        const bool expectedBoolValue = BOOL_VALUE;
        ASSERT_EQ(expectedBoolValue, nestedOffset.getBoolValue());

        const NestedOffsetChoice& nestedOffsetChoice = nestedOffset.getNestedOffsetChoice();
        ASSERT_EQ(expectedBoolValue, nestedOffsetChoice.getType());

        const NestedOffsetUnion& nestedOffsetUnion = nestedOffsetChoice.getNestedOffsetUnion();
        ASSERT_EQ(NestedOffsetUnion::CHOICE_nestedOffsetArrayStructure, nestedOffsetUnion.choiceTag());

        const NestedOffsetArrayStructure& nestedOffsetArrayStructure =
                nestedOffsetUnion.getNestedOffsetArrayStructure();
        const uint8_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, nestedOffsetArrayStructure.getNumElements());

        const std::vector<NestedOffsetStructure>& nestedOffsetStructureList =
                nestedOffsetArrayStructure.getNestedOffsetStructureList();
        ASSERT_EQ(expectedNumElements, nestedOffsetStructureList.size());
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            const NestedOffsetStructure& nestedOffsetStructure = nestedOffsetStructureList[i];
            ASSERT_EQ(FIRST_DATA_OFFSET + i * 8L, nestedOffsetStructure.getDataOffset());
            ASSERT_EQ(i, nestedOffsetStructure.getData());
        }
    }

    void fillNestedOffset(NestedOffset& nestedOffset, bool createWrongOffsets)
    {
        const uint32_t wrongTerminatorOffset = WRONG_TERMINATOR_OFFSET;
        const uint32_t correctTerminatorOffset = TERMINATOR_OFFSET;
        const uint32_t terminatorOffset = (createWrongOffsets)
                ? wrongTerminatorOffset : correctTerminatorOffset;
        nestedOffset.setTerminatorOffset(terminatorOffset);
        nestedOffset.setBoolValue(BOOL_VALUE);
        NestedOffsetChoice& nestedOffsetChoice = nestedOffset.getNestedOffsetChoice();
        fillNestedOffsetChoice(nestedOffsetChoice, createWrongOffsets);
        nestedOffset.setTerminator(TERMINATOR_VALUE);
        nestedOffset.initializeChildren();
    }

    void fillNestedOffsetChoice(NestedOffsetChoice& nestedOffsetChoice, bool createWrongOffsets)
    {
        NestedOffsetUnion nestedOffsetUnion;
        NestedOffsetArrayStructure nestedOffsetArrayStructure;
        nestedOffsetArrayStructure.setNumElements(NUM_ELEMENTS);
        std::vector<NestedOffsetStructure>& nestedOffsetStructureList =
                nestedOffsetArrayStructure.getNestedOffsetStructureList();
        nestedOffsetStructureList.reserve(NUM_ELEMENTS);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            const uint32_t dataOffset = (createWrongOffsets) ? WRONG_DATA_OFFSET : FIRST_DATA_OFFSET + i * 8;
            NestedOffsetStructure nestedOffsetStructure;
            nestedOffsetStructure.setDataOffset(dataOffset);
            nestedOffsetStructure.setData(i);
            nestedOffsetStructureList.push_back(nestedOffsetStructure);
        }
        nestedOffsetUnion.setNestedOffsetArrayStructure(nestedOffsetArrayStructure);
        nestedOffsetChoice.setNestedOffsetUnion(nestedOffsetUnion);
    }

    static const bool     BOOL_VALUE = true;
    static const uint8_t  NUM_ELEMENTS = 2;

    static const uint32_t WRONG_TERMINATOR_OFFSET = 0;
    static const uint32_t TERMINATOR_OFFSET = 7 + NUM_ELEMENTS * 8;

    static const uint32_t WRONG_DATA_OFFSET = 0;
    static const uint32_t FIRST_DATA_OFFSET = 7 + 4;

    static const uint8_t  TERMINATOR_VALUE = 0x45;

    static const size_t   NEST_OFFSET_BIT_SIZE = TERMINATOR_OFFSET * 8 + 7;
};

TEST_F(NestedOffsetTest, read)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer;
    writeNestedOffsetToByteArray(writer, writeWrongOffsets);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    NestedOffset nestedOffset(reader);
    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, readWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer;
    writeNestedOffsetToByteArray(writer, writeWrongOffsets);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    EXPECT_THROW(NestedOffset nestedOffset(reader), zserio::CppRuntimeException);
}

TEST_F(NestedOffsetTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t expectedBitSize = NEST_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, nestedOffset.bitSizeOf());
}

TEST_F(NestedOffsetTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    const size_t expectedBitSize = NEST_OFFSET_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, nestedOffset.bitSizeOf(bitPosition));
}

TEST_F(NestedOffsetTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = NEST_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, nestedOffset.initializeOffsets(bitPosition));
    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    const size_t expectedBitSize = NEST_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, nestedOffset.initializeOffsets(bitPosition));
    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, write)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    zserio::BitStreamWriter writer;
    nestedOffset.write(writer);
    checkNestedOffset(nestedOffset);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    NestedOffset readNestedOffset(reader);
    checkNestedOffset(readNestedOffset);
    ASSERT_TRUE(nestedOffset == readNestedOffset);
}

TEST_F(NestedOffsetTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    zserio::BitStreamWriter writer;
    const size_t bitPosition = 2;
    writer.writeBits(0, bitPosition);
    nestedOffset.write(writer);

    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(nestedOffset.write(writer, zserio::NO_PRE_WRITE_ACTION), zserio::CppRuntimeException);
}

} // namespace nested_offset
} // namespace offsets
