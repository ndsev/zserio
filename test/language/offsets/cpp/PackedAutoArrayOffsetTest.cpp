#include "gtest/gtest.h"
#include "offsets/packed_auto_array_offset/AutoArrayHolder.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace offsets
{
namespace packed_auto_array_offset
{

using allocator_type = AutoArrayHolder::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackedAutoArrayOffsetTest : public ::testing::Test
{
protected:
    void fillAutoArrayHolder(AutoArrayHolder& autoArrayHolder, bool createWrongOffset)
    {
        const uint32_t autoArrayOffset = (createWrongOffset) ? WRONG_AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET;
        autoArrayHolder.setAutoArrayOffset(autoArrayOffset);

        autoArrayHolder.setForceAlignment(FORCED_ALIGNMENT_VALUE);

        vector_type<int8_t> autoArray;
        autoArray.reserve(AUTO_ARRAY_LENGTH);
        for (size_t i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            autoArray.push_back(static_cast<int8_t>(i));
        autoArrayHolder.setAutoArray(autoArray);
    }

    void writeAutoArrayHolderToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffset)
    {
        const uint32_t autoArrayOffset = (writeWrongOffset) ? WRONG_AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET;
        writer.writeBits(autoArrayOffset, 32);

        writer.writeBits(FORCED_ALIGNMENT_VALUE, 1);
        writer.alignTo(8);

        writer.writeVarSize(static_cast<uint32_t>(AUTO_ARRAY_LENGTH));

        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(0, 7);
        for (size_t i = 0; i < AUTO_ARRAY_LENGTH - 1; ++i)
            writer.writeSignedBits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
    }

    size_t getAutoArrayHolderBitSize()
    {
        size_t bitSize = 32; // field: autoArrayOffset
        bitSize += 1; // field: forceAlignment
        bitSize += 7; // padding because of alignment
        bitSize += 8; // auto varsize
        bitSize += 1; // packing descriptor: isPacked
        bitSize += 6; // packing descriptor: maxBitNumber
        bitSize += 7; // first element
        bitSize += (AUTO_ARRAY_LENGTH - 1) * (PACKED_ARRAY_MAX_BIT_NUMBER + 1); // all deltas

        return bitSize;
    }

    void checkAutoArrayHolder(const AutoArrayHolder& autoArrayHolder, size_t bitPosition = 0)
    {
        const uint32_t expectedAutoArrayOffset = static_cast<uint32_t>(
                (bitPosition == 0) ? AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET + (bitPosition / 8));
        ASSERT_EQ(expectedAutoArrayOffset, autoArrayHolder.getAutoArrayOffset());

        ASSERT_EQ(FORCED_ALIGNMENT_VALUE, autoArrayHolder.getForceAlignment());

        const auto& autoArray = autoArrayHolder.getAutoArray();
        ASSERT_EQ(AUTO_ARRAY_LENGTH, autoArray.size());
        for (size_t i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            ASSERT_EQ(i, autoArray[i]);
    }

    static const std::string BLOB_NAME;

    static const size_t AUTO_ARRAY_LENGTH;
    static const uint8_t FORCED_ALIGNMENT_VALUE;

    static const uint32_t WRONG_AUTO_ARRAY_OFFSET;
    static const uint32_t AUTO_ARRAY_OFFSET;

    static const int8_t PACKED_ARRAY_DELTA;
    static const uint8_t PACKED_ARRAY_MAX_BIT_NUMBER;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string PackedAutoArrayOffsetTest::BLOB_NAME = "language/offsets/packed_auto_array_offset.blob";

const size_t PackedAutoArrayOffsetTest::AUTO_ARRAY_LENGTH = 5;
const uint8_t PackedAutoArrayOffsetTest::FORCED_ALIGNMENT_VALUE = 0;

const uint32_t PackedAutoArrayOffsetTest::WRONG_AUTO_ARRAY_OFFSET = 0;
const uint32_t PackedAutoArrayOffsetTest::AUTO_ARRAY_OFFSET = 5;

const int8_t PackedAutoArrayOffsetTest::PACKED_ARRAY_DELTA = 1;
const uint8_t PackedAutoArrayOffsetTest::PACKED_ARRAY_MAX_BIT_NUMBER = 1;

TEST_F(PackedAutoArrayOffsetTest, readConstructor)
{
    const bool writeWrongOffset = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoArrayHolderToByteArray(writer, writeWrongOffset);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const AutoArrayHolder autoArrayHolder(reader);
    checkAutoArrayHolder(autoArrayHolder);
}

TEST_F(PackedAutoArrayOffsetTest, readConstructorWrongOffsets)
{
    const bool writeWrongOffset = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoArrayHolderToByteArray(writer, writeWrongOffset);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(AutoArrayHolder autoArrayHolder(reader), zserio::CppRuntimeException);
}

TEST_F(PackedAutoArrayOffsetTest, bitSizeOf)
{
    const bool createWrongOffset = false;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);
    const size_t autoArrayHolderBitSize = getAutoArrayHolderBitSize();
    ASSERT_EQ(autoArrayHolderBitSize, autoArrayHolder.bitSizeOf());
}

TEST_F(PackedAutoArrayOffsetTest, bitSizeOfWithPosition)
{
    const bool createWrongOffset = false;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 2;
    const size_t autoArrayHolderBitSize = getAutoArrayHolderBitSize();
    ASSERT_EQ(autoArrayHolderBitSize - bitPosition, autoArrayHolder.bitSizeOf(bitPosition));
}

TEST_F(PackedAutoArrayOffsetTest, initializeOffsets)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 0;
    const size_t expectedEndBitPosition = getAutoArrayHolderBitSize();
    ASSERT_EQ(expectedEndBitPosition, autoArrayHolder.initializeOffsets(bitPosition));
    checkAutoArrayHolder(autoArrayHolder);
}

TEST_F(PackedAutoArrayOffsetTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 2;
    const size_t expectedEndBitPosition = getAutoArrayHolderBitSize();
    ASSERT_EQ(expectedEndBitPosition, autoArrayHolder.initializeOffsets(bitPosition));
    checkAutoArrayHolder(autoArrayHolder, bitPosition);
}

TEST_F(PackedAutoArrayOffsetTest, writeRead)
{
    const bool createWrongOffset = false;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    zserio::BitStreamWriter writer(bitBuffer);
    autoArrayHolder.write(writer);
    checkAutoArrayHolder(autoArrayHolder);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const AutoArrayHolder readAutoArrayHolder(reader);
    checkAutoArrayHolder(readAutoArrayHolder);
    ASSERT_EQ(autoArrayHolder, readAutoArrayHolder);
}

TEST_F(PackedAutoArrayOffsetTest, writeReadFile)
{
    const bool createWrongOffset = false;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);
    zserio::serializeToFile(autoArrayHolder, BLOB_NAME);

    const auto readAutoArrayHolder = zserio::deserializeFromFile<AutoArrayHolder>(BLOB_NAME);
    ASSERT_EQ(autoArrayHolder, readAutoArrayHolder);
}

TEST_F(PackedAutoArrayOffsetTest, writeWithPosition)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 2;
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, bitPosition);
    autoArrayHolder.initializeOffsets(writer.getBitPosition());
    autoArrayHolder.write(writer);

    checkAutoArrayHolder(autoArrayHolder, bitPosition);
}

TEST_F(PackedAutoArrayOffsetTest, writeWrongOffset)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(autoArrayHolder.write(writer), zserio::CppRuntimeException);
}

} // namespace packed_auto_array_offset
} // namespace offsets
