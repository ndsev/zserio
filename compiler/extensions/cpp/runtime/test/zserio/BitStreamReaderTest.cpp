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
    zserio::BitStreamReader m_reader;
    static const size_t BUFFER_SIZE = 16;

private:
    uint8_t m_byteBuffer[BUFFER_SIZE];
};

const size_t BitStreamReaderTest::BUFFER_SIZE;

TEST_F(BitStreamReaderTest, readUnalignedData)
{
    // number expected to read at offset
    const uint8_t TestValue = 123u;

    for (int offset = 0; offset <= 8; ++offset) {
        uint8_t data[] = {0x00, 0x00, 0x00};

        // write test value at offset to data buffer
        data[offset / 8    ] |= TestValue >> (offset % 8);
        data[offset / 8 + 1] |= TestValue << (8u - offset % 8);

        zserio::BitBuffer buffer(data, 8u + offset);
        zserio::BitStreamReader reader(buffer);

        // read offset bits
        ASSERT_EQ(0u, reader.readBits(offset));

        // read magic number
        ASSERT_EQ(TestValue, reader.readBits(8u)) << "Offset: " << offset;

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
