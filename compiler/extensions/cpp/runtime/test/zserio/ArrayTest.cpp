#include <string>
#include <vector>
#include <limits>
#include <array>

#include "gtest/gtest.h"

#include "zserio/Array.h"
#include "zserio/ArrayTraits.h"
#include "zserio/Enums.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/BitBuffer.h"

#include "test_object/std_allocator/ArrayBitmask.h"
#include "test_object/std_allocator/ArrayEnum.h"
#include "test_object/std_allocator/ArrayObject.h"

namespace zserio
{

using ArrayEnum = test_object::std_allocator::ArrayEnum;
using ArrayBitmask = test_object::std_allocator::ArrayBitmask;
using ArrayObject = test_object::std_allocator::ArrayObject;

namespace
{

class ArrayTestOwner
{};

class ArrayTestOwnerWithBitSize
{
public:
    explicit ArrayTestOwnerWithBitSize(uint8_t bitSize) :
            m_bitSize(bitSize)
    {}

    uint8_t getBitSize() const
    {
        return m_bitSize;
    }

private:
    uint8_t m_bitSize;
};

template <typename ARRAY, typename OWNER_TYPE>
size_t arrayBitSizeOf(const ARRAY& array, const OWNER_TYPE& owner, size_t bitPosition)
{
    return array.bitSizeOf(owner, bitPosition);
}

template <typename ARRAY>
size_t arrayBitSizeOf(const ARRAY& array, const detail::DummyArrayOwner&, size_t bitPosition)
{
    return array.bitSizeOf(bitPosition);
}

template <typename ARRAY, typename OWNER_TYPE>
size_t arrayBitSizeOfPacked(const ARRAY& array, const OWNER_TYPE& owner, size_t bitPosition)
{
    return array.bitSizeOfPacked(owner, bitPosition);
}

template <typename ARRAY>
size_t arrayBitSizeOfPacked(const ARRAY& array, const detail::DummyArrayOwner&, size_t bitPosition)
{
    return array.bitSizeOfPacked(bitPosition);
}

template <typename ARRAY, typename OWNER_TYPE>
size_t arrayInitializeOffsets(ARRAY& array, OWNER_TYPE& owner, size_t bitPosition)
{
    return array.initializeOffsets(owner, bitPosition);
}

template <typename ARRAY>
size_t arrayInitializeOffsets(ARRAY& array, detail::DummyArrayOwner&, size_t bitPosition)
{
    return array.initializeOffsets(bitPosition);
}

template <typename ARRAY, typename OWNER_TYPE>
size_t arrayInitializeOffsetsPacked(ARRAY& array, OWNER_TYPE& owner, size_t bitPosition)
{
    return array.initializeOffsetsPacked(owner, bitPosition);
}

template <typename ARRAY>
size_t arrayInitializeOffsetsPacked(ARRAY& array, detail::DummyArrayOwner&, size_t bitPosition)
{
    return array.initializeOffsetsPacked(bitPosition);
}

template <typename ARRAY, typename OWNER_TYPE>
void arrayRead(ARRAY& array, OWNER_TYPE& owner, BitStreamReader& in, size_t arrayLength = 0)
{
    array.read(owner, in, arrayLength);
}

template <typename ARRAY>
void arrayRead(ARRAY& array, detail::DummyArrayOwner&, BitStreamReader& in, size_t arrayLength = 0)
{
    array.read(in, arrayLength);
}

template <typename ARRAY, typename OWNER_TYPE>
void arrayReadPacked(ARRAY& array, OWNER_TYPE& owner, BitStreamReader& in, size_t arrayLength = 0)
{
    array.readPacked(owner, in, arrayLength);
}

template <typename ARRAY>
void arrayReadPacked(ARRAY& array, detail::DummyArrayOwner&, BitStreamReader& in, size_t arrayLength = 0)
{
    array.readPacked(in, arrayLength);
}

template <typename ARRAY, typename OWNER_TYPE>
void arrayWrite(const ARRAY& array, const OWNER_TYPE& owner, BitStreamWriter& out)
{
    array.write(owner, out);
}

template <typename ARRAY>
void arrayWrite(const ARRAY& array, const detail::DummyArrayOwner&, BitStreamWriter& out)
{
    array.write(out);
}

template <typename ARRAY, typename OWNER_TYPE>
void arrayWritePacked(const ARRAY& array, const OWNER_TYPE& owner, BitStreamWriter& out)
{
    array.writePacked(owner, out);
}

template <typename ARRAY>
void arrayWritePacked(const ARRAY& array, const detail::DummyArrayOwner&, BitStreamWriter& out)
{
    array.writePacked(out);
}

class ElementBitSizeWithOwner
{
public:
    using OwnerType = ArrayTestOwnerWithBitSize;

    static uint8_t get(const ArrayTestOwnerWithBitSize& owner)
    {
        return owner.getBitSize();
    }
};

template <uint8_t BIT_SIZE>
class ElementBitSizeWithoutOwner
{
public:
    static uint8_t get()
    {
        return BIT_SIZE;
    }
};

class ArrayObjectArrayExpressions
{
public:
    using OwnerType = ArrayTestOwner;

