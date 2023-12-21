#include <vector>

#include "gtest/gtest.h"
#include "zserio/FileUtil.h"

namespace zserio
{

TEST(FileUtilTest, writeReadByteBufferBitSize)
{
    const std::string fileName = "FileUtilTest_byteBufferBitSize.bin";

    auto buffer = std::vector<uint8_t>{0xAB, 0xCD, 0xF0};
    writeBufferToFile(buffer.data(), 20, BitsTag(), fileName);

    BitBuffer readBitBuffer = readBufferFromFile(fileName);
    ASSERT_EQ(24, readBitBuffer.getBitSize());
    for (size_t i = 0; i < 3; ++i)
    {
        ASSERT_EQ(buffer[i], readBitBuffer.getData()[i]);
    }

    const std::string invalidFileName = "";
    ASSERT_THROW(writeBufferToFile(buffer.data(), 20, BitsTag(), invalidFileName), CppRuntimeException);
    ASSERT_THROW(readBufferFromFile(invalidFileName), CppRuntimeException);
}

TEST(FileUtilTest, writeReadByteBufferByteSize)
{
    const std::string fileName = "FileUtilTest_byteBufferByteSize.bin";

    auto buffer = std::vector<uint8_t>{0xAB, 0xCD, 0xF0};
    writeBufferToFile(buffer.data(), buffer.size(), fileName);

    BitBuffer readBitBuffer = readBufferFromFile(fileName);
    ASSERT_EQ(24, readBitBuffer.getBitSize());
    for (size_t i = 0; i < 3; ++i)
    {
        ASSERT_EQ(buffer[i], readBitBuffer.getData()[i]);
    }
}

TEST(FileUtilTest, writeReadBitBuffer)
{
    const std::string fileName = "FileUtilTest_bitBuffer.bin";

    BitBuffer bitBuffer({0xAB, 0xCD, 0xF0}, 20);
    writeBufferToFile(bitBuffer, fileName);

    BitBuffer readBitBuffer = readBufferFromFile(fileName);
    ASSERT_EQ(24, readBitBuffer.getBitSize());
    for (size_t i = 0; i < 3; ++i)
    {
        ASSERT_EQ(bitBuffer.getData()[i], readBitBuffer.getData()[i]);
    }
}

TEST(FileUtilTest, writeReadBitStreamWriter)
{
    const std::string fileName = "FileUtilTest_bitStreamWriter.bin";

    BitBuffer bitBuffer(20);
    BitStreamWriter writer(bitBuffer);
    writer.writeBits(0xAB, 8);
    writer.writeBits(0xCD, 8);
    writer.writeBits(0xF, 4);
    writeBufferToFile(writer, fileName);

    BitBuffer readBitBuffer = readBufferFromFile(fileName);
    ASSERT_EQ(24, readBitBuffer.getBitSize());
    for (size_t i = 0; i < 3; ++i)
    {
        ASSERT_EQ(bitBuffer.getData()[i], readBitBuffer.getData()[i]);
    }
}

} // namespace zserio
