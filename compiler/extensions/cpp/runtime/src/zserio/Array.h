#ifndef ZSERIO_ARRAY_H_INC
#define ZSERIO_ARRAY_H_INC

#include <cstdlib>
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

// overload for DummyElementFactory which is used for array traits which don't need element factory
template <typename RAW_ARRAY, typename ARRAY_TRAITS>
void arrayTraitsRead(RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, const detail::DummyElementFactory&,
        BitStreamReader& in, size_t index)
{
    rawArray.push_back(arrayTraits.read(in, index));
}

// overload for array traits which are based on allocator
template <typename RAW_ARRAY, template <template <typename> class> class ARRAY_TRAITS,
        template <typename> class ALLOC>
void arrayTraitsRead(RAW_ARRAY& rawArray, const ARRAY_TRAITS<ALLOC>& arrayTraits,
        const detail::DummyElementFactory&, BitStreamReader& in, size_t index)
{
    rawArray.push_back(arrayTraits.read(in, index, rawArray.get_allocator()));
}

// helper function to call read on packed array traits which need element factory (PackedObjectArrayTraits)
template <typename RAW_ARRAY, typename PACKED_ARRAY_TRAITS, typename ELEMENT_FACTORY,
        typename PACKED_CONTEXT_NODE>
void packedArrayTraitsRead(RAW_ARRAY& rawArray, const PACKED_ARRAY_TRAITS& packedArrayTraits,
        const ELEMENT_FACTORY& elementFactory, PACKED_CONTEXT_NODE& contextNode,
        BitStreamReader& in, size_t index)
{
    // TODO[Mi-L@]: Not yet implemented!!!
}

// overload for DummyElementFactory which is used for array traits which don't need element factory
template <typename RAW_ARRAY, typename PACKED_ARRAY_TRAITS, typename PACKED_CONTEXT_NODE>
void packedArrayTraitsRead(RAW_ARRAY& rawArray, const PACKED_ARRAY_TRAITS& packedArrayTraits,
        const detail::DummyElementFactory&, PACKED_CONTEXT_NODE& contextNode, BitStreamReader& in, size_t index)
{
    rawArray.push_back(packedArrayTraits.read(contextNode, in, index));
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

inline uint8_t deltaBitLength(int64_t delta)
{
    uint64_t abs_delta = std::abs(delta);

    uint8_t result = 0;
    while (abs_delta > 0)
    {
        result++;
        abs_delta >>= 1;
    }

    return result;
}

} // namespace detail

class DeltaContext
{
public:
    void reset()
    {
        m_isPacked = false;
        m_maxBitNumber = 0;
        m_previousElement.reset();
        m_processingStarted = false;
    }

    template <typename ELEMENT_TYPE>
    void init(ELEMENT_TYPE element)
    {
        if (!m_previousElement.hasValue())
        {
            m_previousElement = static_cast<uint64_t>(element);
        }
        else
        {
            if (m_maxBitNumber <= MAX_BIT_NUMBER_LIMIT)
                m_isPacked = true;
            const int64_t delta = element - static_cast<ELEMENT_TYPE>(m_previousElement.value()); // TODO[Mi-L@]: overflow???
            const uint8_t maxBitNumber = detail::deltaBitLength(delta);
            if (maxBitNumber > m_maxBitNumber)
            {
                m_maxBitNumber = maxBitNumber;
                if (m_maxBitNumber > MAX_BIT_NUMBER_LIMIT)
                    m_isPacked = false;
            }
            m_previousElement = static_cast<uint64_t>(element);
        }
    }

    size_t bitSizeOfDescriptor()
    {
        if (m_isPacked)
            return 1 + MAX_BIT_NUMBER_BITS;
        else
            return 1;
    }

    template <typename ARRAY_TRAITS>
    size_t bitSizeOf(const ARRAY_TRAITS& arrayTraits, size_t bitPosition,
            typename ARRAY_TRAITS::ElementType element)
    {
        if (!m_processingStarted || !m_isPacked)
        {
            m_processingStarted = true;
            return arrayTraits.bitSizeOf(bitPosition, element);
        }
        else
        {
            return m_maxBitNumber + (m_maxBitNumber > 0 ? 1 : 0);
        }
    }

