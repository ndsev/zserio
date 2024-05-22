#include "alignment/auto_optional_member_alignment/AutoOptionalMemberAlignment.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace alignment
{
namespace auto_optional_member_alignment
{

class AutoOptionalMemberAlignmentTest : public ::testing::Test
{
protected:
    void writeAutoOptionalMemberAlignmentToByteArray(
            zserio::BitStreamWriter& writer, bool hasAutoOptional, int32_t autoOptionalField, int32_t field)
    {
        writer.writeBool(hasAutoOptional);

        if (hasAutoOptional)
        {
            writer.writeBits(0, 31);
            writer.writeSignedBits(autoOptionalField, 32);
        }

        writer.writeSignedBits(field, 32);
    }

    void checkAutoOptionalMemberAlignment(const AutoOptionalMemberAlignment& autoOptionalMemberAlignment,
            bool hasAutoOptional, int32_t autoOptionalField, int32_t field)
    {
        if (hasAutoOptional)
        {
            ASSERT_EQ(autoOptionalField, autoOptionalMemberAlignment.getAutoOptionalField());
            ASSERT_TRUE(autoOptionalMemberAlignment.isAutoOptionalFieldUsed());
        }
        else
        {
            ASSERT_FALSE(autoOptionalMemberAlignment.isAutoOptionalFieldUsed());
        }

        ASSERT_EQ(field, autoOptionalMemberAlignment.getField());
    }

    void fillAutoOptionalMemberAlignment(AutoOptionalMemberAlignment& autoOptionalMemberAlignment,
            bool hasAutoOptional, int32_t autoOptionalField, int32_t field)
    {
        if (hasAutoOptional)
        {
            autoOptionalMemberAlignment.setAutoOptionalField(autoOptionalField);
        }
        autoOptionalMemberAlignment.setField(field);
    }

    static const size_t WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    static const size_t WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t AutoOptionalMemberAlignmentTest::WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96;
const size_t AutoOptionalMemberAlignmentTest::WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33;

TEST_F(AutoOptionalMemberAlignmentTest, readWithOptional)
{
    const bool hasAutoOptional = true;
    const int32_t autoOptionalField = 0x1234;
    const int32_t field = 0x7654;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoOptionalMemberAlignmentToByteArray(writer, hasAutoOptional, autoOptionalField, field);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoOptionalMemberAlignment autoOptionalMemberAlignment(reader);
    checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, hasAutoOptional, autoOptionalField, field);
}

TEST_F(AutoOptionalMemberAlignmentTest, readWithoutOptional)
{
    const bool hasAutoOptional = false;
    const int32_t autoOptionalField = 0;
    const int32_t field = 0x2222;
    zserio::BitStreamWriter writer(bitBuffer);
    writeAutoOptionalMemberAlignmentToByteArray(writer, hasAutoOptional, autoOptionalField, field);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoOptionalMemberAlignment autoOptionalMemberAlignment(reader);
    checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, hasAutoOptional, autoOptionalField, field);
}

TEST_F(AutoOptionalMemberAlignmentTest, bitSizeOfWithOptional)
{
    AutoOptionalMemberAlignment autoOptionalMemberAlignment;
    fillAutoOptionalMemberAlignment(autoOptionalMemberAlignment, true, 0x4433, 0x1122);

    const size_t expectedBitSize = WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, autoOptionalMemberAlignment.bitSizeOf());
}

TEST_F(AutoOptionalMemberAlignmentTest, bitSizeOfWithoutOptional)
{
    AutoOptionalMemberAlignment autoOptionalMemberAlignment;
    fillAutoOptionalMemberAlignment(autoOptionalMemberAlignment, false, 0, 0x7624);

    const size_t expectedBitSize = WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, autoOptionalMemberAlignment.bitSizeOf());
}

TEST_F(AutoOptionalMemberAlignmentTest, initializeOffsetsWithOptional)
{
    AutoOptionalMemberAlignment autoOptionalMemberAlignment;
    fillAutoOptionalMemberAlignment(autoOptionalMemberAlignment, true, 0x1111, 0x3333);

    const size_t expectedBitSize = WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    size_t bitPosition = 0;
    for (; bitPosition < 32; ++bitPosition)
    {
        ASSERT_EQ(expectedBitSize, autoOptionalMemberAlignment.initializeOffsets(bitPosition));
    }
    ASSERT_EQ(expectedBitSize + bitPosition, autoOptionalMemberAlignment.initializeOffsets(bitPosition));
}

TEST_F(AutoOptionalMemberAlignmentTest, initializeOffsetsWithoutOptional)
{
    AutoOptionalMemberAlignment autoOptionalMemberAlignment;
    fillAutoOptionalMemberAlignment(autoOptionalMemberAlignment, false, 0, 0x3334);

    const size_t expectedBitSize = WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    const size_t bitPosition = 1;
    ASSERT_EQ(expectedBitSize + bitPosition, autoOptionalMemberAlignment.initializeOffsets(bitPosition));
}

TEST_F(AutoOptionalMemberAlignmentTest, writeWithOptional)
{
    const bool hasAutoOptional = true;
    const int32_t autoOptionalField = 0x9ADB;
    const int32_t field = 0x8ACD;
    AutoOptionalMemberAlignment autoOptionalMemberAlignment;
    fillAutoOptionalMemberAlignment(autoOptionalMemberAlignment, hasAutoOptional, autoOptionalField, field);

    zserio::BitStreamWriter writer(bitBuffer);
    autoOptionalMemberAlignment.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoOptionalMemberAlignment readAutoOptionalMemberAlignment(reader);
    checkAutoOptionalMemberAlignment(
            readAutoOptionalMemberAlignment, hasAutoOptional, autoOptionalField, field);
    ASSERT_TRUE(autoOptionalMemberAlignment == readAutoOptionalMemberAlignment);
}

TEST_F(AutoOptionalMemberAlignmentTest, writeWithoutOptional)
{
    const bool hasAutoOptional = true;
    const int32_t autoOptionalField = 0;
    const int32_t field = 0x7ACF;
    AutoOptionalMemberAlignment autoOptionalMemberAlignment;
    fillAutoOptionalMemberAlignment(autoOptionalMemberAlignment, hasAutoOptional, autoOptionalField, field);

    zserio::BitStreamWriter writer(bitBuffer);
    autoOptionalMemberAlignment.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    AutoOptionalMemberAlignment readAutoOptionalMemberAlignment(reader);
    checkAutoOptionalMemberAlignment(
            readAutoOptionalMemberAlignment, hasAutoOptional, autoOptionalField, field);
    ASSERT_TRUE(autoOptionalMemberAlignment == readAutoOptionalMemberAlignment);
}

} // namespace auto_optional_member_alignment
} // namespace alignment
