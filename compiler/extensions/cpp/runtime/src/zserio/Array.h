#ifndef ZSERIO_ARRAY_H_INC
#define ZSERIO_ARRAY_H_INC

#include <type_traits>
#include <vector>
#include <string>

#include "zserio/AllocatorPropagatingCopy.h"
#include "zserio/ArrayTraits.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/VarSizeUtil.h"
#include "zserio/PreWriteAction.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/Enums.h"

namespace zserio
{

namespace detail
{

// dummy offset initializer used for arrays which don't need to initialize offsets
struct DummyOffsetInitializer
{
    void initializeOffset(size_t, size_t) const
    {}
};


// dummy offset checker used for arrays which don't need to check offsets.
struct DummyOffsetChecker
{
    void checkOffset(size_t, size_t) const
    {}
};

template <typename RAW_ARRAY, typename ARRAY_TRAITS>
size_t bitSizeOf(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t bitPosition)
{
    const size_t arrayLength = rawArray.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        return arrayLength == 0 ? 0 : arrayLength * arrayTraits.bitSizeOf(rawArray, bitPosition, 0);

    size_t endBitPosition = bitPosition;
    for (size_t index = 0; index < arrayLength; ++index)
        endBitPosition += arrayTraits.bitSizeOf(rawArray, endBitPosition, index);

    return endBitPosition - bitPosition;
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS>
size_t bitSizeOfAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t bitPosition)
{
    size_t endBitPosition = bitPosition;
    const size_t arrayLength = rawArray.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && arrayLength > 0)
    {
        const size_t elementBitSize = arrayTraits.bitSizeOf(rawArray, bitPosition, 0);
        endBitPosition = alignTo(8, endBitPosition);
        endBitPosition += (arrayLength - 1) * alignTo(8, elementBitSize) + elementBitSize;
    }
    else
    {
        for (size_t index = 0; index < arrayLength; ++index)
        {
            endBitPosition = alignTo(8, endBitPosition);
            endBitPosition += arrayTraits.bitSizeOf(rawArray, endBitPosition, index);
        }
    }

