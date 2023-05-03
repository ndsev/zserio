#ifndef ZSERIO_ARRAY_H_INC
#define ZSERIO_ARRAY_H_INC

#include <cstdlib>
#include <type_traits>
#include <vector>
#include <string>

#include "zserio/AllocatorPropagatingCopy.h"
#include "zserio/ArrayTraits.h"
#include "zserio/Traits.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/VarSizeUtil.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/Enums.h"
#include "zserio/UniquePtr.h"
#include "zserio/PackingContext.h"

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

// helper function to hide noncompilable code in a "dead" branch
template <typename ARRAY_TRAITS, typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
size_t arrayTraitsConstBitSizeOf(const ARRAY_TRAITS& arrayTraits)
{
    return arrayTraits.bitSizeOf();
}

template <typename ARRAY_TRAITS, typename std::enable_if<!ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
size_t arrayTraitsConstBitSizeOf(const ARRAY_TRAITS&)
{
    return 0; // never comes here, specialization needed only for proper compilation
}

// helper function to call read on array traits which need element factory (ObjectArrayTraits)
template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
void arrayTraitsRead(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
        const ELEMENT_FACTORY& elementFactory, BitStreamReader& in, size_t index)
{
    arrayTraits.read(elementFactory, rawArray, in, index);
}

// helper traits to check if T has allocator_type typedef
template <typename T, typename = void>
struct is_allocator_based : std::false_type
{};

// specialization for types which have allocator_type typdef
template <typename T>
struct is_allocator_based<T, void_t<typename T::allocator_type>> : std::true_type
{};

// overload for DummyElementFactory which is used for array traits which don't need element factory
template <typename RAW_ARRAY, typename ARRAY_TRAITS,
        typename std::enable_if<!is_allocator_based<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsRead(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, const detail::DummyElementFactory&,
        BitStreamReader& in, size_t)
{
    rawArray.push_back(arrayTraits.read(in));
}

// overload for array traits which are based on allocator
template <typename RAW_ARRAY, typename ARRAY_TRAITS,
        typename std::enable_if<is_allocator_based<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsRead(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
        const detail::DummyElementFactory&, BitStreamReader& in, size_t)
{
    rawArray.push_back(arrayTraits.read(in, rawArray.get_allocator()));
}

// helper function to call read on packed array traits which need element factory (i.e. objects)
template <typename RAW_ARRAY, typename PACKED_ARRAY_TRAITS, typename ELEMENT_FACTORY,
        typename PACKED_CONTEXT_NODE>
void packedArrayTraitsRead(RAW_ARRAY& rawArray, const PACKED_ARRAY_TRAITS& packedArrayTraits,
        const ELEMENT_FACTORY& elementFactory, PACKED_CONTEXT_NODE& contextNode,
        BitStreamReader& in, size_t index)
{
    packedArrayTraits.read(contextNode, elementFactory, rawArray, in, index);
}