    static void initializeOffset(ArrayTestOwner&, size_t, size_t)
    {}

    static void checkOffset(const ArrayTestOwner&, size_t, size_t)
    {}

    static void initializeElement(ArrayTestOwner&, ArrayObject& element, size_t)
    {
        // set value instead of call initialize, just for test
        element.setValue(ELEMENT_VALUE);
    }

    static const uint32_t ELEMENT_VALUE = 0x12;
};

class ArrayObjectElementFactory
{
public:
    using OwnerType = ArrayTestOwner;
    using allocator_type = std::allocator<uint8_t>;

    static ArrayObject create(OwnerType&, BitStreamReader& in,
            const allocator_type& allocator, size_t)
    {
        return ArrayObject(in, allocator);
    }

    static ArrayObject create(OwnerType&, ArrayObject::ZserioPackingContext& contextNode, BitStreamReader& in,
            const allocator_type& allocator, size_t)
    {
        return ArrayObject(contextNode, in, allocator);
    }
};

} // namespace

class ArrayTest : public ::testing::Test
{
public:
    ArrayTest() :
            m_byteBuffer()
    {}

protected:
    template <typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions,
            typename OWNER_TYPE = typename detail::array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS>::type,
            typename std::enable_if<!std::is_integral<OWNER_TYPE>::value, int>::type = 0>
    void testArray(const RAW_ARRAY& rawArray, size_t elementBitSize, OWNER_TYPE owner = OWNER_TYPE())
    {
        const size_t arraySize = rawArray.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        const size_t alignedBitSize = (arraySize > 0)
                ? alignTo(8, elementBitSize) * (arraySize - 1) + elementBitSize
                : 0;
        testArray<ARRAY_TRAITS>(rawArray, unalignedBitSize, alignedBitSize, owner);
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions,
            typename OWNER_TYPE = typename detail::array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS>::type>
    void testArray(const RAW_ARRAY& rawArray, size_t unalignedBitSize, size_t alignedBitSize,
            OWNER_TYPE owner = OWNER_TYPE())
    {
        testArray<UnpackedArray, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS, OWNER_TYPE>(
                rawArray, unalignedBitSize, alignedBitSize, owner);
        testArray<Array, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS, OWNER_TYPE>(
                rawArray, unalignedBitSize, alignedBitSize, owner);
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions,
            typename OWNER_TYPE = typename detail::array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS>::type>
    void testArray(const RAW_ARRAY& rawArray, size_t unalignedBitSize, size_t alignedBitSize,
            OWNER_TYPE owner)
    {
        testArrayNormal<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(rawArray, unalignedBitSize, owner);
        testArrayAuto<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, AUTO_LENGTH_BIT_SIZE + unalignedBitSize, owner);
        testArrayAligned<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(rawArray, alignedBitSize, owner);
        testArrayAlignedAuto<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, AUTO_LENGTH_BIT_SIZE + alignedBitSize, owner);
        testArrayImplicit<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(rawArray, unalignedBitSize, owner);
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArrayInitializeElements(const RAW_ARRAY& rawArray)
    {
        testArrayInitializeElements<UnpackedArray<
                RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ArrayObjectArrayExpressions>>(rawArray);
        testArrayInitializeElements<PackedArray<
                RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ArrayObjectArrayExpressions>>(rawArray);
        testArrayInitializeElements<Array<
                RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ArrayObjectArrayExpressions>>(rawArray);
    }

    template <typename ARRAY>
    void testArrayInitializeElements(const typename ARRAY::RawArray& rawArray)
    {
        ARRAY array(rawArray);
        ArrayTestOwner owner;
        array.initializeElements(owner);
        const uint32_t expectedValue = ArrayObjectArrayExpressions::ELEMENT_VALUE;
        for (const auto& element : array.getRawArray())
            ASSERT_EQ(expectedValue, element.getValue());
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions,
            typename OWNER_TYPE = typename detail::array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS>::type>
    void testPackedArray(const RAW_ARRAY& rawArray, OWNER_TYPE owner = OWNER_TYPE())
    {
        testPackedArray<ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, UNKNOWN_BIT_SIZE, UNKNOWN_BIT_SIZE, owner);
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions,
            typename OWNER_TYPE = typename detail::array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS>::type>
    void testPackedArray(const RAW_ARRAY& rawArray, size_t unalignedBitSize, size_t alignedBitSize,
            OWNER_TYPE owner = OWNER_TYPE())
    {
        testPackedArray<PackedArray, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS, OWNER_TYPE>(
                rawArray, unalignedBitSize, alignedBitSize, owner);
        testPackedArray<Array, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS, OWNER_TYPE>(
                rawArray, unalignedBitSize, alignedBitSize, owner);
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions,
            typename OWNER_TYPE = typename detail::array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS>::type>
    void testPackedArray(const RAW_ARRAY& rawArray, size_t unalignedBitSize, size_t alignedBitSize,
            OWNER_TYPE owner = OWNER_TYPE())
    {
        testPackedArrayNormal<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, unalignedBitSize, owner);
        testPackedArrayAuto<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, (unalignedBitSize != UNKNOWN_BIT_SIZE)
                        ? AUTO_LENGTH_BIT_SIZE + unalignedBitSize : UNKNOWN_BIT_SIZE, owner);
        testPackedArrayAligned<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, alignedBitSize, owner);
        testPackedArrayAlignedAuto<ARRAY, ARRAY_TRAITS, RAW_ARRAY, ARRAY_EXPRESSIONS>(
                rawArray, (alignedBitSize != UNKNOWN_BIT_SIZE)
                        ? AUTO_LENGTH_BIT_SIZE + alignedBitSize : UNKNOWN_BIT_SIZE, owner);
    }