    return endBitPosition - bitPosition;
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS>
size_t initializeOffsets(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t bitPosition)
{
    const size_t arrayLength = rawArray.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
    {
        return bitPosition +
                (arrayLength == 0 ? 0 : arrayLength * arrayTraits.bitSizeOf(rawArray, bitPosition, 0));
    }

    size_t endBitPosition = bitPosition;
    for (size_t index = 0; index < arrayLength; ++index)
        endBitPosition = arrayTraits.initializeOffsets(rawArray, endBitPosition, index);

    return endBitPosition;
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename OFFSET_INITIALIZER>
size_t initializeOffsetsAligned(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t bitPosition,
        const OFFSET_INITIALIZER& offsetInitializer)
{
    size_t endBitPosition = bitPosition;
    for (size_t index = 0; index < rawArray.size(); ++index)
    {
        endBitPosition = alignTo(8, endBitPosition);
        offsetInitializer.initializeOffset(index, bitsToBytes(endBitPosition));
        endBitPosition = arrayTraits.initializeOffsets(rawArray, endBitPosition, index);
    }

    return endBitPosition;
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS>
void read(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t size)
{
    rawArray.clear();
    rawArray.reserve(size);
    for (size_t index = 0; index < size; ++index)
        arrayTraits.read(rawArray, in, index);
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void readAligned(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t size,
        const OFFSET_CHECKER& offsetChecker)
{
    rawArray.clear();
    rawArray.reserve(size);
    for (size_t index = 0; index < size; ++index)
    {
        in.alignTo(8);
        offsetChecker.checkOffset(index, bitsToBytes(in.getBitPosition()));
        arrayTraits.read(rawArray, in, index);
    }
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS>
void write(RAW_ARRAY& rawArray, ARRAY_TRAITS& arrayTraits, BitStreamWriter& out)
{
    for (size_t index = 0; index < rawArray.size(); ++index)
        arrayTraits.write(rawArray, out, index);
}

template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void writeAligned(RAW_ARRAY& rawArray,
        ARRAY_TRAITS& arrayTraits, BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
{
    for (size_t index = 0; index < rawArray.size(); ++index)
    {
        out.alignTo(8);
        offsetChecker.checkOffset(index, bitsToBytes(out.getBitPosition()));
        arrayTraits.write(rawArray, out, index);
    }
}

} // namespace detail

enum ArrayType
{
    NORMAL,
    IMPLICIT,
    ALIGNED,
    AUTO,
    ALIGNED_AUTO
};

template <typename RAW_ARRAY, typename ARRAY_TRAITS, ArrayType ARRAY_TYPE,
        typename OFFSET_INITIALIZER = detail::DummyOffsetInitializer,
        typename OFFSET_CHECKER = detail::DummyOffsetChecker>
class Array
{
public:
    using RawArray = RAW_ARRAY;
    using allocator_type = typename RawArray::allocator_type;

    explicit Array(const ARRAY_TRAITS& arrayTraits, const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, OFFSET_INITIALIZER(), OFFSET_CHECKER(), allocator)
    {}

    Array(const ARRAY_TRAITS& arrayTraits,
            const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker,
            const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator), m_arrayTraits(arrayTraits),
            m_offsetInitializer(offsetInitializer), m_offsetChecker(offsetChecker)
    {}

    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
            const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, 0, allocator)
    {}

    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arrayLengthArg,
            const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, arrayLengthArg, OFFSET_INITIALIZER(), OFFSET_CHECKER(), allocator)
    {}

    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arrayLengthArg,
            const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker,
            const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator), m_arrayTraits(arrayTraits),
            m_offsetInitializer(offsetInitializer), m_offsetChecker(offsetChecker)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT || ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT,
                "Implicit array elements must have constant bit size!");

        switch (ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
                detail::read(m_rawArray, m_arrayTraits, in, arrayLengthArg);
                break;
            case ArrayType::IMPLICIT:
                {
                    const size_t remainingBits = in.getBufferBitSize() - in.getBitPosition();
                    const size_t arrayLength = remainingBits / arrayTraits.bitSizeOf(m_rawArray, 0, 0);
                    detail::read(m_rawArray, m_arrayTraits, in, arrayLength);
                }
                break;
            case ArrayType::ALIGNED:
                detail::readAligned(m_rawArray, m_arrayTraits, in, arrayLengthArg, m_offsetChecker);
                break;
            case ArrayType::AUTO:
                {
                    const uint32_t arrayLength = in.readVarSize();
                    detail::read(m_rawArray, m_arrayTraits, in, arrayLength);
                }
                break;
            case ArrayType::ALIGNED_AUTO:
                {
                    const uint32_t arrayLength = in.readVarSize();
                    detail::readAligned(m_rawArray, m_arrayTraits, in, arrayLength, m_offsetChecker);
                }
                break;
        }
    }

    ~Array() = default;

    Array(const Array& other) = default;
    Array& operator=(const Array& other) = default;

    Array(Array&& other) = default;
    Array& operator=(Array&& other) = default;

    Array(::zserio::PropagateAllocatorT,
            const Array& other, const allocator_type& allocator) :
            m_rawArray(::zserio::allocatorPropagatingCopy(other.m_rawArray, allocator)),
            m_arrayTraits(other.m_arrayTraits),
            m_offsetInitializer(other.m_offsetInitializer),
            m_offsetChecker(other.m_offsetChecker)
    {
    }

    Array& operator=(const RawArray& rawArray)
    {
        m_rawArray = rawArray;
        return *this;
    }

    Array& operator=(RawArray&& rawArray)
    {
        m_rawArray = std::move(rawArray);
        return *this;
    }

