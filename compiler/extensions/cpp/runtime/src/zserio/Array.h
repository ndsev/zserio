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

// helper trait to choose packing context type for an array from an element type T
template <typename T, typename = void>
struct packing_context_type
{
    using type = DeltaContext;
};

template <typename T>
struct packing_context_type<T, typename std::enable_if<has_zserio_packing_context<T>::value>::type>
{
    using type = typename T::ZserioPackingContext;
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
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsInitContext(const OWNER_TYPE& owner, PACKING_CONTEXT& packingContext,
        typename PACKED_ARRAY_TRAITS::ElementType element)
{
    PACKED_ARRAY_TRAITS::initContext(owner, packingContext, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsInitContext(const OWNER_TYPE&, PACKING_CONTEXT& packingContext,
        typename PACKED_ARRAY_TRAITS::ElementType element)
{
    PACKED_ARRAY_TRAITS::initContext(packingContext, element);
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
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsBitSizeOf(const OWNER_TYPE& owner, PACKING_CONTEXT& packingContext,
        size_t bitPosition, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::bitSizeOf(owner, packingContext, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsBitSizeOf(const OWNER_TYPE&, PACKING_CONTEXT& packingContext, size_t bitPosition,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::bitSizeOf(packingContext, bitPosition, element);
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
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE& owner, PACKING_CONTEXT& packingContext,
        size_t bitPosition, typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(owner, packingContext, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                !std::is_scalar<typename PACKED_ARRAY_TRAITS::ElementType>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE&, PACKING_CONTEXT& packingContext,
        size_t bitPosition, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(packingContext, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                std::is_scalar<typename PACKED_ARRAY_TRAITS::ElementType>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE&, PACKING_CONTEXT& packingContext,
        size_t bitPosition, typename PACKED_ARRAY_TRAITS::ElementType element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(packingContext, bitPosition, element);
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
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                has_allocator<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsRead(OWNER_TYPE& owner, RAW_ARRAY& rawArray, PACKING_CONTEXT& packingContext,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(owner, packingContext, in, rawArray.get_allocator(), index));
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                !has_allocator<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsRead(const OWNER_TYPE& owner, RAW_ARRAY& rawArray, PACKING_CONTEXT& packingContext,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(owner, packingContext, in, index));
}

// note: types which doesn't have owner and have allocator are never packed (e.g. string, bytes ...)
//       and thus such specialization is not needed

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                !has_allocator<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsRead(const OWNER_TYPE&, RAW_ARRAY& rawArray, PACKING_CONTEXT& packingContext,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(packingContext, in, index));
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
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsWrite(const OWNER_TYPE& owner, PACKING_CONTEXT& packingContext,
        BitStreamWriter& out, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::write(owner, packingContext, out, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsWrite(const OWNER_TYPE&, PACKING_CONTEXT& packingContext,
        BitStreamWriter& out, const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::write(packingContext, out, element);
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
 * Array base.
 */
template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ARRAY_EXPRESSIONS>
class ArrayBase
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
    explicit ArrayBase(const allocator_type& allocator = allocator_type()) :
            m_rawArray(allocator)
    {}

    /**
     * Constructor from l-value raw array.
     *
     * \param rawArray Raw array.
     */
    explicit ArrayBase(const RawArray& rawArray) :
            m_rawArray(rawArray)
    {}

    /**
     * Constructor from r-value raw array.
     *
     * \param rawArray Raw array.
     */
    explicit ArrayBase(RawArray&& rawArray) :
            m_rawArray(std::move(rawArray))
    {}

    /**
     * Method generated by default.
     *
     * \{
     */
    ~ArrayBase() = default;
    ArrayBase(const ArrayBase& other) = default;
    ArrayBase& operator=(const ArrayBase& other) = default;
    ArrayBase(ArrayBase&& other) = default;
    ArrayBase& operator=(ArrayBase&& other) = default;
    /**
     * \}
     */

    /**
     * Copy constructor which forces allocator propagating while copying the raw array.
     *
     * \param other Source array to copy.
     * \param allocator Allocator to propagate during copying.
     */
    ArrayBase(PropagateAllocatorT, const ArrayBase& other, const allocator_type& allocator) :
            m_rawArray(allocatorPropagatingCopy(other.m_rawArray, allocator))
    {}

    /**
     * Operator equality.
     *
     * \param other ArrayBase to compare.
     *
     * \return True when the underlying raw arrays have same contents, false otherwise.
     */
    bool operator==(const ArrayBase& other) const
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

private:
    RawArray m_rawArray;
};

/**
 * Common implementation for array methods.
 */
template <typename ARRAY_BASE, ArrayType ARRAY_TYPE>
struct ArrayMethods
{
    using ArrayTraits = typename ARRAY_BASE::ArrayTraits;
    using ArrayExpressions = typename ARRAY_BASE::ArrayExpressions;
    using OwnerType = typename ARRAY_BASE::OwnerType;

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
    static void alignBitPosition(size_t&)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<
                    ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO, int>::type = 0>
    static void alignBitPosition(size_t& bitPosition)
    {
        bitPosition = alignTo(8, bitPosition);
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
};

/**
 * Common implementation for unpacked array methods.
 */
template <typename ARRAY_BASE, ArrayType ARRAY_TYPE>
struct UnpackedArrayMethods
{
    using RawArray = typename ARRAY_BASE::RawArray;
    using ArrayTraits = typename ARRAY_BASE::ArrayTraits;
    using OwnerType = typename ARRAY_BASE::OwnerType;

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
    static size_t bitSizeOf(const RawArray& rawArray, const OwnerType& owner, size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            const size_t elementBitSize = detail::arrayTraitsConstBitSizeOf<ArrayTraits>(owner);
            endBitPosition += constBitSizeOfElements(endBitPosition, arrayLength, elementBitSize);
        }

        return endBitPosition - bitPosition;
    }

    template <typename ARRAY_TRAITS_ = ArrayTraits,
            typename std::enable_if<!ARRAY_TRAITS_::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    static size_t bitSizeOf(const RawArray& rawArray, const OwnerType& owner, size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::addBitSizeOfArrayLength(endBitPosition, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::alignBitPosition(endBitPosition);
            endBitPosition += detail::arrayTraitsBitSizeOf<ArrayTraits>(
                    owner, endBitPosition, rawArray[index]);
        }

        return endBitPosition - bitPosition;
    }

    static size_t initializeOffsets(RawArray& rawArray, OwnerType& owner, size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::addBitSizeOfArrayLength(endBitPosition, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::initializeOffset(owner, index, endBitPosition);
            endBitPosition = detail::arrayTraitsInitializeOffsets<ArrayTraits>(
                    owner, endBitPosition, rawArray[index]);
        }

        return endBitPosition;
    }

    static void read(RawArray& rawArray, OwnerType& owner, BitStreamReader& in, size_t arrayLength)
    {
        size_t readLength = ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::readArrayLength(owner, in, arrayLength);

        rawArray.clear();
        rawArray.reserve(readLength);
        for (size_t index = 0; index < readLength; ++index)
        {
            ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::alignAndCheckOffset(in, owner, index);
            detail::arrayTraitsRead<ArrayTraits>(owner, rawArray, in, index);
        }
    }

    static void write(const RawArray& rawArray, const OwnerType& owner, BitStreamWriter& out)
    {
        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::writeArrayLength(out, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::alignAndCheckOffset(out, owner, index);
            detail::arrayTraitsWrite<ArrayTraits>(owner, out, rawArray[index]);
        }
    }
};

/**
 * Common implementation for packed array methods.
 */
template <typename ARRAY_BASE, ArrayType ARRAY_TYPE>
struct PackedArrayMethods
{
    using RawArray = typename ARRAY_BASE::RawArray;
    using ArrayTraits = typename ARRAY_BASE::ArrayTraits;
    using OwnerType = typename ARRAY_BASE::OwnerType;
    using PackingContext = typename detail::packing_context_type<typename RawArray::value_type>::type;

    static size_t bitSizeOfPacked(const RawArray& rawArray, const OwnerType& owner, size_t bitPosition)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            PackingContext packingContext;

            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, packingContext, rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::alignBitPosition(endBitPosition);
                endBitPosition += detail::packedArrayTraitsBitSizeOf<PackedArrayTraits<ArrayTraits>>(
                        owner, packingContext, endBitPosition, rawArray[index]);
            }
        }

        return endBitPosition - bitPosition;
    }

    static size_t initializeOffsetsPacked(RawArray& rawArray, OwnerType& owner, size_t bitPosition)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            PackingContext packingContext;

            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, packingContext, rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::initializeOffset(owner, index, endBitPosition);
                endBitPosition = detail::packedArrayTraitsInitializeOffsets<PackedArrayTraits<ArrayTraits>>(
                        owner, packingContext, endBitPosition, rawArray[index]);
            }
        }

        return endBitPosition;
    }

    static void readPacked(RawArray& rawArray, OwnerType& owner, BitStreamReader& in, size_t arrayLength = 0)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t readLength = ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::readArrayLength(owner, in, arrayLength);

        rawArray.clear();

        if (readLength > 0)
        {
            rawArray.reserve(readLength);

            PackingContext packingContext;

            for (size_t index = 0; index < readLength; ++index)
            {
                ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::alignAndCheckOffset(in, owner, index);
                detail::packedArrayTraitsRead<PackedArrayTraits<ArrayTraits>>(
                        owner, rawArray, packingContext, in, index);
            }
        }
    }

    static void writePacked(const RawArray& rawArray, const OwnerType& owner, BitStreamWriter& out)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        const size_t arrayLength = rawArray.size();
        ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::writeArrayLength(out, arrayLength);

        if (arrayLength > 0)
        {
            PackingContext packingContext;

            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, packingContext, rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                ArrayMethods<ARRAY_BASE, ARRAY_TYPE>::alignAndCheckOffset(out, owner, index);
                detail::packedArrayTraitsWrite<PackedArrayTraits<ArrayTraits>>(
                        owner, packingContext, out, rawArray[index]);
            }
        }
    }
};

/**
 * Array wrapper for zserio arrays which are never packed.
 */
template <typename RAW_ARRAY, typename ARRAY_TRAITS, ArrayType ARRAY_TYPE,
        typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions>
class UnpackedArray : public ArrayBase<RAW_ARRAY, ARRAY_TRAITS, ARRAY_EXPRESSIONS>
{
private:
    using Base = ArrayBase<RAW_ARRAY, ARRAY_TRAITS, ARRAY_EXPRESSIONS>;

