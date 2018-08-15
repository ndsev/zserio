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

TEST(BitStreamToFileTest, readNonExisting)
{
    ASSERT_THROW(BitStreamReader("NON_EXISTING_FILE"), CppRuntimeException);
}

} // namespace zserio
