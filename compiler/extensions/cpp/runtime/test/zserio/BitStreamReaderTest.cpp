#include <cstring>

#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

class BitStreamReaderTest : public ::testing::Test
{
public:
    BitStreamReaderTest() : m_reader(m_byteBuffer, bufferSize)
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    zserio::BitStreamReader m_reader;

private:
    static const size_t bufferSize = 16;
    uint8_t m_byteBuffer[bufferSize];
};

TEST_F(BitStreamReaderTest, ReadBits)
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

TEST_F(BitStreamReaderTest, ReadBits64)
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

TEST_F(BitStreamReaderTest, ReadSignedBits)
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

TEST_F(BitStreamReaderTest, ReadSignedBits64)
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

} // namespace zserio
