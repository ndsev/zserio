#include <utility>

#include "zserio/BitBuffer.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitBufferTest, emptyConstructor)
{
    const BitBuffer bitBuffer;
    ASSERT_EQ(0, bitBuffer.getBitSize());
    ASSERT_EQ(0, bitBuffer.getByteSize());
}

TEST(BitBufferTest, bitSizeConstructor)
{
    const size_t bitSize = 11;
    const BitBuffer bitBuffer(bitSize);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());
    ASSERT_EQ((bitSize + 7) / 8, bitBuffer.getByteSize());
}

TEST(BitBufferTest, vectorConstructor)
{
    const size_t byteSize = 2;
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer(byteSize);
    const BitBuffer bitBuffer(buffer, lastByteBits);
    ASSERT_EQ((byteSize - 1) * 8 + lastByteBits, bitBuffer.getBitSize());
    ASSERT_EQ(byteSize, bitBuffer.getByteSize());
}

TEST(BitBufferTest, vectorMoveConstructor)
{
    const size_t byteSize = 2;
    const size_t lastByteBits = 3;
    std::vector<uint8_t> buffer(byteSize);
    const uint8_t* bufferStart = &buffer[0];
    const BitBuffer bitBuffer(std::move(buffer), lastByteBits);
    ASSERT_EQ((byteSize - 1) * 8 + lastByteBits, bitBuffer.getBitSize());
    ASSERT_EQ(byteSize, bitBuffer.getByteSize());
    ASSERT_EQ(bufferStart, bitBuffer.getBuffer());
}

TEST(BitBufferTest, rawPointerConstructor)
{
    const size_t bitSize = 11;
    const size_t byteSize = (bitSize + 7) / 8;
    const std::vector<uint8_t> buffer(byteSize);
    const BitBuffer bitBuffer(&buffer[0], bitSize);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());
    ASSERT_EQ(byteSize, bitBuffer.getByteSize());
}

TEST(BitBufferTest, copyConstructor)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const BitBuffer bitBuffer(buffer, lastByteBits);

    const BitBuffer copiedBitBuffer(bitBuffer);
    ASSERT_EQ(bitBuffer.getBitSize(), copiedBitBuffer.getBitSize());
    ASSERT_EQ(bitBuffer.getByteSize(), copiedBitBuffer.getByteSize());
    const uint8_t* copiedBuffer = copiedBitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *copiedBuffer++);
}

TEST(BitBufferTest, assignmentOperator)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const BitBuffer bitBuffer(buffer, lastByteBits);

    const BitBuffer copiedBitBuffer = bitBuffer;
    ASSERT_EQ(bitBuffer.getBitSize(), copiedBitBuffer.getBitSize());
    ASSERT_EQ(bitBuffer.getByteSize(), copiedBitBuffer.getByteSize());
    const uint8_t* copiedBuffer = copiedBitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *copiedBuffer++);
}

TEST(BitBufferTest, moveConstructor)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const size_t byteSize = buffer.size();
    BitBuffer bitBuffer(buffer, lastByteBits);
    const uint8_t* bufferStart = bitBuffer.getBuffer();

    const BitBuffer movedBitBuffer(std::move(bitBuffer));
    ASSERT_EQ((byteSize - 1) * 8 + lastByteBits, movedBitBuffer.getBitSize());
    ASSERT_EQ(byteSize, movedBitBuffer.getByteSize());
    ASSERT_EQ(bufferStart, movedBitBuffer.getBuffer());
}

TEST(BitBufferTest, moveAssignmentOperator)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const size_t byteSize = buffer.size();
    BitBuffer bitBuffer(buffer, lastByteBits);
    const uint8_t* bufferStart = bitBuffer.getBuffer();

    const BitBuffer movedBitBuffer = std::move(bitBuffer);
    ASSERT_EQ((byteSize - 1) * 8 + lastByteBits, movedBitBuffer.getBitSize());
    ASSERT_EQ(byteSize, movedBitBuffer.getByteSize());
    ASSERT_EQ(bufferStart, movedBitBuffer.getBuffer());
}

TEST(BitBufferTest, equalOperator)
{
    const size_t lastByteBits = 3;
    const BitBuffer bitBuffer1(std::vector<uint8_t>({0xAB, 0x07}), lastByteBits);
    const BitBuffer bitBuffer2(std::vector<uint8_t>({0xAB, 0x0F}), lastByteBits);
    ASSERT_TRUE(bitBuffer1 == bitBuffer2);

    const BitBuffer bitBuffer3(std::vector<uint8_t>({0xAB, 0xFF}), lastByteBits);
    ASSERT_TRUE(bitBuffer1 == bitBuffer3);

    const BitBuffer bitBuffer4(std::vector<uint8_t>({0xAB, 0x03}), lastByteBits);
    ASSERT_FALSE(bitBuffer1 == bitBuffer4);
}

TEST(BitBufferTest, constGet)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const size_t byteSize = buffer.size();
    const BitBuffer bitBuffer(buffer, lastByteBits);

    size_t bitSize;
    const uint8_t* getBuffer = bitBuffer.get(bitSize);
    ASSERT_EQ(bitSize, (byteSize - 1) * 8 + lastByteBits);
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *getBuffer++);
}

TEST(BitBufferTest, get)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const size_t byteSize = buffer.size();
    BitBuffer bitBuffer(buffer, lastByteBits);

    size_t bitSize;
    uint8_t* getBuffer = bitBuffer.get(bitSize);
    ASSERT_EQ((byteSize - 1) * 8 + lastByteBits, bitSize);
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *getBuffer++);
}

TEST(BitBufferTest, constGetBuffer)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const BitBuffer bitBuffer(buffer, lastByteBits);

    const uint8_t* getBuffer = bitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *getBuffer++);
}

TEST(BitBufferTest, getBuffer)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    BitBuffer bitBuffer(buffer, lastByteBits);

    uint8_t* getBuffer = bitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *getBuffer++);
}

TEST(BitBufferTest, getBitSize)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const size_t byteSize = buffer.size();
    const BitBuffer bitBuffer(buffer, lastByteBits);
    ASSERT_EQ((byteSize - 1) * 8 + lastByteBits, bitBuffer.getBitSize());
}

TEST(BitBufferTest, getByteSize)
{
    const size_t lastByteBits = 3;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const size_t byteSize = buffer.size();
    const BitBuffer bitBuffer(buffer, lastByteBits);
    ASSERT_EQ(byteSize, bitBuffer.getByteSize());
}

} // namespace zserio