    using UnpackedMethods = UnpackedArrayMethods<Base, ARRAY_TYPE>;

public:
    using OwnerType = typename Base::OwnerType;
    using Base::Base;
    using Base::getRawArray;

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
        return UnpackedMethods::bitSizeOf(getRawArray(), detail::DummyArrayOwner(), bitPosition);
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
        return UnpackedMethods::bitSizeOf(getRawArray(), owner, bitPosition);
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
        return UnpackedMethods::initializeOffsets(getRawArray(), owner, bitPosition);
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
        return UnpackedMethods::initializeOffsets(getRawArray(), owner, bitPosition);
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
        UnpackedMethods::read(getRawArray(), owner, in, arrayLength);
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
        UnpackedMethods::read(getRawArray(), owner, in, arrayLength);
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
        UnpackedMethods::write(getRawArray(), detail::DummyArrayOwner(), out);
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
        UnpackedMethods::write(getRawArray(), owner, out);
    }
};

/**
 * Array wrapper for zserio arrays which are explicitly packed (and the type is packable) - i.e. always packed.
 */
template <typename RAW_ARRAY, typename ARRAY_TRAITS, ArrayType ARRAY_TYPE,
        typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions>
class PackedArray : public ArrayBase<RAW_ARRAY, ARRAY_TRAITS, ARRAY_EXPRESSIONS>
{
private:
    using Base = ArrayBase<RAW_ARRAY, ARRAY_TRAITS, ARRAY_EXPRESSIONS>;

