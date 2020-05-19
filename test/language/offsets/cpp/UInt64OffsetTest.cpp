#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"

#include "offsets/uint64_offset/UInt64Offset.h"

using namespace zserio;

namespace offsets
{
namespace uint64_offset
{

class UInt64OffsetTest : public ::testing::Test
{
protected:
    void prepare(BitStreamWriter& writer, bool wrongOffset)
    {
        // offset
        writer.writeBits64((wrongOffset ? WRONG_OFFSET : OFFSET), 64);
        writer.writeVarSize(static_cast<uint32_t>(ARRAY_SIZE));
        for (size_t i = 0; i < ARRAY_SIZE; ++i)
        {
            writer.writeSignedBits(0, 8);
        }
        writer.writeSignedBits(0, 32);
    }

    static const size_t ARRAY_SIZE;
    static const size_t OFFSET;
    static const size_t WRONG_OFFSET;
    static const size_t BIT_SIZE;
};

const size_t UInt64OffsetTest::ARRAY_SIZE = 13;
const size_t UInt64OffsetTest::OFFSET = 8 +
        bitsToBytes(bitSizeOfVarUInt64(ARRAY_SIZE)) +
        ARRAY_SIZE;
const size_t UInt64OffsetTest::WRONG_OFFSET = OFFSET + 1;
const size_t UInt64OffsetTest::BIT_SIZE = bytesToBits(OFFSET + 4);

TEST_F(UInt64OffsetTest, read)
{
    BitStreamWriter writer;
    prepare(writer, false);
    size_t bufferSize;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);
    BitStreamReader reader(buffer, bufferSize);

    UInt64Offset uint64Offset;
    uint64Offset.read(reader);
    ASSERT_EQ(OFFSET, uint64Offset.getOffset());
}

TEST_F(UInt64OffsetTest, readWrongOffsets)
{
    BitStreamWriter writer;
    prepare(writer, true);
    size_t bufferSize;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);
    BitStreamReader reader(buffer, bufferSize);

    UInt64Offset uint64Offset;
    ASSERT_THROW(uint64Offset.read(reader), CppRuntimeException);
}

TEST_F(UInt64OffsetTest, bitSizeOf)
{
    BitStreamWriter writer;
    prepare(writer, false);
    size_t bufferSize;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);
    BitStreamReader reader(buffer, bufferSize);

    UInt64Offset uint64Offset(reader);
    ASSERT_EQ(BIT_SIZE, uint64Offset.bitSizeOf());
}

TEST_F(UInt64OffsetTest, bitSizeOfWithPosition)
{
    BitStreamWriter writer;
    prepare(writer, false);
    size_t bufferSize;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);
    BitStreamReader reader(buffer, bufferSize);

    UInt64Offset uint64Offset(reader);
    ASSERT_EQ(BIT_SIZE + 5, uint64Offset.bitSizeOf(3));
}

TEST_F(UInt64OffsetTest, initializeOffsets)
{
    UInt64Offset uint64Offset;
    uint64Offset.getArray().resize(ARRAY_SIZE);
    uint64Offset.initializeOffsets(0);
    ASSERT_EQ(OFFSET, uint64Offset.getOffset());
}

TEST_F(UInt64OffsetTest, initializeOffsetsWithPosition)
{
    UInt64Offset uint64Offset;
    uint64Offset.getArray().resize(ARRAY_SIZE);
    uint64Offset.initializeOffsets(3);
    // 3 bits start position + 5 bits alignment -> + 1 byte
    ASSERT_EQ(OFFSET + 1, uint64Offset.getOffset());
}

TEST_F(UInt64OffsetTest, write)
{
    UInt64Offset uint64Offset;
    uint64Offset.getArray().resize(ARRAY_SIZE);
    BitStreamWriter writer;
    uint64Offset.write(writer);
    ASSERT_EQ(OFFSET, uint64Offset.getOffset());
    size_t bufferSize;
    writer.getWriteBuffer(bufferSize);
    ASSERT_EQ(bitsToBytes(BIT_SIZE), bufferSize);
}

TEST_F(UInt64OffsetTest, writeWithPosition)
{
    UInt64Offset uint64Offset;
    uint64Offset.getArray().resize(ARRAY_SIZE);
    BitStreamWriter writer;
    writer.writeBits(0, 3);
    uint64Offset.write(writer);
    ASSERT_EQ(OFFSET + 1, uint64Offset.getOffset());
    size_t bufferSize;
    writer.getWriteBuffer(bufferSize);
    ASSERT_EQ(bitsToBytes(BIT_SIZE) + 1, bufferSize);
}

TEST_F(UInt64OffsetTest, writeWrongOffsets)
{
    UInt64Offset uint64Offset;
    uint64Offset.getArray().resize(ARRAY_SIZE);
    uint64Offset.setOffset(WRONG_OFFSET);
    BitStreamWriter writer;
    ASSERT_THROW(uint64Offset.write(writer, NO_PRE_WRITE_ACTION), CppRuntimeException);
}

} // namespace uint64_offset
} // namespace offsets



