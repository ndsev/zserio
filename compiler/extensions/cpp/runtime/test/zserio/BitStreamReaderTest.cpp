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

TEST_F(BitStreamReaderTest, spanConstructor)
{
    const uint8_t data[] = {0xAE, 0xEA, 0x80};
    const Span<const uint8_t> span(data);
    BitStreamReader reader(span);

    ASSERT_EQ(span.size() * 8, reader.getBufferBitSize());
    ASSERT_EQ(0xAEE, reader.readBits(12));
    ASSERT_EQ(0xA, reader.readBits(4));
    ASSERT_EQ(0x80, reader.readBits(8));

    ASSERT_THROW(reader.readBits(1), CppRuntimeException);
}

TEST_F(BitStreamReaderTest, bitBufferConstructor)
{
    BitBuffer bitBuffer(17);
    bitBuffer.getBuffer()[0] = 0xAE;
    bitBuffer.getBuffer()[1] = 0xEA;
    bitBuffer.getBuffer()[2] = 0x80;
    BitStreamReader reader(bitBuffer);

    ASSERT_EQ(bitBuffer.getBitSize(), reader.getBufferBitSize());
    ASSERT_EQ(0xAEE, reader.readBits(12));
    ASSERT_EQ(0xA, reader.readBits(4));
    ASSERT_EQ(1, reader.readBits(1));

    ASSERT_THROW(reader.readBits(1), CppRuntimeException);

    ASSERT_THROW(BitStreamReader(nullptr, std::numeric_limits<size_t>::max() / 8),
                 CppRuntimeException);
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

    for (uint8_t offset = 0; offset <= 64; ++offset)
    {
        BitBuffer buffer(8 + offset);

        // write test value at offset to data buffer
        buffer.getBuffer()[offset / 8U] |= static_cast<uint8_t>(testValue >> (offset % 8U));
        if (offset % 8 != 0) // don't write behind the buffer
            buffer.getBuffer()[offset / 8U + 1] |= static_cast<uint8_t>(testValue << (8U - (offset % 8U)));

        BitStreamReader reader(buffer);

        // read offset bits
        ASSERT_EQ(0, reader.readBits64(offset));

        // read magic number
        ASSERT_EQ(testValue, reader.readBits(8)) << "Offset: " << offset;

        // check eof
        ASSERT_THROW(reader.readBits(1), CppRuntimeException) << "Offset: " << offset;
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

TEST_F(BitStreamReaderTest, readVarSize)
{
    {
        // overflow, 2^32 - 1 is too much ({ 0x83, 0xFF, 0xFF, 0xFF, 0xFF } is the maximum)
        uint8_t buffer[] = { 0x87, 0xFF, 0xFF, 0xFF, 0xFF };
        zserio::BitStreamReader reader(buffer, sizeof(buffer) / sizeof(buffer[0]));
        ASSERT_THROW(reader.readVarSize(), CppRuntimeException);
    }

    {
        // overflow, 2^36 - 1 is too much ({ 0x83, 0xFF, 0xFF, 0xFF, 0xFF } is the maximum)
        uint8_t buffer[] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
        zserio::BitStreamReader reader(buffer, sizeof(buffer) / sizeof(buffer[0]));
        ASSERT_THROW(reader.readVarSize(), CppRuntimeException);
    }
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
