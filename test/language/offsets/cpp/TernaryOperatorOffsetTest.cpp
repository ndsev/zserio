#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "offsets/ternary_operator_offset/TernaryOffset.h"

namespace offsets
{
namespace ternary_operator_offset
{

using allocator_type = TernaryOffset::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class TernaryOperatorOffsetTest : public ::testing::Test
{
protected:
    void writeTernaryOffsetToByteArray(zserio::BitStreamWriter& writer, bool isFirstOffsetUsed,
            bool writeWrongOffset)
    {
        writer.writeBool(isFirstOffsetUsed);
        if (isFirstOffsetUsed)
        {
            writer.writeBits((writeWrongOffset) ? WRONG_FIELD_OFFSET : FIELD_OFFSET, 32);
            writer.writeBits(WRONG_FIELD_OFFSET, 32);
        }
        else
        {
            writer.writeBits(WRONG_FIELD_OFFSET, 32);
            writer.writeBits((writeWrongOffset) ? WRONG_FIELD_OFFSET : FIELD_OFFSET, 32);
        }
        writer.writeSignedBits(FIELD_VALUE, 32);
    }

    void checkTernaryOffset(const TernaryOffset& ternaryOffset, bool isFirstOffsetUsed)
    {
        ASSERT_EQ(isFirstOffsetUsed, ternaryOffset.getIsFirstOffsetUsed());
        if (isFirstOffsetUsed)
        {
            ASSERT_EQ(FIELD_OFFSET, ternaryOffset.getOffsets()[0]);
            ASSERT_EQ(WRONG_FIELD_OFFSET, ternaryOffset.getOffsets()[1]);
        }
        else
        {
            ASSERT_EQ(WRONG_FIELD_OFFSET, ternaryOffset.getOffsets()[0]);
            ASSERT_EQ(FIELD_OFFSET, ternaryOffset.getOffsets()[1]);
        }
        ASSERT_EQ(FIELD_VALUE, ternaryOffset.getValue());
    }

    void fillTernaryOffset(TernaryOffset& ternaryOffset, bool isFirstOffsetUsed, bool createWrongOffset)
    {
        ternaryOffset.setIsFirstOffsetUsed(isFirstOffsetUsed);
        const vector_type<uint32_t> offsets = { WRONG_FIELD_OFFSET, WRONG_FIELD_OFFSET };
        ternaryOffset.setOffsets(offsets);
        ternaryOffset.setValue(FIELD_VALUE);
        if (!createWrongOffset)
            ternaryOffset.initializeOffsets();
    }

    void testOffset(bool isFirstOffsetUsed)
    {
        TernaryOffset ternaryOffset;
        const bool writeWrongOffset = false;
        fillTernaryOffset(ternaryOffset, isFirstOffsetUsed, writeWrongOffset);

        zserio::BitStreamWriter writer(bitBuffer);
        ternaryOffset.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const TernaryOffset readTernaryOffset(reader);
        checkTernaryOffset(readTernaryOffset, isFirstOffsetUsed);
    }

    void testOffsetWriteWrong(bool isFirstOffsetUsed)
    {
        TernaryOffset ternaryOffset;
        const bool writeWrongOffset = true;
        fillTernaryOffset(ternaryOffset, isFirstOffsetUsed, writeWrongOffset);

        zserio::BitStreamWriter writer(bitBuffer);
        EXPECT_THROW(ternaryOffset.write(writer), zserio::CppRuntimeException);
    }

    void testOffsetReadWrong(bool isFirstOffsetUsed)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        const bool writeWrongOffset = true;
        writeTernaryOffsetToByteArray(writer, isFirstOffsetUsed, writeWrongOffset);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        EXPECT_THROW(TernaryOffset ternaryOffset(reader), zserio::CppRuntimeException);
    }

    static const uint32_t WRONG_FIELD_OFFSET;
    static const uint32_t FIELD_OFFSET;
    static const int32_t FIELD_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint32_t TernaryOperatorOffsetTest::WRONG_FIELD_OFFSET = 0;
const uint32_t TernaryOperatorOffsetTest::FIELD_OFFSET = (1 + 32 + 32 + /* align */ + 7) / 8;
const int32_t TernaryOperatorOffsetTest::FIELD_VALUE = 0xABCD;

TEST_F(TernaryOperatorOffsetTest, firstOffset)
{
    const bool isFirstOffsetUsed = true;
    testOffset(isFirstOffsetUsed);
}

TEST_F(TernaryOperatorOffsetTest, firstOffsetWriteWrong)
{
    const bool isFirstOffsetUsed = true;
    testOffsetWriteWrong(isFirstOffsetUsed);
}

TEST_F(TernaryOperatorOffsetTest, firstOffsetReadWrong)
{
    const bool isFirstOffsetUsed = true;
    testOffsetReadWrong(isFirstOffsetUsed);
}

TEST_F(TernaryOperatorOffsetTest, secondOffset)
{
    const bool isFirstOffsetUsed = false;
    testOffset(isFirstOffsetUsed);
}

TEST_F(TernaryOperatorOffsetTest, secondOffsetWriteWrong)
{
    const bool isFirstOffsetUsed = false;
    testOffsetWriteWrong(isFirstOffsetUsed);
}

TEST_F(TernaryOperatorOffsetTest, secondOffsetReadWrong)
{
    const bool isFirstOffsetUsed = false;
    testOffsetReadWrong(isFirstOffsetUsed);
}

} // namespace ternary_operator_offset
} // namespace offsets
