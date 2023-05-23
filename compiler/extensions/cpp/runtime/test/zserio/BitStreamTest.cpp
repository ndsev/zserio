#include <cstring>
#include <string>
#include <functional>
#include <array>

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/Types.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/Vector.h"

#include "gtest/gtest.h"

namespace zserio
{

class BitStreamTest : public ::testing::Test
{
public:
    BitStreamTest() : m_byteBuffer(), m_externalWriter(m_byteBuffer.data(), m_byteBuffer.size()),
            m_dummyWriter(nullptr, 0)
    {
        m_byteBuffer.fill(0);
    }

protected:
    template <typename T, size_t N, typename U>
    void testImpl(const std::array<T, N>& values, std::function<void (BitStreamWriter&, U)> writerFunc,
            std::function<T(BitStreamReader&)> readerFunc, uint8_t maxStartBitPos)
    {
        testBitStreamValues(values, m_externalWriter, writerFunc, readerFunc, maxStartBitPos);
        testBitStreamValues(values, m_dummyWriter, writerFunc, readerFunc, maxStartBitPos);
    }

    template <typename T, size_t N, typename U>
    void testBitStreamValues(const std::array<T, N>& values, BitStreamWriter& writer,
            std::function<void (BitStreamWriter&, U)> writerFunc,
            std::function<T(BitStreamReader&)> readerFunc, uint8_t maxStartBitPos)
    {
        for (uint8_t bitPos = 0; bitPos < maxStartBitPos; ++bitPos)
        {
            if (bitPos > 0)
                writer.writeBits64(0, bitPos);
            for (size_t i = 0; i < N; ++i)
            {
                writerFunc(writer, values.at(i));
            }

            if (!writer.hasWriteBuffer())
                continue;

            BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
            if (bitPos > 0)
                reader.readBits64(bitPos);
            for (size_t i = 0; i < N; ++i)
            {
                ASSERT_EQ(readerFunc(reader), values.at(i)) << "[bitPos=" << bitPos << "]";
            }

            writer.setBitPosition(0);
            m_byteBuffer.fill(0);
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

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
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

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
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

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
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

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
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

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
        ASSERT_EQ(UINT8_C(0xCA), reader.readBits(8));
        ASSERT_EQ(UINT16_C(0xCAFE), reader.readBits(16));
        ASSERT_EQ(UINT32_C(0xCAFEC0), reader.readBits(24));
        ASSERT_EQ(UINT32_C(0xCAFEC0DE), reader.readBits(32));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDE), reader.readBits64(40));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDEAD), reader.readBits64(48));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDEADFA), reader.readBits64(56));
        ASSERT_EQ(UINT64_C(0xCAFEC0DEDEADFACE), reader.readBits64(64));
    }

    void testSetBitPosition(BitStreamWriter& writer)
    {
        ASSERT_EQ(0, writer.getBitPosition());
        writer.writeBits(1, 1);
        ASSERT_EQ(1, writer.getBitPosition());
        writer.alignTo(4);
        ASSERT_EQ(4, writer.getBitPosition());
        writer.writeBits(5, 5);
        ASSERT_EQ(9, writer.getBitPosition());
        if (writer.hasWriteBuffer())
        {
            ASSERT_THROW(writer.setBitPosition(m_byteBuffer.size() * 8 + 1), CppRuntimeException);
        }
        else
        {
            // dummy buffer
            writer.setBitPosition(m_byteBuffer.size() * 8 + 1);
            ASSERT_EQ(m_byteBuffer.size() * 8 + 1, writer.getBitPosition());
        }
        writer.setBitPosition(4);
        ASSERT_EQ(4, writer.getBitPosition());
        writer.writeBits(3, 3);
        ASSERT_EQ(7, writer.getBitPosition());

        if (!writer.hasWriteBuffer())
            return;

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
        ASSERT_EQ(0, reader.getBitPosition());
        ASSERT_EQ(1, reader.readBits(1));
        ASSERT_EQ(1, reader.getBitPosition());
        reader.alignTo(4);
        ASSERT_EQ(4, reader.getBitPosition());
        ASSERT_EQ(3, reader.readBits(3));
        ASSERT_EQ(7, reader.getBitPosition());
        ASSERT_THROW(reader.setBitPosition(writer.getBitPosition() + 1), CppRuntimeException);

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

        BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag());
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

    std::array<uint8_t, 256> m_byteBuffer;
    BitStreamWriter m_externalWriter;
    BitStreamWriter m_dummyWriter;
};

