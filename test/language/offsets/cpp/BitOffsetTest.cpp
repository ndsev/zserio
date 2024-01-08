#include "gtest/gtest.h"
#include "offsets/bit_offset/BitOffset.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace offsets
{
namespace bit_offset
{

class BitOffsetTest : public ::testing::Test
{
protected:
    void writeBitOffsetToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffsets)
    {
        if (writeWrongOffsets)
        {
            writer.writeBits(WRONG_FIELD1_OFFSET, 8);
            writer.writeBits(WRONG_FIELD2_OFFSET, 16);
            writer.writeBits(WRONG_FIELD3_OFFSET, 32);
            writer.writeBits(WRONG_FIELD4_OFFSET, 8);
            writer.writeBits(WRONG_FIELD5_OFFSET, 15);
            writer.writeBits(WRONG_FIELD6_OFFSET, 18);
            writer.writeBits(WRONG_FIELD7_OFFSET, 23);
            writer.writeBits(WRONG_FIELD8_OFFSET, 8);
        }
        else
        {
            writer.writeBits(FIELD1_OFFSET, 8);
            writer.writeBits(FIELD2_OFFSET, 16);
            writer.writeBits(FIELD3_OFFSET, 32);
            writer.writeBits(FIELD4_OFFSET, 8);
            writer.writeBits(FIELD5_OFFSET, 15);
            writer.writeBits(FIELD6_OFFSET, 18);
            writer.writeBits(FIELD7_OFFSET, 23);
            writer.writeBits(FIELD8_OFFSET, 8);
        }

        writer.writeBits(FIELD1_VALUE, 1);

        writer.writeBits(0, 7);
        writer.writeBits(FIELD2_VALUE, 2);

        writer.writeBits(0, 6);
        writer.writeBits(FIELD3_VALUE, 3);

        writer.writeBits(0, 5);
        writer.writeBits(FIELD4_VALUE, 4);

        writer.writeBits(0, 4);
        writer.writeBits(FIELD5_VALUE, 5);

        writer.writeBits(0, 3);
        writer.writeBits(FIELD6_VALUE, 6);

        writer.writeBits(0, 2);
        writer.writeBits(FIELD7_VALUE, 7);

        writer.writeBits(0, 1);
        writer.writeBits(FIELD8_VALUE, 8);
    }

    void checkOffsets(const BitOffset& bitOffset, uint16_t offsetShift)
    {
        ASSERT_EQ(FIELD1_OFFSET + offsetShift, bitOffset.getField1Offset());
        ASSERT_EQ(FIELD2_OFFSET + offsetShift, bitOffset.getField2Offset());
        ASSERT_EQ(FIELD3_OFFSET + offsetShift, bitOffset.getField3Offset());
        ASSERT_EQ(FIELD4_OFFSET + offsetShift, bitOffset.getField4Offset());
        ASSERT_EQ(FIELD5_OFFSET + offsetShift, bitOffset.getField5Offset());
        ASSERT_EQ(FIELD6_OFFSET + offsetShift, bitOffset.getField6Offset());
        ASSERT_EQ(FIELD7_OFFSET + offsetShift, bitOffset.getField7Offset());
        ASSERT_EQ(FIELD8_OFFSET + offsetShift, bitOffset.getField8Offset());
    }

    void checkBitOffset(const BitOffset& bitOffset)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(bitOffset, offsetShift);

        uint8_t expectedValue = FIELD1_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField1());

        expectedValue = FIELD2_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField2());

        expectedValue = FIELD3_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField3());

        expectedValue = FIELD4_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField4());

        expectedValue = FIELD5_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField5());

        expectedValue = FIELD6_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField6());

        expectedValue = FIELD7_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField7());

        expectedValue = FIELD8_VALUE;
        ASSERT_EQ(expectedValue, bitOffset.getField8());
    }

    void fillBitOffset(BitOffset& bitOffset, bool createWrongOffsets)
    {
        if (createWrongOffsets)
        {
            bitOffset.setField1Offset(WRONG_FIELD1_OFFSET);
            bitOffset.setField2Offset(WRONG_FIELD2_OFFSET);
            bitOffset.setField3Offset(WRONG_FIELD3_OFFSET);
            bitOffset.setField4Offset(WRONG_FIELD4_OFFSET);
            bitOffset.setField5Offset(WRONG_FIELD5_OFFSET);
            bitOffset.setField6Offset(WRONG_FIELD6_OFFSET);
            bitOffset.setField7Offset(WRONG_FIELD7_OFFSET);
            bitOffset.setField8Offset(WRONG_FIELD8_OFFSET);
        }
        else
        {
            bitOffset.setField1Offset(FIELD1_OFFSET);
            bitOffset.setField2Offset(FIELD2_OFFSET);
            bitOffset.setField3Offset(FIELD3_OFFSET);
            bitOffset.setField4Offset(FIELD4_OFFSET);
            bitOffset.setField5Offset(FIELD5_OFFSET);
            bitOffset.setField6Offset(FIELD6_OFFSET);
            bitOffset.setField7Offset(FIELD7_OFFSET);
            bitOffset.setField8Offset(FIELD8_OFFSET);
        }

        bitOffset.setField1(FIELD1_VALUE);
        bitOffset.setField2(FIELD2_VALUE);
        bitOffset.setField3(FIELD3_VALUE);
        bitOffset.setField4(FIELD4_VALUE);
        bitOffset.setField5(FIELD5_VALUE);
        bitOffset.setField6(FIELD6_VALUE);
        bitOffset.setField7(FIELD7_VALUE);
        bitOffset.setField8(FIELD8_VALUE);
    }

    static const size_t BIT_OFFSET_BIT_SIZE = 192;

    static const uint8_t WRONG_FIELD1_OFFSET = 0;
    static const uint16_t WRONG_FIELD2_OFFSET = 0;
    static const uint32_t WRONG_FIELD3_OFFSET = 0;
    static const uint8_t WRONG_FIELD4_OFFSET = 0;
    static const uint16_t WRONG_FIELD5_OFFSET = 0;
    static const uint32_t WRONG_FIELD6_OFFSET = 0;
    static const uint32_t WRONG_FIELD7_OFFSET = 0;
    static const uint8_t WRONG_FIELD8_OFFSET = 0;

    static const uint8_t FIELD1_OFFSET = 16;
    static const uint16_t FIELD2_OFFSET = 17;
    static const uint32_t FIELD3_OFFSET = 18;
    static const uint8_t FIELD4_OFFSET = 19;
    static const uint16_t FIELD5_OFFSET = 20;
    static const uint32_t FIELD6_OFFSET = 21;
    static const uint32_t FIELD7_OFFSET = 22;
    static const uint8_t FIELD8_OFFSET = 23;

    static const uint8_t FIELD1_VALUE = 1;
    static const uint8_t FIELD2_VALUE = 2;
    static const uint8_t FIELD3_VALUE = 5;
    static const uint8_t FIELD4_VALUE = 13;
    static const uint8_t FIELD5_VALUE = 26;
    static const uint8_t FIELD6_VALUE = 56;
    static const uint8_t FIELD7_VALUE = 88;
    static const uint8_t FIELD8_VALUE = 222;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(BitOffsetTest, readConstructor)
{
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBitOffsetToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BitOffset bitOffset(reader);
    checkBitOffset(bitOffset);
}