    void readDescriptor(BitStreamReader& in)
    {
        m_isPacked = in.readBool();
        if (m_isPacked)
            m_maxBitNumber = in.readSignedBits(MAX_BIT_NUMBER_BITS);
    }

    template <typename ARRAY_TRAITS>
    typename ARRAY_TRAITS::ElementType read(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
            size_t index)
    {
        if (!m_processingStarted || !m_isPacked)
        {
            m_processingStarted = true;
            const auto element = arrayTraits.read(in, index);
            m_previousElement = static_cast<uint64_t>(element);
            return element;
        }
        else
        {
            const auto previousElement =
                    static_cast<typename ARRAY_TRAITS::ElementType>(m_previousElement.value());

            if (m_maxBitNumber > 0)
            {
                const int64_t delta = in.readSignedBits64(m_maxBitNumber + 1);
                const typename ARRAY_TRAITS::ElementType element = previousElement + delta;
                m_previousElement = static_cast<uint64_t>(element);
                return element;
            }
            else
            {
                return previousElement;
            }
        }
    }

    void writeDescriptor(BitStreamWriter& out)
    {
        out.writeBool(m_isPacked);
        if (m_isPacked)
            out.writeSignedBits(m_maxBitNumber, MAX_BIT_NUMBER_BITS);
    }

    template <typename ARRAY_TRAITS>
    void write(const ARRAY_TRAITS& arrayTraits, BitStreamWriter& out,
            typename ARRAY_TRAITS::ElementType element)
    {
        if (!m_processingStarted || !m_isPacked)
        {
            m_processingStarted = true;
            m_previousElement = static_cast<uint64_t>(element);
            arrayTraits.write(out, element);
        }
        else
        {
            if (m_maxBitNumber > 0)
            {
                const auto previousElement =
                        static_cast<typename ARRAY_TRAITS::ElementType>(m_previousElement.value());
                const int64_t delta = element - previousElement; // TODO[Mi-L@]: overflow???
                out.writeSignedBits64(delta, m_maxBitNumber + 1);
            }
            m_previousElement = element;
        }
    }

private:
    static const uint8_t MAX_BIT_NUMBER_BITS = 6;
    static const uint8_t MAX_BIT_NUMBER_LIMIT = 63;

    bool m_isPacked = false;
    uint8_t m_maxBitNumber = 0;
    InplaceOptionalHolder<uint64_t> m_previousElement;
    bool m_processingStarted = false;
};

template <typename ALLOC>
class PackingContextNode
{
public:
    using allocator_type = RebindAlloc<ALLOC, PackingContextNode>;
    using Children = std::vector<PackingContextNode, allocator_type>;

    explicit PackingContextNode(const ALLOC& allocator)
    :   m_children(allocator)
    {}

    PackingContextNode& createChild()
    {
        m_children.emplace_back(m_children.get_allocator());
        return m_children.back();
    }

    Children& getChildren()
    {
        return m_children;
    }

    void createContext()
    {
        m_context = DeltaContext();
    }

    bool hasContext() const
    {
        return m_context.hasValue();
    }

