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
    BitStreamTest() : m_externalWriter(m_byteBuffer, BUFFER_SIZE), m_dummyWriter(NULL, 0)
    {
        memset(m_byteBuffer, 0, BUFFER_SIZE);
    }

protected:
    template <typename T, size_t N, typename U>
    void testBitStreamValues(const T (&values)[N], BitStreamWriter& writer,
            void (BitStreamWriter::*writerMethod)(U value), T (BitStreamReader::*readerMethod)())
    {
        for (size_t i = 0; i < N; ++i)
        {
            (writer.*writerMethod)(values[i]);
        }

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        for (size_t i = 0; i < N; ++i)
        {
            ASSERT_EQ((reader.*readerMethod)(), values[i]);
        }
    }

    void testReadBits(BitStreamWriter& writer)
    {
        writer.writeBits(1, 1);
        writer.writeBits(2, 2);
        writer.writeBits(42, 12);
        writer.writeBits(15999999, 24);
        writer.writeBits(7, 3);

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(1, reader.readBits(1));
        ASSERT_EQ(2, reader.readBits(2));
        ASSERT_EQ(42, reader.readBits(12));
        ASSERT_EQ(15999999, reader.readBits(24));
        ASSERT_EQ(7, reader.readBits(3));
    }

    void testReadBits64(BitStreamWriter& writer)
    {
        writer.writeBits(1, 1);
        writer.writeBits64(UINT64_C(42424242424242), 48);
        writer.writeBits64(UINT64_C(0xFFFFFFFFFFFFFFFE), 64);

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(1, reader.readBits(1));
        ASSERT_EQ(UINT64_C(42424242424242), reader.readBits64(48));
        ASSERT_EQ(UINT64_C(0xFFFFFFFFFFFFFFFE), reader.readBits64(64));
    }

    void testReadSignedBits(BitStreamWriter& writer)
    {
        writer.writeSignedBits(-1, 5);
        writer.writeSignedBits(3, 12);
        writer.writeSignedBits(-142, 9);

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(-1, reader.readSignedBits(5));
        ASSERT_EQ(3, reader.readSignedBits(12));
        ASSERT_EQ(-142, reader.readSignedBits(9));
    }

    void testReadSignedBits64(BitStreamWriter& writer)
    {
        writer.writeSignedBits64(INT64_C(1), 4);
        writer.writeSignedBits64(INT64_C(-1), 48);
        writer.writeSignedBits64(INT64_C(-42424242), 61);
        writer.writeSignedBits64(INT64_C(-820816), 32);

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(INT64_C(1), reader.readSignedBits(4));
        ASSERT_EQ(INT64_C(-1), reader.readSignedBits64(48));
        ASSERT_EQ(INT64_C(-42424242), reader.readSignedBits64(61));
        ASSERT_EQ(INT64_C(-820816), reader.readSignedBits64(32));
    }

    void testAlignedBytes(BitStreamWriter& writer)
    {
        // reads aligned data directly from buffer, bit cache should remain empty
        writer.writeBits(UINT8_C(0xCA), 8);
        writer.writeBits(UINT16_C(0xCAFE), 16);
        writer.writeBits(UINT32_C(0xCAFEC0), 24);
        writer.writeBits(UINT32_C(0xCAFEC0DE), 32);
        writer.writeBits64(UINT64_C(0xCAFEC0DEDE), 40);
        writer.writeBits64(UINT64_C(0xCAFEC0DEDEAD), 48);
        writer.writeBits64(UINT64_C(0xCAFEC0DEDEADFA), 56);
        writer.writeBits64(UINT64_C(0xCAFEC0DEDEADFACE), 64);

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(UINT8_C(0xCA), reader.readBits(8));
        ASSERT_EQ(UINT16_C(0xCAFE), reader.readBits(16));
        ASSERT_EQ(UINT32_C(0xCAFEC0), reader.readBits(24));
        ASSERT_EQ(UINT32_C(0xCAFEC0DE), reader.readBits(32));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDE), reader.readBits64(40));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDEAD), reader.readBits64(48));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDEADFA), reader.readBits64(56));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDEADFACE), reader.readBits64(64));
    }

    void testSetBitPosition(BitStreamWriter& writer, bool isInternal)
    {
        ASSERT_EQ(0, writer.getBitPosition());
        writer.writeBits(1, 1);
        ASSERT_EQ(1, writer.getBitPosition());
        writer.alignTo(4);
        ASSERT_EQ(4, writer.getBitPosition());
        writer.writeBits(5, 5);
        ASSERT_EQ(9, writer.getBitPosition());
        if (!isInternal)
        {
            if (writer.hasWriteBuffer())
            {
                ASSERT_THROW(writer.setBitPosition(BUFFER_SIZE * 8 + 1), BitStreamException);
            }
            else
            {
                // dummy buffer
                writer.setBitPosition(BUFFER_SIZE * 8 + 1);
                ASSERT_EQ(BUFFER_SIZE * 8 + 1, writer.getBitPosition());
            }
        }
        writer.setBitPosition(4);
        ASSERT_EQ(4, writer.getBitPosition());
        writer.writeBits(3, 3);
        ASSERT_EQ(7, writer.getBitPosition());

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(0, reader.getBitPosition());
        ASSERT_EQ(1, reader.readBits(1));
        ASSERT_EQ(1, reader.getBitPosition());
        reader.alignTo(4);
        ASSERT_EQ(4, reader.getBitPosition());
        ASSERT_EQ(3, reader.readBits(3));
        ASSERT_EQ(7, reader.getBitPosition());
        ASSERT_THROW(reader.setBitPosition(writeBufferSize * 8 + 1), BitStreamException);

        reader.setBitPosition(4);
        ASSERT_EQ(4, reader.getBitPosition());
        ASSERT_EQ(3, reader.readBits(3));
        ASSERT_EQ(7, reader.getBitPosition());
    }

    void testAlignTo(BitStreamWriter& writer)
    {
        writer.writeBits(1, 1);
        writer.alignTo(4);
        ASSERT_EQ(4, writer.getBitPosition());
        writer.writeBits(1, 1);
        writer.alignTo(4);
        ASSERT_EQ(8, writer.getBitPosition());
        writer.writeBits(37, 11);
        writer.alignTo(8);
        ASSERT_EQ(24, writer.getBitPosition());
        writer.writeBits(1, 1);
        writer.alignTo(16);
        ASSERT_EQ(32, writer.getBitPosition());
        writer.writeBits(13, 13);
        writer.alignTo(32);
        ASSERT_EQ(64, writer.getBitPosition());
        writer.writeBits(42, 15);
        writer.alignTo(64);
        ASSERT_EQ(128, writer.getBitPosition());
        writer.writeBits(99, 9);
        ASSERT_EQ(137, writer.getBitPosition());

        if (!writer.hasWriteBuffer())
            return;

        size_t writeBufferSize = 0;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferSize);
        BitStreamReader reader(writeBuffer, writeBufferSize);
        ASSERT_EQ(1, reader.readBits(1));
        reader.alignTo(4);
        ASSERT_EQ(1, reader.readBits(1));
        reader.alignTo(4);
        ASSERT_EQ(37, reader.readBits(11));
        reader.alignTo(8);
        ASSERT_EQ(1, reader.readBits(1));
        reader.alignTo(16);
        ASSERT_EQ(13, reader.readBits(13));
        reader.alignTo(32);
        ASSERT_EQ(42, reader.readBits(15));
        reader.alignTo(64);
        ASSERT_EQ(99, reader.readBits(9));
        ASSERT_EQ(137, reader.getBitPosition());
    }

    static const size_t BUFFER_SIZE = 256;

