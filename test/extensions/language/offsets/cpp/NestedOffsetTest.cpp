#include "gtest/gtest.h"
#include "offsets/nested_offset/NestedOffset.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace offsets
{
namespace nested_offset
{

class NestedOffsetTest : public ::testing::Test
{
protected:
    void writeNestedOffsetToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
    {
        writer.writeBits((writeWrongOffsets) ? WRONG_TERMINATOR_OFFSET : TERMINATOR_OFFSET, 32);
        writer.writeBool(BOOL_VALUE);
        // union's choice tag
        writer.writeVarSize(static_cast<uint32_t>(NestedOffsetUnion::CHOICE_nestedOffsetArrayStructure));
        writer.writeBits(NUM_ELEMENTS, 8);
        for (uint32_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits((writeWrongOffsets) ? WRONG_DATA_OFFSET : FIRST_DATA_OFFSET + i * 8, 32);
            writer.writeBits(0, (i == 0) ? 7 : 1);
            writer.writeBits(i, 31);
        }

        writer.alignTo(8);
        writer.writeBits(TERMINATOR_VALUE, 7);
    }

    void checkNestedOffset(const NestedOffset& nestedOffset)
    {
        ASSERT_EQ(TERMINATOR_OFFSET, nestedOffset.getTerminatorOffset());
        ASSERT_EQ(BOOL_VALUE, nestedOffset.getBoolValue());

        const NestedOffsetChoice& nestedOffsetChoice = nestedOffset.getNestedOffsetChoice();
        ASSERT_EQ(BOOL_VALUE, nestedOffsetChoice.getType());

        const NestedOffsetUnion& nestedOffsetUnion = nestedOffsetChoice.getNestedOffsetUnion();
        ASSERT_EQ(NestedOffsetUnion::CHOICE_nestedOffsetArrayStructure, nestedOffsetUnion.choiceTag());

        const NestedOffsetArrayStructure& nestedOffsetArrayStructure =
                nestedOffsetUnion.getNestedOffsetArrayStructure();
        ASSERT_EQ(NUM_ELEMENTS, nestedOffsetArrayStructure.getNumElements());

        const auto& nestedOffsetStructureList = nestedOffsetArrayStructure.getNestedOffsetStructureList();
        ASSERT_EQ(NUM_ELEMENTS, nestedOffsetStructureList.size());
        for (uint32_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            const NestedOffsetStructure& nestedOffsetStructure = nestedOffsetStructureList[i];
            ASSERT_EQ(FIRST_DATA_OFFSET + i * 8, nestedOffsetStructure.getDataOffset());
            ASSERT_EQ(i, nestedOffsetStructure.getData());
        }

        ASSERT_EQ(TERMINATOR_VALUE, nestedOffset.getTerminator());
    }

    void fillNestedOffset(NestedOffset& nestedOffset, bool createWrongOffsets)
    {
        const uint32_t terminatorOffset = (createWrongOffsets) ? WRONG_TERMINATOR_OFFSET : TERMINATOR_OFFSET;
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
        auto& nestedOffsetStructureList = nestedOffsetArrayStructure.getNestedOffsetStructureList();
        nestedOffsetStructureList.reserve(NUM_ELEMENTS);
        for (uint32_t i = 0; i < NUM_ELEMENTS; ++i)
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

    static const bool BOOL_VALUE;
    static const uint8_t NUM_ELEMENTS;

    static const uint32_t WRONG_TERMINATOR_OFFSET;
    static const uint32_t TERMINATOR_OFFSET;

    static const uint32_t WRONG_DATA_OFFSET;
    static const uint32_t FIRST_DATA_OFFSET;

    static const uint8_t TERMINATOR_VALUE;

    static const size_t NESTED_OFFSET_BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const bool NestedOffsetTest::BOOL_VALUE = true;
const uint8_t NestedOffsetTest::NUM_ELEMENTS = 2;

const uint32_t NestedOffsetTest::WRONG_TERMINATOR_OFFSET = 0;
const uint32_t NestedOffsetTest::TERMINATOR_OFFSET = 7 + NUM_ELEMENTS * 8;

const uint32_t NestedOffsetTest::WRONG_DATA_OFFSET = 0;
const uint32_t NestedOffsetTest::FIRST_DATA_OFFSET = 7 + 4;

const uint8_t NestedOffsetTest::TERMINATOR_VALUE = 0x45;

const size_t NestedOffsetTest::NESTED_OFFSET_BIT_SIZE = TERMINATOR_OFFSET * 8 + 7;

TEST_F(NestedOffsetTest, readConstructor)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeNestedOffsetToByteArray(writer, writeWrongOffsets);
    ASSERT_EQ(NESTED_OFFSET_BIT_SIZE, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    NestedOffset nestedOffset(reader);
    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, readConstructorWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeNestedOffsetToByteArray(writer, writeWrongOffsets);
    ASSERT_EQ(NESTED_OFFSET_BIT_SIZE, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(NestedOffset nestedOffset(reader), zserio::CppRuntimeException);
}

TEST_F(NestedOffsetTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    ASSERT_EQ(NESTED_OFFSET_BIT_SIZE, nestedOffset.bitSizeOf());
}

TEST_F(NestedOffsetTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    const size_t expectedBitSize = NESTED_OFFSET_BIT_SIZE - bitPosition;
    ASSERT_EQ(expectedBitSize, nestedOffset.bitSizeOf(bitPosition));
}

TEST_F(NestedOffsetTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitPosition = NESTED_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitPosition, nestedOffset.initializeOffsets(bitPosition));
    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    const size_t expectedBitPosition = NESTED_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitPosition, nestedOffset.initializeOffsets(bitPosition));
    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, write)
{
    const bool createWrongOffsets = false;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    nestedOffset.write(writer);
    ASSERT_EQ(NESTED_OFFSET_BIT_SIZE, writer.getBitPosition());
    checkNestedOffset(nestedOffset);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    NestedOffset readNestedOffset(reader);
    checkNestedOffset(readNestedOffset);
    ASSERT_TRUE(nestedOffset == readNestedOffset);
}

TEST_F(NestedOffsetTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    nestedOffset.initializeOffsets(writer.getBitPosition());
    nestedOffset.write(writer);

    checkNestedOffset(nestedOffset);
}

TEST_F(NestedOffsetTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    NestedOffset nestedOffset;
    fillNestedOffset(nestedOffset, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(nestedOffset.write(writer), zserio::CppRuntimeException);
}

} // namespace nested_offset
} // namespace offsets