TEST_F(BitStreamTest, readBits)
{
    testReadBits(m_externalWriter);
    testReadBits(m_dummyWriter);
}

TEST_F(BitStreamTest, readBits64)
{
    testReadBits64(m_externalWriter);
    testReadBits64(m_dummyWriter);
}

TEST_F(BitStreamTest, readSignedBits)
{
    testReadSignedBits(m_externalWriter);
    testReadSignedBits(m_dummyWriter);
}

TEST_F(BitStreamTest, readSignedBits64)
{
    testReadSignedBits64(m_externalWriter);
    testReadSignedBits64(m_dummyWriter);
}

TEST_F(BitStreamTest, alignedBytes)
{
    testAlignedBytes(m_externalWriter);
    testAlignedBytes(m_dummyWriter);
}

TEST_F(BitStreamTest, readVarInt64)
{
    const std::array<int64_t, 33> values =
    {
        INT64_C(0),
        INT64_C(-32),
        INT64_C(32),
        INT64_C(-4096),
        INT64_C(4096),
        INT64_C(-524288),
        INT64_C(524288),
        INT64_C(-67108864),
        INT64_C(67108864),
        INT64_C(-8589934592),
        INT64_C(8589934592),
        INT64_C(-1099511627776),
        INT64_C(1099511627776),
        INT64_C(-140737488355328),
        INT64_C(140737488355328),
        INT64_C(-18014398509481984),
        INT64_C(18014398509481984),

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

    std::function<void (BitStreamWriter&, int64_t)> writerFunc = &BitStreamWriter::writeVarInt64;
    std::function<int64_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarInt64;

    testImpl(values, writerFunc, readerFunc, 63);
}

TEST_F(BitStreamTest, readVarInt32)
{
    const std::array<int32_t, 17> values =
    {
        static_cast<int32_t>(0),
        static_cast<int32_t>(-32),
        static_cast<int32_t>(32),
        static_cast<int32_t>(-4096),
        static_cast<int32_t>(4096),
        static_cast<int32_t>(-524288),
        static_cast<int32_t>(524288),
        static_cast<int32_t>(-67108864),
        static_cast<int32_t>(67108864),

        static_cast<int32_t>(1U << (0U)),
        static_cast<int32_t>(1U << (6U)) - 1,

        static_cast<int32_t>(1U << (6U)),
        static_cast<int32_t>(1U << (6U+7)) - 1,

        static_cast<int32_t>(1U << (6U+7)),
        static_cast<int32_t>(1U << (6U+7+7)) - 1,

        static_cast<int32_t>(1U << (6U+7+7)),
        static_cast<int32_t>(1U << (6U+7+7+8)) - 1,
    };

    std::function<void (BitStreamWriter&, int32_t)> writerFunc = &BitStreamWriter::writeVarInt32;
    std::function<int32_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarInt32;

    testImpl(values, writerFunc, readerFunc, 31);
}

TEST_F(BitStreamTest, readVarInt16)
{
    const std::array<int16_t, 9> values =
    {
        static_cast<int16_t>(0),
        static_cast<int16_t>(-32),
        static_cast<int16_t>(32),
        static_cast<int16_t>(-4096),
        static_cast<int16_t>(4096),

        static_cast<int16_t>(1U << (0U)),
        static_cast<int16_t>(1U << (6U)) - 1,

        static_cast<int16_t>(1U << (6U)),
        static_cast<int16_t>(1U << (6+8U)) - 1,
    };

    std::function<void (BitStreamWriter&, int16_t)> writerFunc = &BitStreamWriter::writeVarInt16;
    std::function<int16_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarInt16;

    testImpl(values, writerFunc, readerFunc, 15);
}

TEST_F(BitStreamTest, readVarUInt64)
{
    const std::array<uint64_t, 19> values =
    {
        0,
        262144,
        524288,

        (UINT64_C(1) << (0U)),
        (UINT64_C(1) << (7U)) - 1,

        (UINT64_C(1) << (7U)),
        (UINT64_C(1) << (7U+7)) - 1,

        (UINT64_C(1) << (7U+7)),
        (UINT64_C(1) << (7U+7+7)) - 1,

        (UINT64_C(1) << (7U+7+7)),
        (UINT64_C(1) << (7U+7+7+7)) - 1,

        (UINT64_C(1) << (7U+7+7+7)),
        (UINT64_C(1) << (7U+7+7+7 +7)) - 1,

        (UINT64_C(1) << (7U+7+7+7 +7)),
        (UINT64_C(1) << (7U+7+7+7 +7+7)) - 1,

        (UINT64_C(1) << (7U+7+7+7 +7+7)),
        (UINT64_C(1) << (7U+7+7+7 +7+7+7)) - 1,

        (UINT64_C(1) << (7U+7+7+7 +7+7+7)),
        (UINT64_C(1) << (7U+7+7+7 +7+7+7+8)) - 1,
    };

    std::function<void (BitStreamWriter&, uint64_t)> writerFunc = &BitStreamWriter::writeVarUInt64;
    std::function<uint64_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarUInt64;

    testImpl(values, writerFunc, readerFunc, 63);
}

TEST_F(BitStreamTest, readVarUInt32)
{
    const std::array<uint32_t, 11> values =
    {
        0,
        65536,
        131072,

        (1U << (0U)),
        (1U << (7U)) - 1,

        (1U << (7U)),
        (1U << (7U+7)) - 1,

        (1U << (7U+7)),
        (1U << (7U+7+7)) - 1,

        (1U << (7U+7+7)),
        (1U << (7U+7+7+8)) - 1,
    };

    std::function<void (BitStreamWriter&, uint32_t)> writerFunc = &BitStreamWriter::writeVarUInt32;
    std::function<uint32_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarUInt32;

    testImpl(values, writerFunc, readerFunc, 31);
}

TEST_F(BitStreamTest, readVarUInt16)
{
    const std::array<uint16_t, 7> values =
    {
        0,
        8192,
        16384,

        (1U << (0U)),
        (1U << (6U)) - 1,

        (1U << (6U)),
        (1U << (6U+8)) - 1,
    };

    std::function<void (BitStreamWriter&, uint16_t)> writerFunc = &BitStreamWriter::writeVarUInt16;
    std::function<uint16_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarUInt16;

    testImpl(values, writerFunc, readerFunc, 15);
}

TEST_F(BitStreamTest, readVarInt)
{
    const std::array<int64_t, 38> values =
    {
        // 1 byte
        0,
        -1,
        1,
        -static_cast<int64_t>(UINT64_C(1) << 6U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 6U) - 1,
        // 2 bytes
        -static_cast<int64_t>(UINT64_C(1) << 6U),
        static_cast<int64_t>(UINT64_C(1) << 6U),
        -static_cast<int64_t>(UINT64_C(1) << 13U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 13U) - 1,
        // 3 bytes
        -static_cast<int64_t>(UINT64_C(1) << 13U),
        static_cast<int64_t>(UINT64_C(1) << 13U),
        -static_cast<int64_t>(UINT64_C(1) << 20U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 20U) - 1,
        // 4 bytes
        -static_cast<int64_t>(UINT64_C(1) << 20U),
        static_cast<int64_t>(UINT64_C(1) << 20U),
        -static_cast<int64_t>(UINT64_C(1) << 27U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 27U) - 1,
        // 5 bytes
        -static_cast<int64_t>(UINT64_C(1) << 27U),
        static_cast<int64_t>(UINT64_C(1) << 27U),
        -static_cast<int64_t>(UINT64_C(1) << 34U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 34U) - 1,
        // 6 bytes
        -static_cast<int64_t>(UINT64_C(1) << 34U),
        static_cast<int64_t>(UINT64_C(1) << 34U),
        -static_cast<int64_t>(UINT64_C(1) << 41U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 41U) - 1,
        // 7 bytes
        -static_cast<int64_t>(UINT64_C(1) << 41U),
        static_cast<int64_t>(UINT64_C(1) << 41U),
        -static_cast<int64_t>(UINT64_C(1) << 48U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 48U) - 1,
        // 8 bytes
        -static_cast<int64_t>(UINT64_C(1) << 48U),
        static_cast<int64_t>(UINT64_C(1) << 48U),
        -static_cast<int64_t>(UINT64_C(1) << 55U) + 1,
        static_cast<int64_t>(UINT64_C(1) << 55U) - 1,
        // 9 bytes
        -static_cast<int64_t>(UINT64_C(1) << 55U),
        static_cast<int64_t>(UINT64_C(1) << 55U),
        INT64_MIN + 1,
        INT64_MAX,

        // special case - stored as -0 (1 byte)
        INT64_MIN,
    };

    std::function<void (BitStreamWriter&, int64_t)> writerFunc = &BitStreamWriter::writeVarInt;
    std::function<int64_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarInt;

    testImpl(values, writerFunc, readerFunc, 63);
}

TEST_F(BitStreamTest, readVarUInt)
{
    const std::array<uint64_t, 19> values =
    {
        // 1 byte
        0,
        1,
        (UINT64_C(1) << 7U) - 1,
        // 2 bytes
        (UINT64_C(1) << 7U),
        (UINT64_C(1) << 14U) - 1,
        // 3 bytes
        (UINT64_C(1) << 14U),
        (UINT64_C(1) << 21U) - 1,
        // 4 bytes
        (UINT64_C(1) << 21U),
        (UINT64_C(1) << 28U) - 1,
        // 5 bytes
        (UINT64_C(1) << 28U),
        (UINT64_C(1) << 35U) - 1,
        // 6 bytes
        (UINT64_C(1) << 35U),
        (UINT64_C(1) << 42U) - 1,
        // 7 bytes
        (UINT64_C(1) << 42U),
        (UINT64_C(1) << 49U) - 1,
        // 8 bytes
        (UINT64_C(1) << 49U),
        (UINT64_C(1) << 56U) - 1,
        // 9 bytes
        (UINT64_C(1) << 56U),
        UINT64_MAX
    };

    std::function<void (BitStreamWriter&, uint64_t)> writerFunc = &BitStreamWriter::writeVarUInt;
    std::function<uint64_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarUInt;

    testImpl(values, writerFunc, readerFunc, 63);
}

TEST_F(BitStreamTest, readVarSize)
{
    const std::array<uint32_t, 13> values =
    {
        0,
        65536,
        131072,

        (1U << (0U)),
        (1U << (7U)) - 1,

        (1U << (7U)),
        (1U << (7U+7)) - 1,

        (1U << (7U+7)),
        (1U << (7U+7+7)) - 1,

        (1U << (7U+7+7)),
        (1U << (7U+7+7+7)) - 1,

        (1U << (7U+7+7+7)),
        (1U << (7U+7+7+7+3)) - 1,
    };

    std::function<void (BitStreamWriter&, uint32_t)> writerFunc = &BitStreamWriter::writeVarSize;
    std::function<uint32_t(BitStreamReader&)> readerFunc = &BitStreamReader::readVarSize;

    testImpl(values, writerFunc, readerFunc, 31);
}

TEST_F(BitStreamTest, readFloat16)
{
    const std::array<float, 6> values = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };

    std::function<void (BitStreamWriter&, float)> writerFunc = &BitStreamWriter::writeFloat16;
    std::function<float(BitStreamReader&)> readerFunc = &BitStreamReader::readFloat16;

    testImpl(values, writerFunc, readerFunc, 15);
}

