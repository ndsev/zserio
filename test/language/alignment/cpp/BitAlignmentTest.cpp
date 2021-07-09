#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "alignment/bit_alignment/BitAlignment.h"

namespace alignment
{
namespace bit_alignment
{

class BitAlignmentTest : public ::testing::Test
{
protected:
    void writeBitAlignmentToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(ALIGNED1_FIELD_VALUE, 1);

        writer.writeBits(0, 1);
        writer.writeBits(ALIGNED2_FIELD_VALUE, 2);

        writer.writeBits(0, 2);
        writer.writeBits(ALIGNED3_FIELD_VALUE, 3);

        writer.writeBits(0, 3);
        writer.writeBits(ALIGNED4_FIELD_VALUE, 4);

        writer.writeBits(0, 4);
        writer.writeBits(ALIGNED5_FIELD_VALUE, 5);

        writer.writeBits(0, 5);
        writer.writeBits(ALIGNED6_FIELD_VALUE, 6);

        writer.writeBits(0, 6);
        writer.writeBits(ALIGNED7_FIELD_VALUE, 7);

        writer.writeBits(0, 7);
        writer.writeBits(ALIGNED8_FIELD_VALUE, 8);

        writer.writeBits(0, 1);
        writer.writeBits(0, 15);
        writer.writeBits(ALIGNED16_FIELD_VALUE, 16);

        writer.writeBits(0, 1);
        writer.writeBits(0, 31);
        writer.writeBits(ALIGNED32_FIELD_VALUE, 32);

        writer.writeBits64(0, 33);
        writer.writeBits64(0, 63);
        writer.writeBits64(ALIGNED64_FIELD_VALUE, 64);
    }

    void checkBitAlignment(const BitAlignment& bitAlignment)
    {
        ASSERT_EQ(ALIGNED1_FIELD_VALUE, bitAlignment.getAligned1Field());
        ASSERT_EQ(ALIGNED2_FIELD_VALUE, bitAlignment.getAligned2Field());
        ASSERT_EQ(ALIGNED3_FIELD_VALUE, bitAlignment.getAligned3Field());
        ASSERT_EQ(ALIGNED4_FIELD_VALUE, bitAlignment.getAligned4Field());
        ASSERT_EQ(ALIGNED5_FIELD_VALUE, bitAlignment.getAligned5Field());
        ASSERT_EQ(ALIGNED6_FIELD_VALUE, bitAlignment.getAligned6Field());
        ASSERT_EQ(ALIGNED7_FIELD_VALUE, bitAlignment.getAligned7Field());
        ASSERT_EQ(ALIGNED8_FIELD_VALUE, bitAlignment.getAligned8Field());
        ASSERT_EQ(ALIGNMENT16_BREAK, bitAlignment.getAlignment16Break());
        ASSERT_EQ(ALIGNED16_FIELD_VALUE, bitAlignment.getAligned16Field());
        ASSERT_EQ(ALIGNMENT32_BREAK, bitAlignment.getAlignment32Break());
        ASSERT_EQ(ALIGNED32_FIELD_VALUE, bitAlignment.getAligned32Field());
        ASSERT_EQ(ALIGNMENT64_BREAK, bitAlignment.getAlignment64Break());
        ASSERT_EQ(ALIGNED64_FIELD_VALUE, bitAlignment.getAligned64Field());
    }

    void fillBitAlignment(BitAlignment& bitAlignment)
    {
        bitAlignment.setAligned1Field(ALIGNED1_FIELD_VALUE);
        bitAlignment.setAligned2Field(ALIGNED2_FIELD_VALUE);
        bitAlignment.setAligned3Field(ALIGNED3_FIELD_VALUE);
        bitAlignment.setAligned4Field(ALIGNED4_FIELD_VALUE);
        bitAlignment.setAligned5Field(ALIGNED5_FIELD_VALUE);
        bitAlignment.setAligned6Field(ALIGNED6_FIELD_VALUE);
        bitAlignment.setAligned7Field(ALIGNED7_FIELD_VALUE);
        bitAlignment.setAligned8Field(ALIGNED8_FIELD_VALUE);
        bitAlignment.setAlignment16Break(ALIGNMENT16_BREAK);
        bitAlignment.setAligned16Field(ALIGNED16_FIELD_VALUE);
        bitAlignment.setAlignment32Break(ALIGNMENT32_BREAK);
        bitAlignment.setAligned32Field(ALIGNED32_FIELD_VALUE);
        bitAlignment.setAlignment64Break(ALIGNMENT64_BREAK);
        bitAlignment.setAligned64Field(ALIGNED64_FIELD_VALUE);
    }

    // constants
    static const size_t BIT_ALIGNMENT_BIT_SIZE;