TEST_F(BitOffsetTest, readConstructorWrongOffsets)
{
    const bool writeWrongOffsets = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBitOffsetToByteArray(writer, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(BitOffset bitOffset(reader), zserio::CppRuntimeException);
}

TEST_F(BitOffsetTest, bitSizeOf)
{
    const bool createWrongOffsets = false;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    const size_t expectedBitSize = BIT_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, bitOffset.bitSizeOf());
}

TEST_F(BitOffsetTest, bitSizeOfWithPosition)
{
    const bool createWrongOffsets = false;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    const size_t expectedBitSize = BIT_OFFSET_BIT_SIZE + 7;
    const size_t bitPosition = 1;
    ASSERT_EQ(expectedBitSize, bitOffset.bitSizeOf(bitPosition));
}

TEST_F(BitOffsetTest, initializeOffsets)
{
    const bool createWrongOffsets = true;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = BIT_OFFSET_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, bitOffset.initializeOffsets(bitPosition));
    checkBitOffset(bitOffset);
}

TEST_F(BitOffsetTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffsets = true;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    const size_t expectedBitSize = BIT_OFFSET_BIT_SIZE + bitPosition + 6;
    ASSERT_EQ(expectedBitSize, bitOffset.initializeOffsets(bitPosition));

    const uint16_t offsetShift = 1;
    checkOffsets(bitOffset, offsetShift);
}

TEST_F(BitOffsetTest, write)
{
    const bool createWrongOffsets = false;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    bitOffset.write(writer);
    checkBitOffset(bitOffset);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BitOffset readBitOffset(reader);
    checkBitOffset(readBitOffset);
    ASSERT_TRUE(bitOffset == readBitOffset);
}

TEST_F(BitOffsetTest, writeWithPosition)
{
    const bool createWrongOffsets = true;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    const size_t bitPosition = 2;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    bitOffset.initializeOffsets(writer.getBitPosition());
    bitOffset.write(writer);

    const uint16_t offsetShift = 1;
    checkOffsets(bitOffset, offsetShift);
}

TEST_F(BitOffsetTest, writeWrongOffsets)
{
    const bool createWrongOffsets = true;
    BitOffset bitOffset;
    fillBitOffset(bitOffset, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(bitOffset.write(writer), zserio::CppRuntimeException);
}

} // namespace bit_offset
} // namespace offsets
