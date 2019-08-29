#ifndef ZSERIO_ARRAY_BASE_H_INC
#define ZSERIO_ARRAY_BASE_H_INC

#include <cstddef>

#include "Container.h"
#include "HashCodeUtil.h"
#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "BitPositionUtil.h"
#include "BitStreamException.h"
#include "CppRuntimeException.h"
#include "StringConvertUtil.h"
#include "BitSizeOfCalculator.h"
#include "VarUInt64Util.h"

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    #include "inspector/BlobInspectorTree.h"
#endif

namespace zserio
{

struct ImplicitLength
{
};

struct Aligned
{
};

struct AutoLength
{
};

template <class ARRAY_TRAITS>
class ArrayBase : public Container<typename ARRAY_TRAITS::type>
{
public:
    // typedefs
    typedef typename ARRAY_TRAITS::type element_type;
    typedef Container<element_type>     container_type;

    // constructors
    ArrayBase()
    {
    }

    explicit ArrayBase(size_t size) : container_type(size)
    {
    }

protected:
    template <typename ELEMENT_INITIALIZER>
    void initializeElementsImpl(ELEMENT_INITIALIZER elementInitializer)
    {
        size_t index = 0;
        for (typename container_type::iterator it = container_type::begin(); it != container_type::end(); ++it)
        {
            elementInitializer.initialize(*it, index);
            index++;
        }
    }

    int hashCodeImpl() const
    {
        int result = HASH_SEED;
        for (typename container_type::const_iterator it = container_type::begin(); it != container_type::end();
                ++it)
            result = calcHashCode(result, *it);

        return result;
    }

    template <typename AUTO_LENGTH_WRAPPER, typename BITSIZEOF_ALIGNER>
    size_t bitSizeOfImpl(size_t bitPosition, AUTO_LENGTH_WRAPPER autoLengthWrapper, BITSIZEOF_ALIGNER aligner,
            uint8_t numBits = 0) const
    {
        size_t endBitPosition = bitPosition;
        endBitPosition += autoLengthWrapper.getBitSizeOfLength(container_type::size());
        for (typename container_type::const_iterator it = container_type::begin(); it != container_type::end();
                ++it)
        {
            endBitPosition = aligner.align(endBitPosition);
            endBitPosition += ARRAY_TRAITS::bitSizeOf(endBitPosition, *it, numBits);
        }

        return endBitPosition - bitPosition;
    }

    template <typename AUTO_LENGTH_WRAPPER, typename OFFSET_SETTER_WRAPPER>
    size_t initializeOffsetsImpl(size_t bitPosition, AUTO_LENGTH_WRAPPER autoLengthWrapper,
            OFFSET_SETTER_WRAPPER setterWrapper, uint8_t numBits = 0)
    {
        size_t endBitPosition = bitPosition;
        endBitPosition += autoLengthWrapper.getBitSizeOfLength(container_type::size());
        size_t index = 0;
        for (typename container_type::iterator it = container_type::begin(); it != container_type::end(); ++it)
        {
            endBitPosition = setterWrapper.alignAndSetOffset(index, endBitPosition);
            endBitPosition = ARRAY_TRAITS::initializeOffsets(endBitPosition, *it, numBits);
            index++;
        }

        return endBitPosition;
    }

    template <typename ELEMENT_FACTORY, typename OFFSET_CHECKER_WRAPPER>
    void readImpl(BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory,
            OFFSET_CHECKER_WRAPPER checkerWrapper, uint8_t numBits = 0)
    {
        container_type::clear();
        container_type::reserve(size);
        for (size_t index = 0; index < size; ++index)
        {
            checkerWrapper.alignAndCheckOffset(index, in);
            element_type* storage = reinterpret_cast<element_type*>(container_type::get_next_storage());
            ARRAY_TRAITS::read(storage, in, index, elementFactory, numBits);
            container_type::commit_storage(storage);
        }
    }

    template <typename ELEMENT_FACTORY, typename OFFSET_CHECKER_WRAPPER>
    void readImpl(BitStreamReader& in, AutoLength, ELEMENT_FACTORY elementFactory,
            OFFSET_CHECKER_WRAPPER checkerWrapper, uint8_t numBits = 0)
    {
        const uint64_t arraySize = in.readVarUInt64();
        readImpl(in, convertVarUInt64ToArraySize(arraySize), elementFactory, checkerWrapper, numBits);
    }

