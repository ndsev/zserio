#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "alignment/optional_member_alignment/OptionalMemberAlignment.h"

namespace alignment
{
namespace optional_member_alignment
{

class OptionalMemberAlignmentTest : public ::testing::Test
{
protected:
    void writeOptionalMemberAlignmentToByteArray(zserio::BitStreamWriter& writer, bool hasOptional,
            int32_t optionalField, int32_t field)
    {
        writer.writeBool(hasOptional);

        if (hasOptional)
        {
            writer.writeBits(0, 31);
            writer.writeSignedBits(optionalField, 32);
        }

        writer.writeSignedBits(field, 32);
    }

    void checkOptionalMemberAlignment(const OptionalMemberAlignment& optionalMemberAlignment, bool hasOptional,
            int32_t optionalField, int32_t field)
    {
        ASSERT_EQ(hasOptional, optionalMemberAlignment.getHasOptional());

        if (hasOptional)
        {
            ASSERT_EQ(optionalField, optionalMemberAlignment.getOptionalField());
            ASSERT_TRUE(optionalMemberAlignment.isOptionalFieldUsed());
        }
        else
        {
            ASSERT_FALSE(optionalMemberAlignment.isOptionalFieldUsed());
        }

        ASSERT_EQ(field, (int)optionalMemberAlignment.getField());
    }

    void fillOptionalMemberAlignment(OptionalMemberAlignment& optionalMemberAlignment, bool hasOptional,
            int32_t optionalField, int32_t field)
    {
        optionalMemberAlignment.setHasOptional(hasOptional);
        if (hasOptional)
            optionalMemberAlignment.setOptionalField(optionalField);
        optionalMemberAlignment.setField(field);
    }

    static const size_t WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    static const size_t WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
};

const size_t OptionalMemberAlignmentTest::WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96;
const size_t OptionalMemberAlignmentTest::WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33;

TEST_F(OptionalMemberAlignmentTest, readWithOptional)
{
    const bool hasOptional = true;
    const int32_t optionalField = 0x1234;
    const int32_t field = 0x7654;
    zserio::BitStreamWriter writer;
    writeOptionalMemberAlignmentToByteArray(writer, hasOptional, optionalField, field);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    OptionalMemberAlignment optionalMemberAlignment(reader);
    checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field);
}

TEST_F(OptionalMemberAlignmentTest, readWithoutOptional)
{
    const bool hasOptional = false;
    const int32_t optionalField = 0;
    const int32_t field = 0x2222;
    zserio::BitStreamWriter writer;
    writeOptionalMemberAlignmentToByteArray(writer, hasOptional, optionalField, field);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    OptionalMemberAlignment optionalMemberAlignment(reader);
    checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field);
}

TEST_F(OptionalMemberAlignmentTest, bitSizeOfWithOptional)
{
    OptionalMemberAlignment optionalMemberAlignment;
    fillOptionalMemberAlignment(optionalMemberAlignment, true, 0x4433, 0x1122);

    const size_t expectedBitSize = WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, optionalMemberAlignment.bitSizeOf());
}

TEST_F(OptionalMemberAlignmentTest, bitSizeOfWithoutOptional)
{
    OptionalMemberAlignment optionalMemberAlignment;
    fillOptionalMemberAlignment(optionalMemberAlignment, false, 0, 0x7624);

    const size_t expectedBitSize = WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, optionalMemberAlignment.bitSizeOf());
}

TEST_F(OptionalMemberAlignmentTest, initializeOffsetsWithOptional)
{
    OptionalMemberAlignment optionalMemberAlignment;
    fillOptionalMemberAlignment(optionalMemberAlignment, true, 0x1111, 0x3333);

    const size_t expectedBitSize = WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    size_t bitPosition = 0;
    for (; bitPosition < 32; ++bitPosition)
        ASSERT_EQ(expectedBitSize, optionalMemberAlignment.initializeOffsets(bitPosition));
    ASSERT_EQ(expectedBitSize + bitPosition, optionalMemberAlignment.initializeOffsets(bitPosition));
}

TEST_F(OptionalMemberAlignmentTest, initializeOffsetsWithoutOptional)
{
    OptionalMemberAlignment optionalMemberAlignment;
    fillOptionalMemberAlignment(optionalMemberAlignment, false, 0, 0x3334);

    const size_t expectedBitSize = WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE;
    const size_t bitPosition = 1;
    ASSERT_EQ(expectedBitSize + bitPosition, optionalMemberAlignment.initializeOffsets(bitPosition));
}

TEST_F(OptionalMemberAlignmentTest, writeWithOptional)
{
    const bool hasOptional = true;
    const int32_t optionalField = 0x9ADB;
    const int32_t field = 0x8ACD;
    OptionalMemberAlignment optionalMemberAlignment;
    fillOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field);

    zserio::BitStreamWriter writer;
    optionalMemberAlignment.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    OptionalMemberAlignment readOptionalMemberAlignment(reader);
    checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, optionalField, field);
    ASSERT_TRUE(optionalMemberAlignment == readOptionalMemberAlignment);
}

TEST_F(OptionalMemberAlignmentTest, writeWithoutOptional)
{
    const bool hasOptional = true;
    const int32_t optionalField = 0;
    const int32_t field = 0x7ACF;
    OptionalMemberAlignment optionalMemberAlignment;
    fillOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field);

    zserio::BitStreamWriter writer;
    optionalMemberAlignment.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    OptionalMemberAlignment readOptionalMemberAlignment(reader);
    checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, optionalField, field);
    ASSERT_TRUE(optionalMemberAlignment == readOptionalMemberAlignment);
}

} // namespace optional_member_alignment
} // namespace alignment