    using PackedMethods = PackedArrayMethods<Base, ARRAY_TYPE>;

public:
    using OwnerType = typename Base::OwnerType;
    using Base::Base;
    using Base::getRawArray;

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
        return PackedMethods::bitSizeOfPacked(getRawArray(), detail::DummyArrayOwner(), bitPosition);
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
        return PackedMethods::bitSizeOfPacked(getRawArray(), ownerType, bitPosition);
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
        return PackedMethods::initializeOffsetsPacked(getRawArray(), owner, bitPosition);
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
        return PackedMethods::initializeOffsetsPacked(getRawArray(), owner, bitPosition);
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
        PackedMethods::readPacked(getRawArray(), owner, in, arrayLength);
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
        PackedMethods::readPacked(getRawArray(), owner, in, arrayLength);
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
        PackedMethods::writePacked(getRawArray(), detail::DummyArrayOwner(), out);
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
        PackedMethods::writePacked(getRawArray(), owner, out);
    }
};

/**
 * Array wrapper for zserio arrays which are not explicitly packed but the element type is packable
 * and thus it can be packed if requested from a parent.
 */
/**
 * Array wrapper for zserio arrays which are never packed.
 */
template <typename RAW_ARRAY, typename ARRAY_TRAITS, ArrayType ARRAY_TYPE,
        typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions>
