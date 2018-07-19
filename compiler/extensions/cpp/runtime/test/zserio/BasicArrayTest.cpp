#include <cstring>

#include "zserio/BasicArray.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

class ArrayTestOffsetHandler
{
public:
    void checkOffset(size_t, size_t)
    {
    }

    void setOffset(size_t, size_t)
    {
    }
};

class BasicArrayTest : public ::testing::Test
{
public:
    BasicArrayTest()
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <class ARRAY_TYPE>
    void ArrayTest(ARRAY_TYPE& array, size_t unalignedBitSize)
    {
        ArrayTest(array, unalignedBitSize, unalignedBitSize);
    }

    template <class ARRAY_TYPE>
    void ArrayTest(ARRAY_TYPE& array, size_t unalignedBitSize, size_t alignedBitSize)
    {
        ReadConstructorTest(array);
        AutoReadConstructorTest(array);
        AlignedReadConstructorTest(array);
        AutoAlignedReadConstructorTest(array);
        ImplicitReadConstructorTest(array);
        BitSizeOfTest(array, unalignedBitSize);
        AutoBitSizeOfTest(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        AlignedBitSizeOfTest(array, alignedBitSize);
        AutoAlignedBitSizeOfTest(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        InitializeOffsetsTest(array, unalignedBitSize);
        AutoInitializeOffsetsTest(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        AlignedInitializeOffsetsTest(array, alignedBitSize);
        AutoAlignedInitializeOffsetsTest(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        EqualOperatorTest(array);
        HashCodeTest(array);
        SumTest(array);
        ReadTest(array);
        AutoReadTest(array);
        AlignedReadTest(array);
        AutoAlignedReadTest(array);
        ImplicitReadTest(array);
        WriteTest(array, unalignedBitSize);
        AutoWriteTest(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        AlignedWriteTest(array, alignedBitSize);
        AutoAlignedWriteTest(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
    }

private:
    template <class ARRAY_TYPE>
    void ReadConstructorTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE readConstructorArray(reader, array.size());
        EXPECT_EQ(array, readConstructorArray);
    }

    template <class ARRAY_TYPE>
    void AutoReadConstructorTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoReadConstructorArray(reader, AutoLength());
        EXPECT_EQ(array, autoReadConstructorArray);
    }

    template <class ARRAY_TYPE>
    void AlignedReadConstructorTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE alignedReadConstructorArray(reader, array.size(), ArrayTestOffsetHandler());
        EXPECT_EQ(array, alignedReadConstructorArray);
    }

    template <class ARRAY_TYPE>
    void AutoAlignedReadConstructorTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoAlignedReadConstructorArray(reader, AutoLength(), ArrayTestOffsetHandler());
        EXPECT_EQ(array, autoAlignedReadConstructorArray);
    }

    template <class ARRAY_TYPE>
    void ImplicitReadConstructorTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        ARRAY_TYPE implicitReadConstructorArray(reader, ImplicitLength());
        for (size_t i = 0; i < implicitReadConstructorArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadConstructorArray[i]);
    }

    template <class ARRAY_TYPE>
    void BitSizeOfTest(ARRAY_TYPE& array, size_t unalignedBitSize)
    {
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(0));
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(7));
    }

    template <class ARRAY_TYPE>
    void AutoBitSizeOfTest(ARRAY_TYPE& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(autoUnalignedBitSize, array.bitSizeOf(0, AutoLength()));
        EXPECT_EQ(autoUnalignedBitSize, array.bitSizeOf(7, AutoLength()));
    }

    template <class ARRAY_TYPE>
    void AlignedBitSizeOfTest(ARRAY_TYPE& array, size_t alignedBitSize)
    {
        EXPECT_EQ(alignedBitSize, array.bitSizeOf(0, Aligned()));
        EXPECT_EQ(alignedBitSize + 1, array.bitSizeOf(7, Aligned()));
    }

    template <class ARRAY_TYPE>
    void AutoAlignedBitSizeOfTest(ARRAY_TYPE& array, size_t autoAlignedBitSize)
    {
        EXPECT_EQ(autoAlignedBitSize, array.bitSizeOf(0, AutoLength(), Aligned()));
        EXPECT_EQ(autoAlignedBitSize + 1, array.bitSizeOf(7, AutoLength(), Aligned()));
    }

    template <class ARRAY_TYPE>
    void InitializeOffsetsTest(ARRAY_TYPE& array, size_t unalignedBitSize)
    {
        EXPECT_EQ(0 + unalignedBitSize, array.initializeOffsets(0));
        EXPECT_EQ(7 + unalignedBitSize, array.initializeOffsets(7));
    }

    template <class ARRAY_TYPE>
    void AutoInitializeOffsetsTest(ARRAY_TYPE& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(0 + autoUnalignedBitSize, array.initializeOffsets(0, AutoLength()));
        EXPECT_EQ(7 + autoUnalignedBitSize, array.initializeOffsets(7, AutoLength()));
    }

    template <class ARRAY_TYPE>
    void AlignedInitializeOffsetsTest(ARRAY_TYPE& array, size_t alignedBitSize)
    {
        EXPECT_EQ(0 + alignedBitSize, array.initializeOffsets(0, ArrayTestOffsetHandler()));
        EXPECT_EQ(7 + alignedBitSize + 1, array.initializeOffsets(7, ArrayTestOffsetHandler()));
    }

    template <class ARRAY_TYPE>
    void AutoAlignedInitializeOffsetsTest(ARRAY_TYPE& array, size_t autoAlignedBitSize)
    {
        EXPECT_EQ(0 + autoAlignedBitSize, array.initializeOffsets(0, AutoLength(), ArrayTestOffsetHandler()));
        EXPECT_EQ(7 + autoAlignedBitSize + 1, array.initializeOffsets(7, AutoLength(),
                ArrayTestOffsetHandler()));
    }

    template <class ARRAY_TYPE>
    void EqualOperatorTest(ARRAY_TYPE& array)
    {
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE EqualArray(reader, array.size());
        ARRAY_TYPE NoneEqualArray;
        EXPECT_TRUE(array == EqualArray);
        EXPECT_FALSE(array == NoneEqualArray);
    }

    template <class ARRAY_TYPE>
    void HashCodeTest(ARRAY_TYPE& array)
    {
        // check only if hashCode exists
        EXPECT_TRUE(array.hashCode() != 0);
    }

    template<typename ARRAY_TYPE>
    void SumTest(const ARRAY_TYPE& array)
    {
        typedef typename ARRAY_TYPE::element_type element_type;

        element_type sum = element_type();
        for (typename ARRAY_TYPE::const_iterator it = array.begin(); it != array.end(); ++it)
            sum += *it;

        EXPECT_EQ(sum, array.sum());
    }

    template <class ARRAY_TYPE>
    void ReadTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE readArray;
        readArray.read(reader, array.size());
        EXPECT_EQ(array, readArray);
    }

    template <class ARRAY_TYPE>
    void AutoReadTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoReadArray;
        autoReadArray.read(reader, AutoLength());
        EXPECT_EQ(array, autoReadArray);
    }

    template <class ARRAY_TYPE>
    void AlignedReadTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE alignedReadArray;
        alignedReadArray.read(reader, array.size(), ArrayTestOffsetHandler());
        EXPECT_EQ(array, alignedReadArray);
    }

    template <class ARRAY_TYPE>
    void AutoAlignedReadTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ARRAY_TYPE autoAlignedReadArray;
        autoAlignedReadArray.read(reader, AutoLength(), ArrayTestOffsetHandler());
        EXPECT_EQ(array, autoAlignedReadArray);
    }

    template <class ARRAY_TYPE>
    void ImplicitReadTest(ARRAY_TYPE& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        ARRAY_TYPE implicitReadArray;
        implicitReadArray.read(reader, ImplicitLength());
        for (size_t i = 0; i < implicitReadArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadArray[i]);
    }

    template <class ARRAY_TYPE>
    void WriteTest(ARRAY_TYPE& array, size_t unalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        EXPECT_EQ(unalignedBitSize, writer.getBitPosition());
    }

    template <class ARRAY_TYPE>
    void AutoWriteTest(ARRAY_TYPE& array, size_t autoUnalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength());
        EXPECT_EQ(autoUnalignedBitSize, writer.getBitPosition());
    }

    template <class ARRAY_TYPE>
    void AlignedWriteTest(ARRAY_TYPE& array, size_t alignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler());
        EXPECT_EQ(alignedBitSize, writer.getBitPosition());
    }

    template <class ARRAY_TYPE>
    void AutoAlignedWriteTest(ARRAY_TYPE& array, size_t autoAlignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler());
        EXPECT_EQ(autoAlignedBitSize, writer.getBitPosition());
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t BUFFER_SIZE = 256;

    uint8_t m_byteBuffer[BUFFER_SIZE];
};

