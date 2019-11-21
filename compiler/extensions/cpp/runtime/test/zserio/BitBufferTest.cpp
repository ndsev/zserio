#include <utility>

#include "zserio/CppRuntimeException.h"
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
}

TEST(BitBufferTest, vectorConstructor)
{
    const size_t byteSize = 2;
    const std::vector<uint8_t> buffer(byteSize);
    const BitBuffer bitBuffer(buffer);
    ASSERT_EQ(8 * byteSize, bitBuffer.getBitSize());

    const size_t emptyBitSize = 0;
    const std::vector<uint8_t> emptyBuffer;
    const BitBuffer emptyBitBuffer(emptyBuffer);
    ASSERT_EQ(emptyBitSize, emptyBitBuffer.getBitSize());
}

TEST(BitBufferTest, vectorBitSizeConstructor)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer((bitSize + 7) / 8);
    const BitBuffer bitBuffer(buffer, bitSize);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());

    const size_t emptyBitSize = 0;
    const std::vector<uint8_t> emptyBuffer;
    const BitBuffer emptyBitBuffer(emptyBuffer, emptyBitSize);
    ASSERT_EQ(emptyBitSize, emptyBitBuffer.getBitSize());

    const size_t outOfRangeBitSize = 9;
    const std::vector<uint8_t> outOfRangeBuffer(1);
    ASSERT_THROW(BitBuffer(outOfRangeBuffer, outOfRangeBitSize), CppRuntimeException);
}

TEST(BitBufferTest, vectorMoveConstructor)
{
    const size_t byteSize = 2;
    std::vector<uint8_t> buffer(byteSize);
    const uint8_t* bufferStart = &buffer[0];
    const BitBuffer bitBuffer(std::move(buffer));
    ASSERT_EQ(8 * byteSize, bitBuffer.getBitSize());
    ASSERT_EQ(bufferStart, bitBuffer.getBuffer());
}

TEST(BitBufferTest, vectorBitSizeMoveConstructor)
{
    const size_t bitSize = 11;
    std::vector<uint8_t> buffer((bitSize + 7) / 8);
    const uint8_t* bufferStart = &buffer[0];
    const BitBuffer bitBuffer(std::move(buffer), bitSize);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());
    ASSERT_EQ(bufferStart, bitBuffer.getBuffer());

    const size_t outOfRangeBitSize = 9;
    ASSERT_THROW(BitBuffer(std::vector<uint8_t>(1), outOfRangeBitSize), CppRuntimeException);
}

TEST(BitBufferTest, rawPointerConstructor)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer((bitSize + 7) / 8);
    const BitBuffer bitBuffer(&buffer[0], bitSize);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());

    const size_t emptyBitSize = 0;
    const std::vector<uint8_t> emptyBuffer;
    const BitBuffer emptyBitBuffer(&emptyBuffer[0], emptyBitSize);
    ASSERT_EQ(emptyBitSize, emptyBitBuffer.getBitSize());
}

TEST(BitBufferTest, copyConstructor)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const BitBuffer bitBuffer(buffer, bitSize);

    const BitBuffer copiedBitBuffer(bitBuffer);
    ASSERT_EQ(bitBuffer.getBitSize(), copiedBitBuffer.getBitSize());
    ASSERT_EQ(bitBuffer.getByteSize(), copiedBitBuffer.getByteSize());
    const uint8_t* copiedBuffer = copiedBitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *copiedBuffer++);
}

TEST(BitBufferTest, assignmentOperator)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const BitBuffer bitBuffer(buffer, bitSize);

    const BitBuffer copiedBitBuffer = bitBuffer;
    ASSERT_EQ(bitBuffer.getBitSize(), copiedBitBuffer.getBitSize());
    ASSERT_EQ(bitBuffer.getByteSize(), copiedBitBuffer.getByteSize());
    const uint8_t* copiedBuffer = copiedBitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *copiedBuffer++);
}

TEST(BitBufferTest, moveConstructor)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const size_t byteSize = buffer.size();
    BitBuffer bitBuffer(buffer, bitSize);
    const uint8_t* bufferStart = bitBuffer.getBuffer();

    const BitBuffer movedBitBuffer(std::move(bitBuffer));
    ASSERT_EQ(bitSize, movedBitBuffer.getBitSize());
    ASSERT_EQ(byteSize, movedBitBuffer.getByteSize());
    ASSERT_EQ(bufferStart, movedBitBuffer.getBuffer());
}

