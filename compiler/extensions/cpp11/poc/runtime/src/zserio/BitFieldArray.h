#ifndef ZSERIO_BIT_FIELD_ARRAY_H_INC
#define ZSERIO_BIT_FIELD_ARRAY_H_INC

#include "ArrayBase.h"
#include "Types.h"
#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "BitSizeOfCalculator.h"

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    #include "inspector/BlobInspectorTree.h"
    #include "inspector/BlobTreeArrayHandler.h"
#endif

namespace zserio
{

template <class ARRAY_TRAITS>
class BitFieldArray : public ArrayBase<ARRAY_TRAITS>
{
public:
    typedef ARRAY_TRAITS traits;

    // constructors
    BitFieldArray() {}
    explicit BitFieldArray(size_t size) : ArrayBase<ARRAY_TRAITS>(size) {}

    BitFieldArray(zserio::BitStreamReader& in, size_t size, uint8_t numBits)
    {
        read(in, size, numBits);
    }

    BitFieldArray(zserio::BitStreamReader& in, AutoLength autoLength, uint8_t numBits)
    {
        read(in, autoLength, numBits);
    }

    template <typename OFFSET_CHECKER>
    BitFieldArray(zserio::BitStreamReader& in, size_t size, OFFSET_CHECKER checker, uint8_t numBits)
    {
        read(in, size, checker, numBits);
    }

    template <typename OFFSET_CHECKER>
    BitFieldArray(zserio::BitStreamReader& in, AutoLength autoLength, OFFSET_CHECKER checker, uint8_t numBits)
    {
        read(in, autoLength, checker, numBits);
    }

    BitFieldArray(zserio::BitStreamReader& in, ImplicitLength implicitLength, uint8_t numBits)
    {
        read(in, implicitLength, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    BitFieldArray(const zserio::BlobInspectorNode& arrayNode, uint8_t numBits)
    {
        read(arrayNode, numBits);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    // Zserio interface
    typename ArrayBase<ARRAY_TRAITS>::element_type sum() const
    {
        return ArrayBase<ARRAY_TRAITS>::sumImpl();
    }

    int hashCode() const
    {
        return ArrayBase<ARRAY_TRAITS>::hashCodeImpl();
    }

    size_t bitSizeOf(size_t, uint8_t numBits) const
    {
        return numBits * ArrayBase<ARRAY_TRAITS>::size();
    }

    size_t bitSizeOf(size_t, AutoLength, uint8_t numBits) const
    {
        const size_t arraySize = ArrayBase<ARRAY_TRAITS>::size();

        return getBitSizeOfVarUInt64(arraySize) + numBits * arraySize;
    }

    size_t bitSizeOf(size_t bitPosition, Aligned, uint8_t numBits) const
    {
        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::BitSizeOfAligner(), numBits);
    }

    size_t bitSizeOf(size_t bitPosition, AutoLength, Aligned, uint8_t numBits) const
    {
        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::AutoLengthWrapper(),
                detail::BitSizeOfAligner(), numBits);
    }

    size_t initializeOffsets(size_t bitPosition, uint8_t numBits)
    {
        return bitPosition + numBits * ArrayBase<ARRAY_TRAITS>::size();
    }

    size_t initializeOffsets(size_t bitPosition, AutoLength, uint8_t numBits)
    {
        const size_t arraySize = ArrayBase<ARRAY_TRAITS>::size();

        return bitPosition + getBitSizeOfVarUInt64(arraySize) + numBits * arraySize;
    }

    template <typename OFFSET_SETTER>
    size_t initializeOffsets(size_t bitPosition, OFFSET_SETTER setter, uint8_t numBits)
    {
        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::OffsetSetterWrapper<OFFSET_SETTER>(setter), numBits);
    }

    template <typename OFFSET_SETTER>
    size_t initializeOffsets(size_t bitPosition, AutoLength, OFFSET_SETTER setter, uint8_t numBits)
    {
        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::AutoLengthWrapper(),
                detail::OffsetSetterWrapper<OFFSET_SETTER>(setter), numBits);
    }

