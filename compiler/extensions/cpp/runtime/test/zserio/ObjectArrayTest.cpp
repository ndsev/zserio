#include <cstring>

#include "zserio/ObjectArray.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/HashCodeUtil.h"

#include "gtest/gtest.h"

namespace zserio
{

/**
 * A test class that holds a 31-bit unsigned integer.
 */
class DummyObject
{
public:
    DummyObject() : m_value(0) {}
    explicit DummyObject(uint32_t value) : m_value(value) {}
    explicit DummyObject(BitStreamReader& in) { read(in); }

    void initialize(uint32_t value)
    {
        m_value = value;
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return sizeof(uint32_t) * 8 - 1 /* to make an unaligned type */;
    }

    size_t initializeOffsets(size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    int hashCode() const { return calcHashCode(HASH_SEED, m_value); }

    bool operator==(const DummyObject& other) const
    {
        return m_value == other.m_value;
    }

    DummyObject operator+(const DummyObject& other) const
    {
        return DummyObject(m_value + other.m_value);
    }

    DummyObject& operator+=(const DummyObject& other)
    {
        m_value += other.m_value;
        return *this;
    }

    uint32_t getValue() const
    {
        return m_value;
    }

    void write(BitStreamWriter& out, PreWriteAction)
    {
        out.writeBits(m_value, static_cast<uint8_t>(bitSizeOf()));
    }

    void read(BitStreamReader& in)
    {
        m_value = in.readBits(bitSizeOf());
    }

private:
    uint32_t    m_value;
};

template <typename T>
class ArrayTestElementFactory;

template <>
class ArrayTestElementFactory<DummyObject>
{
public:
    void create(void* storage, BitStreamReader& in, size_t)
    {
        new (storage) DummyObject(in);
    }
};

template <typename T>
class ArrayTestElementInitializer;

template <>
class ArrayTestElementInitializer<DummyObject>
{
public:
    void initialize(DummyObject& element, size_t _index)
    {
        (void)_index;
        element.initialize(ELEMENT_VALUE);
    }

    static const uint32_t ELEMENT_VALUE = 0x12;
};

template <class OBJECT>
class ArrayTestOffsetHandler
{
public:
    explicit ArrayTestOffsetHandler(const ObjectArray<OBJECT>& array) :
        m_initialized(false),
        m_initialOffset(0)
    {
        size_t currentByteOffset = 0;
        for (typename ObjectArray<OBJECT>::const_iterator it = array.begin(); it != array.end(); ++it)
        {
            m_elementByteOffsets.push_back(currentByteOffset);
            currentByteOffset += bitsToBytes(alignTo(NUM_BITS_PER_BYTE, it->bitSizeOf()));
        }
    }

    void checkOffset(size_t index, size_t bytePosition)
    {
        if (!m_initialized)
        {
            m_initialOffset = bytePosition;
            m_initialized = true;
        }
        EXPECT_EQ(m_initialOffset + m_elementByteOffsets.at(index), bytePosition);
    }

    void setOffset(size_t index, size_t bytePosition)
    {
        checkOffset(index, bytePosition);
    }

private:
    bool m_initialized;
    std::vector<size_t> m_elementByteOffsets;
    size_t m_initialOffset;
};

class ObjectArrayTest : public ::testing::Test
{
public:
    ObjectArrayTest()
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <class OBJECT>
    void ArrayTest(ObjectArray<OBJECT>& array, size_t unalignedBitSize, size_t alignedBitSize)
    {
        ReadConstructorTest(array);
        AutoReadConstructorTest(array);
        AlignedReadConstructorTest(array);
        AutoAlignedReadConstructorTest(array);
        ImplicitReadConstructorTest(array);
        BitsizeOfTest(array, unalignedBitSize);
        AutoBitsizeOfTest(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        AlignedBitsizeOfTest(array, alignedBitSize);
        AutoAlignedBitsizeOfTest(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        InitializeOffsetsTest(array, unalignedBitSize);
        AutoInitializeOffsetsTest(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        AlignedInitializeOffsetsTest(array, alignedBitSize);
        AutoAlignedInitializeOffsetsTest(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        EqualOperatorTest(array);
        InitializeElementsTest(array);
        HashCodeTest(array);
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
    template <class OBJECT>
    void ReadConstructorTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> readConstructorArray(reader, array.size(), ArrayTestElementFactory<OBJECT>());
        EXPECT_EQ(array, readConstructorArray);
    }

    template <class OBJECT>
    void AutoReadConstructorTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> autoReadConstructorArray(reader, AutoLength(), ArrayTestElementFactory<OBJECT>());
        EXPECT_EQ(array, autoReadConstructorArray);
    }

    template <class OBJECT>
    void AlignedReadConstructorTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler<OBJECT>(array));
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> alignedReadConstructorArray(reader, array.size(), ArrayTestElementFactory<OBJECT>(),
                ArrayTestOffsetHandler<OBJECT>(array));
        EXPECT_EQ(array, alignedReadConstructorArray);
    }

    template <class OBJECT>
    void AutoAlignedReadConstructorTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler<OBJECT>(array));
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> autoAlignedReadConstructorArray(reader, AutoLength(),
                ArrayTestElementFactory<OBJECT>(), ArrayTestOffsetHandler<OBJECT>(array));
        EXPECT_EQ(array, autoAlignedReadConstructorArray);
    }

