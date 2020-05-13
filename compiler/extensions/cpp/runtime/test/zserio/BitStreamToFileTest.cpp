#include "gtest/gtest.h"

#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace zserio
{

static const char* TEST_FILE_NAME  = "BitStreamFileUtilTest.bin";

TEST(BitStreamToFileTest, writeAndRead)
{
    BitStreamWriter writer;
    writer.writeBits(13, 7);
    writer.writeString(TEST_FILE_NAME);
    writer.writeVarInt(-123456);
    writer.writeBufferToFile(TEST_FILE_NAME);

    BitStreamReader reader(TEST_FILE_NAME);
    EXPECT_EQ(13, reader.readBits(7));
    EXPECT_EQ(TEST_FILE_NAME, reader.readString());
    EXPECT_EQ(-123456, reader.readVarInt());
}

TEST(BitStreamToFileTest, writeAndReadInBitBuffer)
{
    BitBuffer bitBuffer(12);
    // TODO[Mi-L@]: This should be fixed in #211!
    //bitBuffer.getBuffer()[1] = 0x0A; // last four bits should be written to the file untouched by the writer
    BitStreamWriter writer(bitBuffer);
    writer.writeBits(0x3F, 12);
    writer.writeBufferToFile(TEST_FILE_NAME);

    BitStreamReader reader(TEST_FILE_NAME);
    ASSERT_EQ(0x3F, reader.readBits(12));
    //ASSERT_EQ(0x0A, reader.readBits(4));
}

TEST(BitStreamToFileTest, readNonExisting)
{
    ASSERT_THROW(BitStreamReader("NON_EXISTING_FILE"), CppRuntimeException);
}

} // namespace zserio