TEST(BitBufferTest, moveAssignmentOperator)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x07};
    const size_t byteSize = buffer.size();
    BitBuffer bitBuffer(buffer, bitSize);
    const uint8_t* bufferStart = bitBuffer.getBuffer();

    const BitBuffer movedBitBuffer = std::move(bitBuffer);
    ASSERT_EQ(bitSize, movedBitBuffer.getBitSize());
    ASSERT_EQ(byteSize, movedBitBuffer.getByteSize());
    ASSERT_EQ(bufferStart, movedBitBuffer.getBuffer());
}

TEST(BitBufferTest, equalOperator)
{
    const size_t bitSize = 11;
    const BitBuffer bitBuffer1(std::vector<uint8_t>({0xAB, 0x07}), bitSize);
    const BitBuffer bitBuffer2(std::vector<uint8_t>({0xAB, 0x0F}), bitSize);
    ASSERT_TRUE(bitBuffer1 == bitBuffer2);

    const BitBuffer bitBuffer3(std::vector<uint8_t>({0xAB, 0xFF}), bitSize);
    ASSERT_TRUE(bitBuffer1 == bitBuffer3);

    const BitBuffer bitBuffer4(std::vector<uint8_t>({0xAB, 0x03}), bitSize);
    ASSERT_FALSE(bitBuffer1 == bitBuffer4);

    const BitBuffer bitBuffer5(std::vector<uint8_t>({0xBA, 0x07}), bitSize);
    ASSERT_FALSE(bitBuffer1 == bitBuffer5);

    const BitBuffer bitBuffer6(std::vector<uint8_t>({0xAB}));
    ASSERT_FALSE(bitBuffer1 == bitBuffer6);

    const BitBuffer bitBuffer7;
    ASSERT_FALSE(bitBuffer1 == bitBuffer7);
}

TEST(BitBufferTest, hashCode)
{
    const size_t bitSize = 11;
    const BitBuffer bitBuffer1(std::vector<uint8_t>({0xAB, 0x07}), bitSize);
    const BitBuffer bitBuffer2(std::vector<uint8_t>({0xAB, 0x0F}), bitSize);
    ASSERT_EQ(bitBuffer1.hashCode(), bitBuffer2.hashCode());

    const BitBuffer bitBuffer3(std::vector<uint8_t>({0xAB, 0xFF}), bitSize);
    ASSERT_EQ(bitBuffer1.hashCode(), bitBuffer3.hashCode());

    const BitBuffer bitBuffer4(std::vector<uint8_t>({0xAB, 0x03}), bitSize);
    ASSERT_NE(bitBuffer1.hashCode(), bitBuffer4.hashCode());

    const BitBuffer bitBuffer5(std::vector<uint8_t>({0xBA, 0x07}), bitSize);
    ASSERT_NE(bitBuffer1.hashCode(), bitBuffer5.hashCode());

    const BitBuffer bitBuffer6(std::vector<uint8_t>({0xAB}));
    ASSERT_NE(bitBuffer1.hashCode(), bitBuffer6.hashCode());

    const BitBuffer bitBuffer7;
    ASSERT_NE(bitBuffer1.hashCode(), bitBuffer7.hashCode());
}

TEST(BitBufferTest, constGetBuffer)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const BitBuffer bitBuffer(buffer, bitSize);

    const uint8_t* readBuffer = bitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *readBuffer++);
}

TEST(BitBufferTest, getBuffer)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    BitBuffer bitBuffer(buffer, bitSize);

    uint8_t* readBuffer = bitBuffer.getBuffer();
    for (uint8_t element : buffer)
        ASSERT_EQ(element, *readBuffer++);
}

TEST(BitBufferTest, getBitSize)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const BitBuffer bitBuffer(buffer, bitSize);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());
}

TEST(BitBufferTest, getByteSize)
{
    const size_t bitSize = 11;
    const std::vector<uint8_t> buffer = {0xAB, 0x03};
    const size_t byteSize = buffer.size();
    const BitBuffer bitBuffer(buffer, bitSize);
    ASSERT_EQ(byteSize, bitBuffer.getByteSize());
}

} // namespace zserio