    /**
     * Gets raw array.
     *
     * \return Constant reference to the raw array.
     */
    const RawArray& getRawArray() const
    {
        return m_rawArray;
    }

    /**
     * Gets raw array.
     *
     * \return Reference to the raw array.
     */
    RawArray& getRawArray()
    {
        return m_rawArray;
    }

    bool operator==(const Array& other) const
    {
        return m_rawArray == other.m_rawArray;
    }

    uint32_t hashCode() const
    {
        return calcHashCode(HASH_SEED, m_rawArray);
    }

    /**
    * Calculates bit size of this array.
    *
    * \param bitPosition Current bit position.
    *
    * \return Bit size of the array.
    */
    size_t bitSizeOf(size_t bitPosition = 0) const
    {
        switch (ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
            case ArrayType::IMPLICIT:
                return detail::bitSizeOf(m_rawArray, m_arrayTraits, bitPosition);
            case ArrayType::ALIGNED:
                return detail::bitSizeOfAligned(m_rawArray, m_arrayTraits, bitPosition);
            case ArrayType::AUTO:
            {
                const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(convertSizeToUInt32(m_rawArray.size()));
                return lengthBitSizeOf + detail::bitSizeOf(
                        m_rawArray, m_arrayTraits, lengthBitSizeOf + bitPosition);
            }
            case ArrayType::ALIGNED_AUTO:
            {
                const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(convertSizeToUInt32(m_rawArray.size()));
                return lengthBitSizeOf + detail::bitSizeOfAligned(
                        m_rawArray, m_arrayTraits, lengthBitSizeOf + bitPosition);
            }
        }
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        switch(ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
            case ArrayType::IMPLICIT:
                return detail::initializeOffsets(m_rawArray, m_arrayTraits, bitPosition);
            case ArrayType::ALIGNED:
                return detail::initializeOffsetsAligned(
                        m_rawArray, m_arrayTraits, bitPosition, m_offsetInitializer);
            case ArrayType::AUTO:
                {
                    const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(
                            convertSizeToUInt32(m_rawArray.size()));
                    return detail::initializeOffsets(m_rawArray, m_arrayTraits, bitPosition + lengthBitSizeOf);
                }
            case ArrayType::ALIGNED_AUTO:
                {
                    const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(
                            convertSizeToUInt32(m_rawArray.size()));
                    return detail::initializeOffsetsAligned(
                            m_rawArray, m_arrayTraits, bitPosition + lengthBitSizeOf, m_offsetInitializer);
                }
        }
    }

    void write(BitStreamWriter& out)
    {
        switch (ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
            case ArrayType::IMPLICIT:
                detail::write(m_rawArray, m_arrayTraits, out);
                break;
            case ArrayType::ALIGNED:
                detail::writeAligned(m_rawArray, m_arrayTraits, out, m_offsetChecker);
                break;
            case ArrayType::AUTO:
                out.writeVarSize(convertSizeToUInt32(getRawArray().size()));
                detail::write(m_rawArray, m_arrayTraits, out);
                break;
            case ArrayType::ALIGNED_AUTO:
                out.writeVarSize(convertSizeToUInt32(getRawArray().size()));
                detail::writeAligned(m_rawArray, m_arrayTraits, out, m_offsetChecker);
                break;
        }
    }

    /**
    * Initializes array elements using the given element initializer.
    *
    * \param elementInitializer Initializer which knows how to initialize a single array element.
    */
    template <typename ELEMENT_INITIALIZER>
    void initializeElements(const ELEMENT_INITIALIZER& elementInitializer)
    {
        size_t index = 0;
        for (auto&& element : m_rawArray)
        {
            elementInitializer.initialize(element, index);
            index++;
        }
    }

private:
    RawArray m_rawArray;
    ARRAY_TRAITS m_arrayTraits;
    OFFSET_INITIALIZER m_offsetInitializer;
    OFFSET_CHECKER m_offsetChecker;
};

} // namespace zserio

#endif // ZSERIO_ARRAY_H_INC
