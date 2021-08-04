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
#include "zserio/OptionalHolder.h"

namespace zserio
{

namespace detail
{

// dummy element factory used for arrays which traits don't need element factory
struct DummyElementFactory
{};

// helper traits to choose proper element factory
template <typename ARRAY_TRAITS>
struct ElementFactoryTraits
{
    using type = DummyElementFactory;
};

// specialization for object array traits which has info about the real element factory
template <typename T, typename ELEMENT_FACTORY>
struct ElementFactoryTraits<ObjectArrayTraits<T, ELEMENT_FACTORY>>
{
    using type = ELEMENT_FACTORY;
};

// using typedef to simplify accessing the element factory type
template <typename ARRAY_TRAITS>
using ElementFactory = typename ElementFactoryTraits<ARRAY_TRAITS>::type;

// helper function to call read on an array traits which needs an element factory
template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY, typename RAW_ARRAY>
void arrayTraitsRead(const ARRAY_TRAITS& arrayTraits, const ELEMENT_FACTORY& elementFactory,
        RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    arrayTraits.read(elementFactory, rawArray, in, index);
}

// overload for DummyElementFactory which is used for array traits which doesn't need element factory
template <typename ARRAY_TRAITS, typename RAW_ARRAY>
void arrayTraitsRead(const ARRAY_TRAITS& arrayTraits, const detail::DummyElementFactory&,
        RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    arrayTraits.read(rawArray, in, index);
}

// dummy offset initializer used for arrays which don't need to initialize offsets
struct DummyOffsetInitializer
{};

// helper function to call initializeOffset on an offset initializer
template <typename OFFSET_INITIALIZER>
void initializeOffset(const OFFSET_INITIALIZER& offsetInitializer, size_t index, size_t byteOffset)
{
    offsetInitializer.initializeOffset(index, byteOffset);
}

// overload for DummyOffsetInitializer which does nothing
inline void initializeOffset(const DummyOffsetInitializer&, size_t, size_t)
{}

// dummy offset checker used for arrays which don't need to check offsets.
struct DummyOffsetChecker
{};

// helper function to call checkOffset on an offset checker
template <typename OFFSET_CHECKER>
void checkOffset(const OFFSET_CHECKER& offsetChecker, size_t index, size_t byteOffset)
{
    offsetChecker.checkOffset(index, byteOffset);
}

// overload for DummyOffsetChecker which does nothing
inline void checkOffset(const DummyOffsetChecker&, size_t, size_t)
{}

} // namespace detail

/**
 * Array type enum which defined type of the underlying array.
 */
enum ArrayType
{
    NORMAL, /**< Normal zserio array which has size defined by the Zserio schema. */
    IMPLICIT, /**< Implicit zserio array which size is defined by number of remaining bits in the bit stream. */
    ALIGNED, /**< Aligned zserio array which is normal zserio array with indexed offsets. */
    AUTO, /**< Auto zserio array which has size stored in a hidden field before the array. */
    ALIGNED_AUTO /**< Aligned auto zserio array which is auto zserio array with indexed offsets. */
};

/**
 * Array wrapper for zserio arrays.
 *
 * The wrapper is used to encapsulate logic of operations with zserio arrays.
 */
template <typename RAW_ARRAY, typename ARRAY_TRAITS, ArrayType ARRAY_TYPE,
        typename OFFSET_CHECKER = detail::DummyOffsetChecker,
        typename OFFSET_INITIALIZER = detail::DummyOffsetInitializer>
class Array
{
public:
    /** Typedef for raw array type. */
    using RawArray = RAW_ARRAY;

    /** Typedef for allocator type. */
    using allocator_type = typename RawArray::allocator_type;

    /**
     * Empty constructor.
     *
     * \param arrayTraits Array traits.
     * \param allocator Allocator to use for the raw array.
     */
    explicit Array(const ARRAY_TRAITS& arrayTraits, const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator), m_arrayTraits(arrayTraits)
    {}

    /**
     * Constructor from l-value raw array.
     *
     * \param rawArray Raw array.
     * \param arrayTraits Array traits.
     */
    Array(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits) :
            m_rawArray(rawArray), m_arrayTraits(arrayTraits)
    {}

