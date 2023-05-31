#ifndef ZSERIO_ARRAY_H_INC
#define ZSERIO_ARRAY_H_INC

#include <cstdlib>
#include <type_traits>

#include "zserio/AllocatorPropagatingCopy.h"
#include "zserio/ArrayTraits.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/PackingContext.h"
#include "zserio/Traits.h"
#include "zserio/UniquePtr.h"
#include "zserio/SizeConvertUtil.h"

namespace zserio
{

namespace detail
{

// array expressions for arrays which do not need expressions
struct DummyArrayExpressions
{};

// array owner for arrays which do not need the owner
struct DummyArrayOwner
{};

// helper trait to choose the owner type for an array from combination of ARRAY_TRAITS and ARRAY_EXPRESSIONS
template <typename ARRAY_TRAITS, typename ARRAY_EXPRESSIONS, typename = void>
struct array_owner_type
{
    using type = DummyArrayOwner;
};

template <typename ARRAY_TRAITS, typename ARRAY_EXPRESSIONS>
struct array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value>::type>
{
    using type = typename ARRAY_TRAITS::OwnerType;
};

template <typename ARRAY_TRAITS, typename ARRAY_EXPRESSIONS>
struct array_owner_type<ARRAY_TRAITS, ARRAY_EXPRESSIONS,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                has_owner_type<ARRAY_EXPRESSIONS>::value>::type>
{
    using type = typename ARRAY_EXPRESSIONS::OwnerType;
};

// calls the initializeOffset static method on ARRAY_EXPRESSIONS if available
template <typename ARRAY_EXPRESSIONS, typename OWNER_TYPE,
        typename std::enable_if<has_initialize_offset<ARRAY_EXPRESSIONS>::value, int>::type = 0>
void initializeOffset(OWNER_TYPE& owner, size_t index, size_t bitPosition)
{
    ARRAY_EXPRESSIONS::initializeOffset(owner, index, bitPosition);
}

template <typename ARRAY_EXPRESSIONS, typename OWNER_TYPE,
        typename std::enable_if<!has_initialize_offset<ARRAY_EXPRESSIONS>::value, int>::type = 0>
void initializeOffset(OWNER_TYPE&, size_t, size_t)
{}

// calls the checkOffset static method on ARRAY_EXPRESSIONS if available
template <typename ARRAY_EXPRESSIONS, typename OWNER_TYPE,
        typename std::enable_if<has_check_offset<ARRAY_EXPRESSIONS>::value, int>::type = 0>
void checkOffset(const OWNER_TYPE& owner, size_t index, size_t bitPosition)
{
    ARRAY_EXPRESSIONS::checkOffset(owner, index, bitPosition);
}

template <typename ARRAY_EXPRESSIONS, typename OWNER_TYPE,
        typename std::enable_if<!has_check_offset<ARRAY_EXPRESSIONS>::value, int>::type = 0>
void checkOffset(const OWNER_TYPE&, size_t, size_t)
{}

// call the initContext method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsInitContext(const OWNER_TYPE& owner, PACKING_CONTEXT_NODE& contextNode,
        typename PACKED_ARRAY_TRAITS::ElementType element)
{
    PACKED_ARRAY_TRAITS::initContext(owner, contextNode, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsInitContext(const OWNER_TYPE&, PACKING_CONTEXT_NODE& contextNode,
        typename PACKED_ARRAY_TRAITS::ElementType element)
{
    PACKED_ARRAY_TRAITS::initContext(contextNode, element);
}

// calls the bitSizeOf method properly on array traits which have constant bit size
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<
                ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsConstBitSizeOf(const OWNER_TYPE& owner)
{
    return ARRAY_TRAITS::bitSizeOf(owner);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<
                ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && !has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsConstBitSizeOf(const OWNER_TYPE&)
{
    return ARRAY_TRAITS::bitSizeOf();
}

// calls the bitSizeOf method properly on array traits which haven't constant bit size
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsBitSizeOf(const OWNER_TYPE& owner, size_t bitPosition,
        const typename ARRAY_TRAITS::ElementType& element)
{
    return ARRAY_TRAITS::bitSizeOf(owner, bitPosition, element);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsBitSizeOf(const OWNER_TYPE&, size_t bitPosition,
        const typename ARRAY_TRAITS::ElementType& element)
{
    return ARRAY_TRAITS::bitSizeOf(bitPosition, element);
}

// calls the bitSizeOf method properly on packed array traits which haven't constant bit size
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsBitSizeOf(const OWNER_TYPE& owner, PACKING_CONTEXT_NODE& contextNode,
        size_t bitPosition, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::bitSizeOf(owner, contextNode, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsBitSizeOf(const OWNER_TYPE&, PACKING_CONTEXT_NODE& contextNode, size_t bitPosition,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::bitSizeOf(contextNode, bitPosition, element);
}

// calls the initializeOffsets method properly on array traits
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsInitializeOffsets(OWNER_TYPE& owner, size_t bitPosition,
        typename ARRAY_TRAITS::ElementType& element)
{
    return ARRAY_TRAITS::initializeOffsets(owner, bitPosition, element);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                !std::is_scalar<typename ARRAY_TRAITS::ElementType>::value, int>::type = 0>
size_t arrayTraitsInitializeOffsets(OWNER_TYPE&, size_t bitPosition,
        const typename ARRAY_TRAITS::ElementType& element)
{
    return ARRAY_TRAITS::initializeOffsets(bitPosition, element);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                std::is_scalar<typename ARRAY_TRAITS::ElementType>::value, int>::type = 0>
size_t arrayTraitsInitializeOffsets(OWNER_TYPE&, size_t bitPosition, typename ARRAY_TRAITS::ElementType element)
{
    return ARRAY_TRAITS::initializeOffsets(bitPosition, element);
}

// calls the initializeOffsets method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE& owner, PACKING_CONTEXT_NODE& contextNode,
        size_t bitPosition, typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(owner, contextNode, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                !std::is_scalar<typename PACKED_ARRAY_TRAITS::ElementType>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE&, PACKING_CONTEXT_NODE& contextNode,
        size_t bitPosition, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(contextNode, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                std::is_scalar<typename PACKED_ARRAY_TRAITS::ElementType>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE&, PACKING_CONTEXT_NODE& contextNode,
        size_t bitPosition, typename PACKED_ARRAY_TRAITS::ElementType element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(contextNode, bitPosition, element);
}

// calls the read method properly on array traits
template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value &&
                !has_allocator<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsRead(const OWNER_TYPE& owner, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    return rawArray.push_back(ARRAY_TRAITS::read(owner, in, index));
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value &&
                has_allocator<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsRead(OWNER_TYPE& owner, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    return rawArray.push_back(ARRAY_TRAITS::read(owner, in, rawArray.get_allocator(), index));
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                !has_allocator<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsRead(const OWNER_TYPE&, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    return rawArray.push_back(ARRAY_TRAITS::read(in, index));
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                has_allocator<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsRead(const OWNER_TYPE&, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    return rawArray.push_back(ARRAY_TRAITS::read(in, rawArray.get_allocator(), index));
}

// calls the read method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                has_allocator<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsRead(OWNER_TYPE& owner, RAW_ARRAY& rawArray, PACKING_CONTEXT_NODE& contextNode,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(owner, contextNode, in, rawArray.get_allocator(), index));
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                !has_allocator<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsRead(const OWNER_TYPE& owner, RAW_ARRAY& rawArray, PACKING_CONTEXT_NODE& contextNode,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(owner, contextNode, in, index));
}

// note: types which doesn't have owner and have allocator are never packed (e.g. string, bytes ...)
//       and thus such specialization is not needed

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                !has_allocator<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsRead(const OWNER_TYPE&, RAW_ARRAY& rawArray, PACKING_CONTEXT_NODE& contextNode,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(contextNode, in, index));
}

// call the write method properly on array traits
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsWrite(const OWNER_TYPE& owner,
        BitStreamWriter& out, const typename ARRAY_TRAITS::ElementType& element)
{
    return ARRAY_TRAITS::write(owner, out, element);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsWrite(const OWNER_TYPE&,
        BitStreamWriter& out, const typename ARRAY_TRAITS::ElementType& element)
{
    return ARRAY_TRAITS::write(out, element);
}

// call the write method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsWrite(const OWNER_TYPE& owner, PACKING_CONTEXT_NODE& contextNode,
        BitStreamWriter& out, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::write(owner, contextNode, out, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT_NODE,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsWrite(const OWNER_TYPE&, PACKING_CONTEXT_NODE& contextNode,
        BitStreamWriter& out, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::write(contextNode, out, element);
}

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
        typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions>
class Array
{
public:
    /** Typedef for raw array type. */
    using RawArray = RAW_ARRAY;

    /** Typedef for array traits. */
    using ArrayTraits = ARRAY_TRAITS;

    /** Typedef for array expressions. */
    using ArrayExpressions = ARRAY_EXPRESSIONS;

    /**
     * Typedef for the array's owner type.
     *
     * Owner type is needed for proper expressions evaluation. If neither traits nor array need the owner
     * for expressions evaluation, detail::DummyArrayOwner is used and such array do not need the owner at all.
     */
    using OwnerType = typename detail::array_owner_type<ArrayTraits, ArrayExpressions>::type;

    /** Typedef for allocator type. */
    using allocator_type = typename RawArray::allocator_type;

    /**
     * Empty constructor.
     *
     * \param allocator Allocator to use for the raw array.
     */
    explicit Array(const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator)
    {}

    /**
     * Constructor from l-value raw array.
     *
     * \param rawArray Raw array.
     */
    explicit Array(const RawArray& rawArray) :
            m_rawArray(rawArray)
    {}

    /**
     * Constructor from r-value raw array.
     *
     * \param rawArray Raw array.
     */
    explicit Array(RawArray&& rawArray) :
            m_rawArray(std::move(rawArray))
    {}

    /**
     * Default destructor.
     */
    ~Array() = default;

    /**
     * Copy constructor.
     *
     * \param other Source array to copy.
     */
    Array(const Array& other) :
            m_rawArray(other.m_rawArray)
    {
        if (other.m_packingContextNode)
            createContext();
    }

    /**
     * Copy assignment operator.
     *
     * \param other Source array to copy.
     */
    Array& operator=(const Array& other)
    {
        m_rawArray = other.m_rawArray;

        if (other.m_packingContextNode)
            createContext();

        return *this;
    }

    /**
     * Method generated by default.
     *
     * \{
     */
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
    Array(PropagateAllocatorT, const Array& other, const allocator_type& allocator) :
            m_rawArray(allocatorPropagatingCopy(other.m_rawArray, allocator))
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
     * \param other Array to compare.
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
     * Initializes array elements.
     *
     * \param owner Array owner.
     */
    template <typename ARRAY_EXPRESSIONS_ = ArrayExpressions,
            typename std::enable_if<has_initialize_element<ARRAY_EXPRESSIONS_>::value, int>::type = 0>
    void initializeElements(OwnerType& owner)
    {
        size_t index = 0;
        for (auto&& element : m_rawArray)
        {
            ArrayExpressions::initializeElement(owner, element, index);
            index++;
        }
    }

    /**
     * Calculates bit size of this array.
     *
     * Available for arrays which do not need the owner.
     *
     * \param bitPosition Current bit position.
     *
     * \return Bit size of the array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t bitSizeOf(size_t bitPosition) const
    {
        return bitSizeOfImpl(detail::DummyArrayOwner(), bitPosition);
    }

    /**
     * Calculates bit size of this array.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param bitPosition Current bit position.
     *
     * \return Bit size of the array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t bitSizeOf(const OwnerType& owner, size_t bitPosition) const
    {
        return bitSizeOfImpl(owner, bitPosition);
    }

    /**
     * Returns length of the packed array stored in the bit stream in bits.
     *
     * Available for arrays which do not need the owner.
     *
     * \param bitPosition Current bit stream position.
     *
     * \return Length of the array stored in the bit stream in bits.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t bitSizeOfPacked(size_t bitPosition) const
    {
        return bitSizeOfPackedImpl(detail::DummyArrayOwner(), bitPosition);
    }

    /**
     * Returns length of the packed array stored in the bit stream in bits.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param bitPosition Current bit stream position.
     *
     * \return Length of the array stored in the bit stream in bits.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t bitSizeOfPacked(const OwnerType& ownerType, size_t bitPosition) const
    {
        return bitSizeOfPackedImpl(ownerType, bitPosition);
    }

    /**
     * Initializes indexed offsets.
     *
     * Available for arrays which do not need the owner.
     *
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t initializeOffsets(size_t bitPosition)
    {
        detail::DummyArrayOwner owner;
        return initializeOffsetsImpl(owner, bitPosition);
    }

    /**
     * Initializes indexed offsets.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t initializeOffsets(OwnerType& owner, size_t bitPosition)
    {
        return initializeOffsetsImpl(owner, bitPosition);
    }

    /**
     * Initializes indexed offsets for the packed array.
     *
     * Available for arrays which do not need the owner.
     *
     * \param bitPosition Current bit stream position.
     *
     * \return Updated bit stream position which points to the first bit after the array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t initializeOffsetsPacked(size_t bitPosition)
    {
        detail::DummyArrayOwner owner;
        return initializeOffsetsPackedImpl(owner, bitPosition);
    }

    /**
     * Initializes indexed offsets for the packed array.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param bitPosition Current bit stream position.
     *
     * \return Updated bit stream position which points to the first bit after the array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    size_t initializeOffsetsPacked(OwnerType& owner, size_t bitPosition)
    {
        return initializeOffsetsPackedImpl(owner, bitPosition);
    }

    /**
     * Reads the array from the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Not needed for auto / implicit arrays.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void read(BitStreamReader& in, size_t arrayLength = 0)
    {
        detail::DummyArrayOwner owner;
        readImpl(owner, in, arrayLength);
    }

    /**
     * Reads the array from the bit stream.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Not needed for auto / implicit arrays.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void read(OwnerType& owner, BitStreamReader& in, size_t arrayLength = 0)
    {
        readImpl(owner, in, arrayLength);
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void readPacked(BitStreamReader& in, size_t arrayLength = 0)
    {
        detail::DummyArrayOwner owner;
        readPackedImpl(owner, in, arrayLength);
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void readPacked(OwnerType& owner, BitStreamReader& in, size_t arrayLength = 0)
    {
        readPackedImpl(owner, in, arrayLength);
    }

    /**
     * Writes the array to the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param out Bit stream write to use for writing.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void write(BitStreamWriter& out) const
    {
        writeImpl(detail::DummyArrayOwner(), out);
    }

    /**
     * Writes the array to the bit stream.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param out Bit stream write to use for writing.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void write(const OwnerType& owner, BitStreamWriter& out) const
    {
        writeImpl(owner, out);
    }

    /**
     * Writes packed array to the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param out Bit stream where to write.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void writePacked(BitStreamWriter& out) const
    {
        writePackedImpl(detail::DummyArrayOwner(), out);
    }

    /**
     * Writes packed array to the bit stream.
     *
     * Available for arrays which need the owner.
     *
     * \param owner Array owner.
     * \param out Bit stream where to write.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    void writePacked(const OwnerType& owner, BitStreamWriter& out) const
    {
        writePackedImpl(owner, out);
    }

private:
    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::AUTO && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void addBitSizeOfArrayLength(size_t&, size_t)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::AUTO || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void addBitSizeOfArrayLength(size_t& bitPosition, size_t arrayLength)
    {
        bitPosition += bitSizeOfVarSize(convertSizeToUInt32(arrayLength));
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO, int>::type = 0>
    static size_t constBitSizeOfElements(size_t, size_t arrayLength, size_t elementBitSize)
    {
        return arrayLength * elementBitSize;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static size_t constBitSizeOfElements(size_t bitPosition, size_t arrayLength, size_t elementBitSize)
    {
        size_t endBitPosition = alignTo(8, bitPosition);
        endBitPosition += elementBitSize + (arrayLength - 1) * alignTo(8, elementBitSize);

        return endBitPosition - bitPosition;
    }

    template <typename ARRAY_TRAITS_ = ArrayTraits,
            typename std::enable_if<ARRAY_TRAITS_::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    size_t bitSizeOfImpl(const OwnerType& owner, size_t bitPosition) const
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            const size_t elementBitSize = detail::arrayTraitsConstBitSizeOf<ArrayTraits>(owner);
            endBitPosition += constBitSizeOfElements(endBitPosition, arrayLength, elementBitSize);
        }

        return endBitPosition - bitPosition;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void alignBitPosition(size_t&)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void alignBitPosition(size_t& bitPosition)
    {
        bitPosition = alignTo(8, bitPosition);
    }

    template <typename ARRAY_TRAITS_ = ArrayTraits,
            typename std::enable_if<!ARRAY_TRAITS_::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    size_t bitSizeOfImpl(const OwnerType& owner, size_t bitPosition) const
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            alignBitPosition(endBitPosition);
            endBitPosition += detail::arrayTraitsBitSizeOf<ArrayTraits>(
                    owner, endBitPosition, m_rawArray[index]);
        }

        return endBitPosition - bitPosition;
    }

    size_t bitSizeOfPackedImpl(const OwnerType& owner, size_t bitPosition) const
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, contextNode, m_rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                alignBitPosition(endBitPosition);
                endBitPosition += detail::packedArrayTraitsBitSizeOf<PackedArrayTraits<ArrayTraits>>(
                        owner, contextNode, endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition - bitPosition;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void initializeOffset(OwnerType&, size_t, size_t&)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void initializeOffset(OwnerType& owner, size_t index, size_t& bitPosition)
    {
        bitPosition = alignTo(8, bitPosition);
        detail::initializeOffset<ArrayExpressions>(owner, index, bitPosition / 8);
    }

    size_t initializeOffsetsImpl(OwnerType& owner, size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            initializeOffset(owner, index, endBitPosition);
            endBitPosition = detail::arrayTraitsInitializeOffsets<ArrayTraits>(
                    owner, endBitPosition, m_rawArray[index]);
        }

        return endBitPosition;
    }

    size_t initializeOffsetsPackedImpl(OwnerType& owner, size_t bitPosition)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, contextNode, m_rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                initializeOffset(owner, index, endBitPosition);
                endBitPosition = detail::packedArrayTraitsInitializeOffsets<PackedArrayTraits<ArrayTraits>>(
                        owner, contextNode, endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::AUTO &&
                    ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO &&
                    ARRAY_TYPE_ != ArrayType::IMPLICIT, int>::type = 0>
    static size_t readArrayLength(OwnerType&, BitStreamReader&, size_t arrayLength)
    {
        return arrayLength;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::AUTO || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static size_t readArrayLength(OwnerType&, BitStreamReader& in, size_t)
    {
        return in.readVarSize();
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::IMPLICIT, int>::type = 0>
    static size_t readArrayLength(OwnerType& owner, BitStreamReader& in, size_t)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT || ArrayTraits::IS_BITSIZEOF_CONSTANT,
                "Implicit array elements must have constant bit size!");

        const size_t remainingBits = in.getBufferBitSize() - in.getBitPosition();
        return remainingBits / detail::arrayTraitsConstBitSizeOf<ArrayTraits>(owner);
    }

    template <typename IO, typename OWNER_TYPE, ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void alignAndCheckOffset(IO&, OWNER_TYPE&, size_t)
    {}

    template <typename IO, typename OWNER_TYPE, ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void alignAndCheckOffset(IO& io, OWNER_TYPE& owner, size_t index)
    {
        io.alignTo(8);
        detail::checkOffset<ArrayExpressions>(owner, index, io.getBitPosition() / 8);
    }

    void readImpl(OwnerType& owner, BitStreamReader& in, size_t arrayLength)
    {
        size_t readLength = readArrayLength(owner, in, arrayLength);

        m_rawArray.clear();
        m_rawArray.reserve(readLength);
        for (size_t index = 0; index < readLength; ++index)
        {
            alignAndCheckOffset(in, owner, index);
            detail::arrayTraitsRead<ArrayTraits>(owner, m_rawArray, in, index);
        }
    }

    void readPackedImpl(OwnerType& owner, BitStreamReader& in, size_t arrayLength = 0)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t readLength = readArrayLength(owner, in, arrayLength);

        m_rawArray.clear();

        if (readLength > 0)
        {
            m_rawArray.reserve(readLength);

            auto& contextNode = getPackingContextNode();

            for (size_t index = 0; index < readLength; ++index)
            {
                alignAndCheckOffset(in, owner, index);
                detail::packedArrayTraitsRead<PackedArrayTraits<ArrayTraits>>(
                        owner, m_rawArray, contextNode, in, index);
            }
        }
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ != ArrayType::AUTO && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void writeArrayLength(BitStreamWriter&, size_t)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::AUTO || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void writeArrayLength(BitStreamWriter& out, size_t arrayLength)
    {
        out.writeVarSize(convertSizeToUInt32(arrayLength));
    }

    void writeImpl(const OwnerType& owner, BitStreamWriter& out) const
    {
        const size_t arrayLength = m_rawArray.size();
        writeArrayLength(out, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            alignAndCheckOffset(out, owner, index);
            detail::arrayTraitsWrite<ArrayTraits>(owner, out, m_rawArray[index]);
        }
    }

    void writePackedImpl(const OwnerType& owner, BitStreamWriter& out) const
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        const size_t arrayLength = m_rawArray.size();
        writeArrayLength(out, arrayLength);

        if (arrayLength > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, contextNode, m_rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                alignAndCheckOffset(out, owner, index);
                detail::packedArrayTraitsWrite<PackedArrayTraits<ArrayTraits>>(
                        owner, contextNode, out, m_rawArray[index]);
            }
        }
    }

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
        PackedArrayTraits<ArrayTraits>::createContext(*m_packingContextNode);
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

    // mutable context is ok since it's just a cache and we need to keep bitSizeOfPacked method const
    mutable unique_ptr<PackingContextNodeType,
            RebindAlloc<allocator_type, PackingContextNodeType>> m_packingContextNode;
};

/**
 * Helper for creating an optional array within templated field constructor, where the raw array can be
 * actually the NullOpt.
 */
template <typename ARRAY, typename RAW_ARRAY>
ARRAY createOptionalArray(RAW_ARRAY&& rawArray)
{
    return ARRAY(std::forward<RAW_ARRAY>(rawArray));
}

/**
 * Overload for NullOpt.
 */
template <typename ARRAY>
NullOptType createOptionalArray(NullOptType)
{
    return NullOpt;
}

} // namespace zserio

#endif // ZSERIO_ARRAY_H_INC