    DeltaContext& getContext()
    {
        return m_context.value();
    }

private:
    Children m_children;
    InplaceOptionalHolder<DeltaContext> m_context;
};

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
            m_rawArray(allocator), m_arrayTraits(arrayTraits), m_packedArrayTraits(m_arrayTraits),
            m_packingContextNode(allocator)
    {}

    /**
     * Constructor from l-value raw array.
     *
     * \param rawArray Raw array.
     * \param arrayTraits Array traits.
     */
    Array(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits) :
            m_rawArray(rawArray), m_arrayTraits(arrayTraits), m_packedArrayTraits(m_arrayTraits),
            m_packingContextNode(rawArray.get_allocator())
    {}

    /**
     * Constructor from r-value raw array.
     *
     * \param rawArray Raw array.
     * \param arrayTraits Array traits.
     */
    Array(RAW_ARRAY&& rawArray, const ARRAY_TRAITS& arrayTraits) :
            m_rawArray(std::move(rawArray)), m_arrayTraits(arrayTraits), m_packedArrayTraits(m_arrayTraits),
            m_packingContextNode(rawArray.get_allocator())
    {}

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
            m_arrayTraits(other.m_arrayTraits), m_packedArrayTraits(other.m_packedArrayTraits),
            m_packingContextNode(other.m_packingContextNode) // TODO[Mi-L@]: Propagate any holder value???
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
                endBitPosition += m_arrayTraits.bitSizeOf(endBitPosition, m_rawArray.at(index));
            }
        }

        return endBitPosition - bitPosition;
    }

    size_t bitSizeOfPacked(size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        if (size > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < size; ++index)
                m_packedArrayTraits.initContext(contextNode, m_rawArray.at(index));
            endBitPosition += bitSizeOfDescriptor(contextNode, endBitPosition);

            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                    endBitPosition = alignTo(8, endBitPosition);
                endBitPosition += m_packedArrayTraits.bitSizeOf(contextNode, endBitPosition,
                        m_rawArray.at(index));
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
                detail::initializeOffset(offsetInitializer, index, bitsToBytes(endBitPosition));
            }
            endBitPosition = m_arrayTraits.initializeOffsets(endBitPosition, m_rawArray.at(index));
        }

        return endBitPosition;
    }

    size_t initializeOffsetsPacked(size_t bitPosition, const OFFSET_INITIALIZER& offsetInitializer)
    {
        size_t endBitPosition = bitPosition;

        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            endBitPosition += zserio::bitSizeOfVarSize(convertSizeToUInt32(size));

        if (size > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < size; ++index)
                m_packedArrayTraits.initContext(contextNode, m_rawArray.at(index));
            endBitPosition += bitSizeOfDescriptor(contextNode, endBitPosition);

            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                {
                    endBitPosition = alignTo(8, endBitPosition);
                    detail::initializeOffset(offsetInitializer, index, bitsToBytes(endBitPosition));
                }
                endBitPosition = m_packedArrayTraits.initializeOffsets(
                        contextNode, endBitPosition, m_rawArray.at(index));
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
     * \param elementFactory Element factory.
     * \param offsetChecker Offset checker.
     */
    void read(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory, const OFFSET_CHECKER& offsetChecker)
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
                detail::checkOffset(offsetChecker, index, bitsToBytes(in.getBitPosition()));
            }
            detail::arrayTraitsRead(m_rawArray, m_arrayTraits, elementFactory, in, index);
        }
    }

    void readPacked(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory, const OFFSET_CHECKER& offsetChecker)
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
            readDescriptor(contextNode, in);

            for (size_t index = 0; index < readSize; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                {
                    in.alignTo(8);
                    detail::checkOffset(offsetChecker, index, bitsToBytes(in.getBitPosition()));
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
            m_arrayTraits.write(out, m_rawArray.at(index));
        }
    }

    void writePacked(BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
    {
        const size_t size = m_rawArray.size();
        if (ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
            out.writeVarSize(convertSizeToUInt32(size));

        if (size > 0)
        {
            auto& contextNode = getPackingContextNode();
            for (size_t index = 0; index < size; ++index)
                m_packedArrayTraits.initContext(contextNode, m_rawArray.at(index));
            writeDescriptor(contextNode, out);

            for (size_t index = 0; index < size; ++index)
            {
                if (ARRAY_TYPE == ArrayType::ALIGNED || ARRAY_TYPE == ArrayType::ALIGNED_AUTO)
                {
                    out.alignTo(8);
                    detail::checkOffset(offsetChecker, index, bitsToBytes(out.getBitPosition()));
                }
                m_packedArrayTraits.write(contextNode, out, m_rawArray.at(index));
            }
        }
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
     * \param elementFactory Element factory.
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
     * \param elementFactory Element factory.
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
     * \param elementFactory Element factory.
     * \param allocator Allocator to use for raw array.
     */
    void read(BitStreamReader& in, size_t arrayLength,
            const detail::ElementFactory<ARRAY_TRAITS>& elementFactory)
    {
        read(in, arrayLength, elementFactory, detail::DummyOffsetChecker());
    }

    void readPacked(BitStreamReader& in)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, detail::DummyElementFactory(), detail::DummyOffsetChecker());
    }

    void readPacked(BitStreamReader& in, const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, detail::DummyElementFactory(), offsetChecker);
    }

    void readPacked(BitStreamReader& in, const detail::ElementFactory<ARRAY_TRAITS>& elementFactory)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, elementFactory, detail::DummyOffsetChecker());
    }

    void readPacked(BitStreamReader& in, const detail::ElementFactory<ARRAY_TRAITS>& elementFactory,
            const OFFSET_CHECKER& offsetChecker)
    {
        static_assert(ARRAY_TYPE == ArrayType::AUTO || ARRAY_TYPE == ArrayType::ALIGNED_AUTO,
                "Allowed only for auto arrays!");
        readPacked(in, 0, elementFactory, offsetChecker);
    }

    void readPacked(BitStreamReader& in, size_t arrayLength)
    {
        readPacked(in, arrayLength, detail::DummyElementFactory(), detail::DummyOffsetChecker());
    }

    void readPacked(BitStreamReader& in, size_t arrayLength, const OFFSET_CHECKER& offsetChecker)
    {
        readPacked(in, arrayLength, detail::DummyElementFactory(), offsetChecker);
    }

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
    void write(BitStreamWriter& out)
    {
        write(out, detail::DummyOffsetChecker());
    }

    void writePacked(BitStreamWriter& out)
    {
        writePacked(out, detail::DummyOffsetChecker());
    }

