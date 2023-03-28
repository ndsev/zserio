#include <cstring>

#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

class BitStreamWriterTest : public ::testing::Test
{
public:
    BitStreamWriterTest() :
        m_externalBufferWriter(m_externalBuffer, EXTERNAL_BUFFER_SIZE),
        m_dummyBufferWriter(nullptr, 0)
    {
        memset(m_externalBuffer, 0, sizeof(m_externalBuffer) / sizeof(m_externalBuffer[0]));
    }

protected:
    BitStreamWriter m_externalBufferWriter;
    BitStreamWriter m_dummyBufferWriter;

    static const size_t EXTERNAL_BUFFER_SIZE = 512;

    uint8_t m_externalBuffer[EXTERNAL_BUFFER_SIZE];
};

const size_t BitStreamWriterTest::EXTERNAL_BUFFER_SIZE;

TEST_F(BitStreamWriterTest, spanConstructor)
{
    uint8_t data[] = { 0x00, 0x00 };
    const Span<uint8_t> span(data);
    BitStreamWriter writer(span);

    ASSERT_EQ(span.data(), writer.getWriteBuffer());
    ASSERT_EQ(span.size() * 8, writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getWriteBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0xFFFF, 16), CppRuntimeException);
    writer.writeBits(0x07, 3);
    writer.writeBits(0x00, 5);
    ASSERT_EQ(0xE0, writer.getWriteBuffer()[1]);
}

TEST_F(BitStreamWriterTest, bitBufferConstructor)
{
    BitBuffer bitBuffer(11);
    BitStreamWriter writer(bitBuffer);

    ASSERT_EQ(bitBuffer.getBuffer(), writer.getWriteBuffer());
    ASSERT_EQ(bitBuffer.getBitSize(), writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getWriteBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0x0F, 4), CppRuntimeException);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xE0, writer.getWriteBuffer()[1]);
}

TEST_F(BitStreamWriterTest, writeUnalignedData)
{
    // number expected to be written at offset
    const uint8_t testValue = 123;

    for (uint8_t offset = 0; offset <= 64; ++offset)
    {
        BitBuffer bitBuffer(8 + offset);
        // fill the buffer with 1s to check proper masking
        std::memset(bitBuffer.getBuffer(), 0xFF, bitBuffer.getByteSize());

        BitStreamWriter writer(bitBuffer);

        writer.writeBits64(0, offset);
        writer.writeBits(testValue, 8);

        // check eof
        ASSERT_THROW(writer.writeBits64(0, 1), CppRuntimeException);

        // check written value
        uint8_t writtenTestValue = static_cast<uint8_t>(bitBuffer.getBuffer()[offset / 8U] << (offset % 8U));
        if (offset % 8 != 0)
        {
            writtenTestValue |=
                    static_cast<uint8_t>(bitBuffer.getBuffer()[offset / 8U + 1U] >> (8U - (offset % 8U)));
        }
        ASSERT_EQ(testValue, writtenTestValue) << "Offset: " << offset;
    }
}

TEST_F(BitStreamWriterTest, writeBits)
{
    // check invalid bitlength acceptance
    const uint8_t numBits[] = { 255, 0, 33 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_externalBufferWriter.writeBits(1, numBits[i]), CppRuntimeException);
    }

    // check value out of range
    for (int i = 1; i < 32; ++i)
    {
        const uint32_t maxUnsigned = static_cast<uint32_t>((UINT64_C(1) << i) - 1);
        m_externalBufferWriter.writeBits(maxUnsigned, static_cast<uint8_t>(i));

        const uint32_t maxUnsignedViolation = maxUnsigned + 1;
        ASSERT_THROW(m_externalBufferWriter.writeBits(maxUnsignedViolation, static_cast<uint8_t>(i)),
                CppRuntimeException);
    }
}

TEST_F(BitStreamWriterTest, writeBits64)
{
    // check invalid bitlength acceptance
    const uint8_t numBits[] = { 255, 0, 65 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_externalBufferWriter.writeBits64(1, numBits[i]), CppRuntimeException);
    }

    // check value out of range
    for (int i = 1; i < 64; ++i)
    {
        const uint64_t maxUnsigned = (UINT64_C(1) << i) - 1;
        m_externalBufferWriter.writeBits64(maxUnsigned, static_cast<uint8_t>(i));

        const uint64_t maxUnsignedViolation = maxUnsigned + 1;
        ASSERT_THROW(m_externalBufferWriter.writeBits64(maxUnsignedViolation, static_cast<uint8_t>(i)),
                CppRuntimeException);
    }
}

TEST_F(BitStreamWriterTest, writeSignedBits)
{
    // check invalid bitlength acceptance
    const uint8_t numBits[] = { 255, 0, 33 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits(1, numBits[i]), CppRuntimeException);
    }

    // check value out of range
    for (uint32_t i = 1; i < 32; ++i)
    {
        const int32_t minSigned = -static_cast<int32_t>(1U << (i - 1U));
        const int32_t maxSigned =  static_cast<int32_t>((1U << (i - 1U)) - 1U);
        m_externalBufferWriter.writeSignedBits(minSigned, static_cast<uint8_t>(i));
        m_externalBufferWriter.writeSignedBits(maxSigned, static_cast<uint8_t>(i));

        const int32_t minSignedViolation = minSigned - 1;
        const int32_t maxSignedViolation = maxSigned + 1;
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits(minSignedViolation, static_cast<uint8_t>(i)),
                CppRuntimeException);
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits(maxSignedViolation, static_cast<uint8_t>(i)),
                CppRuntimeException);
    }
}