private:
    uint8_t m_byteBuffer[BUFFER_SIZE];

protected:
    BitStreamWriter m_externalWriter;
    BitStreamWriter m_internalWriter;
    BitStreamWriter m_dummyWriter;
};

TEST_F(BitStreamTest, readBits)
{
    testReadBits(m_externalWriter);
    testReadBits(m_internalWriter);
    testReadBits(m_dummyWriter);
}

TEST_F(BitStreamTest, readBits64)
{
    testReadBits64(m_externalWriter);
    testReadBits64(m_internalWriter);
    testReadBits64(m_dummyWriter);
}

TEST_F(BitStreamTest, readSignedBits)
{
    testReadSignedBits(m_externalWriter);
    testReadSignedBits(m_internalWriter);
    testReadSignedBits(m_dummyWriter);
}

TEST_F(BitStreamTest, readSignedBits64)
{
    testReadSignedBits64(m_externalWriter);
    testReadSignedBits64(m_internalWriter);
    testReadSignedBits64(m_dummyWriter);
}

TEST_F(BitStreamTest, alignedBytes)
{
    testAlignedBytes(m_externalWriter);
    testAlignedBytes(m_internalWriter);
    testAlignedBytes(m_dummyWriter);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarInt64, &BitStreamReader::readVarInt64);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarInt64, &BitStreamReader::readVarInt64);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarInt64, &BitStreamReader::readVarInt64);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarInt32, &BitStreamReader::readVarInt32);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarInt32, &BitStreamReader::readVarInt32);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarInt32, &BitStreamReader::readVarInt32);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarInt16, &BitStreamReader::readVarInt16);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarInt16, &BitStreamReader::readVarInt16);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarInt16, &BitStreamReader::readVarInt16);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarUInt64, &BitStreamReader::readVarUInt64);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarUInt64, &BitStreamReader::readVarUInt64);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarUInt64, &BitStreamReader::readVarUInt64);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarUInt32, &BitStreamReader::readVarUInt32);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarUInt32, &BitStreamReader::readVarUInt32);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarUInt32, &BitStreamReader::readVarUInt32);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarUInt16, &BitStreamReader::readVarUInt16);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarUInt16, &BitStreamReader::readVarUInt16);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarUInt16, &BitStreamReader::readVarUInt16);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarInt, &BitStreamReader::readVarInt);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarInt, &BitStreamReader::readVarInt);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarInt, &BitStreamReader::readVarInt);
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

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeVarUInt, &BitStreamReader::readVarUInt);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeVarUInt, &BitStreamReader::readVarUInt);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeVarUInt, &BitStreamReader::readVarUInt);
}