    void read(zserio::BitStreamReader& in, size_t size, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, detail::DummyElementFactory(),
                detail::DummyOffsetCheckerWrapper(), numBits);
    }

    void read(zserio::BitStreamReader& in, AutoLength autoLength, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, autoLength, detail::DummyElementFactory(),
                detail::DummyOffsetCheckerWrapper(), numBits);
    }

    template <typename OFFSET_CHECKER>
    void read(zserio::BitStreamReader& in, size_t size, OFFSET_CHECKER checker, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, detail::DummyElementFactory(),
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker), numBits);
    }

    template <typename OFFSET_CHECKER>
    void read(zserio::BitStreamReader& in, AutoLength autoLength, OFFSET_CHECKER checker, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, autoLength, detail::DummyElementFactory(),
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker), numBits);
    }

    void read(zserio::BitStreamReader& in, ImplicitLength implicitLength, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, implicitLength, detail::DummyElementFactory(), numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    void read(const zserio::BlobInspectorNode& arrayNode, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(arrayNode, detail::DummyElementFactory(), numBits);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    void write(zserio::BitStreamWriter& out, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, detail::DummyOffsetCheckerWrapper(), numBits);
    }

    void write(zserio::BitStreamWriter& out, AutoLength autoLength, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, autoLength, detail::DummyOffsetCheckerWrapper(), numBits);
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, OFFSET_CHECKER checker, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker), numBits);
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, AutoLength autoLength, OFFSET_CHECKER checker, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, autoLength,
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker), numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    void write(zserio::BitStreamWriter& out, zserio::BlobInspectorNode& arrayNode,
            const zserio::StringHolder& elementZserioTypeName, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out,
            detail::BlobTreeNumericArrayHandler(arrayNode, ArrayBase<ARRAY_TRAITS>::size(), elementZserioTypeName),
            detail::DummyOffsetCheckerWrapper(), numBits);
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, zserio::BlobInspectorNode& arrayNode,
            const zserio::StringHolder& elementZserioTypeName, OFFSET_CHECKER checker, uint8_t numBits)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out,
            detail::BlobTreeNumericArrayHandler(arrayNode, ArrayBase<ARRAY_TRAITS>::size(), elementZserioTypeName),
            detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker), numBits);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

namespace detail
{

template <typename T>
struct bit_field_array_traits;

template <>
struct bit_field_array_traits<int8_t>
{
    typedef int8_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = static_cast<type>(in.readSignedBits(numBits));
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeSignedBits(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<int16_t>
{
    typedef int16_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = static_cast<type>(in.readSignedBits(numBits));
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeSignedBits(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<int32_t>
{
    typedef int32_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = in.readSignedBits(numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeSignedBits(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = in.readSignedBits64(numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeSignedBits64(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<uint8_t>
{
    typedef uint8_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = static_cast<type>(in.readBits(numBits));
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeBits(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<uint16_t>
{
    typedef uint16_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = static_cast<type>(in.readBits(numBits));
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeBits(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<uint32_t>
{
    typedef uint32_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = in.readBits(numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeBits(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

template <>
struct bit_field_array_traits<uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t, type, uint8_t numBits) { return numBits; }

    static size_t initializeOffsets(size_t bitPosition, type, uint8_t numBits) { return bitPosition + numBits; }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t numBits)
    {
        *storage = in.readBits64(numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t numBits)
    {
        out.writeBits64(value, numBits);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type value, uint8_t numBits)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        write(out, value, numBits);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

} // namespace detail

// typedefs for supported arrays
typedef BitFieldArray<detail::bit_field_array_traits<uint8_t> >     UInt8Array;
typedef BitFieldArray<detail::bit_field_array_traits<uint16_t> >    UInt16Array;
typedef BitFieldArray<detail::bit_field_array_traits<uint32_t> >    UInt32Array;
typedef BitFieldArray<detail::bit_field_array_traits<uint64_t> >    UInt64Array;

typedef BitFieldArray<detail::bit_field_array_traits<int8_t> >      Int8Array;
typedef BitFieldArray<detail::bit_field_array_traits<int16_t> >     Int16Array;
typedef BitFieldArray<detail::bit_field_array_traits<int32_t> >     Int32Array;
typedef BitFieldArray<detail::bit_field_array_traits<int64_t> >     Int64Array;

} // namespace zserio

#endif // ZSERIO_BIT_FIELD_ARRAY_H_INC