TEST_F(BitStreamWriterTest, writeSignedBits64)
{
    // check invalid bitlength acceptance
    const uint8_t numBits[] = { 255, 0, 65 };
    for (size_t i = 0; i < sizeof(numBits) / sizeof(numBits[0]); ++i)
    {
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits64(1, numBits[i]), CppRuntimeException);
    }

    // check value out of range
    for (int i = 1; i < 64; ++i)
    {
        const int64_t minSigned = -(INT64_C(1) << (i - 1));
        const int64_t maxSigned =  (INT64_C(1) << (i - 1)) - 1;
        m_externalBufferWriter.writeSignedBits64(minSigned, static_cast<uint8_t>(i));
        m_externalBufferWriter.writeSignedBits64(maxSigned, static_cast<uint8_t>(i));

        const int64_t minSignedViolation = minSigned - 1;
        const int64_t maxSignedViolation = maxSigned + 1;
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits64(minSignedViolation, static_cast<uint8_t>(i)),
                CppRuntimeException);
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits64(maxSignedViolation, static_cast<uint8_t>(i)),
                CppRuntimeException);
    }
}

TEST_F(BitStreamWriterTest, writeVarInt64)
{
    // check value out of range
    const int64_t outOfRangeValue =
            static_cast<int64_t>(UINT64_C(1) << (6U + 7U + 7U + 7U + 7U + 7U + 7U + 8U));
    ASSERT_THROW(m_externalBufferWriter.writeVarInt64(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeVarInt32)
{
    // check value out of range
    const int32_t outOfRangeValue = static_cast<int32_t>(1U << (6U + 7U + 7U + 8U));
    ASSERT_THROW(m_externalBufferWriter.writeVarInt32(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeVarInt16)
{
    // check value out of range
    const int16_t outOfRangeValue = static_cast<int16_t>(1U << (6U + 8U));
    ASSERT_THROW(m_externalBufferWriter.writeVarInt16(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeVarUInt64)
{
    // check value out of range
    const uint64_t outOfRangeValue = UINT64_C(1) << (7U + 7U + 7U + 7U + 7U + 7U + 7U + 8U);
    ASSERT_THROW(m_externalBufferWriter.writeVarUInt64(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeVarUInt32)
{
    // check value out of range
    const uint32_t outOfRangeValue = UINT32_C(1) << (7U + 7U + 7U + 8U);
    ASSERT_THROW(m_externalBufferWriter.writeVarUInt32(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeVarUInt16)
{
    // check value out of range
    const uint16_t outOfRangeValue = static_cast<uint16_t>(1U << (7U + 8U));
    ASSERT_THROW(m_externalBufferWriter.writeVarUInt16(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeVarInt)
{
    ASSERT_NO_THROW(m_externalBufferWriter.writeVarInt(INT64_MIN));
    ASSERT_NO_THROW(m_externalBufferWriter.writeVarInt(INT64_MAX));
}

TEST_F(BitStreamWriterTest, writeVarUInt)
{
    ASSERT_NO_THROW(m_externalBufferWriter.writeVarUInt(0));
    ASSERT_NO_THROW(m_externalBufferWriter.writeVarUInt(UINT64_MAX));
}

TEST_F(BitStreamWriterTest, writeVarSize)
{
    // check value out of range
    const uint32_t outOfRangeValue = UINT32_C(1) << (2U + 7U + 7U + 7U + 8U);
    ASSERT_THROW(m_externalBufferWriter.writeVarSize(outOfRangeValue), CppRuntimeException);
}

TEST_F(BitStreamWriterTest, writeBitBuffer)
{
    static const size_t bitBufferBitSize = 24;
    BitBuffer bitBuffer(std::vector<uint8_t>{0xAB, 0xAB, 0xAB}, bitBufferBitSize);

    {
        ASSERT_NO_THROW(m_externalBufferWriter.writeBitBuffer(bitBuffer));
        const uint8_t* buffer = m_externalBufferWriter.getWriteBuffer();
        BitBuffer readBitBuffer{buffer + 1, bitBufferBitSize}; // first byte is bit buffer size
        ASSERT_EQ(bitBuffer, readBitBuffer);
    }

    ASSERT_NO_THROW(m_dummyBufferWriter.writeBitBuffer(bitBuffer));
    ASSERT_EQ(bitBufferBitSize + 8, m_dummyBufferWriter.getBitPosition()); // first byte is bit buffer size
}

TEST_F(BitStreamWriterTest, hasWriteBuffer)
{
    ASSERT_TRUE(m_externalBufferWriter.hasWriteBuffer());
    ASSERT_FALSE(m_dummyBufferWriter.hasWriteBuffer());
}

TEST_F(BitStreamWriterTest, getWriteBuffer)
{
    ASSERT_EQ(m_externalBuffer, m_externalBufferWriter.getWriteBuffer());

    ASSERT_EQ(nullptr, m_dummyBufferWriter.getWriteBuffer());
}

TEST_F(BitStreamWriterTest, dummyBufferTest)
{
    m_dummyBufferWriter.writeBits(1, 1);
    m_dummyBufferWriter.alignTo(4);
    m_dummyBufferWriter.writeBits(1, 1);
    m_dummyBufferWriter.alignTo(4);
    m_dummyBufferWriter.writeBits(37, 11);
    m_dummyBufferWriter.alignTo(8);
    m_dummyBufferWriter.writeBits(1, 1);
    ASSERT_EQ(25, m_dummyBufferWriter.getBitPosition());
}

} // namespace zserio