// overload for DummyElementFactory which is used for array traits which don't need element factory
template <typename RAW_ARRAY, typename PACKED_ARRAY_TRAITS, typename PACKED_CONTEXT_NODE>
void packedArrayTraitsRead(RAW_ARRAY& rawArray, const PACKED_ARRAY_TRAITS& packedArrayTraits,
        const detail::DummyElementFactory&, PACKED_CONTEXT_NODE& contextNode, BitStreamReader& in, size_t)
{
    rawArray.push_back(packedArrayTraits.read(contextNode, in));
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

    /** Typedef for array traits. */
    using ArrayTraits = ARRAY_TRAITS;

    /** Typedef for allocator type. */
    using allocator_type = typename RawArray::allocator_type;

    /**
     * Empty constructor.
     *
     * \param arrayTraits Array traits.
     * \param allocator Allocator to use for the raw array.
     */
    explicit Array(const ARRAY_TRAITS& arrayTraits, const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator), m_arrayTraits(arrayTraits), m_packedArrayTraits(m_arrayTraits)
    {}

    /**
     * Constructor from l-value raw array.
     *
     * \param rawArray Raw array.
     * \param arrayTraits Array traits.
     */
    Array(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits) :
            m_rawArray(rawArray), m_arrayTraits(arrayTraits), m_packedArrayTraits(m_arrayTraits)
    {}

    /**
     * Constructor from r-value raw array.
     *
     * \param rawArray Raw array.
     * \param arrayTraits Array traits.
     */
    Array(RAW_ARRAY&& rawArray, const ARRAY_TRAITS& arrayTraits) :
            m_rawArray(std::move(rawArray)), m_arrayTraits(arrayTraits), m_packedArrayTraits(m_arrayTraits)
    {}

    /**
     * Copy constructor.
     *
     * \param other Source array to copy.
     */
    Array(const Array& other)
    :       m_rawArray(other.m_rawArray), m_arrayTraits(other.m_arrayTraits),
            m_packedArrayTraits(other.m_packedArrayTraits)
    {
        if (other.m_packingContextNode)
            createContext();
    }

    /**
     * Copy assignment operator.
     *
     * \param other Source array to copy.
     *
     * \return Reference to this array.
     */
    Array& operator=(const Array& other)
    {
        m_rawArray = other.m_rawArray;
        m_arrayTraits = other.m_arrayTraits;
        m_packedArrayTraits = other.m_packedArrayTraits;

        if (other.m_packingContextNode)
            createContext();

        return *this;
    }

    /**
     * Method generated by default.
     * \{
     */
    ~Array() = default;

    Array(Array&& other) = default;
    Array& operator=(Array&& other) = default;
    /**
     * \}
     */

    /**
     * Copy constructor which forces allocator propagating while copying the raw array.
     *
     * \param other Source array to copy.
     * \param allocator Allocator to propagate during copying.
     */
    Array(::zserio::PropagateAllocatorT,
            const Array& other, const allocator_type& allocator) :
            m_rawArray(::zserio::allocatorPropagatingCopy(other.m_rawArray, allocator)),
            m_arrayTraits(other.m_arrayTraits), m_packedArrayTraits(other.m_packedArrayTraits)
    {
        if (other.m_packingContextNode)
            createContext();
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

    /**
     * Sets new array traits instance. Needed e.g. when an object which holds the array is copied / moved.
     *
     * \param arrayTraits New instance of array traits.
     */
    void initializeArrayTraits(const ARRAY_TRAITS& arrayTraits)
    {
        m_arrayTraits = arrayTraits;
        m_packedArrayTraits = PackedArrayTraits<ARRAY_TRAITS>(arrayTraits);
    }

    /**
    * Calculates bit size of this array.
    *
    * \param bitPosition Current bit position.
    *
    * \return Bit size of the array.
    */
    size_t bitSizeOf(size_t bitPosition) const
    {
        size_t endBitPosition = bitPosition;

        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && size > 0)
        {
            const size_t elementBitSize = detail::arrayTraitsConstBitSizeOf(m_arrayTraits);
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
                endBitPosition += m_arrayTraits.bitSizeOf(endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition - bitPosition;
    }

    /**
     * Returns length of the packed array stored in the bit stream in bits.
     *
     * \param bitPosition Current bit stream position.
     *
     * \return Length of the array stored in the bit stream in bits.
     */
    size_t bitSizeOfPacked(size_t bitPosition) const
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        if (size > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < size; ++index)
                m_packedArrayTraits.initContext(contextNode, m_rawArray[index]);

            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                    endBitPosition = alignTo(8, endBitPosition);
                endBitPosition += m_packedArrayTraits.bitSizeOf(contextNode, endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition - bitPosition;
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
                detail::initializeOffset(offsetInitializer, index, endBitPosition / 8);
            }
            endBitPosition = m_arrayTraits.initializeOffsets(endBitPosition, m_rawArray[index]);
        }

        return endBitPosition;
    }

    /**
     * Initializes indexed offsets for the packed array.
     *
     * \param bitPosition Current bit stream position.
     * \param offsetInitializer Initializer which initializes offsets for each element.
     *
     * \return Updated bit stream position which points to the first bit after the array.
     */
    size_t initializeOffsetsPacked(size_t bitPosition, const OFFSET_INITIALIZER& offsetInitializer)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        if (size > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < size; ++index)
                m_packedArrayTraits.initContext(contextNode, m_rawArray[index]);

            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                {
                    endBitPosition = alignTo(8, endBitPosition);
                    detail::initializeOffset(offsetInitializer, index, endBitPosition / 8);
                }
                endBitPosition = m_packedArrayTraits.initializeOffsets(
                        contextNode, endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition;
    }

    /**
     * Reads the array from the bit stream.
     *
     * This method has all possible arguments and from generated code is used for aligned object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Empty for auto / implicit arrays.
     * \param elementFactory Factory which knows how to create a single array element.
     * \param offsetChecker Offset checker.
     */
    void read(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT || ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT,
                "Implicit array elements must have constant bit size!");

        size_t readSize = arrayLength;
        if (ARRAY_TYPE == ArrayType::IMPLICIT)
        {
            const size_t remainingBits = in.getBufferBitSize() - in.getBitPosition();
            readSize = remainingBits / detail::arrayTraitsConstBitSizeOf(m_arrayTraits);
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
                detail::checkOffset(offsetChecker, index, in.getBitPosition() / 8);
            }
            detail::arrayTraitsRead(m_rawArray, m_arrayTraits, elementFactory, in, index);
        }
    }

    /**
     * Reads packed array from the bit stream.
     *
     * This method has all possible arguments and from generated code is used for aligned object arrays.
     *
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     * \param elementFactory Factory which knows how to create a single array element.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void readPacked(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t readSize = arrayLength;
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            readSize = in.readVarSize();

        m_rawArray.clear();

        if (readSize > 0)
        {
            m_rawArray.reserve(readSize);

            auto& contextNode = getPackingContextNode();

            for (size_t index = 0; index < readSize; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                {
                    in.alignTo(8);
                    detail::checkOffset(offsetChecker, index, in.getBitPosition() / 8);
                }
                detail::packedArrayTraitsRead(m_rawArray, m_packedArrayTraits, elementFactory,
                        contextNode, in, index);
            }
        }
    }

    /**
     * Writes the array to the bit stream.
     *
     * \param out Bit stream write to use for writing.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void write(BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker) const
    {
        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            out.writeVarSize(convertSizeToUInt32(size));

        for (size_t index = 0; index < size; ++index)
        {
            if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            {
                out.alignTo(8);
                detail::checkOffset(offsetChecker, index, out.getBitPosition() / 8);
            }
            m_arrayTraits.write(out, m_rawArray[index]);
        }
    }

    /**
     * Writes packed array to the bit stream.
     *
     * \param out Bit stream where to write.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void writePacked(BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker) const
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            out.writeVarSize(convertSizeToUInt32(size));

        if (size > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < size; ++index)
                m_packedArrayTraits.initContext(contextNode, m_rawArray[index]);

            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                {
                    out.alignTo(8);
                    detail::checkOffset(offsetChecker, index, out.getBitPosition() / 8);
                }
                m_packedArrayTraits.write(contextNode, out, m_rawArray[index]);
            }
        }
    }

    // public methods overloads follow

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
     * Initializes indexed offsets for the packed array.
     *
     * Overloaded method used for unaligned arrays.
     *
     * \param bitPosition Current bit stream position.
     *
     * \return Updated bit stream position which points to the first bit after the array.
     */
    size_t initializeOffsetsPacked(size_t bitPosition)
    {
        return initializeOffsetsPacked(bitPosition, detail::DummyOffsetInitializer());
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for unaligned auto / implicit non-object arrays.
     *
     * \param in Bit stream reader to use for reading.
     */
    void read(BitStreamReader& in)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO ||
                ARRAY_TYPE == ArrayType::IMPLICIT, "Allowed only for auto / implicit arrays!");
        read(in, 0, detail::DummyElementFactory(), detail::DummyOffsetChecker());
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for aligned auto / implicit non-object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param offsetChecker Offset checker.
     */
    void read(BitStreamReader& in, const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO ||
                ARRAY_TYPE == ArrayType::IMPLICIT, "Allowed only for auto / implicit arrays!");
        read(in, 0, detail::DummyElementFactory(), offsetChecker);
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for unaligned auto object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param elementFactory Factory which knows how to create a single array element.
     */
    void read(BitStreamReader& in, const detail::ElementFactory<ARRAY_TRAITS>& elementFactory)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO ||
                ARRAY_TYPE == ArrayType::IMPLICIT, "Allowed only for auto / implicit arrays!");
        read(in, 0, elementFactory, detail::DummyOffsetChecker());
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for aligned auto object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param elementFactory Factory which knows how to create a single array element.
     * \param offsetChecker Offset checker.
     */
    void read(BitStreamReader& in, const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO ||
                ARRAY_TYPE == ArrayType::IMPLICIT, "Allowed only for auto / implicit arrays!");
        read(in, 0, elementFactory, offsetChecker);
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for unaligned non-object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Empty for auto / implicit arrays.
     */
    void read(BitStreamReader& in, size_t arrayLength)
    {
        read(in, arrayLength, detail::DummyElementFactory(), detail::DummyOffsetChecker());
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for aligned non-object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Empty for auto / implicit arrays.
     * \param offsetChecker Offset checker.
     */
    void read(BitStreamReader& in, size_t arrayLength, const OFFSET_CHECKER& offsetChecker)
    {
        read(in, arrayLength, detail::DummyElementFactory(), offsetChecker);
    }

    /**
     * Reads the array from the bit stream.
     *
     * Overloaded method used for unaligned object arrays.
     *
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Empty for auto / implicit arrays.
     * \param elementFactory Factory which knows how to create a single array element.
     */
    void read(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory)
    {
        read(in, arrayLength, elementFactory, detail::DummyOffsetChecker());
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for unaligned auto non-object arrays.
     *
     * \param in Bit stream from which to read.
     */
    void readPacked(BitStreamReader& in)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, detail::DummyElementFactory(), detail::DummyOffsetChecker());
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for aligned auto non-object arrays.
     *
     * \param in Bit stream from which to read.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void readPacked(BitStreamReader& in, const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, detail::DummyElementFactory(), offsetChecker);
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for unaligned auto object arrays.
     *
     * \param in Bit stream from which to read.
     * \param elementFactory Factory which knows how to create a single array element.
     */
    void readPacked(BitStreamReader& in, const detail::ElementFactory<ARRAY_TRAITS>& elementFactory)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, elementFactory, detail::DummyOffsetChecker());
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for aligned auto object arrays.
     *
     * \param in Bit stream from which to read.
     * \param elementFactory Factory which knows how to create a single array element.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void readPacked(BitStreamReader& in, const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, elementFactory, offsetChecker);
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for unaligned non-object arrays.
     *
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     */
    void readPacked(BitStreamReader& in, size_t arrayLength)
    {
        readPacked(in, arrayLength, detail::DummyElementFactory(), detail::DummyOffsetChecker());
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for aligned non-object arrays.
     *
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     * \param offsetChecker Offset checker used to check offsets before writing.
     */
    void readPacked(BitStreamReader& in, size_t arrayLength, const OFFSET_CHECKER& offsetChecker)
    {
        readPacked(in, arrayLength, detail::DummyElementFactory(), offsetChecker);
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Overloaded method used for unaligned object arrays.
     *
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     * \param elementFactory Factory which knows how to create a single array element.
     */
    void readPacked(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory)
    {
        readPacked(in, arrayLength, elementFactory, detail::DummyOffsetChecker());
    }

    /**
     * Writes the array to the bit stream.
     *
     * Overloaded method used for unaligned arrays.
     *
     * \param out Bit stream write to use for writing.
     */
    void write(BitStreamWriter& out) const
    {
        write(out, detail::DummyOffsetChecker());
    }

    /**
     * Writes packed array to the bit stream.
     *
     * Overloaded method used for unaligned arrays.
     *
     * \param out Bit stream where to write.
     */
    void writePacked(BitStreamWriter& out) const
    {
        writePacked(out, detail::DummyOffsetChecker());
    }

private:
    // RebindAlloc is used here to prevent multiple instantiations of the PackingContextNode template
    using PackingContextNodeType = BasicPackingContextNode<RebindAlloc<allocator_type, uint8_t>>;

    PackingContextNodeType& getPackingContextNode() const
    {
        if (!m_packingContextNode) // lazy init
            createContext();
        else
            resetContext(*m_packingContextNode);

        return *m_packingContextNode;
    }

    void createContext() const
    {
        m_packingContextNode = allocate_unique<PackingContextNodeType>(
                m_rawArray.get_allocator(), m_rawArray.get_allocator());
        m_packedArrayTraits.createContext(*m_packingContextNode);
    }

    static void resetContext(PackingContextNodeType& contextNode)
    {
        if (contextNode.hasContext())
        {
            contextNode.getContext().reset();
        }
        else
        {
            for (auto& childNode : contextNode.getChildren())
                resetContext(childNode);
        }
    }

    RawArray m_rawArray;
    ARRAY_TRAITS m_arrayTraits;
    PackedArrayTraits<ARRAY_TRAITS> m_packedArrayTraits;
    // mutable context is ok since it's just a cache and we need to keep bitSizeOfPacked method const
    mutable unique_ptr<PackingContextNodeType,
            RebindAlloc<allocator_type, PackingContextNodeType>> m_packingContextNode;
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