private:
    // RebindAlloc is used here to prevent multiple instantiations of the PackingContextNode template
    using PackingContextNodeType = PackingContextNode<RebindAlloc<allocator_type, uint8_t>>;

    PackingContextNodeType& getPackingContextNode()
    {
        if (!m_packingContextNode.hasContext() && m_packingContextNode.getChildren().empty())
            m_packedArrayTraits.createContext(m_packingContextNode); // lazy init
        else
            resetContext(m_packingContextNode);

        return m_packingContextNode;
    }

    void resetContext(PackingContextNodeType& contextNode)
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

    size_t bitSizeOfDescriptor(PackingContextNodeType& contextNode, size_t bitPosition)
    {
        size_t endBitPosition = bitPosition;

        if (contextNode.hasContext())
        {
            endBitPosition += contextNode.getContext().bitSizeOfDescriptor();
        }
        else
        {
            for (auto& childNode : contextNode.getChildren())
                endBitPosition += bitSizeOfDescriptor(childNode, endBitPosition);
        }

        return endBitPosition - bitPosition;
    }

    void readDescriptor(PackingContextNodeType& contextNode, BitStreamReader& in)
    {
        if (contextNode.hasContext())
        {
            contextNode.getContext().readDescriptor(in);
        }
        else
        {
            for (auto& childNode : contextNode.getChildren())
                readDescriptor(childNode, in);
        }
    }

    void writeDescriptor(PackingContextNodeType& contextNode, BitStreamWriter& out)
    {
        if (contextNode.hasContext())
        {
            contextNode.getContext().writeDescriptor(out);
        }
        else
        {
            for (auto& childNode : contextNode.getChildren())
                writeDescriptor(childNode, out);
        }
    }

    RawArray m_rawArray;
    ARRAY_TRAITS m_arrayTraits;
    PackedArrayTraits<ARRAY_TRAITS> m_packedArrayTraits;
    PackingContextNodeType m_packingContextNode; // TODO[Mi-L@]: heap optional holder??? / unique pointer
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
