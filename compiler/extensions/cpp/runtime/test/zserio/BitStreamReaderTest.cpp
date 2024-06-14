#include <array>
#include <cstring>

#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

namespace zserio
{

class BitStreamReaderTest : public ::testing::Test
{
public:
    BitStreamReaderTest() :
            m_byteBuffer(),
            m_reader(m_byteBuffer.data(), m_byteBuffer.size())
    {
        m_byteBuffer.fill(0);
    }

protected:
    std::array<uint8_t, 16> m_byteBuffer;
    BitStreamReader m_reader;
};

TEST_F(BitStreamReaderTest, spanConstructor)
{
    const std::array<const uint8_t, 3> data = {0xAE, 0xEA, 0x80};
    const Span<const uint8_t> span(data);
    BitStreamReader reader(span);

    ASSERT_EQ(span.size() * 8, reader.getBufferBitSize());
    ASSERT_EQ(0xAEE, reader.readBits(12));
    ASSERT_EQ(0xA, reader.readBits(4));
    ASSERT_EQ(0x80, reader.readBits(8));

    ASSERT_THROW(reader.readBits(1), CppRuntimeException);
}

TEST_F(BitStreamReaderTest, spanConstructorWithBitSize)
{
    const std::array<const uint8_t, 3> data = {0xAE, 0xEA, 0x80};
    const Span<const uint8_t> span(data);
    BitStreamReader reader(span, 23);
    ASSERT_THROW(BitStreamReader wrongReader(span, 25), CppRuntimeException);

    ASSERT_EQ(23, reader.getBufferBitSize());
    ASSERT_EQ(0xAEE, reader.readBits(12));
    ASSERT_EQ(0xA, reader.readBits(4));
    ASSERT_EQ(0x40, reader.readBits(7));

    ASSERT_THROW(reader.readBits(1), CppRuntimeException);
}

TEST_F(BitStreamReaderTest, bitBufferConstructor)
{
    const std::vector<uint8_t> data = {0xAE, 0xEA, 0x80};
    BitBuffer bitBuffer(data, 17);
    BitStreamReader reader(bitBuffer);

    ASSERT_EQ(bitBuffer.getBitSize(), reader.getBufferBitSize());
    ASSERT_EQ(0xAEE, reader.readBits(12));
    ASSERT_EQ(0xA, reader.readBits(4));
    ASSERT_EQ(1, reader.readBits(1));

    ASSERT_THROW(reader.readBits(1), CppRuntimeException);

    ASSERT_THROW(BitStreamReader(nullptr, std::numeric_limits<size_t>::max() / 8), CppRuntimeException);
}

TEST_F(BitStreamReaderTest, bitBufferConstructorOverflow)
{
    const std::vector<uint8_t> data = {0xFF, 0xFF, 0xF0};
    BitBuffer bitBuffer(data, 19);
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
        BitBuffer buffer(8U + offset);

        // write test value at offset to data buffer
        buffer.getData()[offset / 8U] = static_cast<uint8_t>(
                buffer.getData()[offset / 8U] | static_cast<uint8_t>(testValue >> (offset % 8U)));
        if (offset % 8 != 0) // don't write behind the buffer
        {
            buffer.getData()[offset / 8U + 1] = static_cast<uint8_t>(buffer.getData()[offset / 8U + 1] |
                    static_cast<uint8_t>(testValue << (8U - (offset % 8U))));
        }

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
    ASSERT_THROW(m_reader.readBits(255), CppRuntimeException);
    ASSERT_THROW(m_reader.readBits(33), CppRuntimeException);

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readBits(0));
}

TEST_F(BitStreamReaderTest, readBits64)
{
    // check invalid bit length acceptance
    ASSERT_THROW(m_reader.readBits64(255), CppRuntimeException);
    ASSERT_THROW(m_reader.readBits64(65), CppRuntimeException);

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readBits64(0));
}

TEST_F(BitStreamReaderTest, readSignedBits)
{
    // check invalid bit length acceptance
    ASSERT_THROW(m_reader.readSignedBits(255), CppRuntimeException);
    ASSERT_THROW(m_reader.readSignedBits(33), CppRuntimeException);

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readSignedBits(0));
}

TEST_F(BitStreamReaderTest, readSignedBits64)
{
    // check invalid bit length acceptance
    ASSERT_THROW(m_reader.readSignedBits64(255), CppRuntimeException);
    ASSERT_THROW(m_reader.readSignedBits64(65), CppRuntimeException);

    // return 0 for 0 bits
    ASSERT_EQ(0, m_reader.readSignedBits64(0));
}

TEST_F(BitStreamReaderTest, readVarSize)
{
    {
        // overflow, 2^32 - 1 is too much ({ 0x83, 0xFF, 0xFF, 0xFF, 0xFF } is the maximum)
        const std::array<uint8_t, 5> buffer = {0x87, 0xFF, 0xFF, 0xFF, 0xFF};
        zserio::BitStreamReader reader(buffer.data(), buffer.size());
        ASSERT_THROW(reader.readVarSize(), CppRuntimeException);
    }

    {
        // overflow, 2^36 - 1 is too much ({ 0x83, 0xFF, 0xFF, 0xFF, 0xFF } is the maximum)
        const std::array<uint8_t, 5> buffer = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
        zserio::BitStreamReader reader(buffer.data(), buffer.size());
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
    ASSERT_EQ(m_byteBuffer.size() * 8, m_reader.getBufferBitSize());
}

} // namespace zserio