TEST_F(BasicArrayTest, VarUInt16Array)
{
    const size_t elementBitSize = 8;
    VarUInt16Array array;
    array.push_back(0);
    array.push_back(1 << 5);
    array.push_back(1 << 6);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, VarUInt16ArrayBitSizeOf)
{
    VarUInt16Array array;
    array.push_back(1 << 6);
    array.push_back(1 << (6 + 8));
    const size_t bitSize = 8 * (1 + 2);
    ArrayTest(array, bitSize);
}

TEST_F(BasicArrayTest, VarUInt32Array)
{
    const size_t elementBitSize = 16;
    VarUInt32Array array;
    array.push_back(1 << 7);
    array.push_back(1 << 8);
    array.push_back(1 << 9);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, VarUInt32ArrayBitSizeOf)
{
    VarUInt32Array array;
    array.push_back(1 << 6);
    array.push_back(1 << (6 + 7));
    array.push_back(1 << (6 + 7 + 7));
    array.push_back(1 << (6 + 7 + 7 + 8));
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    ArrayTest(array, bitSize);
}

TEST_F(BasicArrayTest, VarUInt64Array)
{
    const size_t elementBitSize = 24;
    VarUInt64Array array;
    array.push_back(1 << 14);
    array.push_back(1 << 15);
    array.push_back(1 << 16);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, VarUInt64ArrayBitSizeOf)
{
    VarUInt64Array array;
    array.push_back((uint64_t) 1 << 6);
    array.push_back((uint64_t) 1 << (6 + 7));
    array.push_back((uint64_t) 1 << (6 + 7 + 7));
    array.push_back((uint64_t) 1 << (6 + 7 + 7 + 7));
    array.push_back((uint64_t) 1 << (6 + 7 + 7 + 7 + 7));
    array.push_back((uint64_t) 1 << (6 + 7 + 7 + 7 + 7 + 7));
    array.push_back((uint64_t) 1 << (6 + 7 + 7 + 7 + 7 + 7 + 7));
    array.push_back((uint64_t) 1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8));
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    ArrayTest(array, bitSize);
}