TEST_F(BitStreamTest, readFloat16)
{
    const float values[] = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };
    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeFloat16, &BitStreamReader::readFloat16);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeFloat16, &BitStreamReader::readFloat16);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeFloat16, &BitStreamReader::readFloat16);
}

TEST_F(BitStreamTest, readFloat32)
{
    const float values[] = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };
    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeFloat32, &BitStreamReader::readFloat32);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeFloat32, &BitStreamReader::readFloat32);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeFloat32, &BitStreamReader::readFloat32);
}

TEST_F(BitStreamTest, readFloat64)
{
    const double values[] = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };
    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeFloat64, &BitStreamReader::readFloat64);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeFloat64, &BitStreamReader::readFloat64);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeFloat64, &BitStreamReader::readFloat64);
}

TEST_F(BitStreamTest, readString)
{
    const std::string values[] =
    {
        "Hello World",
        "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2\0nonWrittenPart",
        "Price: \xE2\x82\xAC 3 what's this? -> \xC2\xA2" /* '€' '¢' */
    };

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeString, &BitStreamReader::readString);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeString, &BitStreamReader::readString);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeString, &BitStreamReader::readString);
}

TEST_F(BitStreamTest, readBool)
{
    const bool values[] = {true, false};
    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeBool, &BitStreamReader::readBool);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeBool, &BitStreamReader::readBool);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeBool, &BitStreamReader::readBool);
}

TEST_F(BitStreamTest, readBitBuffer)
{
    const BitBuffer values[] =
    {
        BitBuffer(std::vector<uint8_t>({0xAB, 0x07}), 3),
        BitBuffer(std::vector<uint8_t>({0xAB, 0xCD, 0x7F}), 7)
    };

    testBitStreamValues(values, m_externalWriter,
            &BitStreamWriter::writeBitBuffer, &BitStreamReader::readBitBuffer);
    testBitStreamValues(values, m_internalWriter,
            &BitStreamWriter::writeBitBuffer, &BitStreamReader::readBitBuffer);
    testBitStreamValues(values, m_dummyWriter,
            &BitStreamWriter::writeBitBuffer, &BitStreamReader::readBitBuffer);
}

TEST_F(BitStreamTest, setBitPosition)
{
    testSetBitPosition(m_externalWriter, false);
    testSetBitPosition(m_internalWriter, true);
    testSetBitPosition(m_dummyWriter, false);
}

TEST_F(BitStreamTest, alignTo)
{
    testAlignTo(m_externalWriter);
    testAlignTo(m_internalWriter);
    testAlignTo(m_dummyWriter);
}

} // namespace zserio