class Array : public ArrayBase<RAW_ARRAY, ARRAY_TRAITS, ARRAY_EXPRESSIONS>
{
private:
    using Base = ArrayBase<RAW_ARRAY, ARRAY_TRAITS, ARRAY_EXPRESSIONS>;

    using UnpackedMethods = UnpackedArrayMethods<Base, ARRAY_TYPE>;
    using PackedMethods = PackedArrayMethods<Base, ARRAY_TYPE>;

public:
    using OwnerType = typename Base::OwnerType;
    using Base::Base;
    using Base::getRawArray;

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
        return UnpackedMethods::bitSizeOf(getRawArray(), detail::DummyArrayOwner(), bitPosition);
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
        return UnpackedMethods::bitSizeOf(getRawArray(), owner, bitPosition);
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
        return UnpackedMethods::initializeOffsets(getRawArray(), owner, bitPosition);
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
        return UnpackedMethods::initializeOffsets(getRawArray(), owner, bitPosition);
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
        UnpackedMethods::read(getRawArray(), owner, in, arrayLength);
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
        UnpackedMethods::read(getRawArray(), owner, in, arrayLength);
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
        UnpackedMethods::write(getRawArray(), detail::DummyArrayOwner(), out);
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
        UnpackedMethods::write(getRawArray(), owner, out);
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
        return PackedMethods::bitSizeOfPacked(getRawArray(), detail::DummyArrayOwner(), bitPosition);
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
        return PackedMethods::bitSizeOfPacked(getRawArray(), ownerType, bitPosition);
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
        return PackedMethods::initializeOffsetsPacked(getRawArray(), owner, bitPosition);
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
        return PackedMethods::initializeOffsetsPacked(getRawArray(), owner, bitPosition);
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
        PackedMethods::readPacked(getRawArray(), owner, in, arrayLength);
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
        PackedMethods::readPacked(getRawArray(), owner, in, arrayLength);
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
        PackedMethods::writePacked(getRawArray(), detail::DummyArrayOwner(), out);
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
        PackedMethods::writePacked(getRawArray(), owner, out);
    }
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