    size_t calcPackedBitSize(size_t elementBitSize, size_t arraySize, size_t maxDeltaBitSize)
    {
        return PACKING_DESCRIPTOR_BITSIZE + elementBitSize + (arraySize - 1) * (maxDeltaBitSize + 1);
    }

    size_t calcAlignedPackedBitSize(size_t elementBitSize, size_t arraySize, size_t maxDeltaBitSize)
    {
        const size_t firstElementWithDescriptorBitSize = PACKING_DESCRIPTOR_BITSIZE + elementBitSize;
        const size_t alignedFirstElementWithDescriptorBitSize = (firstElementWithDescriptorBitSize + 7) / 8 * 8;
        const size_t alignedMaxDeltaBitSize = (maxDeltaBitSize + 1 + 7) / 8 * 8;

        return alignedFirstElementWithDescriptorBitSize +
                (arraySize - 2) * alignedMaxDeltaBitSize + (maxDeltaBitSize + 1);
    }

    static const size_t PACKING_DESCRIPTOR_BITSIZE = 1 + 6;

private:
    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testArrayNormal(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, owner, i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, owner, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testArrayAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, owner, i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, owner, reader);
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testArrayAligned(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, owner, i);
            if (expectedBitSize == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            else
            {
                ASSERT_EQ(alignTo(8, i) - i + expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, owner, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testArrayAlignedAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, owner, i);

            if (expectedBitSize == AUTO_LENGTH_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            else
            {
                ASSERT_EQ(alignTo(8, i) - i + expectedBitSize, bitSize) << expectedBitSize;
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, owner, reader);
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE,
            typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testArrayImplicit(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::IMPLICIT, ARRAY_EXPRESSIONS>;

        if (detail::arrayTraitsConstBitSizeOf<ARRAY_TRAITS>(owner) % 8 != 0)
            return; // implicit array allowed for types with constant bitsize rounded to bytes

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, owner, i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, owner, reader);
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE,
            typename std::enable_if<!ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testArrayImplicit(const RAW_ARRAY&, size_t, OWNER_TYPE&)
    {
        // implicit array not allowed for types with non-constant bitsize, so skip the test
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testPackedArrayNormal(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, owner, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, owner, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testPackedArrayAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, owner, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, owner, reader);
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testPackedArrayAligned(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, owner, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE && i == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, owner, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <template <typename, typename, ArrayType, typename> class ARRAY,
            typename ARRAY_TRAITS, typename RAW_ARRAY, typename ARRAY_EXPRESSIONS, typename OWNER_TYPE>
    void testPackedArrayAlignedAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize, OWNER_TYPE& owner)
    {
        using ArrayT = ARRAY<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO, ARRAY_EXPRESSIONS>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, owner, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE && i == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, owner, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, owner, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, owner, reader);
            ASSERT_EQ(array, readArray);

            testArrayCopiesAndMoves(array);
        }
    }

    template <typename ARRAY>
    void testArrayCopiesAndMoves(const ARRAY& array)
    {
        ARRAY arrayCopy(array);
        ASSERT_EQ(array, arrayCopy);
        ASSERT_EQ(array.getRawArray(), arrayCopy.getRawArray());

        ARRAY arrayCopyAssigned;
        arrayCopyAssigned = array;
        ASSERT_EQ(array, arrayCopyAssigned);
        ASSERT_EQ(array.getRawArray(), arrayCopyAssigned.getRawArray());

        const ARRAY arrayMoved = std::move(arrayCopy);
        ASSERT_EQ(array, arrayMoved);
        ASSERT_EQ(array.getRawArray(), arrayMoved.getRawArray());

        ARRAY arrayMoveAssigned;
        arrayMoveAssigned = std::move(arrayCopyAssigned);
        ASSERT_EQ(array, arrayMoveAssigned);
        ASSERT_EQ(array.getRawArray(), arrayMoveAssigned.getRawArray());

        ARRAY arrayCopyWithPropagateAllocator(PropagateAllocator, array, std::allocator<uint8_t>());
        ASSERT_EQ(array, arrayCopyWithPropagateAllocator);
        ASSERT_EQ(array.getRawArray(), arrayCopyWithPropagateAllocator.getRawArray());
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t UNKNOWN_BIT_SIZE = std::numeric_limits<size_t>::max();

    std::array<uint8_t, 256> m_byteBuffer;
};

TEST_F(ArrayTest, intField4Array)
{
    const size_t NUM_BITS = 4;
    std::vector<int8_t> rawArray = {
            -static_cast<int8_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int8_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int8_t, NUM_BITS>>(rawArray, NUM_BITS);

    // empty
    testArray<BitFieldArrayTraits<int8_t, NUM_BITS>>(std::vector<int8_t>(), NUM_BITS);
}

TEST_F(ArrayTest, intField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<int16_t> rawArray = {
            -static_cast<int16_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int16_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int16_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, intField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<int32_t> rawArray = {
            -static_cast<int32_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int32_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int32_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, intField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<int64_t> rawArray = {
            -static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)),
            7,
            static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int64_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField4Array)
{
    constexpr size_t NUM_BITS = 4;
    std::vector<uint8_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint8_t, NUM_BITS>>(rawArray, NUM_BITS);

    // empty
    testArray<BitFieldArrayTraits<uint8_t, NUM_BITS>>(std::vector<uint8_t>(), NUM_BITS);
}

TEST_F(ArrayTest, bitField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<uint16_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint16_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<uint32_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint32_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<uint64_t> rawArray = {0, 7, (UINT64_C(1) << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint64_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField4Array)
{
    constexpr size_t NUM_BITS = 4;
    std::vector<int8_t> rawArray = {
            -static_cast<int8_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int8_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int8_t, ElementBitSizeWithOwner>>(rawArray, NUM_BITS,
            ArrayTestOwnerWithBitSize(NUM_BITS));

    // empty
    testArray<DynamicBitFieldArrayTraits<int8_t, ElementBitSizeWithoutOwner<NUM_BITS>>>(
            std::vector<int8_t>(), NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField8Array)
{
    constexpr size_t NUM_BITS = 8; // aligned to allow implicit array
    std::vector<int8_t> rawArray = {INT8_MIN, 7, INT8_MAX};
    testArray<DynamicBitFieldArrayTraits<int8_t, ElementBitSizeWithOwner>>(rawArray, NUM_BITS,
            ArrayTestOwnerWithBitSize(NUM_BITS));
}

TEST_F(ArrayTest, dynamicIntField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<int16_t> rawArray = {
            -static_cast<int16_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int16_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int16_t, ElementBitSizeWithoutOwner<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<int32_t> rawArray = {
            -static_cast<int32_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int32_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int32_t, ElementBitSizeWithOwner>>(rawArray, NUM_BITS,
            ArrayTestOwnerWithBitSize(NUM_BITS));
}

TEST_F(ArrayTest, dynamicIntField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<int64_t> rawArray = {
            -static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)),
            7,
            static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int64_t, ElementBitSizeWithoutOwner<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField4Array)
{
    constexpr size_t NUM_BITS = 4;
    std::vector<uint8_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint8_t, ElementBitSizeWithOwner>>(rawArray, NUM_BITS,
            ArrayTestOwnerWithBitSize(NUM_BITS));

    // empty
    testArray<DynamicBitFieldArrayTraits<uint8_t, ElementBitSizeWithoutOwner<NUM_BITS>>>(
            std::vector<uint8_t>(), NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField8Array)
{
    constexpr size_t NUM_BITS = 8; // aligned to allow implicit array
    std::vector<uint8_t> rawArray = {0, 7, UINT8_MAX};
    testArray<DynamicBitFieldArrayTraits<uint8_t, ElementBitSizeWithOwner>>(rawArray, NUM_BITS,
            ArrayTestOwnerWithBitSize(NUM_BITS));
}

TEST_F(ArrayTest, dynamicBitField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<uint16_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint16_t, ElementBitSizeWithoutOwner<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<uint32_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint32_t, ElementBitSizeWithOwner>>(rawArray, NUM_BITS,
            ArrayTestOwnerWithBitSize(NUM_BITS));
}

TEST_F(ArrayTest, dynamicBitField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<uint64_t> rawArray = {0, 7, (UINT64_C(1) << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint64_t, ElementBitSizeWithoutOwner<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, stdInt8Array)
{
    std::vector<int8_t> rawArray = {INT8_MIN, 7, INT8_MAX};
    testArray<StdIntArrayTraits<int8_t>>(rawArray, 8);

    // empty
    testArray<StdIntArrayTraits<int8_t>>(std::vector<int8_t>(), 8);
}

TEST_F(ArrayTest, stdInt16Array)
{
    std::vector<int16_t> rawArray = {INT16_MIN, 7, INT16_MAX};
    testArray<StdIntArrayTraits<int16_t>>(rawArray, 16);
}

TEST_F(ArrayTest, stdInt32Array)
{
    std::vector<int32_t> rawArray = {INT32_MIN, 7, INT32_MAX};
    testArray<StdIntArrayTraits<int32_t>>(rawArray, 32);
}

TEST_F(ArrayTest, stdInt64Array)
{
    std::vector<int64_t> rawArray = {INT64_MIN, 7, INT64_MAX};
    testArray<StdIntArrayTraits<int64_t>>(rawArray, 64);
}

TEST_F(ArrayTest, stdUInt8Array)
{
    std::vector<uint8_t> rawArray = {0, 7, UINT8_MAX};
    testArray<StdIntArrayTraits<uint8_t>>(rawArray, 8);

    // empty
    testArray<StdIntArrayTraits<uint8_t>>(std::vector<uint8_t>(), 8);
}

TEST_F(ArrayTest, stdUInt16Array)
{
    std::vector<uint16_t> rawArray = {0, 7, UINT16_MAX};
    testArray<StdIntArrayTraits<uint16_t>>(rawArray, 16);
}

TEST_F(ArrayTest, stdUInt32Array)
{
    std::vector<uint32_t> rawArray = {0, 7, UINT32_MAX};
    testArray<StdIntArrayTraits<uint32_t>>(rawArray, 32);
}

TEST_F(ArrayTest, stdUInt64Array)
{
    std::vector<uint64_t> rawArray = {0, 7, UINT64_MAX};
    testArray<StdIntArrayTraits<uint64_t>>(rawArray, 64);
}

TEST_F(ArrayTest, varInt16Array)
{
    std::vector<int16_t> rawArray = {static_cast<int16_t>(1U << 5U), static_cast<int16_t>(1U << (5U + 8))};
    const size_t bitSize = 8 * (1 + 2);
    testArray<VarIntNNArrayTraits<int16_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntNNArrayTraits<int16_t>>(std::vector<int16_t>(), 0, 0);
}

TEST_F(ArrayTest, varInt32Array)
{
    std::vector<int32_t> rawArray = {
            static_cast<int32_t>(1U << 5U),
            static_cast<int32_t>(1U << (5U + 7)),
            static_cast<int32_t>(1U << (5U + 7 + 7)),
            static_cast<int32_t>(1U << (5U + 7 + 7 + 8))};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray<VarIntNNArrayTraits<int32_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntNNArrayTraits<int32_t>>(std::vector<int32_t>(), 0, 0);
}

TEST_F(ArrayTest, varInt64Array)
{
    std::vector<int64_t> rawArray = {
            static_cast<int64_t>(UINT64_C(1) << 5U),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7 + 7 + 7 + 8))};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray<VarIntNNArrayTraits<int64_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntNNArrayTraits<int64_t>>(std::vector<int64_t>(), 0, 0);
}

TEST_F(ArrayTest, varUInt16Array)
{
    std::vector<uint16_t> rawArray = {1U << 6U, 1U << (6U + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray<VarIntNNArrayTraits<uint16_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntNNArrayTraits<uint16_t>>(std::vector<uint16_t>(), 0, 0);
}

TEST_F(ArrayTest, varUInt32Array)
{
    std::vector<uint32_t> rawArray = {1U << 6U, 1U << (6U + 7), 1U << (6U + 7 + 7), 1U << (6U + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray<VarIntNNArrayTraits<uint32_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntNNArrayTraits<uint32_t>>(std::vector<uint32_t>(), 0, 0);
}

TEST_F(ArrayTest, varUInt64Array)
{
    std::vector<uint64_t> rawArray = {
            UINT64_C(1) << 6U,
            UINT64_C(1) << (6U + 7),
            UINT64_C(1) << (6U + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray<VarIntNNArrayTraits<uint64_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntNNArrayTraits<uint64_t>>(std::vector<uint64_t>(), 0, 0);
}

TEST_F(ArrayTest, varIntArray)
{
    std::vector<int64_t> rawArray;
    // 1 byte
    rawArray.push_back(0);
    rawArray.push_back(-1);
    rawArray.push_back(1);
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 6U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 6U) - 1);
    // 2 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 13U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 13U) - 1);
    // 3 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 20U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 20U) - 1);
    // 4 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 27U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 27U) - 1);
    // 5 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 34U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 34U) - 1);
    // 6 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 41U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 41U) - 1);
    // 7 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 48U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 48U) - 1);
    // 8 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 55U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 55U) - 1);
    // 9 bytes
    rawArray.push_back(INT64_MIN + 1);
    rawArray.push_back(INT64_MAX);
    // 1 byte - special case, INT64_MIN stored as -0
    rawArray.push_back(INT64_MIN);
    const size_t bitSize = 8 * (3 + 2 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9) + 1);
    testArray<VarIntArrayTraits<int64_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntArrayTraits<int64_t>>(std::vector<int64_t>(), 0, 0);
}

TEST_F(ArrayTest, varUIntArray)
{
    std::vector<uint64_t> rawArray;
    // 1 byte
    rawArray.push_back(0);
    rawArray.push_back(1);
    rawArray.push_back((UINT64_C(1) << 7U) - 1);
    // 2 bytes
    rawArray.push_back((UINT64_C(1) << 14U) - 1);
    // 3 bytes
    rawArray.push_back((UINT64_C(1) << 21U) - 1);
    // 4 bytes
    rawArray.push_back((UINT64_C(1) << 28U) - 1);
    // 5 bytes
    rawArray.push_back((UINT64_C(1) << 35U) - 1);
    // 6 bytes
    rawArray.push_back((UINT64_C(1) << 42U) - 1);
    // 7 bytes
    rawArray.push_back((UINT64_C(1) << 49U) - 1);
    // 8 bytes
    rawArray.push_back((UINT64_C(1) << 56U) - 1);
    // 9 bytes
    rawArray.push_back(UINT64_MAX);
    const size_t bitSize = 8 * (2 + (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9));
    testArray<VarIntArrayTraits<uint64_t>>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarIntArrayTraits<uint64_t>>(std::vector<uint64_t>(), 0, 0);
}

TEST_F(ArrayTest, varSizeArray)
{
    std::vector<uint32_t> rawArray = {
            UINT32_C(1) << 6U,
            UINT32_C(1) << (6U + 7),
            UINT32_C(1) << (6U + 7 + 7),
            UINT32_C(1) << (6U + 7 + 7 + 7),
            UINT32_C(1) << (1U + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5);
    testArray<VarSizeArrayTraits>(rawArray, bitSize, bitSize);

    // empty
    testArray<VarSizeArrayTraits>(std::vector<uint32_t>(), 0, 0);
}

TEST_F(ArrayTest, float16Array)
{
    const size_t elementBitSize = 16;
    std::vector<float> rawArray = {-9.0, 0.0,  10.0};
    testArray<Float16ArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<Float16ArrayTraits>(std::vector<float>(), elementBitSize);
}

TEST_F(ArrayTest, float32Array)
{
    const size_t elementBitSize = 32;
    std::vector<float> rawArray = {-9.0, 0.0,  10.0};
    testArray<Float32ArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<Float32ArrayTraits>(std::vector<float>(), elementBitSize);
}

TEST_F(ArrayTest, float64Array)
{
    const size_t elementBitSize = 64;
    std::vector<double> rawArray = {-9.0, 0.0, 10.0};
    testArray<Float64ArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<Float64ArrayTraits>(std::vector<double>(), elementBitSize);
}

TEST_F(ArrayTest, boolArray)
{
    const size_t elementBitSize = 1;
    std::vector<bool> rawArray = {false, true};
    testArray<BoolArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<BoolArrayTraits>(std::vector<bool>(), elementBitSize);
}

TEST_F(ArrayTest, bytesArray)
{
    const size_t bytesLengthBitSize = 8;
    const size_t bytesBitSize = 2 * 8;
    const size_t elementBitSize = bytesLengthBitSize + bytesBitSize;
    std::vector<std::vector<uint8_t>> rawArray = {{ {{ 1, 255 }}, {{ 127, 128 }} }};
    testArray<BytesArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<BytesArrayTraits>(std::vector<std::vector<uint8_t>>(), elementBitSize);
}

TEST_F(ArrayTest, stringArray)
{
    const size_t stringLengthBitSize = 8;
    const size_t stringBitSize = (sizeof("StringX") - 1) * 8; // without terminating character
    const size_t elementBitSize = stringLengthBitSize + stringBitSize;
    std::vector<std::string> rawArray = {"String0", "String1", "String2"};
    testArray<StringArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<StringArrayTraits>(std::vector<std::string>(), 0);
}

TEST_F(ArrayTest, bitBufferArray)
{
    const size_t bitBufferLengthBitSize = 8;
    const size_t bitBufferBitSize = 10;
    const size_t elementBitSize = bitBufferLengthBitSize + bitBufferBitSize;
    std::vector<BitBuffer> rawArray = {BitBuffer(bitBufferBitSize), BitBuffer(bitBufferBitSize),
            BitBuffer(bitBufferBitSize)};
    testArray<BitBufferArrayTraits>(rawArray, elementBitSize);

    // empty
    testArray<BitBufferArrayTraits>(std::vector<BitBuffer>(), 0);
}

TEST_F(ArrayTest, enumArray)
{
    std::vector<ArrayEnum> rawArray = {ArrayEnum::VALUE1, ArrayEnum::VALUE2, ArrayEnum::VALUE3};
    const size_t elementBitSize = 8;
    testArray<EnumArrayTraits<ArrayEnum>>(rawArray, elementBitSize);

    // empty
    testArray<EnumArrayTraits<ArrayEnum>>(std::vector<ArrayEnum>(), elementBitSize);

    std::vector<ArrayEnum> invalidRawArray = {static_cast<ArrayEnum>(10)};
    ASSERT_THROW(testArray<EnumArrayTraits<ArrayEnum>>(invalidRawArray, elementBitSize), CppRuntimeException);
}

TEST_F(ArrayTest, bitmaskArray)
{
    std::vector<ArrayBitmask> rawArray = {ArrayBitmask::Values::READ, ArrayBitmask::Values::WRITE,
            ArrayBitmask::Values::CREATE};
    const size_t elementBitSize = 8;
    testArray<BitmaskArrayTraits<ArrayBitmask>>(rawArray, elementBitSize);

    // empty
    testArray<BitmaskArrayTraits<ArrayBitmask>>(std::vector<ArrayBitmask>(), elementBitSize);
}

TEST_F(ArrayTest, objectArray)
{
    std::vector<ArrayObject> rawArray = {ArrayObject(0xAB), ArrayObject(0xCD), ArrayObject(0xEF)};
    testArrayInitializeElements<ObjectArrayTraits<ArrayObject, ArrayObjectElementFactory>>(rawArray);
    testArray<ObjectArrayTraits<ArrayObject, ArrayObjectElementFactory>, std::vector<ArrayObject>,
            ArrayObjectArrayExpressions>(rawArray, 31);

    // empty
    testArray<ObjectArrayTraits<ArrayObject, ArrayObjectElementFactory>, std::vector<ArrayObject>,
            ArrayObjectArrayExpressions>(std::vector<ArrayObject>(), 31);

}

TEST_F(ArrayTest, stdInt8PackedArray)
{
    std::vector<int8_t> rawArray = { -4, -3, -1, 0, 2, 4, 6, 8, 10, 10, 11 };
    testPackedArray<StdIntArrayTraits<int8_t>>(rawArray);
}

TEST_F(ArrayTest, stdInt64PackedArray)
{
    // will not be packed
    std::vector<int64_t> rawArray = { INT64_MIN, 1, -1, INT64_MAX };
    testPackedArray<StdIntArrayTraits<int64_t>>(rawArray);
}

TEST_F(ArrayTest, stdUInt64PackedArray)
{
    // will have maxBitNumber 62 bits
    std::vector<uint64_t> rawArray = { 0, INT64_MAX / 2, 100, 200, 300, 400, 500, 600, 700 };
    testPackedArray<StdIntArrayTraits<uint64_t>>(rawArray);
}

TEST_F(ArrayTest, bitField64PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<uint64_t, 64>;

    // none-zero delta
    std::vector<uint64_t> rawArray1 = {10, 11, 12};
    const size_t array1MaxDeltaBitSize = 1;
    const size_t array1BitSizeOf = calcPackedBitSize(64, rawArray1.size(), array1MaxDeltaBitSize);
    const size_t array1AlignedBitSizeOf = calcAlignedPackedBitSize(64, rawArray1.size(), array1MaxDeltaBitSize);
    testPackedArray<ArrayTraits>(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf);

    // zero delta
    std::vector<uint64_t> rawArray2 = {10, 10, 10};
    const size_t array2BitSizeOf = PACKING_DESCRIPTOR_BITSIZE + 64;
    const size_t array2AlignedBitSizeOf = PACKING_DESCRIPTOR_BITSIZE + 64 +
            /* alignment before element 2 */ (8 - PACKING_DESCRIPTOR_BITSIZE);
    testPackedArray<ArrayTraits>(rawArray2, array2BitSizeOf, array2AlignedBitSizeOf);

    // one-element array
    std::vector<uint64_t> rawArray3 = {10};
    const size_t array3BitSizeOf = 1 + 64;
    const size_t array3AlignedBitSizeOf = 1 + 64; // no alignment before first element, no more elements
    testPackedArray<ArrayTraits>(rawArray3, array3BitSizeOf, array3AlignedBitSizeOf);

    // empty array
    std::vector<uint64_t> rawArray4 = {};
    const size_t array4BitSizeOf = 0;
    const size_t array4AlignedBitSizeOf = 0;
    testPackedArray<ArrayTraits>(rawArray4, array4BitSizeOf, array4AlignedBitSizeOf);

    // packing not enabled, delta is too big
    testPackedArray<ArrayTraits>(std::vector<uint64_t>{0, UINT64_MAX});
    testPackedArray<ArrayTraits>(std::vector<uint64_t>{UINT64_MAX, UINT64_MAX / 2, 0});

    // will have maxBitNumber 62 bits
    testPackedArray<ArrayTraits>(
            std::vector<uint64_t>{0, static_cast<uint64_t>(INT64_MAX / 2), 100, 200, 300, 400, 500, 600, 700});
}

TEST_F(ArrayTest, bitField8PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<uint8_t, 8>;

    // will not be packed because unpacked 8bit values will be more efficient
    std::vector<uint8_t> rawArray1 = {UINT8_MAX, 0, 10, 20, 30, 40}; // max_bit_number 8, delta needs 9 bits
    const size_t array1BitSizeOf = 1 + 6 * 8;
    const size_t array1AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<ArrayTraits>(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf);

    // will not be packed because unpacked 8bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<uint8_t> rawArray2 =
            {UINT8_MAX, UINT8_MAX / 2 + 1, 10, 20, 30, 40}; // max_bit_number 7, delta needs 8 bits
    const size_t array2BitSizeOf = 1 + 6 * 8;
    const size_t array2AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<ArrayTraits>(rawArray2, array2BitSizeOf, array2AlignedBitSizeOf);
}

TEST_F(ArrayTest, intField64PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<int64_t, 64>;

    testPackedArray<ArrayTraits>(std::vector<int64_t>{-10, 11, -12});
    testPackedArray<ArrayTraits>(std::vector<int64_t>{-10, -10, -10}); // zero delta

    testPackedArray<ArrayTraits>(std::vector<int64_t>{}); // empty
    testPackedArray<ArrayTraits>(std::vector<int64_t>{10}); // single element

    // packing not enabled, delta is too big
    testPackedArray<ArrayTraits>(std::vector<int64_t>{INT64_MIN, INT64_MAX});
    testPackedArray<ArrayTraits>(std::vector<int64_t>{INT64_MIN, 0, INT64_MAX});
}

TEST_F(ArrayTest, intField16PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<int16_t, 16>;

    // will not be packed because unpacked 16bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<int16_t> rawArray = {INT16_MIN, -1, 10, 20, 30, 40}; // max_bit_number 15, delta needs 16 bits
    const size_t unpackedBitSizeOf = 1 + 6 * 16;
    const size_t unpackedAlignedBitSizeOf = 1 + 16 + /* alignment */ 7 + 5 * 16;
    testPackedArray<ArrayTraits>(rawArray, unpackedBitSizeOf, unpackedAlignedBitSizeOf);
}

TEST_F(ArrayTest, dynamicBitField8PackedArray)
{
    // will not be packed because unpacked 8bit values will be more efficient
    std::vector<uint8_t> rawArray1 = {UINT8_MAX, 0, 10, 20, 30, 40}; // max_bit_number 8, delta needs 9 bits
    const size_t array1BitSizeOf = 1 + 6 * 8;
    const size_t array1AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<DynamicBitFieldArrayTraits<uint8_t, ElementBitSizeWithOwner>>(
            rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, ArrayTestOwnerWithBitSize(8));

    // will not be packed because unpacked 8bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<uint8_t> rawArray2 =
            {UINT8_MAX, UINT8_MAX / 2 + 1, 10, 20, 30, 40}; // max_bit_number 7, delta needs 8 bits
    const size_t array2BitSizeOf = 1 + 6 * 8;
    const size_t array2AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<DynamicBitFieldArrayTraits<uint8_t, ElementBitSizeWithoutOwner<8>>>(
            rawArray2, array2BitSizeOf, array2AlignedBitSizeOf);
}

TEST_F(ArrayTest, varUInt64PackedArray)
{
    std::vector<uint64_t> rawArray = {
            UINT64_C(1) << 6U,
            UINT64_C(1) << (6U + 7),
            UINT64_C(1) << (6U + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    testPackedArray<VarIntNNArrayTraits<uint64_t>>(rawArray);

    std::vector<uint64_t> unpackedRawArray = {UINT64_C(5000000), 0, 0, 0, 0, 0, 0};
    const size_t unpackedBitSizeOf = 1 + 32 + 6 * 8;
    const size_t unpackedAlignedBitSizeOf = 1 + 32 + /* alignment */ 7 + 6 * 8;
    testPackedArray<VarIntNNArrayTraits<uint64_t>>(unpackedRawArray,
            unpackedBitSizeOf, unpackedAlignedBitSizeOf);
}

TEST_F(ArrayTest, varSizePackedArray)
{
    std::vector<uint32_t> rawArray = {
            UINT32_C(1) << 6U,
            UINT32_C(1) << (6U + 7),
            UINT32_C(1) << (6U + 7 + 7),
            UINT32_C(1) << (6U + 7 + 7 + 7),
            UINT32_C(1) << (1U + 7 + 7 + 7 + 8)};
    testPackedArray<VarSizeArrayTraits>(rawArray);
}

TEST_F(ArrayTest, enumPackedArray)
{
    std::vector<ArrayEnum> rawArray = {ArrayEnum::VALUE1, ArrayEnum::VALUE2, ArrayEnum::VALUE3};
    testPackedArray<EnumArrayTraits<ArrayEnum>>(rawArray);
}

TEST_F(ArrayTest, bitmaskPackedArray)
{
    std::vector<ArrayBitmask> rawArray = {ArrayBitmask::Values::READ, ArrayBitmask::Values::WRITE,
            ArrayBitmask::Values::CREATE};
    testPackedArray<BitmaskArrayTraits<ArrayBitmask>>(rawArray);
}

TEST_F(ArrayTest, objectPackedArray)
{
    std::vector<ArrayObject> rawArray = {ArrayObject(0xAB), ArrayObject(0xCD), ArrayObject(0xEF)};
    testPackedArray<ObjectArrayTraits<ArrayObject, ArrayObjectElementFactory>, std::vector<ArrayObject>,
            ArrayObjectArrayExpressions>(rawArray);
}

TEST_F(ArrayTest, createOptionalArray)
{
    using ArrayT = Array<std::vector<uint8_t>, BitFieldArrayTraits<uint8_t, 8>, ArrayType::NORMAL>;
    InplaceOptionalHolder<ArrayT> optionalArray = createOptionalArray<ArrayT>(NullOpt);
    ASSERT_FALSE(optionalArray.hasValue());

    const std::vector<uint8_t> array = {0, 7, UINT8_MAX};
    optionalArray = createOptionalArray<ArrayT>(array);
    ASSERT_TRUE(optionalArray.hasValue());
    ASSERT_EQ(array, optionalArray->getRawArray());
}

} // namespace zserio
