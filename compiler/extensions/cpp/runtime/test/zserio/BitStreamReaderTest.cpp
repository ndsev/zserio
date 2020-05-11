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

TEST_F(BitStreamReaderTest, bitBufferCtor)
{
    uint8_t data[] = {1, 2, 3};

    zserio::BitBuffer buffer(data, 17u);
    zserio::BitStreamReader reader(buffer);

    ASSERT_EQ(17u, reader.getBufferBitSize());
}

TEST_F(BitStreamReaderTest, readUnalignedData)
{
    /* 1bit = 0, 8bit = 8, 8bit = 1 */
    uint8_t data[] = {0x04, 0x00, 0x80};

    zserio::BitBuffer buffer(data, 17u);
    zserio::BitStreamReader reader(buffer);

    auto readBool = reader.readBool();
    ASSERT_EQ(false, readBool);

    zserio::BitBuffer readBuffer = reader.readBitBuffer();
    ASSERT_EQ(8u, readBuffer.getBitSize());
    ASSERT_EQ(1u, readBuffer.getBuffer()[0]);
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