TEST_F(BitStreamTest, readFloat32)
{
    const std::array<float, 6> values = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };

    std::function<void (BitStreamWriter&, float)> writerFunc = &BitStreamWriter::writeFloat32;
    std::function<float(BitStreamReader&)> readerFunc = &BitStreamReader::readFloat32;

    testImpl(values, writerFunc, readerFunc, 31);
}

TEST_F(BitStreamTest, readFloat64)
{
    const std::array<double, 6> values = { 2.0, -2.0, 0.6171875, 0.875, 9.875, 42.5 };

    std::function<void (BitStreamWriter&, double)> writerFunc = &BitStreamWriter::writeFloat64;
    std::function<double(BitStreamReader&)> readerFunc = &BitStreamReader::readFloat64;

    testImpl(values, writerFunc, readerFunc, 61);
}

TEST_F(BitStreamTest, readString)
{
    const std::array<std::string, 3> values =
    {
        "Hello World",
        "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2\0nonWrittenPart",
        "Price: \xE2\x82\xAC 3 what's this? -> \xC2\xA2" /* '€' '¢' */
    };

    std::function<void (BitStreamWriter&, const std::string&)> writerFunc = &BitStreamWriter::writeString;
    std::function<std::string (BitStreamReader&)> readerFunc =
            std::bind(&BitStreamReader::readString<std::allocator<char>>,
                    std::placeholders::_1, std::allocator<char>());

    testImpl(values, writerFunc, readerFunc, 7);
}

