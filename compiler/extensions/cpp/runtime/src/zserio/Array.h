#ifndef ZSERIO_ARRAY_H_INC
#define ZSERIO_ARRAY_H_INC

#include <cstdlib>
#include <type_traits>

#include "zserio/AllocatorPropagatingCopy.h"
#include "zserio/ArrayTraits.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/DeltaContext.h"
#include "zserio/SizeConvertUtil.h"
#include "zserio/Traits.h"
#include "zserio/UniquePtr.h"

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

template <typename T>
struct packing_context_type<std::shared_ptr<T>, typename std::enable_if<has_zserio_packing_context<T>::value>::type>
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
void packedArrayTraitsInitContext(
        const OWNER_TYPE& owner, PACKING_CONTEXT& context, typename PACKED_ARRAY_TRAITS::ElementType element)
{
    PACKED_ARRAY_TRAITS::initContext(owner, context, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsInitContext(
        const OWNER_TYPE&, PACKING_CONTEXT& context, typename PACKED_ARRAY_TRAITS::ElementType element)
{
    PACKED_ARRAY_TRAITS::initContext(context, element);
}

// calls the bitSizeOf method properly on array traits which have constant bit size
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && has_owner_type<ARRAY_TRAITS>::value,
                int>::type = 0>
size_t arrayTraitsConstBitSizeOf(const OWNER_TYPE& owner)
{
    return ARRAY_TRAITS::bitSizeOf(owner);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && !has_owner_type<ARRAY_TRAITS>::value,
                int>::type = 0>
size_t arrayTraitsConstBitSizeOf(const OWNER_TYPE&)
{
    return ARRAY_TRAITS::bitSizeOf();
}

// calls the bitSizeOf method properly on array traits which haven't constant bit size
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsBitSizeOf(
        OWNER_TYPE owner, size_t bitPosition, const typename ARRAY_TRAITS::ElementType& element, size_t index)
{
    return ARRAY_TRAITS::bitSizeOf(owner, bitPosition, element, index);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsBitSizeOf(
        const OWNER_TYPE&, size_t bitPosition, const typename ARRAY_TRAITS::ElementType& element, size_t index)
{
    return ARRAY_TRAITS::bitSizeOf(bitPosition, element, index);
}

// calls the bitSizeOf method properly on packed array traits which haven't constant bit size
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsBitSizeOf(const OWNER_TYPE& owner, PACKING_CONTEXT& context, size_t bitPosition,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::bitSizeOf(owner, context, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsBitSizeOf(const OWNER_TYPE&, PACKING_CONTEXT& context, size_t bitPosition,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::bitSizeOf(context, bitPosition, element);
}

// calls the initializeOffsets method properly on array traits
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
size_t arrayTraitsInitializeOffsets(
        OWNER_TYPE& owner, size_t bitPosition, typename ARRAY_TRAITS::ElementType& element, size_t index)
{
    return ARRAY_TRAITS::initializeOffsets(owner, bitPosition, element, index);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                        !std::is_scalar<typename ARRAY_TRAITS::ElementType>::value,
                int>::type = 0>
size_t arrayTraitsInitializeOffsets(
        OWNER_TYPE&, size_t bitPosition, const typename ARRAY_TRAITS::ElementType& element, size_t index)
{
    return ARRAY_TRAITS::initializeOffsets(bitPosition, element, index);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value &&
                        std::is_scalar<typename ARRAY_TRAITS::ElementType>::value,
                int>::type = 0>
size_t arrayTraitsInitializeOffsets(OWNER_TYPE&, size_t bitPosition, typename ARRAY_TRAITS::ElementType element, size_t)
{
    return ARRAY_TRAITS::initializeOffsets(bitPosition, element);
}

// calls the initializeOffsets method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE& owner, PACKING_CONTEXT& context, size_t bitPosition,
        typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(owner, context, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                        !std::is_scalar<typename PACKED_ARRAY_TRAITS::ElementType>::value,
                int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE&, PACKING_CONTEXT& context, size_t bitPosition,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(context, bitPosition, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                        std::is_scalar<typename PACKED_ARRAY_TRAITS::ElementType>::value,
                int>::type = 0>
size_t packedArrayTraitsInitializeOffsets(OWNER_TYPE&, PACKING_CONTEXT& context, size_t bitPosition,
        typename PACKED_ARRAY_TRAITS::ElementType element)
{
    return PACKED_ARRAY_TRAITS::initializeOffsets(context, bitPosition, element);
}

// calls the read method properly on array traits
template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value && !has_allocator<ARRAY_TRAITS>::value,
                int>::type = 0>
void arrayTraitsRead(const OWNER_TYPE& owner, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    rawArray.push_back(ARRAY_TRAITS::read(owner, in, index));
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value && has_allocator<ARRAY_TRAITS>::value,
                int>::type = 0>
void arrayTraitsRead(OWNER_TYPE& owner, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    ARRAY_TRAITS::read(owner, rawArray, in, index);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value && !has_allocator<ARRAY_TRAITS>::value,
                int>::type = 0>
void arrayTraitsRead(const OWNER_TYPE&, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    rawArray.push_back(ARRAY_TRAITS::read(in, index));
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value && has_allocator<ARRAY_TRAITS>::value,
                int>::type = 0>
void arrayTraitsRead(const OWNER_TYPE&, RAW_ARRAY& rawArray, BitStreamReader& in, size_t index)
{
    ARRAY_TRAITS::read(rawArray, in, index);
}

// calls the read method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                        has_allocator<PACKED_ARRAY_TRAITS>::value,
                int>::type = 0>
void packedArrayTraitsRead(
        OWNER_TYPE& owner, RAW_ARRAY& rawArray, PACKING_CONTEXT& context, BitStreamReader& in, size_t index)
{
    PACKED_ARRAY_TRAITS::read(owner, rawArray, context, in, index);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                        !has_allocator<PACKED_ARRAY_TRAITS>::value,
                int>::type = 0>
void packedArrayTraitsRead(const OWNER_TYPE& owner, RAW_ARRAY& rawArray, PACKING_CONTEXT& context,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(owner, context, in, index));
}

// note: types which doesn't have owner and have allocator are never packed (e.g. string, bytes ...)
//       and thus such specialization is not needed

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename RAW_ARRAY, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value &&
                        !has_allocator<PACKED_ARRAY_TRAITS>::value,
                int>::type = 0>
void packedArrayTraitsRead(
        const OWNER_TYPE&, RAW_ARRAY& rawArray, PACKING_CONTEXT& context, BitStreamReader& in, size_t index)
{
    rawArray.push_back(PACKED_ARRAY_TRAITS::read(context, in, index));
}

// call the write method properly on array traits
template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsWrite(
        const OWNER_TYPE& owner, BitStreamWriter& out, const typename ARRAY_TRAITS::ElementType& element, size_t index)
{
    ARRAY_TRAITS::write(owner, out, element, index);
}

template <typename ARRAY_TRAITS, typename OWNER_TYPE,
        typename std::enable_if<!has_owner_type<ARRAY_TRAITS>::value, int>::type = 0>
void arrayTraitsWrite(
        const OWNER_TYPE&, BitStreamWriter& out, const typename ARRAY_TRAITS::ElementType& element, size_t)
{
    ARRAY_TRAITS::write(out, element);
}

// call the write method properly on packed array traits
template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsWrite(const OWNER_TYPE& owner, PACKING_CONTEXT& context, BitStreamWriter& out,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    PACKED_ARRAY_TRAITS::write(owner, context, out, element);
}

template <typename PACKED_ARRAY_TRAITS, typename OWNER_TYPE, typename PACKING_CONTEXT,
        typename std::enable_if<!has_owner_type<PACKED_ARRAY_TRAITS>::value, int>::type = 0>
void packedArrayTraitsWrite(const OWNER_TYPE&, PACKING_CONTEXT& context, BitStreamWriter& out,
        const typename PACKED_ARRAY_TRAITS::ElementType& element)
{
    PACKED_ARRAY_TRAITS::write(context, out, element);
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
 * Trait used to check whether the type has an OwnerType.
 * \{
 */
template <typename T, typename = void>
struct has_view_type : std::false_type
{};

template <typename T>
struct has_view_type<T, detail::void_t<typename T::ViewType>> : std::true_type
{};
/** \} */

/**
 * Array wrapper for zserio arrays which are not explicitly packed but the element type is packable
 * and thus it can be packed if requested from a parent.
 */
/**
 * Array wrapper for zserio arrays which are never packed.
 */
template <typename RAW_ARRAY, typename ARRAY_TRAITS, ArrayType ARRAY_TYPE,
        typename ARRAY_EXPRESSIONS = detail::DummyArrayExpressions>
class Array/*View*/
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
     * Constructor from l-value raw array.
     *
     * \param rawArray Raw array.
     */
    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<!std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    explicit Array(const OwnerType& owner, RawArray& rawArray) :
            m_owner(owner), m_rawArray(rawArray)
    {}

    template <typename OWNER_TYPE_ = OwnerType,
            typename std::enable_if<std::is_same<OWNER_TYPE_, detail::DummyArrayOwner>::value, int>::type = 0>
    explicit Array(RawArray& rawArray) :
            m_rawArray(rawArray)
    {}

    /**
     * Method generated by default.
     *
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
     * Operator less than.
     *
     * \param other Array to compare.
     *
     * \return True when the underlying raw array is less than the other underlying raw array.
     */
    bool operator<(const Array& other) const
    {
        return m_rawArray < other.m_rawArray;
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

    size_t size() const
    {
        return m_rawArray.size();
    }

    template <typename ARRAY_TRAITS_ = ArrayTraits,
            typename std::enable_if<has_view_type<ARRAY_TRAITS_>::value, int>::type = 0>
    typename ARRAY_TRAITS_::ViewType at(size_t i) const
    {
        // TODO[Mi-L@]: PoC const_cast
        return ARRAY_TRAITS::at(m_owner, const_cast<RAW_ARRAY&>(m_rawArray).at(i), i);
    }

    template <typename ARRAY_TRAITS_ = ArrayTraits,
            typename std::enable_if<!has_view_type<ARRAY_TRAITS_>::value, int>::type = 0>
    typename RawArray::value_type at(size_t i) const
    {
        return m_rawArray.at(i);
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
     * Calculates bit size of this array.
     *
     * Available for arrays which do not need the owner.
     *
     * \param bitPosition Current bit position.
     *
     * \return Bit size of the array.
     */
    size_t bitSizeOf(size_t bitPosition) const
    {
        return bitSizeOfImpl(m_owner, bitPosition);
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
    size_t initializeOffsets(size_t bitPosition)
    {
        return initializeOffsetsImpl(m_owner, bitPosition);
    }

    /**
     * Reads the array from the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param in Bit stream reader to use for reading.
     * \param arrayLength Array length. Not needed for auto / implicit arrays.
     */
    void read(BitStreamReader& in, size_t arrayLength = 0)
    {
        readImpl(m_owner, in, arrayLength);
    }

    /**
     * Writes the array to the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param out Bit stream write to use for writing.
     */
    void write(BitStreamWriter& out) const
    {
        writeImpl(m_owner, out);
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
    size_t bitSizeOfPacked(size_t bitPosition) const
    {
        return bitSizeOfPackedImpl(m_owner, bitPosition);
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
    size_t initializeOffsetsPacked(size_t bitPosition)
    {
        return initializeOffsetsPackedImpl(m_owner, bitPosition);
    }

    /**
     * Reads packed array from the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param in Bit stream from which to read.
     * \param arrayLength Number of elements to read or 0 in case of auto arrays.
     */
    void readPacked(BitStreamReader& in, size_t arrayLength = 0)
    {
        readPackedImpl(m_owner, in, arrayLength);
    }

    /**
     * Writes packed array to the bit stream.
     *
     * Available for arrays which do not need the owner.
     *
     * \param out Bit stream where to write.
     */
    void writePacked(BitStreamWriter& out) const
    {
        writePackedImpl(m_owner, out);
    }

private:
    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::AUTO && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void addBitSizeOfArrayLength(size_t&, size_t)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::AUTO || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void addBitSizeOfArrayLength(size_t& bitPosition, size_t arrayLength)
    {
        bitPosition += bitSizeOfVarSize(convertSizeToUInt32(arrayLength));
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void alignBitPosition(size_t&)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void alignBitPosition(size_t& bitPosition)
    {
        bitPosition = alignTo(8, bitPosition);
    }

    template <typename IO, typename OWNER_TYPE_, ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void alignAndCheckOffset(IO&, OWNER_TYPE_&, size_t)
    {}

    template <typename IO, typename OWNER_TYPE_, ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void alignAndCheckOffset(IO& io, OWNER_TYPE_& owner, size_t index)
    {
        io.alignTo(8);
        detail::checkOffset<ArrayExpressions>(owner, index, io.getBitPosition() / 8);
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void initializeOffset(OwnerType&, size_t, size_t&)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void initializeOffset(OwnerType& owner, size_t index, size_t& bitPosition)
    {
        bitPosition = alignTo(8, bitPosition);
        detail::initializeOffset<ArrayExpressions>(owner, index, bitPosition / 8);
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::AUTO && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO &&
                            ARRAY_TYPE_ != ArrayType::IMPLICIT,
                    int>::type = 0>
    static size_t readArrayLength(OwnerType&, BitStreamReader&, size_t arrayLength)
    {
        return arrayLength;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::AUTO || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
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
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::AUTO && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void writeArrayLength(BitStreamWriter&, size_t)
    {}

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::AUTO || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static void writeArrayLength(BitStreamWriter& out, size_t arrayLength)
    {
        out.writeVarSize(convertSizeToUInt32(arrayLength));
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ != ArrayType::ALIGNED && ARRAY_TYPE_ != ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
    static size_t constBitSizeOfElements(size_t, size_t arrayLength, size_t elementBitSize)
    {
        return arrayLength * elementBitSize;
    }

    template <ArrayType ARRAY_TYPE_ = ARRAY_TYPE,
            typename std::enable_if<ARRAY_TYPE_ == ArrayType::ALIGNED || ARRAY_TYPE_ == ArrayType::ALIGNED_AUTO,
                    int>::type = 0>
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
            endBitPosition +=
                    detail::arrayTraitsBitSizeOf<ArrayTraits>(owner, endBitPosition, m_rawArray[index], index);
        }

        return endBitPosition - bitPosition;
    }

    size_t initializeOffsetsImpl(OwnerType& owner, size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            initializeOffset(owner, index, endBitPosition);
            endBitPosition =
                    detail::arrayTraitsInitializeOffsets<ArrayTraits>(owner, endBitPosition, m_rawArray[index], index);
        }

        return endBitPosition;
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

    void writeImpl(const OwnerType& owner, BitStreamWriter& out) const
    {
        const size_t arrayLength = m_rawArray.size();
        writeArrayLength(out, arrayLength);

        for (size_t index = 0; index < arrayLength; ++index)
        {
            alignAndCheckOffset(out, owner, index);
            detail::arrayTraitsWrite<ArrayTraits>(owner, out, m_rawArray[index], index);
        }
    }

    using PackingContext = typename detail::packing_context_type<typename RawArray::value_type>::type;

    size_t bitSizeOfPackedImpl(const OwnerType& owner, size_t bitPosition) const
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            PackingContext context;

            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, context, m_rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                alignBitPosition(endBitPosition);
                endBitPosition += detail::packedArrayTraitsBitSizeOf<PackedArrayTraits<ArrayTraits>>(
                        owner, context, endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition - bitPosition;
    }

    size_t initializeOffsetsPackedImpl(OwnerType& owner, size_t bitPosition)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t endBitPosition = bitPosition;

        const size_t arrayLength = m_rawArray.size();
        addBitSizeOfArrayLength(endBitPosition, arrayLength);

        if (arrayLength > 0)
        {
            PackingContext context;

            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, context, m_rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                initializeOffset(owner, index, endBitPosition);
                endBitPosition = detail::packedArrayTraitsInitializeOffsets<PackedArrayTraits<ArrayTraits>>(
                        owner, context, endBitPosition, m_rawArray[index]);
            }
        }

        return endBitPosition;
    }

    void readPackedImpl(OwnerType& owner, BitStreamReader& in, size_t arrayLength = 0)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        size_t readLength = readArrayLength(owner, in, arrayLength);

        m_rawArray.clear();

        if (readLength > 0)
        {
            m_rawArray.reserve(readLength);

            PackingContext context;

            for (size_t index = 0; index < readLength; ++index)
            {
                alignAndCheckOffset(in, owner, index);
                detail::packedArrayTraitsRead<PackedArrayTraits<ArrayTraits>>(
                        owner, m_rawArray, context, in, index);
            }
        }
    }

    void writePackedImpl(const OwnerType& owner, BitStreamWriter& out) const
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT, "Implicit array cannot be packed!");

        const size_t arrayLength = m_rawArray.size();
        writeArrayLength(out, arrayLength);

        if (arrayLength > 0)
        {
            PackingContext context;

            for (size_t index = 0; index < arrayLength; ++index)
            {
                detail::packedArrayTraitsInitContext<PackedArrayTraits<ArrayTraits>>(
                        owner, context, m_rawArray[index]);
            }

            for (size_t index = 0; index < arrayLength; ++index)
            {
                alignAndCheckOffset(out, owner, index);
                detail::packedArrayTraitsWrite<PackedArrayTraits<ArrayTraits>>(
                        owner, context, out, m_rawArray[index]);
            }
        }
    }

    OwnerType m_owner; // PoC -> copy parameters by value - should be no problem!
    RawArray& m_rawArray;
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
