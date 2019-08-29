#include <cstring>
#include <string>

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/Types.h"
#include "zserio/BitStreamException.h"

#include "gtest/gtest.h"

namespace zserio
{

class BitStreamTest : public ::testing::Test
{
public:
    BitStreamTest() : m_writer(m_byteBuffer, bufferSize), m_reader(m_byteBuffer, bufferSize)
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <typename T, size_t N, typename U>
    void testBitStreamValues(const T (&values)[N], void (BitStreamWriter::*writerMethod)(U value),
                             T (BitStreamReader::*readerMethod)())
    {
        for (size_t i = 0; i < N; ++i)
        {
            (m_writer.*writerMethod)(values[i]);
        }

        for (size_t i = 0; i < N; ++i)
        {
            ASSERT_EQ((m_reader.*readerMethod)(), values[i]);
        }
    }

    static const size_t bufferSize = 256;

private:
    uint8_t m_byteBuffer[bufferSize];

protected:
    BitStreamWriter m_writer;
    BitStreamReader m_reader;
};

TEST_F(BitStreamTest, readBits)
{
    m_writer.writeBits(1, 1);
    m_writer.writeBits(2, 2);
    m_writer.writeBits(42, 12);
    m_writer.writeBits(15999999, 24);
    m_writer.writeBits(7, 3);

    ASSERT_EQ(1, m_reader.readBits(1));
    ASSERT_EQ(2, m_reader.readBits(2));
    ASSERT_EQ(42, m_reader.readBits(12));
    ASSERT_EQ(15999999, m_reader.readBits(24));
    ASSERT_EQ(7, m_reader.readBits(3));
}

TEST_F(BitStreamTest, readBits64)
{
    m_writer.writeBits(1, 1);
    m_writer.writeBits64(UINT64_C(42424242424242), 48);
    m_writer.writeBits64(UINT64_C(0xFFFFFFFFFFFFFFFE), 64);

    ASSERT_EQ(1, m_reader.readBits(1));
    ASSERT_EQ(UINT64_C(42424242424242), m_reader.readBits64(48));
    ASSERT_EQ(UINT64_C(0xFFFFFFFFFFFFFFFE), m_reader.readBits64(64));
}

TEST_F(BitStreamTest, readSignedBits)
{
    m_writer.writeSignedBits(-1, 5);
    m_writer.writeSignedBits(3, 12);
    m_writer.writeSignedBits(-142, 9);

    ASSERT_EQ(-1, m_reader.readSignedBits(5));
    ASSERT_EQ(3, m_reader.readSignedBits(12));
    ASSERT_EQ(-142, m_reader.readSignedBits(9));
}

TEST_F(BitStreamTest, readSignedBits64)
{
    m_writer.writeSignedBits64(INT64_C(1), 4);
    m_writer.writeSignedBits64(INT64_C(-1), 48);
    m_writer.writeSignedBits64(INT64_C(-42424242), 61);
    m_writer.writeSignedBits64(INT64_C(-820816), 32);

    ASSERT_EQ(INT64_C(1), m_reader.readSignedBits(4));
    ASSERT_EQ(INT64_C(-1), m_reader.readSignedBits64(48));
    ASSERT_EQ(INT64_C(-42424242), m_reader.readSignedBits64(61));
    ASSERT_EQ(INT64_C(-820816), m_reader.readSignedBits64(32));
}

TEST_F(BitStreamTest, alignedBytes)
{
    // reads aligned data directly from buffer, bit cache should remain empty
    m_writer.writeBits(UINT8_C(0xCA), 8);
    m_writer.writeBits(UINT16_C(0xCAFE), 16);
    m_writer.writeBits(UINT32_C(0xCAFEC0), 24);
    m_writer.writeBits(UINT32_C(0xCAFEC0DE), 32);
    m_writer.writeBits64(UINT64_C(0xCAFEC0DEDE), 40);
    m_writer.writeBits64(UINT64_C(0xCAFEC0DEDEAD), 48);
    m_writer.writeBits64(UINT64_C(0xCAFEC0DEDEADFA), 56);
    m_writer.writeBits64(UINT64_C(0xCAFEC0DEDEADFACE), 64);

    ASSERT_EQ(UINT8_C(0xCA), m_reader.readBits(8));
    ASSERT_EQ(UINT16_C(0xCAFE), m_reader.readBits(16));
    ASSERT_EQ(UINT32_C(0xCAFEC0), m_reader.readBits(24));
    ASSERT_EQ(UINT32_C(0xCAFEC0DE), m_reader.readBits(32));
    ASSERT_EQ(UINT64_C(0xCAFEC0DEDE), m_reader.readBits64(40));
    ASSERT_EQ(UINT64_C(0xCAFEC0DEDEAD), m_reader.readBits64(48));
    ASSERT_EQ(UINT64_C(0xCAFEC0DEDEADFA), m_reader.readBits64(56));
    ASSERT_EQ(UINT64_C(0xCAFEC0DEDEADFACE), m_reader.readBits64(64));
}

TEST_F(BitStreamTest, readVarInt64)
{
    const int64_t values[] =
    {
        0,
        -262144,
        262144,

        ( INT64_C(1) << (0 ) ),
        ( INT64_C(1) << (6 ) ) - 1,

        ( INT64_C(1) << (6 ) ),
        ( INT64_C(1) << (6+7 ) ) - 1,

        ( INT64_C(1) << (6+7 ) ),
        ( INT64_C(1) << (6+7+7 ) ) - 1,

        ( INT64_C(1) << (6+7+7 ) ),
        ( INT64_C(1) << (6+7+7+7 ) ) - 1,

        ( INT64_C(1) << (6+7+7+7 ) ),
        ( INT64_C(1) << (6+7+7+7 +7 ) ) - 1,

        ( INT64_C(1) << (6+7+7+7 +7 ) ),
        ( INT64_C(1) << (6+7+7+7 +7+7 ) ) - 1,

        ( INT64_C(1) << (6+7+7+7 +7+7 ) ),
        ( INT64_C(1) << (6+7+7+7 +7+7+7 ) ) - 1,

        ( INT64_C(1) << (6+7+7+7 +7+7+7 ) ),
        ( INT64_C(1) << (6+7+7+7 +7+7+7+8 ) ) - 1
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarInt64, &BitStreamReader::readVarInt64);
}

TEST_F(BitStreamTest, readVarInt32)
{
    const int32_t values[] =
    {
        0,
        -65536,
        65536,

        ( INT32_C(1) << ( 0 ) ),
        ( INT32_C(1) << ( 6 ) ) - 1,

        ( INT32_C(1) << ( 6 ) ),
        ( INT32_C(1) << ( 6+7 ) ) - 1,

        ( INT32_C(1) << ( 6+7 ) ),
        ( INT32_C(1) << ( 6+7+7 ) ) - 1,

        ( INT32_C(1) << ( 6+7+7 ) ),
        ( INT32_C(1) << ( 6+7+7+8 ) ) - 1,
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarInt32, &BitStreamReader::readVarInt32);
}

TEST_F(BitStreamTest, readVarInt16)
{
    const int16_t values[] =
    {
        0,
        -8192,
        8192,

        ( INT16_C(1) << ( 0 ) ),
        ( INT16_C(1) << ( 6 ) ) - 1,

        ( INT16_C(1) << ( 6 ) ),
        ( INT16_C(1) << ( 6+8 ) ) - 1,
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarInt16, &BitStreamReader::readVarInt16);
}

TEST_F(BitStreamTest, readVarUInt64)
{
    const uint64_t values[] =
    {
        0,
        262144,
        524288,

        ( UINT64_C(1) << ( 0 ) ),
        ( UINT64_C(1) << ( 7 ) ) - 1,

        ( UINT64_C(1) << ( 7 ) ),
        ( UINT64_C(1) << ( 7+7 ) ) - 1,

        ( UINT64_C(1) << ( 7+7 ) ),
        ( UINT64_C(1) << ( 7+7+7 ) ) - 1,

        ( UINT64_C(1) << ( 7+7+7 ) ),
        ( UINT64_C(1) << ( 7+7+7+7 ) ) - 1,

        ( UINT64_C(1) << ( 7+7+7+7 ) ),
        ( UINT64_C(1) << ( 7+7+7+7 +7 ) ) - 1,

        ( UINT64_C(1) << ( 7+7+7+7 +7 ) ),
        ( UINT64_C(1) << ( 7+7+7+7 +7+7 ) ) - 1,

        ( UINT64_C(1) << ( 7+7+7+7 +7+7 ) ),
        ( UINT64_C(1) << ( 7+7+7+7 +7+7+7 ) ) - 1,

        ( UINT64_C(1) << ( 7+7+7+7 +7+7+7 ) ),
        ( UINT64_C(1) << ( 7+7+7+7 +7+7+7+8 ) ) - 1,
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarUInt64, &BitStreamReader::readVarUInt64);
}

TEST_F(BitStreamTest, readVarUInt32)
{
    const uint32_t values[] =
    {
        0,
        65536,
        131072,

        ( UINT32_C(1) << ( 0 ) ),
        ( UINT32_C(1) << ( 7 ) ) - 1,

        ( UINT32_C(1) << ( 7 ) ),
        ( UINT32_C(1) << ( 7+7 ) ) - 1,

        ( UINT32_C(1) << ( 7+7 ) ),
        ( UINT32_C(1) << ( 7+7+7 ) ) - 1,

        ( UINT32_C(1) << ( 7+7+7 ) ),
        ( UINT32_C(1) << ( 7+7+7+8 ) ) - 1,
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarUInt32, &BitStreamReader::readVarUInt32);
}

TEST_F(BitStreamTest, readVarUInt16)
{
    const uint16_t values[] =
    {
        0,
        8192,
        16384,

        ( UINT16_C(1) << ( 0 ) ),
        ( UINT16_C(1) << ( 6 ) ) - 1,

        ( UINT16_C(1) << ( 6 ) ),
        ( UINT16_C(1) << ( 6+8 ) ) - 1,
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarUInt16, &BitStreamReader::readVarUInt16);
}

TEST_F(BitStreamTest, readVarInt)
{
    const int64_t values[] =
    {
        // 1 byte
        0,
        -1,
        1,
        -(INT64_C(1) << 6) + 1,
        (INT64_C(1) << 6) - 1,
        // 2 bytes
        -(INT64_C(1) << 6),
        (INT64_C(1) << 6),
        -(INT64_C(1) << 13) + 1,
        (INT64_C(1) << 13) - 1,
        // 3 bytes
        -(INT64_C(1) << 13),
        (INT64_C(1) << 13),
        -(INT64_C(1) << 20) + 1,
        (INT64_C(1) << 20) - 1,
        // 4 bytes
        -(INT64_C(1) << 20),
        (INT64_C(1) << 20),
        -(INT64_C(1) << 27) + 1,
        (INT64_C(1) << 27) - 1,
        // 5 bytes
        -(INT64_C(1) << 27),
        (INT64_C(1) << 27),
        -(INT64_C(1) << 34) + 1,
        (INT64_C(1) << 34) - 1,
        // 6 bytes
        -(INT64_C(1) << 34),
        (INT64_C(1) << 34),
        -(INT64_C(1) << 41) + 1,
        (INT64_C(1) << 41) - 1,
        // 7 bytes
        -(INT64_C(1) << 41),
        (INT64_C(1) << 41),
        -(INT64_C(1) << 48) + 1,
        (INT64_C(1) << 48) - 1,
        // 8 bytes
        -(INT64_C(1) << 48),
        (INT64_C(1) << 48),
        -(INT64_C(1) << 55) + 1,
        (INT64_C(1) << 55) - 1,
        // 9 bytes
        -(INT64_C(1) << 55),
        (INT64_C(1) << 55),
        INT64_MIN + 1,
        INT64_MAX,

        // special case - stored as -0 (1 byte)
        INT64_MIN,
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarInt, &BitStreamReader::readVarInt);
}

TEST_F(BitStreamTest, readVarUInt)
{
    const uint64_t values[] =
    {
        // 1 byte
        0,
        1,
        (UINT64_C(1) << 7) - 1,
        // 2 bytes
        (UINT64_C(1) << 7),
        (UINT64_C(1) << 14) - 1,
        // 3 bytes
        (UINT64_C(1) << 14),
        (UINT64_C(1) << 21) - 1,
        // 4 bytes
        (UINT64_C(1) << 21),
        (UINT64_C(1) << 28) - 1,
        // 5 bytes
        (UINT64_C(1) << 28),
        (UINT64_C(1) << 35) - 1,
        // 6 bytes
        (UINT64_C(1) << 35),
        (UINT64_C(1) << 42) - 1,
        // 7 bytes
        (UINT64_C(1) << 42),
        (UINT64_C(1) << 49) - 1,
        // 8 bytes
        (UINT64_C(1) << 49),
        (UINT64_C(1) << 56) - 1,
        // 9 bytes
        (UINT64_C(1) << 56),
        UINT64_MAX
    };

    testBitStreamValues(values, &BitStreamWriter::writeVarUInt, &BitStreamReader::readVarUInt);
}

TEST_F(BitStreamTest, readFloat16)
{
    const float values[] = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };
    testBitStreamValues(values, &BitStreamWriter::writeFloat16, &BitStreamReader::readFloat16);
}

