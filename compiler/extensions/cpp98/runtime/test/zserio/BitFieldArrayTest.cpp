#include <string.h>

#include "zserio/BitFieldArray.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

class ArrayTestOffsetHandler
{
public:
    explicit ArrayTestOffsetHandler(size_t numBits):
        m_initialized(false),
        m_elementByteSize(bitsToBytes(alignTo(NUM_BITS_PER_BYTE, numBits))),
        m_initialOffset(0)
    {}

    void checkOffset(size_t index, size_t bytePosition)
    {
        if (!m_initialized)
        {
            m_initialOffset = bytePosition;
            m_initialized = true;
        }
        ASSERT_EQ(m_initialOffset + m_elementByteSize * index, bytePosition);
    }

    void setOffset(size_t index, size_t bytePosition)
    {
        checkOffset(index, bytePosition);
    }

private:
    bool m_initialized;
    size_t m_elementByteSize;
    size_t m_initialOffset;
};

class BitFieldArrayTest : public ::testing::Test
{
public:
    BitFieldArrayTest()
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <class ARRAY_TYPE>
    void ArrayTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        ReadConstructorTest(array, numBits);
        AutoReadConstructorTest(array, numBits);
        AlignedReadConstructorTest(array, numBits);
        AutoAlignedReadConstructorTest(array, numBits);
        ImplicitReadConstructorTest(array, numBits);
        BitsizeOfTest(array, numBits);
        AutoBitsizeOfTest(array, numBits);
        AlignedBitsizeOfTest(array, numBits);
        AutoAlignedBitsizeOfTest(array, numBits);
        InitializeOffsetsTest(array, numBits);
        AutoInitializeOffsetsTest(array, numBits);
        AlignedInitializeOffsetsTest(array, numBits);
        AutoAlignedInitializeOffsetsTest(array, numBits);
        EqualOperatorTest(array, numBits);
        HashCodeTest(array, numBits);
        ReadTest(array, numBits);
        AutoReadTest(array, numBits);
        AlignedReadTest(array, numBits);
        AutoAlignedReadTest(array, numBits);
        ImplicitReadTest(array, numBits);
        WriteTest(array, numBits);
        AutoWriteTest(array, numBits);
        AlignedWriteTest(array, numBits);
        AutoAlignedWriteTest(array, numBits);
    }