TEST_F(BitStreamTest, readBool)
{
    const std::array<bool, 2> values = {true, false};

    std::function<void (BitStreamWriter&, bool)> writerFunc = &BitStreamWriter::writeBool;
    std::function<bool(BitStreamReader&)> readerFunc = &BitStreamReader::readBool;

    testImpl(values, writerFunc, readerFunc, 1);
}

TEST_F(BitStreamTest, readBitBuffer)
{
    const std::array<BitBuffer, 2> values =
    {
        BitBuffer(std::vector<uint8_t>({0xAB, 0xE0}), 11),
        BitBuffer(std::vector<uint8_t>({0xAB, 0xCD, 0xFE}), 23)
    };

    std::function<void (BitStreamWriter&, const BitBuffer&)> writerFunc =
                &BitStreamWriter::writeBitBuffer<std::allocator<uint8_t>>;
    std::function<BitBuffer (BitStreamReader&)> readerFunc =
            std::bind(&BitStreamReader::readBitBuffer<
                    std::allocator<uint8_t>>, std::placeholders::_1, std::allocator<uint8_t>());

    testImpl(values, writerFunc, readerFunc, 7);
}

TEST_F(BitStreamTest, readBytes)
{
    const std::array<vector<uint8_t>, 2> values =
    {
        vector<uint8_t>{{0, 255}},
        vector<uint8_t>{{1, 127, 128, 254}},
    };

    std::function<void (BitStreamWriter&, const vector<uint8_t>&)> writerFunc =
                &BitStreamWriter::writeBytes;
    std::function<vector<uint8_t> (BitStreamReader&)> readerFunc =
            std::bind(&BitStreamReader::readBytes<
                    std::allocator<uint8_t>>, std::placeholders::_1, std::allocator<uint8_t>());

    testImpl(values, writerFunc, readerFunc, 7);
}

TEST_F(BitStreamTest, setBitPosition)
{
    testSetBitPosition(m_externalWriter);
    testSetBitPosition(m_dummyWriter);
}

TEST_F(BitStreamTest, alignTo)
{
    testAlignTo(m_externalWriter);
    testAlignTo(m_dummyWriter);
}

} // namespace zserio