TEST_F(BitStreamTest, readFloat32)
{
    const float values[] = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };
    testBitStreamValues(values, &BitStreamWriter::writeFloat32, &BitStreamReader::readFloat32);
}

TEST_F(BitStreamTest, readFloat64)
{
    const double values[] = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };
    testBitStreamValues(values, &BitStreamWriter::writeFloat64, &BitStreamReader::readFloat64);
}

TEST_F(BitStreamTest, readString)
{
    const std::string values[] =
    {
        "Hello World",
        "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2\0nonWrittenPart",
        "Price: \xE2\x82\xAC 3 what's this? -> \xC2\xA2" /* '€' '¢' */
    };

    testBitStreamValues(values, &BitStreamWriter::writeString, &BitStreamReader::readString);
}

TEST_F(BitStreamTest, readBool)
{
    const bool values[] = {true, false};
    testBitStreamValues(values, &BitStreamWriter::writeBool, &BitStreamReader::readBool);
}

TEST_F(BitStreamTest, setBitPosition)
{
    ASSERT_EQ(0, m_writer.getBitPosition());
    m_writer.writeBits(1, 1);
    ASSERT_EQ(1, m_writer.getBitPosition());
    m_writer.alignTo(4);
    ASSERT_EQ(4, m_writer.getBitPosition());
    m_writer.writeBits(5, 5);
    ASSERT_EQ(9, m_writer.getBitPosition());
    ASSERT_THROW(m_writer.setBitPosition(bufferSize * 8 + 1), BitStreamException);
    m_writer.setBitPosition(4);
    ASSERT_EQ(4, m_writer.getBitPosition());
    m_writer.writeBits(3, 3);
    ASSERT_EQ(7, m_writer.getBitPosition());

    ASSERT_EQ(0, m_reader.getBitPosition());
    ASSERT_EQ(1, m_reader.readBits(1));
    ASSERT_EQ(1, m_reader.getBitPosition());
    m_reader.alignTo(4);
    ASSERT_EQ(4, m_reader.getBitPosition());
    ASSERT_EQ(3, m_reader.readBits(3));
    ASSERT_EQ(7, m_reader.getBitPosition());
    ASSERT_THROW(m_reader.setBitPosition(bufferSize * 8 + 1), BitStreamException);

    m_reader.setBitPosition(4);
    ASSERT_EQ(4, m_reader.getBitPosition());
    ASSERT_EQ(3, m_reader.readBits(3));
    ASSERT_EQ(7, m_reader.getBitPosition());
}

