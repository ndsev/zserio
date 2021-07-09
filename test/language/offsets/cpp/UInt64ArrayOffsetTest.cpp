#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/BitSizeOfCalculator.h"

#include "offsets/uint64_array_offset/UInt64ArrayOffset.h"

using namespace zserio;

namespace offsets
{
namespace uint64_array_offset
{

class UInt64ArrayOffsetTest : public ::testing::Test
{
protected:
    void prepare(BitStreamWriter& writer, bool wrongOffset)
    {
        // offset
        writer.writeVarSize(static_cast<uint32_t>(VALUES_SIZE));
        for (size_t i = 0; i < VALUES_SIZE; ++i)
        {
            const uint64_t offset = FIRST_OFFSET + i * 4 + (wrongOffset && i == VALUES_SIZE - 1 ? 1 : 0);
            writer.writeBits64(offset, 64);
        }
        // array
        writer.writeVarSize(static_cast<uint32_t>(ARRAY_SIZE));
        for (size_t i = 0; i < ARRAY_SIZE; ++i)
        {
            writer.writeBits(0, 8);
        }
        // values
        writer.writeVarSize(static_cast<uint32_t>(VALUES_SIZE));
        for (size_t i = 0; i < VALUES_SIZE; ++i)
        {
            writer.writeBits(0, 32);
        }
    }

    static const size_t ARRAY_SIZE;
    static const size_t VALUES_SIZE;
    static const size_t FIRST_OFFSET;
    static const size_t BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t UInt64ArrayOffsetTest::ARRAY_SIZE = 13;
const size_t UInt64ArrayOffsetTest::VALUES_SIZE = 42;
const size_t UInt64ArrayOffsetTest::FIRST_OFFSET =
        bitsToBytes(bitSizeOfVarUInt64(VALUES_SIZE)) +
        8 * VALUES_SIZE +
        bitsToBytes(bitSizeOfVarUInt64(ARRAY_SIZE)) +
        ARRAY_SIZE +
        bitsToBytes(bitSizeOfVarUInt64(VALUES_SIZE));
const size_t UInt64ArrayOffsetTest::BIT_SIZE = bytesToBits(FIRST_OFFSET + 4 * VALUES_SIZE);

TEST_F(UInt64ArrayOffsetTest, readConstructor)
{
    BitStreamWriter writer(bitBuffer);
    prepare(writer, false);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UInt64ArrayOffset uint64ArrayOffset(reader);
    ASSERT_EQ(FIRST_OFFSET, uint64ArrayOffset.getOffsets().at(0));
}

TEST_F(UInt64ArrayOffsetTest, readConstructorWrongOffsets)
{
    BitStreamWriter writer(bitBuffer);
    prepare(writer, true);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(UInt64ArrayOffset uint64ArrayOffset(reader), CppRuntimeException);
}

TEST_F(UInt64ArrayOffsetTest, bitSizeOf)
{
    BitStreamWriter writer(bitBuffer);
    prepare(writer, false);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UInt64ArrayOffset uint64ArrayOffset(reader);
    ASSERT_EQ(BIT_SIZE, uint64ArrayOffset.bitSizeOf());
}

TEST_F(UInt64ArrayOffsetTest, bitSizeOfWithPosition)
{
    BitStreamWriter writer(bitBuffer);
    prepare(writer, false);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UInt64ArrayOffset uint64ArrayOffset(reader);
    ASSERT_EQ(BIT_SIZE + 5, uint64ArrayOffset.bitSizeOf(3));
}

TEST_F(UInt64ArrayOffsetTest, initializeOffsets)
{
    UInt64ArrayOffset uint64ArrayOffset;
    uint64ArrayOffset.getOffsets().resize(VALUES_SIZE);
    uint64ArrayOffset.getArray().resize(ARRAY_SIZE);
    uint64ArrayOffset.getValues().resize(VALUES_SIZE);
    uint64ArrayOffset.initializeOffsets(0);
    ASSERT_EQ(FIRST_OFFSET, uint64ArrayOffset.getOffsets().at(0));
}

TEST_F(UInt64ArrayOffsetTest, initializeOffsetsWithPosition)
{
    UInt64ArrayOffset uint64ArrayOffset;
    uint64ArrayOffset.getOffsets().resize(VALUES_SIZE);
    uint64ArrayOffset.getArray().resize(ARRAY_SIZE);
    uint64ArrayOffset.getValues().resize(VALUES_SIZE);
    uint64ArrayOffset.initializeOffsets(3);
    // 3 bits start position + 5 bits alignment -> + 1 byte
    ASSERT_EQ(FIRST_OFFSET + 1, uint64ArrayOffset.getOffsets().at(0));
}

TEST_F(UInt64ArrayOffsetTest, write)
{
    UInt64ArrayOffset uint64ArrayOffset;
    uint64ArrayOffset.getOffsets().resize(VALUES_SIZE);
    uint64ArrayOffset.getArray().resize(ARRAY_SIZE);
    uint64ArrayOffset.getValues().resize(VALUES_SIZE);
    BitStreamWriter writer(bitBuffer);
    uint64ArrayOffset.write(writer);
    ASSERT_EQ(FIRST_OFFSET, uint64ArrayOffset.getOffsets().at(0));
    ASSERT_EQ(BIT_SIZE, writer.getBitPosition());
}

TEST_F(UInt64ArrayOffsetTest, writeWithPosition)
{
    UInt64ArrayOffset uint64ArrayOffset;
    uint64ArrayOffset.getOffsets().resize(VALUES_SIZE);
    uint64ArrayOffset.getArray().resize(ARRAY_SIZE);
    uint64ArrayOffset.getValues().resize(VALUES_SIZE);
    BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, 3);
    uint64ArrayOffset.write(writer);
    ASSERT_EQ(FIRST_OFFSET + 1, uint64ArrayOffset.getOffsets().at(0));
    ASSERT_EQ(BIT_SIZE + 8, writer.getBitPosition());
}

TEST_F(UInt64ArrayOffsetTest, writeWrongOffsets)
{
    UInt64ArrayOffset uint64ArrayOffset;
    auto& offsets = uint64ArrayOffset.getOffsets();
    for (size_t i = 0; i < VALUES_SIZE; ++i)
        offsets.push_back(FIRST_OFFSET + i * 4 + (i == VALUES_SIZE - 1 ? 1 : 0));
    uint64ArrayOffset.getArray().resize(ARRAY_SIZE);
    uint64ArrayOffset.getValues().resize(VALUES_SIZE);

    BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(uint64ArrayOffset.write(writer, NO_PRE_WRITE_ACTION), CppRuntimeException);
}

} // namespace uint64_array_offset
} // namespace offsets