    template <class OBJECT>
    void ImplicitReadConstructorTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        ObjectArray<OBJECT> implicitReadConstructorArray(reader, ImplicitLength(),
                ArrayTestElementFactory<OBJECT>());
        for (size_t i = 0; i < implicitReadConstructorArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadConstructorArray[i]);
    }

    template <class OBJECT>
    void BitsizeOfTest(ObjectArray<OBJECT>& array, size_t unalignedBitSize)
    {
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(0));
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(7));
    }

    template <class OBJECT>
    void AutoBitsizeOfTest(ObjectArray<OBJECT>& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(autoUnalignedBitSize, array.bitSizeOf(0, AutoLength()));
        EXPECT_EQ(autoUnalignedBitSize, array.bitSizeOf(7, AutoLength()));
    }

    template <class OBJECT>
    void AlignedBitsizeOfTest(ObjectArray<OBJECT>& array, size_t alignedBitSize)
    {
        EXPECT_EQ(alignedBitSize, array.bitSizeOf(0, Aligned()));
        EXPECT_EQ(alignedBitSize + 1, array.bitSizeOf(7, Aligned()));
    }

    template <class OBJECT>
    void AutoAlignedBitsizeOfTest(ObjectArray<OBJECT>& array, size_t autoAlignedBitSize)
    {
        EXPECT_EQ(autoAlignedBitSize, array.bitSizeOf(0, AutoLength(), Aligned()));
        EXPECT_EQ(autoAlignedBitSize + 1, array.bitSizeOf(7, AutoLength(), Aligned()));
    }

    template <class OBJECT>
    void InitializeOffsetsTest(ObjectArray<OBJECT>& array, size_t unalignedBitSize)
    {
        EXPECT_EQ(0 + unalignedBitSize, array.initializeOffsets(0));
        EXPECT_EQ(7 + unalignedBitSize, array.initializeOffsets(7));
    }

    template <class OBJECT>
    void AutoInitializeOffsetsTest(ObjectArray<OBJECT>& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(0 + autoUnalignedBitSize, array.initializeOffsets(0, AutoLength()));
        EXPECT_EQ(7 + autoUnalignedBitSize, array.initializeOffsets(7, AutoLength()));
    }

    template <class OBJECT>
    void AlignedInitializeOffsetsTest(ObjectArray<OBJECT>& array, size_t alignedBitSize)
    {
        EXPECT_EQ(0 + alignedBitSize, array.initializeOffsets(0, ArrayTestOffsetHandler<OBJECT>(array)));
        EXPECT_EQ(7 + alignedBitSize + 1, array.initializeOffsets(7, ArrayTestOffsetHandler<OBJECT>(array)));
    }

    template <class OBJECT>
    void AutoAlignedInitializeOffsetsTest(ObjectArray<OBJECT>& array, size_t autoAlignedBitSize)
    {
        EXPECT_EQ(0 + autoAlignedBitSize,
                array.initializeOffsets(0, AutoLength(), ArrayTestOffsetHandler<OBJECT>(array)));
        EXPECT_EQ(7 + autoAlignedBitSize + 1,
                array.initializeOffsets(7, AutoLength(), ArrayTestOffsetHandler<OBJECT>(array)));
    }

    template <class OBJECT>
    void EqualOperatorTest(ObjectArray<OBJECT>& array)
    {
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> EqualArray(reader, array.size(), ArrayTestElementFactory<OBJECT>());
        ObjectArray<OBJECT> NoneEqualArray;
        EXPECT_TRUE(array == EqualArray);
        EXPECT_FALSE(array == NoneEqualArray);
    }

    template <class OBJECT>
    void InitializeElementsTest(ObjectArray<OBJECT>& array)
    {
        array.initializeElements(ArrayTestElementInitializer<OBJECT>());
        const uint32_t expectedValue = ArrayTestElementInitializer<OBJECT>::ELEMENT_VALUE;
        for (typename ObjectArray<OBJECT>::const_iterator it = array.begin(); it != array.end(); ++it)
            EXPECT_EQ(expectedValue, it->getValue());
    }

    template <class OBJECT>
    void HashCodeTest(ObjectArray<OBJECT>& array)
    {
        // check only if hashCode exists
        EXPECT_TRUE(array.hashCode() != 0);
    }

    template <class OBJECT>
    void ReadTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> readArray;
        readArray.read(reader, array.size(), ArrayTestElementFactory<OBJECT>());
        EXPECT_EQ(array, readArray);
    }

    template <class OBJECT>
    void AutoReadTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> autoReadArray;
        autoReadArray.read(reader, AutoLength(), ArrayTestElementFactory<OBJECT>());
        EXPECT_EQ(array, autoReadArray);
    }

    template <class OBJECT>
    void AlignedReadTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler<OBJECT>(array));
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> alignedReadArray;
        alignedReadArray.read(reader, array.size(), ArrayTestElementFactory<OBJECT>(),
                ArrayTestOffsetHandler<OBJECT>(array));
        EXPECT_EQ(array, alignedReadArray);
    }

    template <class OBJECT>
    void AutoAlignedReadTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler<OBJECT>(array));
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        ObjectArray<OBJECT> autoAlignedReadArray;
        autoAlignedReadArray.read(reader, AutoLength(), ArrayTestElementFactory<OBJECT>(),
                ArrayTestOffsetHandler<OBJECT>(array));
        EXPECT_EQ(array, autoAlignedReadArray);
    }

    template <class OBJECT>
    void ImplicitReadTest(ObjectArray<OBJECT>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        ObjectArray<OBJECT> implicitReadArray;
        implicitReadArray.read(reader, ImplicitLength(), ArrayTestElementFactory<OBJECT>());
        for (size_t i = 0; i < implicitReadArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadArray[i]);
    }

    template <class OBJECT>
    void WriteTest(ObjectArray<OBJECT>& array, size_t unalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        EXPECT_EQ(unalignedBitSize, writer.getBitPosition());
    }

    template <class OBJECT>
    void AutoWriteTest(ObjectArray<OBJECT>& array, size_t autoUnalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength());
        EXPECT_EQ(autoUnalignedBitSize, writer.getBitPosition());
    }

    template <class OBJECT>
    void AlignedWriteTest(ObjectArray<OBJECT>& array, size_t alignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetHandler<OBJECT>(array));
        EXPECT_EQ(alignedBitSize, writer.getBitPosition());
    }

    template <class OBJECT>
    void AutoAlignedWriteTest(ObjectArray<OBJECT>& array, size_t autoAlignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, AutoLength(), ArrayTestOffsetHandler<OBJECT>(array));
        EXPECT_EQ(autoAlignedBitSize, writer.getBitPosition());
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t BUFFER_SIZE = 256;

    uint8_t         m_byteBuffer[BUFFER_SIZE];
};

TEST_F(ObjectArrayTest, ObjectArray)
{
    ObjectArray<DummyObject> array;
    array.push_back(DummyObject(0xAB));
    array.push_back(DummyObject(0xCD));
    array.push_back(DummyObject(0xEF));
    size_t unalignedBitSize = 0;
    size_t alignedBitSize = 0;
    for (size_t i = 0; i < array.size(); ++i)
    {
        const size_t bitSize = array[i].bitSizeOf();
        unalignedBitSize += bitSize;
        alignedBitSize += (i == 0) ? bitSize : alignTo(NUM_BITS_PER_BYTE, bitSize);
    }
    ArrayTest<DummyObject>(array, unalignedBitSize, alignedBitSize);
}

} // namespace zserio