private:
    template <class ARRAY_TYPE>
    void ReadConstructorTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE readConstructorArray(reader, array.size(), numBits);
        EXPECT_EQ(array, readConstructorArray);
    }

    template <class ARRAY_TYPE>
    void AutoReadConstructorTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoReadConstructorArray(reader, AutoLength(), numBits);
        EXPECT_EQ(array, autoReadConstructorArray);
    }

    template <class ARRAY_TYPE>
    void AlignedReadConstructorTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler(numBits), numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE alignedReadConstructorArray(reader, array.size(), ArrayTestOffsetHandler(numBits), numBits);
        EXPECT_EQ(array, alignedReadConstructorArray);
    }

    template <class ARRAY_TYPE>
    void AutoAlignedReadConstructorTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler(numBits), numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoAlignedReadConstructorArray(reader, AutoLength(), ArrayTestOffsetHandler(numBits),
                numBits);
        EXPECT_EQ(array, autoAlignedReadConstructorArray);
    }

    template <class ARRAY_TYPE>
    void ImplicitReadConstructorTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, numBits);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        ARRAY_TYPE implicitReadConstructorArray(reader, ImplicitLength(), numBits);
        for (size_t i = 0; i < implicitReadConstructorArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadConstructorArray[i]);
    }

    template <class ARRAY_TYPE>
    void BitsizeOfTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t unalignedBitSize = array.size() * numBits;
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(0, numBits));
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(7, numBits));
    }

    template <class ARRAY_TYPE>
    void AutoBitsizeOfTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t unalignedBitSize = AUTO_LENGTH_BIT_SIZE + array.size() * numBits;
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(0, AutoLength(), numBits));
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(7, AutoLength(), numBits));
    }

    template <class ARRAY_TYPE>
    void AlignedBitsizeOfTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t alignedNumBits = alignTo(NUM_BITS_PER_BYTE, numBits);
        size_t alignedBitSize = numBits;
        if (array.size() > 1)
            alignedBitSize += (array.size() - 1) * alignedNumBits;
        EXPECT_EQ(alignedBitSize, array.bitSizeOf(0, Aligned(), numBits));
        EXPECT_EQ(alignedBitSize + 1, array.bitSizeOf(7, Aligned(), numBits));
    }

    template <class ARRAY_TYPE>
    void AutoAlignedBitsizeOfTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t alignedNumBits = alignTo(NUM_BITS_PER_BYTE, numBits);
        size_t alignedBitSize = numBits;
        if (array.size() > 1)
            alignedBitSize += (array.size() - 1) * alignedNumBits;
        EXPECT_EQ(AUTO_LENGTH_BIT_SIZE + alignedBitSize, array.bitSizeOf(0, AutoLength(), Aligned(), numBits));
        EXPECT_EQ(AUTO_LENGTH_BIT_SIZE + alignedBitSize + 1, array.bitSizeOf(7, AutoLength(), Aligned(),
                numBits));
    }

    template <class ARRAY_TYPE>
    void InitializeOffsetsTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t unalignedBitSize = array.size() * numBits;
        EXPECT_EQ(0 + unalignedBitSize, array.initializeOffsets(0, numBits));
        EXPECT_EQ(7 + unalignedBitSize, array.initializeOffsets(7, numBits));
    }

    template <class ARRAY_TYPE>
    void AutoInitializeOffsetsTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t unalignedBitSize = AUTO_LENGTH_BIT_SIZE + array.size() * numBits;
        EXPECT_EQ(0 + unalignedBitSize, array.initializeOffsets(0, AutoLength(), numBits));
        EXPECT_EQ(7 + unalignedBitSize, array.initializeOffsets(7, AutoLength(), numBits));
    }

    template <class ARRAY_TYPE>
    void AlignedInitializeOffsetsTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t alignedNumBits = alignTo(NUM_BITS_PER_BYTE, numBits);
        size_t alignedBitSize = numBits;
        if (array.size() > 1)
            alignedBitSize += (array.size() - 1) * alignedNumBits;
        EXPECT_EQ(0 + alignedBitSize, array.initializeOffsets(0, ArrayTestOffsetHandler(numBits), numBits));
        EXPECT_EQ(7 + alignedBitSize + 1, array.initializeOffsets(7, ArrayTestOffsetHandler(numBits), numBits));
    }

    template <class ARRAY_TYPE>
    void AutoAlignedInitializeOffsetsTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        const size_t alignedNumBits = alignTo(NUM_BITS_PER_BYTE, numBits);
        size_t alignedBitSize = numBits;
        if (array.size() > 1)
            alignedBitSize += (array.size() - 1) * alignedNumBits;
        EXPECT_EQ(0 + AUTO_LENGTH_BIT_SIZE + alignedBitSize,
                array.initializeOffsets(0, AutoLength(), ArrayTestOffsetHandler(numBits), numBits));
        EXPECT_EQ(7 + AUTO_LENGTH_BIT_SIZE + alignedBitSize + 1,
                array.initializeOffsets(7, AutoLength(), ArrayTestOffsetHandler(numBits), numBits));
    }

    template <class ARRAY_TYPE>
    void EqualOperatorTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE EqualArray(reader, array.size(), numBits);
        ARRAY_TYPE NoneEqualArray;
        EXPECT_TRUE(array == EqualArray);
        EXPECT_FALSE(array == NoneEqualArray);
    }

    template <class ARRAY_TYPE>
    void HashCodeTest(ARRAY_TYPE& array, uint8_t)
    {
        // check only if hashCode exists
        EXPECT_TRUE(array.hashCode() != 0);
    }

    template <class ARRAY_TYPE>
    void ReadTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE readArray;
        readArray.read(reader, array.size(), numBits);
        EXPECT_EQ(array, readArray);
    }

    template <class ARRAY_TYPE>
    void AutoReadTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoReadArray;
        autoReadArray.read(reader, AutoLength(), numBits);
        EXPECT_EQ(array, autoReadArray);
    }

    template <class ARRAY_TYPE>
    void AlignedReadTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler(numBits), numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE alignedReadArray;
        alignedReadArray.read(reader, array.size(), ArrayTestOffsetHandler(numBits), numBits);
        EXPECT_EQ(array, alignedReadArray);
    }

    template <class ARRAY_TYPE>
    void AutoAlignedReadTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler(numBits), numBits);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoAlignedReadArray;
        autoAlignedReadArray.read(reader, AutoLength(), ArrayTestOffsetHandler(numBits), numBits);
        EXPECT_EQ(array, autoAlignedReadArray);
    }

    template <class ARRAY_TYPE>
    void ImplicitReadTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, numBits);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        ARRAY_TYPE implicitReadArray;
        implicitReadArray.read(reader, ImplicitLength(), numBits);
        for (size_t i = 0; i < implicitReadArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadArray[i]);
    }

    template <class ARRAY_TYPE>
    void WriteTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, numBits);
        const size_t bitSize = array.size() * numBits;
        EXPECT_EQ(bitSize, writer.getBitPosition());
    }

    template <class ARRAY_TYPE>
    void AutoWriteTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), numBits);
        const size_t bitSize = AUTO_LENGTH_BIT_SIZE + array.size() * numBits;
        EXPECT_EQ(bitSize, writer.getBitPosition());
    }

    template <class ARRAY_TYPE>
    void AlignedWriteTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler(numBits), numBits);
        const size_t alignedNumBits = alignTo(NUM_BITS_PER_BYTE, numBits);
        size_t alignedBitSize = numBits;
        if (array.size() > 1)
            alignedBitSize += (array.size() - 1) * alignedNumBits;
        EXPECT_EQ(alignedBitSize, writer.getBitPosition());
    }

    template <class ARRAY_TYPE>
    void AutoAlignedWriteTest(ARRAY_TYPE& array, uint8_t numBits)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler(numBits), numBits);
        const size_t autoAlignedNumBits = alignTo(NUM_BITS_PER_BYTE, numBits);
        size_t autoAlignedBitSize = AUTO_LENGTH_BIT_SIZE + numBits;
        if (array.size() > 1)
            autoAlignedBitSize += (array.size() - 1) * autoAlignedNumBits;
        EXPECT_EQ(autoAlignedBitSize, writer.getBitPosition());
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t BUFFER_SIZE = 256;

    uint8_t         m_byteBuffer[BUFFER_SIZE];
};