TEST_F(BasicArrayTest, VarInt16Array)
{
    const size_t elementBitSize = 8;
    VarInt16Array array;
    array.push_back(-(1 << 5));
    array.push_back(1 << 4);
    array.push_back(1 << 5);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, VarInt16ArrayBitSizeOf)
{
    VarInt16Array array;
    array.push_back(1 << 5);
    array.push_back(1 << (5 + 8));
    const size_t bitSize = 8 * (1 + 2);
    ArrayTest(array, bitSize);
}

TEST_F(BasicArrayTest, VarInt32Array)
{
    const size_t elementBitSize = 16;
    VarInt32Array array;
    array.push_back(-(1 << 6));
    array.push_back(1 << 7);
    array.push_back(1 << 6);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, VarInt32ArrayBitSizeOf)
{
    VarInt32Array array;
    array.push_back(1 << 5);
    array.push_back(1 << (5 + 7));
    array.push_back(1 << (5 + 7 + 7));
    array.push_back(1 << (5 + 7 + 7 + 8));
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    ArrayTest(array, bitSize);
}

TEST_F(BasicArrayTest, VarInt64Array)
{
    const size_t elementBitSize = 24;
    VarInt64Array array;
    array.push_back(-(1 << 13));
    array.push_back(1 << 14);
    array.push_back(1 << 13);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, VarInt64ArrayBitSizeOf)
{
    VarInt64Array array;
    array.push_back((int64_t) 1 << 5);
    array.push_back((int64_t) 1 << (5 + 7));
    array.push_back((int64_t) 1 << (5 + 7 + 7));
    array.push_back((int64_t) 1 << (5 + 7 + 7 + 7));
    array.push_back((int64_t) 1 << (5 + 7 + 7 + 7 + 7));
    array.push_back((int64_t) 1 << (5 + 7 + 7 + 7 + 7 + 7));
    array.push_back((int64_t) 1 << (5 + 7 + 7 + 7 + 7 + 7 + 7));
    array.push_back((int64_t) 1 << (5 + 7 + 7 + 7 + 7 + 7 + 7 + 8));
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    ArrayTest(array, bitSize);
}

TEST_F(BasicArrayTest, FloatArray)
{
    const size_t elementBitSize = 16;
    FloatArray array;
    array.push_back(-9.0);
    array.push_back(0.0);
    array.push_back(10.0);
    ArrayTest(array, elementBitSize * array.size());
}

TEST_F(BasicArrayTest, BoolArray)
{
    const size_t elementBitSize = 1;
    BoolArray array;
    array.push_back(false);
    array.push_back(true);
    ArrayTest(array, elementBitSize * array.size(), elementBitSize + NUM_BITS_PER_BYTE);
}

TEST_F(BasicArrayTest, StringArray)
{
    const size_t stringLengthBitSize = 8;
    const size_t stringBitSize = (sizeof("StringX") - 1) * 8; // without terminating character
    const size_t elementBitSize = stringLengthBitSize + stringBitSize;
    StringArray array;
    array.push_back("String0");
    array.push_back("String1");
    array.push_back("String2");
    ArrayTest(array, elementBitSize * array.size());
}

} // namespace zserio
