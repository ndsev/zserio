#include <cstring>

#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

class BitStreamReaderTest : public ::testing::Test
{
public:
    BitStreamReaderTest() : m_reader(m_byteBuffer, BUFFER_SIZE)
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    BitStreamReader m_reader;
    static const size_t BUFFER_SIZE = 16;

private:
    uint8_t m_byteBuffer[BUFFER_SIZE];
};

const size_t BitStreamReaderTest::BUFFER_SIZE;

TEST_F(BitStreamReaderTest, bitBufferConstructor)
{
    BitBuffer bitBuffer(17);
    bitBuffer.getBuffer()[0] = 0xAE;
    bitBuffer.getBuffer()[1] = 0xEA;
    bitBuffer.getBuffer()[2] = 0x80;
    BitStreamReader reader(bitBuffer);

    ASSERT_EQ(bitBuffer.getBitSize(), reader.getBufferBitSize());
    ASSERT_EQ(0xAEE, reader.readBits(12));
    ASSERT_EQ(0x0A, reader.readBits(4));
    ASSERT_EQ(0x01, reader.readBits(1));

    ASSERT_THROW(reader.readBits(1), CppRuntimeException);
}

TEST_F(BitStreamReaderTest, bitBufferConstructorOverflow)
{
    BitBuffer bitBuffer(19);
    bitBuffer.getBuffer()[0] = 0xFF;
    bitBuffer.getBuffer()[1] = 0xFF;
    bitBuffer.getBuffer()[2] = 0xF0;
    BitStreamReader reader(bitBuffer);

    ASSERT_EQ(bitBuffer.getBitSize(), reader.getBufferBitSize());
    ASSERT_THROW(reader.readBits(20), CppRuntimeException);
}

TEST_F(BitStreamReaderTest, readUnalignedData)
{
    // number expected to read at offset
    const uint8_t testValue = 123;

    for (int offset = 0; offset <= 64; ++offset)
    {
        BitBuffer buffer(8 + offset);

        // write test value at offset to data buffer
        buffer.getBuffer()[offset / 8] |= testValue >> (offset % 8);
        buffer.getBuffer()[offset / 8 + 1] |= testValue << (8 - offset % 8);

        BitStreamReader reader(buffer);

        // read offset bits
        ASSERT_EQ(0u, reader.readBits64(offset));

        // read magic number
        ASSERT_EQ(testValue, reader.readBits(8)) << "Offset: " << offset;

        // check eof
        ASSERT_THROW(reader.readBits(1), CppRuntimeException);
    }
}

TEST_F(BitStreamReaderTest, readBits)
{
    // check invalid bitlength acceptance
    uint8_t numBits[] = { 255, 33 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_reader.readBits(numBits[i]), CppRuntimeException);
    }

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readBits(0));
}

TEST_F(BitStreamReaderTest, readBits64)
{
    // check invalid bit length acceptance
    uint8_t numBits[] = { 255, 65 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_reader.readBits64(numBits[i]), CppRuntimeException);
    }

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readBits64(0));
}

TEST_F(BitStreamReaderTest, readSignedBits)
{
    // check invalid bit length acceptance
    uint8_t numBits[] = { 255, 33 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_reader.readSignedBits(numBits[i]), CppRuntimeException);
    }

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readSignedBits(0));
}

TEST_F(BitStreamReaderTest, readSignedBits64)
{
    // check invalid bit length acceptance
    uint8_t numBits[] = { 255, 65 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_reader.readSignedBits64(numBits[i]), CppRuntimeException);
    }

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readSignedBits64(0));
}

TEST_F(BitStreamReaderTest, getBitPosition)
{
    ASSERT_EQ(0, m_reader.getBitPosition());
    m_reader.readBits(10);
    ASSERT_EQ(10, m_reader.getBitPosition());
}

TEST_F(BitStreamReaderTest, getBufferBitSize)
{
    ASSERT_EQ(BUFFER_SIZE * 8, m_reader.getBufferBitSize());
}

} // namespace zserio