    static const uint8_t ALIGNED1_FIELD_VALUE;
    static const uint8_t ALIGNED2_FIELD_VALUE;
    static const uint8_t ALIGNED3_FIELD_VALUE;
    static const uint8_t ALIGNED4_FIELD_VALUE;
    static const uint8_t ALIGNED5_FIELD_VALUE;
    static const uint8_t ALIGNED6_FIELD_VALUE;
    static const uint8_t ALIGNED7_FIELD_VALUE;
    static const uint8_t ALIGNED8_FIELD_VALUE;
    static const uint8_t ALIGNMENT16_BREAK;
    static const uint16_t ALIGNED16_FIELD_VALUE;
    static const uint8_t ALIGNMENT32_BREAK;
    static const uint32_t ALIGNED32_FIELD_VALUE;
    static const uint64_t ALIGNMENT64_BREAK;
    static const uint64_t ALIGNED64_FIELD_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

// constants definitions
const size_t BitAlignmentTest::BIT_ALIGNMENT_BIT_SIZE = 320;

const uint8_t BitAlignmentTest::ALIGNED1_FIELD_VALUE = 1;
const uint8_t BitAlignmentTest::ALIGNED2_FIELD_VALUE = 2;
const uint8_t BitAlignmentTest::ALIGNED3_FIELD_VALUE = 5;
const uint8_t BitAlignmentTest::ALIGNED4_FIELD_VALUE = 13;
const uint8_t BitAlignmentTest::ALIGNED5_FIELD_VALUE = 26;
const uint8_t BitAlignmentTest::ALIGNED6_FIELD_VALUE = 56;
const uint8_t BitAlignmentTest::ALIGNED7_FIELD_VALUE = 88;
const uint8_t BitAlignmentTest::ALIGNED8_FIELD_VALUE = 222;
const uint8_t BitAlignmentTest::ALIGNMENT16_BREAK = 0;
const uint16_t BitAlignmentTest::ALIGNED16_FIELD_VALUE = 0xcafe;
const uint8_t BitAlignmentTest::ALIGNMENT32_BREAK = 0;
const uint32_t BitAlignmentTest::ALIGNED32_FIELD_VALUE = 0xcafec0de;
const uint64_t BitAlignmentTest::ALIGNMENT64_BREAK = UINT64_C(0);
const uint64_t BitAlignmentTest::ALIGNED64_FIELD_VALUE = UINT64_C(0xcafec0dedeadface);

TEST_F(BitAlignmentTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeBitAlignmentToByteArray(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BitAlignment bitAlignment(reader);
    checkBitAlignment(bitAlignment);
}

TEST_F(BitAlignmentTest, bitSizeOf)
{
    BitAlignment bitAlignment;
    fillBitAlignment(bitAlignment);

    // test default argument
    ASSERT_EQ(BIT_ALIGNMENT_BIT_SIZE, bitAlignment.bitSizeOf());
}

TEST_F(BitAlignmentTest, bitSizeOfWithPosition)
{
    BitAlignment bitAlignment;
    fillBitAlignment(bitAlignment);

    // starting up to bit position 77, the structure still fits into original size thanks to alignments
    size_t startBitPosition = 0;
    for (; startBitPosition < 78; ++startBitPosition)
        ASSERT_EQ(BIT_ALIGNMENT_BIT_SIZE - startBitPosition, bitAlignment.bitSizeOf(startBitPosition));
    // starting at bit position 78, also next 64bits are needed
    ASSERT_EQ(BIT_ALIGNMENT_BIT_SIZE - startBitPosition + 64, bitAlignment.bitSizeOf(startBitPosition));
}

TEST_F(BitAlignmentTest, initializeOffsets)
{
    BitAlignment bitAlignment;
    fillBitAlignment(bitAlignment);

    // starting up to bit position 77, the structure still fits into original size thanks to alignments
    size_t startBitPosition = 0;
    for (; startBitPosition < 78; ++startBitPosition)
        ASSERT_EQ(BIT_ALIGNMENT_BIT_SIZE, bitAlignment.initializeOffsets(startBitPosition));
    // starting at bit position 78, also next 64bits are needed
    ASSERT_EQ(BIT_ALIGNMENT_BIT_SIZE + 64, bitAlignment.initializeOffsets(startBitPosition));
}

TEST_F(BitAlignmentTest, write)
{
    BitAlignment bitAlignment;
    fillBitAlignment(bitAlignment);
    zserio::BitStreamWriter writer(bitBuffer);
    bitAlignment.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BitAlignment readBitAlignment(reader);
    checkBitAlignment(readBitAlignment);
    ASSERT_TRUE(bitAlignment == readBitAlignment);
}

} // namespace bit_alignment
} // namespace alignment