    /**
     * Constructor from r-value raw array.
     *
     * \param rawArray Raw array.
     * \param arrayTraits Array traits.
     */
    Array(RAW_ARRAY&& rawArray, const ARRAY_TRAITS& arrayTraits) :
            m_rawArray(std::move(rawArray)), m_arrayTraits(arrayTraits)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for unaligned auto / implicit non-object arrays.
     *
     * \param arrayTraits Array traits.
     * \param in Bit stream reader to use for reading.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
            const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, 0, detail::DummyElementFactory(), detail::DummyOffsetChecker(), allocator)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for aligned auto / implicit non-object arrays.
     *
     * \param arrayTraits Array traits.
     * \param offsetChecker Offset checker.
     * \param in Bit stream reader to use for reading.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
            const OFFSET_CHECKER& offsetChecker, const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, 0, detail::DummyElementFactory(), offsetChecker, allocator)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for unaligned auto / implicit object arrays.
     *
     * \param arrayTraits Array traits.
     * \param elementFactory Element factory.
     * \param in Bit stream reader to use for reading.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, 0, elementFactory, detail::DummyOffsetChecker(), allocator)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for aligned auto / implicit object arrays.
     *
     * \param arrayTraits Array traits.
     * \param elementFactory Element factory.
     * \param offsetChecker Offset checker.
     * \param in Bit stream reader to use for reading.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker, const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, 0, elementFactory, offsetChecker, allocator)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for unaligned non-object arrays.
     *
     * \param arrayTraits Array traits.
     * \param in Bit stream reader to use for reading.
     * \param arrayLengthArg Array length. Empty for auto / implicit arrays.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arrayLengthArg,
            const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, arrayLengthArg,
                    detail::DummyElementFactory(), detail::DummyOffsetChecker(), allocator)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for aligned non-object arrays.
     *
     * \param arrayTraits Array traits.
     * \param offsetChecker Offset checker.
     * \param in Bit stream reader to use for reading.
     * \param arrayLengthArg Array length. Empty for auto / implicit arrays.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arrayLengthArg,
            const OFFSET_CHECKER& offsetChecker, const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, arrayLengthArg, detail::DummyElementFactory(), offsetChecker,
                    allocator)
    {}

    /**
     * Read constructor overload.
     *
     * This constructor is used for unaligned object arrays.
     *
     * \param arrayTraits Array traits.
     * \param elementFactory Element factory.
     * \param in Bit stream reader to use for reading.
     * \param arrayLengthArg Array length. Empty for auto / implicit arrays.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arrayLengthArg,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const allocator_type& allocator = allocator_type()) :
            Array(arrayTraits, in, arrayLengthArg, elementFactory, detail::DummyOffsetChecker(), allocator)
    {}

    /**
     * Read constructor.
     *
     * This constructor has all possible arguments and implements the reading logic.
     * The constructor itself is used from all other overloads.
     *
     * From generated code the constructor is used for aligned object arrays.
     *
     * \param arrayTraits Array traits.
     * \param elementFactory Element factory.
     * \param offsetChecker Offset checker.
     * \param in Bit stream reader to use for reading.
     * \param arrayLengthArg Array length. Empty for auto / implicit arrays.
     * \param allocator Allocator to use for raw array.
     */
    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arrayLengthArg,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker, const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator), m_arrayTraits(arrayTraits)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT || ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT,
                "Implicit array elements must have constant bit size!");

        size_t readSize = arrayLengthArg;
        if (ARRAY_TYPE == ArrayType::IMPLICIT)
        {
            const size_t remainingBits = in.getBufferBitSize() - in.getBitPosition();
            readSize = remainingBits / arrayTraits.bitSizeOf(m_rawArray, 0, 0);
        }
        else if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
        {
            readSize = in.readVarSize();
        }

        m_rawArray.clear();
        m_rawArray.reserve(readSize);
        for (size_t index = 0; index < readSize; ++index)
        {
            if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            {
                in.alignTo(8);
                detail::checkOffset(offsetChecker, index, bitsToBytes(in.getBitPosition()));
            }
            detail::arrayTraitsRead(arrayTraits, elementFactory, m_rawArray, in, index);
        }
    }

    /**
     * Method generated by default.
     * \{
     */
    ~Array() = default;

    Array(const Array& other) = default;
    Array& operator=(const Array& other) = default;

    Array(Array&& other) = default;
    Array& operator=(Array&& other) = default;
    /**
     * \}
     */

    /**
     * Copy constructor which forces allocator propagating while copying the raw array.
     */
    Array(::zserio::PropagateAllocatorT,
            const Array& other, const allocator_type& allocator) :
            m_rawArray(::zserio::allocatorPropagatingCopy(other.m_rawArray, allocator)),
            m_arrayTraits(other.m_arrayTraits)
    {}

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

    /**
     * Operator equality.
     *
     * \return True when the underlying raw arrays have same contents, false otherwise.
     */
    bool operator==(const Array& other) const
    {
        return m_rawArray == other.m_rawArray;
    }

    /**
     * Hash code.
     *
     * \return Hash code calculated on the underlying raw array.
     */
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
        size_t endBitPosition = bitPosition;
        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && size > 0)
        {
            const size_t elementBitSize = m_arrayTraits.bitSizeOf(m_rawArray, bitPosition, 0);
            if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            {
                endBitPosition = alignTo(8, endBitPosition);
                endBitPosition += elementBitSize + (size - 1) * alignTo(8, elementBitSize);
            }
            else
            {
                endBitPosition += size * elementBitSize;
            }
        }
        else
        {
            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                    endBitPosition = alignTo(8, endBitPosition);
                endBitPosition += m_arrayTraits.bitSizeOf(m_rawArray, endBitPosition, index);
            }
        }

        return endBitPosition - bitPosition;
    }

    /**
     * Initializes indexed offsets.
     *
     * Overloaded method used for unaligned arrays.
     *
     * \return Updated bit position which points to the first bit after the array.
     */
    size_t initializeOffsets(size_t bitPosition)
    {
        return initializeOffsets(bitPosition, detail::DummyOffsetInitializer());
    }

    /**
     * Initializes indexed offsets.
     *
     * \param bitPosition Current bit position.
     * \param offsetInitializer Initializer which initializes offsets for each element.
     *
     * \return Updated bit position which points to the first bit after the array.
     */
    size_t initializeOffsets(size_t bitPosition, const OFFSET_INITIALIZER& offsetInitializer)
    {
        size_t endBitPosition = bitPosition;
        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        for (size_t index = 0; index < size; ++index)
        {
            if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            {
                endBitPosition = alignTo(8, endBitPosition);
                detail::initializeOffset(offsetInitializer, index, bitsToBytes(endBitPosition));
            }
            endBitPosition = m_arrayTraits.initializeOffsets(m_rawArray, endBitPosition, index);
        }

        return endBitPosition;
    }

    /**
     * Writes the array to the bit stream.
     *
     * Overloaded method used for unaligned arrays.
     *
     * \param out Bit stream write to use for writing.
     */
    void write(BitStreamWriter& out)
    {
        write(out, detail::DummyOffsetChecker());
    }

    /**
     * Writes the array to the bit stream.
     *
     * \param out Bit stream write to use for writing.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void write(BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
    {
        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            out.writeVarSize(convertSizeToUInt32(size));

        for (size_t index = 0; index < size; ++index)
        {
            if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            {
                out.alignTo(8);
                detail::checkOffset(offsetChecker, index, bitsToBytes(out.getBitPosition()));
            }
            m_arrayTraits.write(m_rawArray, out, index);
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
};

/**
 * Helper for creating an optional array within templated field constructor, where the raw array can be
 * actually the NullOpt.
 */
template <typename ARRAY, typename RAW_ARRAY, typename ARRAY_TRAITS>
ARRAY createOptionalArray(RAW_ARRAY&& rawArray, const ARRAY_TRAITS& arrayTraits)
{
    return ARRAY(std::forward<RAW_ARRAY>(rawArray), arrayTraits);
}

/**
 * Overload for NullOpt.
 */
template <typename ARRAY, typename ARRAY_TRAITS>
NullOptType createOptionalArray(NullOptType, const ARRAY_TRAITS&)
{
    return NullOpt;
}

} // namespace zserio

#endif // ZSERIO_ARRAY_H_INC
