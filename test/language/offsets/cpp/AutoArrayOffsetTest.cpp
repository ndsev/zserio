#include "gtest/gtest.h"

#include "offsets/auto_array_offset/AutoArrayHolder.h"

#include "zserio/RebindAlloc.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace offsets
{
namespace auto_array_offset
{

using allocator_type = AutoArrayHolder::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class AutoArrayOffsetTest : public ::testing::Test
{
protected:
    void writeAutoArrayHolderToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffset)
    {
        const uint32_t wrongAutoArrayOffset = WRONG_AUTO_ARRAY_OFFSET;
        const uint32_t correctAutoArrayOffset = AUTO_ARRAY_OFFSET;
        const uint32_t autoArrayOffset = (writeWrongOffset) ? wrongAutoArrayOffset : correctAutoArrayOffset;
        writer.writeBits(autoArrayOffset, 32);

        writer.writeBits(FORCED_ALIGNMENT_VALUE, 8);

        writer.writeVarSize(static_cast<uint32_t>(AUTO_ARRAY_LENGTH));
        for (size_t i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 7);
    }

    void checkAutoArrayHolder(const AutoArrayHolder& autoArrayHolder, size_t bitPosition = 0)
    {
        const uint32_t expectedAutoArrayOffset = static_cast<uint32_t>(
                (bitPosition == 0) ? AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET + (bitPosition / 8));
        ASSERT_EQ(expectedAutoArrayOffset, autoArrayHolder.getAutoArrayOffset());

        ASSERT_EQ(FORCED_ALIGNMENT_VALUE, autoArrayHolder.getForceAlignment());

        const vector_type<int8_t>& autoArray = autoArrayHolder.getAutoArray();
        ASSERT_EQ(AUTO_ARRAY_LENGTH, autoArray.size());
        for (size_t i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            ASSERT_EQ(i, autoArray[i]);
    }

    void fillAutoArrayHolder(AutoArrayHolder& autoArrayHolder, bool createWrongOffset)
    {
        const uint32_t wrongAutoArrayOffset = WRONG_AUTO_ARRAY_OFFSET;
        const uint32_t correctAutoArrayOffset = AUTO_ARRAY_OFFSET;
        const uint32_t autoArrayOffset = (createWrongOffset) ? wrongAutoArrayOffset : correctAutoArrayOffset;
        autoArrayHolder.setAutoArrayOffset(autoArrayOffset);

        autoArrayHolder.setForceAlignment(FORCED_ALIGNMENT_VALUE);

        vector_type<int8_t> autoArray;
        autoArray.reserve(AUTO_ARRAY_LENGTH);
        for (size_t i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            autoArray.push_back(static_cast<int8_t>(i));
        autoArrayHolder.setAutoArray(autoArray);
    }

    static const size_t     AUTO_ARRAY_LENGTH;
    static const uint8_t    FORCED_ALIGNMENT_VALUE;

    static const uint32_t   WRONG_AUTO_ARRAY_OFFSET;
    static const uint32_t   AUTO_ARRAY_OFFSET;

    static const size_t     AUTO_ARRAY_HOLDER_BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t    AutoArrayOffsetTest::AUTO_ARRAY_LENGTH = 5;
const uint8_t   AutoArrayOffsetTest::FORCED_ALIGNMENT_VALUE = 0;

const uint32_t  AutoArrayOffsetTest::WRONG_AUTO_ARRAY_OFFSET = 0;
const uint32_t  AutoArrayOffsetTest::AUTO_ARRAY_OFFSET = 5;

const size_t    AutoArrayOffsetTest::AUTO_ARRAY_HOLDER_BIT_SIZE = 32 + 1 + 7 + 8 + AUTO_ARRAY_LENGTH * 7;

TEST_F(AutoArrayOffsetTest, readConstructor)
{
    const bool writeWrongOffset = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoArrayHolderToByteArray(writer, writeWrongOffset);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const AutoArrayHolder autoArrayHolder(reader);
    checkAutoArrayHolder(autoArrayHolder);
}

TEST_F(AutoArrayOffsetTest, readConstructorWrongOffsets)
{
    const bool writeWrongOffset = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoArrayHolderToByteArray(writer, writeWrongOffset);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EXPECT_THROW(AutoArrayHolder autoArrayHolder(reader), zserio::CppRuntimeException);
}

TEST_F(AutoArrayOffsetTest, bitSizeOf)
{
    const bool createWrongOffset = false;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);
    ASSERT_EQ(AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.bitSizeOf());
}

TEST_F(AutoArrayOffsetTest, bitSizeOfWithPosition)
{
    const bool createWrongOffset = false;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 2;
    ASSERT_EQ(AUTO_ARRAY_HOLDER_BIT_SIZE - bitPosition, autoArrayHolder.bitSizeOf(bitPosition));
}

TEST_F(AutoArrayOffsetTest, initializeOffsets)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 0;
    ASSERT_EQ(AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.initializeOffsets(bitPosition));
    checkAutoArrayHolder(autoArrayHolder);
}

TEST_F(AutoArrayOffsetTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    const size_t bitPosition = 2;
    ASSERT_EQ(AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.initializeOffsets(bitPosition));
    checkAutoArrayHolder(autoArrayHolder, bitPosition);
}

TEST_F(AutoArrayOffsetTest, write)
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
    ASSERT_TRUE(autoArrayHolder == readAutoArrayHolder);
}

TEST_F(AutoArrayOffsetTest, writeWithPosition)
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

TEST_F(AutoArrayOffsetTest, writeWrongOffset)
{
    const bool createWrongOffset = true;
    AutoArrayHolder autoArrayHolder;
    fillAutoArrayHolder(autoArrayHolder, createWrongOffset);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(autoArrayHolder.write(writer), zserio::CppRuntimeException);
}

} // namespace auto_array_offset
} // namespace offsets