    template <typename ELEMENT_FACTORY>
    void readImpl(BitStreamReader& in, ImplicitLength, ELEMENT_FACTORY elementFactory, uint8_t numBits = 0)
    {
        container_type::clear();
        BitStreamReader::BitPosType bitPosition;
        // we must read until end of the stream because we don't know element sizes
        while (true)
        {
            bitPosition = in.getBitPosition();
            const size_t index = container_type::size();
            element_type* storage = reinterpret_cast<element_type*>(container_type::get_next_storage());
            try
            {
                ARRAY_TRAITS::read(storage, in, index, elementFactory, numBits);
            }
            catch (BitStreamException&)
            {
                // set exact end bit position in the stream avoiding padding at the end
                in.setBitPosition(bitPosition);
                break;
            }
            container_type::commit_storage(storage);
        }
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    void readImpl(const zserio::BlobInspectorNode& arrayNode, ELEMENT_FACTORY elementFactory,
            uint8_t numBits = 0)
    {
        container_type::clear();
        const Container<zserio::BlobInspectorNode>& arrayElements = arrayNode.getChildren();
        const size_t numArrayElements = arrayElements.size();
        container_type::reserve(numArrayElements);
        for (size_t index = 0; index < numArrayElements; ++index)
        {
            element_type* storage = reinterpret_cast<element_type*>(container_type::get_next_storage());
            ARRAY_TRAITS::read(storage, arrayElements[index], index, elementFactory, numBits);
            container_type::commit_storage(storage);
        }
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    template <typename OFFSET_CHECKER_WRAPPER>
    void writeImpl(BitStreamWriter& out, OFFSET_CHECKER_WRAPPER checkerWrapper, uint8_t numBits = 0)
    {
        size_t index = 0;
        for (typename container_type::iterator it = container_type::begin(); it != container_type::end(); ++it)
        {
            checkerWrapper.alignAndCheckOffset(index, out);
            ARRAY_TRAITS::write(out, *it, numBits);
            index++;
        }
    }

    template <typename OFFSET_CHECKER_WRAPPER>
    void writeImpl(BitStreamWriter& out, AutoLength, OFFSET_CHECKER_WRAPPER checkerWrapper, uint8_t numBits = 0)
    {
        out.writeVarUInt64(static_cast<uint64_t>(container_type::size()));
        writeImpl(out, checkerWrapper, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER, typename OFFSET_CHECKER_WRAPPER>
    void writeImpl(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler,
            OFFSET_CHECKER_WRAPPER checkerWrapper, uint8_t numBits = 0)
    {
        size_t index = 0;
        for (typename container_type::iterator it = container_type::begin(); it != container_type::end(); ++it)
        {
            checkerWrapper.alignAndCheckOffset(index, out);
            ARRAY_TRAITS::write(out, blobTreeHandler, *it, numBits);
            index++;
        }
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

namespace detail
{

/**
 * A wrapper for real offset checker.
 *
 * Before the checker is called, the reader or writer needs to be aligned so using this actually has a side
 * effect.
 */
template <typename T>
class OffsetCheckerWrapper
{
public:
    explicit OffsetCheckerWrapper(T &checker) : m_checker(checker)
    {}

    void alignAndCheckOffset(size_t index, BitStreamReader& in)
    {
        in.alignTo(NUM_BITS_PER_BYTE);
        m_checker.checkOffset(index, bitsToBytes(in.getBitPosition()));
    }

    void alignAndCheckOffset(size_t index, BitStreamWriter& out)
    {
        out.alignTo(NUM_BITS_PER_BYTE);
        m_checker.checkOffset(index, bitsToBytes(out.getBitPosition()));
    }

private:
    T& m_checker;
};

/**
 * A dummy check wrapper that does nothing.
 *
 * Unlike the real implementation this does not align the streams.
 */
struct DummyOffsetCheckerWrapper
{
    void alignAndCheckOffset(size_t, BitStreamReader&)
    {}

    void alignAndCheckOffset(size_t, BitStreamWriter&)
    {}
};

/**
 * A wrapper for real offset setter.
 *
 * Before the setter is called, the bit position needs to be aligned.
 */
template <typename T>
class OffsetSetterWrapper
{
public:
    explicit OffsetSetterWrapper(T &setter) : m_setter(setter)
    {}

    size_t alignAndSetOffset(size_t index, size_t bitPosition)
    {
        const size_t endBitPosition = alignTo(NUM_BITS_PER_BYTE, bitPosition);
        m_setter.setOffset(index, bitsToBytes(endBitPosition));

        return endBitPosition;
    }

private:
    T& m_setter;
};

/**
 * A dummy offset setter wrapper that does nothing.
 *
 * Unlike the real implementation this does not align the bit position.
 */
class DummyOffsetSetterWrapper
{
public:
    size_t alignAndSetOffset(size_t, size_t bitPosition)
    {
        return bitPosition;
    }
};

/**
 * Real bitsizeof aligner.
 */
class BitSizeOfAligner
{
public:
    size_t align(size_t bitPosition)
    {
        return zserio::alignTo(NUM_BITS_PER_BYTE, bitPosition);
    }
};

/**
 * A dummy bitsizeof aligner that does nothing.
 *
 * Unlike the real implementation this does not align the bit position.
 */
class DummyBitSizeOfAligner
{
public:
    size_t align(size_t bitPosition)
    {
        return bitPosition;
    }
};

/**
 * A dummy element factory that does nothing.
 */
struct DummyElementFactory
{
};

/**
 * A wrapper for real auto length array.
 */
class AutoLengthWrapper
{
public:
    size_t getBitSizeOfLength(size_t length)
    {
        return getBitSizeOfVarUInt64(length);
    }
};

/**
 * A dummy wrapper for auto length array which does nothing.
 */
class DummyAutoLengthWrapper
{
public:
    size_t getBitSizeOfLength(size_t)
    {
        return 0;
    }
};

} // namespace detail

} // namespace zserio

#endif // ZSERIO_ARRAY_BASE_H_INC