TEST_F(BitStreamTest, alignTo)
{
    m_writer.writeBits(1, 1);
    m_writer.alignTo(4);
    ASSERT_EQ(4, m_writer.getBitPosition());
    m_writer.writeBits(1, 1);
    m_writer.alignTo(4);
    ASSERT_EQ(8, m_writer.getBitPosition());
    m_writer.writeBits(37, 11);
    m_writer.alignTo(8);
    ASSERT_EQ(24, m_writer.getBitPosition());
    m_writer.writeBits(1, 1);
    m_writer.alignTo(16);
    ASSERT_EQ(32, m_writer.getBitPosition());
    m_writer.writeBits(13, 13);
    m_writer.alignTo(32);
    ASSERT_EQ(64, m_writer.getBitPosition());
    m_writer.writeBits(42, 15);
    m_writer.alignTo(64);
    ASSERT_EQ(128, m_writer.getBitPosition());
    m_writer.writeBits(99, 9);
    ASSERT_EQ(137, m_writer.getBitPosition());

    ASSERT_EQ(1, m_reader.readBits(1));
    m_reader.alignTo(4);
    ASSERT_EQ(1, m_reader.readBits(1));
    m_reader.alignTo(4);
    ASSERT_EQ(37, m_reader.readBits(11));
    m_reader.alignTo(8);
    ASSERT_EQ(1, m_reader.readBits(1));
    m_reader.alignTo(16);
    ASSERT_EQ(13, m_reader.readBits(13));
    m_reader.alignTo(32);
    ASSERT_EQ(42, m_reader.readBits(15));
    m_reader.alignTo(64);
    ASSERT_EQ(99, m_reader.readBits(9));
    ASSERT_EQ(137, m_writer.getBitPosition());
}

} // namespace zserio