TEST_F(BitFieldArrayTest, UInt8Array)
{
    const uint8_t numBits = 4;
    UInt8Array array;
    array.push_back(0);
    array.push_back(7);
    array.push_back((1 << numBits) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, UInt16Array)
{
    const uint8_t numBits = 12;
    UInt16Array array;
    array.push_back(0);
    array.push_back(7);
    array.push_back((1 << numBits) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, UInt32Array)
{
    const uint8_t numBits = 20;
    UInt32Array array;
    array.push_back(0);
    array.push_back(7);
    array.push_back((1 << numBits) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, UInt64Array)
{
    const uint8_t numBits = 36;
    UInt64Array array;
    array.push_back(0);
    array.push_back(7);
    array.push_back(((uint64_t) 1 << numBits) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, Int8Array)
{
    const uint8_t numBits = 4;
    Int8Array array;
    array.push_back(-(1 << (numBits - 1)));
    array.push_back(7);
    array.push_back((1 << (numBits - 1)) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, Int16Array)
{
    const uint8_t numBits = 12;
    Int16Array array;
    array.push_back(-(1 << (numBits - 1)));
    array.push_back(7);
    array.push_back((1 << (numBits - 1)) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, Int32Array)
{
    const uint8_t numBits = 20;
    Int32Array array;
    array.push_back(-(1 << (numBits - 1)));
    array.push_back(7);
    array.push_back((1 << (numBits - 1)) - 1);
    ArrayTest(array, numBits);
}

TEST_F(BitFieldArrayTest, Int64Array)
{
    const uint8_t numBits = 36;
    Int64Array array;
    array.push_back(-((int64_t) 1 << (numBits - 1)));
    array.push_back(7);
    array.push_back(((int64_t) 1 << (numBits - 1)) - 1);
    ArrayTest(array, numBits);
}

} // namespace zserio
