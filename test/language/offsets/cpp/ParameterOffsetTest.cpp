#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "offsets/parameter_offset/School.h"

namespace offsets
{
namespace parameter_offset
{

class ParameterOffsetTest : public ::testing::Test
{
protected:
    void writeSchoolToByteArray(zserio::BitStreamWriter& writer, bool writeWrongOffset)
    {
        writer.writeBits(SCHOOL_ID, 16);
        const uint32_t wrongRoomOffset = WRONG_ROOM_OFFSET;
        const uint32_t correctRoomOffset = ROOM_OFFSET;
        const uint32_t roomOffset = (writeWrongOffset) ? wrongRoomOffset : correctRoomOffset;
        writer.writeBits(roomOffset, 32);
        writer.writeBits(ROOM_ID, 16);
    }

    void checkSchool(const School& school, size_t bitPosition = 0)
    {
        const uint16_t expectedSchoolId = SCHOOL_ID;
        ASSERT_EQ(expectedSchoolId, school.getSchoolId());

        const uint32_t expectedRoomOffset = static_cast<uint32_t>(
                (bitPosition == 0) ? ROOM_OFFSET : ROOM_OFFSET + (bitPosition / 8) + 1);
        ASSERT_EQ(expectedRoomOffset, school.getOffsetHolder().getRoomOffset());

        const uint16_t expectedRoomId = ROOM_ID;
        ASSERT_EQ(expectedRoomId, school.getRoom().getRoomId());
    }

    void fillSchool(School& school, bool createWrongOffset)
    {
        school.setSchoolId(SCHOOL_ID);
        const uint32_t wrongRoomOffset = WRONG_ROOM_OFFSET;
        const uint32_t correctRoomOffset = ROOM_OFFSET;
        const uint32_t roomOffset = (createWrongOffset) ? wrongRoomOffset : correctRoomOffset;
        school.getOffsetHolder().setRoomOffset(roomOffset);
        school.getRoom().setRoomId(ROOM_ID);
        school.initializeChildren();
    }

    static const uint16_t   SCHOOL_ID = 0x01;
    static const uint16_t   ROOM_ID = 0x11;

    static const uint32_t   WRONG_ROOM_OFFSET = 0;
    static const uint32_t   ROOM_OFFSET = 6;

    static const size_t     SCHOOL_BIT_SIZE = (ROOM_OFFSET + 2) * 8;
};

TEST_F(ParameterOffsetTest, read)
{
    const bool writeWrongOffset = false;
    zserio::BitStreamWriter writer;
    writeSchoolToByteArray(writer, writeWrongOffset);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    const School school(reader);
    checkSchool(school);
}

TEST_F(ParameterOffsetTest, readWrongOffsets)
{
    const bool writeWrongOffset = true;
    zserio::BitStreamWriter writer;
    writeSchoolToByteArray(writer, writeWrongOffset);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    EXPECT_THROW(School school(reader), zserio::CppRuntimeException);
}

TEST_F(ParameterOffsetTest, bitSizeOf)
{
    const bool createWrongOffset = false;
    School school;
    fillSchool(school, createWrongOffset);

    const size_t expectedBitSize = SCHOOL_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, school.bitSizeOf());
}

TEST_F(ParameterOffsetTest, bitSizeOfWithPosition)
{
    const bool createWrongOffset = false;
    School school;
    fillSchool(school, createWrongOffset);

    const size_t bitPosition = 2;
    const size_t expectedBitSize = SCHOOL_BIT_SIZE + 8 - bitPosition;
    ASSERT_EQ(expectedBitSize, school.bitSizeOf(bitPosition));
}

TEST_F(ParameterOffsetTest, initializeOffsets)
{
    const bool createWrongOffset = true;
    School school;
    fillSchool(school, createWrongOffset);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = SCHOOL_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, school.initializeOffsets(bitPosition));
    checkSchool(school);
}

TEST_F(ParameterOffsetTest, initializeOffsetsWithPosition)
{
    const bool createWrongOffset = true;
    School school;
    fillSchool(school, createWrongOffset);

    const size_t bitPosition = 2;
    const size_t expectedBitSize = SCHOOL_BIT_SIZE + 8;
    ASSERT_EQ(expectedBitSize, school.initializeOffsets(bitPosition));
    checkSchool(school, bitPosition);
}

TEST_F(ParameterOffsetTest, write)
{
    const bool createWrongOffset = true;
    School school;
    fillSchool(school, createWrongOffset);

    zserio::BitStreamWriter writer;
    school.write(writer);
    checkSchool(school);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    const School readSchool(reader);
    checkSchool(readSchool);
    ASSERT_TRUE(school == readSchool);
}

TEST_F(ParameterOffsetTest, writeWithPosition)
{
    const bool createWrongOffset = true;
    School school;
    fillSchool(school, createWrongOffset);

    zserio::BitStreamWriter writer;
    const size_t bitPosition = 2;
    writer.writeBits(0, bitPosition);
    school.write(writer);

    checkSchool(school, bitPosition);
}

TEST_F(ParameterOffsetTest, writeWrongOffset)
{
    const bool createWrongOffset = true;
    School school;
    fillSchool(school, createWrongOffset);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(school.write(writer, zserio::NO_PRE_WRITE_ACTION), zserio::CppRuntimeException);
}

} // namespace parameter_offset
} // namespace offsets
