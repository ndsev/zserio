#include <array>
#include <cstring>

#include "gtest/gtest.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace zserio
{

class BitStreamWriterTest : public ::testing::Test
{
public:
    BitStreamWriterTest() :
            m_externalBuffer(),
            m_externalBufferWriter(m_externalBuffer.data(), m_externalBuffer.size()),
            m_dummyBufferWriter(nullptr, 0)
    {
        m_externalBuffer.fill(0);
    }

protected:
    std::array<uint8_t, 512> m_externalBuffer;
    BitStreamWriter m_externalBufferWriter;
    BitStreamWriter m_dummyBufferWriter;
};

TEST_F(BitStreamWriterTest, rawConstructor)
{
    std::array<uint8_t, 2> data = {0x00, 0x00};
    BitStreamWriter writer(data.data(), data.size());

    ASSERT_EQ(data.data(), writer.getBuffer().data());
    ASSERT_EQ(data.size() * 8, writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0xFFFF, 16), CppRuntimeException);
    writer.writeBits(0x07, 3);
    writer.writeBits(0x00, 5);
    ASSERT_EQ(0xE0, writer.getBuffer()[1]);
}

TEST_F(BitStreamWriterTest, rawConstructorWithBitSize)
{
    std::array<uint8_t, 2> data = {0x00, 0x00};
    BitStreamWriter writer(data.data(), 15, BitsTag());

    ASSERT_EQ(data.data(), writer.getBuffer().data());
    ASSERT_EQ(15, writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0xFF, 8), CppRuntimeException);
    writer.writeBits(0x07, 3);
    writer.writeBits(0x00, 4);
    ASSERT_EQ(0xE0, writer.getBuffer()[1]);
}

TEST_F(BitStreamWriterTest, spanConstructor)
{
    std::array<uint8_t, 2> data = {0x00, 0x00};
    const Span<uint8_t> span(data);
    BitStreamWriter writer(span);

    ASSERT_EQ(span.data(), writer.getBuffer().data());
    ASSERT_EQ(span.size() * 8, writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0xFFFF, 16), CppRuntimeException);
    writer.writeBits(0x07, 3);
    writer.writeBits(0x00, 5);
    ASSERT_EQ(0xE0, writer.getBuffer()[1]);
}

TEST_F(BitStreamWriterTest, spanConstructorWithBitSize)
{
    std::array<uint8_t, 2> data = {0x00, 0x00};
    const Span<uint8_t> span(data);
    BitStreamWriter writer(span, 15);
    ASSERT_THROW(BitStreamWriter wrongWriter(span, 17), CppRuntimeException);

    ASSERT_EQ(span.data(), writer.getBuffer().data());
    ASSERT_EQ(15, writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0xFF, 8), CppRuntimeException);
    writer.writeBits(0x07, 3);
    writer.writeBits(0x00, 4);
    ASSERT_EQ(0xE0, writer.getBuffer()[1]);
}

TEST_F(BitStreamWriterTest, bitBufferConstructor)
{
    BitBuffer bitBuffer(11);
    BitStreamWriter writer(bitBuffer);

    ASSERT_EQ(bitBuffer.getBuffer(), writer.getBuffer().data());
    ASSERT_EQ(bitBuffer.getBitSize(), writer.getBufferBitSize());

    writer.writeBits(0x1F, 5);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xFF, writer.getBuffer()[0]);
    ASSERT_THROW(writer.writeBits(0x0F, 4), CppRuntimeException);
    writer.writeBits(0x07, 3);
    ASSERT_EQ(0xE0, writer.getBuffer()[1]);
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
        uint8_t writtenTestValue = static_cast<uint8_t>(bitBuffer.getData()[offset / 8U] << (offset % 8U));
        if (offset % 8 != 0)
        {
            writtenTestValue |=
                    static_cast<uint8_t>(bitBuffer.getData()[offset / 8U + 1U] >> (8U - (offset % 8U)));
        }
        ASSERT_EQ(testValue, writtenTestValue) << "Offset: " << offset;
    }
}

TEST_F(BitStreamWriterTest, writeBits)
{
    // check invalid bitlength acceptance
    const std::array<uint8_t, 3> numBitsArray = {255, 0, 33};
    for (uint8_t numBits : numBitsArray)
    {
        ASSERT_THROW(m_externalBufferWriter.writeBits(1, numBits), CppRuntimeException);
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
    const std::array<uint8_t, 3> numBitsArray = {255, 0, 65};
    for (uint8_t numBits : numBitsArray)
    {
        ASSERT_THROW(m_externalBufferWriter.writeBits64(1, numBits), CppRuntimeException);
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
    const std::array<uint8_t, 3> numBitsArray = {255, 0, 33};
    for (uint8_t numBits : numBitsArray)
    {
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits(1, numBits), CppRuntimeException);
    }

    // check value out of range
    for (uint32_t i = 1; i < 32; ++i)
    {
        const int32_t minSigned = -static_cast<int32_t>(1U << (i - 1U));
        const int32_t maxSigned = static_cast<int32_t>((1U << (i - 1U)) - 1U);
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
    const std::array<uint8_t, 3> numBitsArray = {255, 0, 65};
    for (uint8_t numBits : numBitsArray)
    {
        ASSERT_THROW(m_externalBufferWriter.writeSignedBits64(1, numBits), CppRuntimeException);
    }

    // check value out of range
    for (int i = 1; i < 64; ++i)
    {
        const int64_t minSigned = -(INT64_C(1) << (i - 1));
        const int64_t maxSigned = (INT64_C(1) << (i - 1)) - 1;
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
        Span<const uint8_t> buffer = m_externalBufferWriter.getBuffer();
        BitBuffer readBitBuffer{&buffer[1], bitBufferBitSize}; // first byte is bit buffer size
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
    ASSERT_EQ(m_externalBuffer.data(), m_externalBufferWriter.getWriteBuffer());

    ASSERT_EQ(nullptr, m_dummyBufferWriter.getWriteBuffer());
}

TEST_F(BitStreamWriterTest, getBuffer)
{
    ASSERT_EQ(m_externalBuffer.data(), m_externalBufferWriter.getBuffer().data());

    ASSERT_EQ(nullptr, m_dummyBufferWriter.getBuffer().data());
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
