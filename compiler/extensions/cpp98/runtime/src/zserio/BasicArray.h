#ifndef ZSERIO_BASIC_ARRAY_H_INC
#define ZSERIO_BASIC_ARRAY_H_INC

#include <string>

#include "ArrayBase.h"
#include "Types.h"
#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "BitSizeOfCalculator.h"
#include "StringConvertUtil.h"

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    #include "inspector/BlobInspectorTree.h"
    #include "inspector/BlobTreeArrayHandler.h"
#endif

namespace zserio
{

template <class ARRAY_TRAITS>
class BasicArray : public ArrayBase<ARRAY_TRAITS>
{
public:
    // constructors
    BasicArray() {}
    explicit BasicArray(size_t size) : ArrayBase<ARRAY_TRAITS>(size) {}

    BasicArray(zserio::BitStreamReader& in, size_t size)
    {
        read(in, size);
    }

    BasicArray(zserio::BitStreamReader& in, AutoLength autoLength)
    {
        read(in, autoLength);
    }

    template <typename OFFSET_CHECKER>
    BasicArray(zserio::BitStreamReader& in, size_t size, OFFSET_CHECKER checker)
    {
        read(in, size, checker);
    }

    template <typename OFFSET_CHECKER>
    BasicArray(zserio::BitStreamReader& in, AutoLength autoLength, OFFSET_CHECKER checker)
    {
        read(in, autoLength, checker);
    }

    BasicArray(zserio::BitStreamReader& in, ImplicitLength implicitLength)
    {
        read(in, implicitLength);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    explicit BasicArray(const zserio::BlobInspectorNode& arrayNode)
    {
        read(arrayNode);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    // Zserio interface
    int hashCode() const
    {
        return ArrayBase<ARRAY_TRAITS>::hashCodeImpl();
    }

    size_t bitSizeOf(size_t bitPosition) const
    {
        if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
            return ARRAY_TRAITS::BIT_SIZE * ArrayBase<ARRAY_TRAITS>::size();

        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::DummyBitSizeOfAligner());
    }

    size_t bitSizeOf(size_t bitPosition, AutoLength) const
    {
        if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        {
            const size_t arraySize = ArrayBase<ARRAY_TRAITS>::size();
            return getBitSizeOfVarUInt64(arraySize) + ARRAY_TRAITS::BIT_SIZE * arraySize;
        }

        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::AutoLengthWrapper(),
                detail::DummyBitSizeOfAligner());
    }

    size_t bitSizeOf(size_t bitPosition, Aligned) const
    {
        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::BitSizeOfAligner());
    }

    size_t bitSizeOf(size_t bitPosition, AutoLength, Aligned) const
    {
        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::AutoLengthWrapper(),
                detail::BitSizeOfAligner());
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
            return bitPosition + ARRAY_TRAITS::BIT_SIZE * ArrayBase<ARRAY_TRAITS>::size();

        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::DummyOffsetSetterWrapper());
    }

    size_t initializeOffsets(size_t bitPosition, AutoLength)
    {
        if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        {
            const size_t arraySize = ArrayBase<ARRAY_TRAITS>::size();
            return bitPosition + getBitSizeOfVarUInt64(arraySize) + ARRAY_TRAITS::BIT_SIZE * arraySize;
        }

        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::AutoLengthWrapper(),
                detail::DummyOffsetSetterWrapper());
    }

    template <typename OFFSET_SETTER>
    size_t initializeOffsets(size_t bitPosition, OFFSET_SETTER setter)
    {
        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::OffsetSetterWrapper<OFFSET_SETTER>(setter));
    }

    template <typename OFFSET_SETTER>
    size_t initializeOffsets(size_t bitPosition, AutoLength, OFFSET_SETTER setter)
    {
        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::AutoLengthWrapper(),
                detail::OffsetSetterWrapper<OFFSET_SETTER>(setter));
    }

    void read(zserio::BitStreamReader& in, size_t size)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, detail::DummyElementFactory(),
                detail::DummyOffsetCheckerWrapper());
    }

    void read(zserio::BitStreamReader& in, AutoLength autoLength)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, autoLength, detail::DummyElementFactory(),
                detail::DummyOffsetCheckerWrapper());
    }

    template <typename OFFSET_CHECKER>
    void read(zserio::BitStreamReader& in, size_t size, OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, detail::DummyElementFactory(),
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }

    template <typename OFFSET_CHECKER>
    void read(zserio::BitStreamReader& in, AutoLength autoLength, OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, autoLength, detail::DummyElementFactory(),
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }

    void read(zserio::BitStreamReader& in, ImplicitLength implicitLength)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, implicitLength, detail::DummyElementFactory());
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    void read(const zserio::BlobInspectorNode& arrayNode)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(arrayNode, detail::DummyElementFactory());
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    void write(zserio::BitStreamWriter& out)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, detail::DummyOffsetCheckerWrapper());
    }

    void write(zserio::BitStreamWriter& out, AutoLength autoLength)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, autoLength, detail::DummyOffsetCheckerWrapper());
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, AutoLength autoLength, OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out, autoLength,
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    void write(zserio::BitStreamWriter& out, zserio::BlobInspectorNode& arrayNode,
            const zserio::StringHolder& elementZserioTypeName)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out,
            detail::BlobTreeNumericArrayHandler(arrayNode, ArrayBase<ARRAY_TRAITS>::size(), elementZserioTypeName),
            detail::DummyOffsetCheckerWrapper());
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, zserio::BlobInspectorNode& arrayNode,
            const zserio::StringHolder& elementZserioTypeName, OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out,
            detail::BlobTreeNumericArrayHandler(arrayNode, ArrayBase<ARRAY_TRAITS>::size(), elementZserioTypeName),
            detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

namespace detail
{

template <typename T>
struct var_int_nn_array_traits;

template <>
struct var_int_nn_array_traits<int16_t>
{
    typedef int16_t type;

    static size_t bitSizeOf(size_t, type value, uint8_t) { return getBitSizeOfVarInt16(value); }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarInt16();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarInt16(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<int32_t>
{
    typedef int32_t type;

    static size_t bitSizeOf(size_t, type value, uint8_t) { return getBitSizeOfVarInt32(value); }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarInt32();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarInt32(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t, type value, uint8_t) { return getBitSizeOfVarInt64(value); }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarInt64();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarInt64(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<uint16_t>
{
    typedef uint16_t type;

    static size_t bitSizeOf(size_t, type value, uint8_t) { return getBitSizeOfVarUInt16(value); }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarUInt16();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarUInt16(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<uint32_t>
{
    typedef uint32_t type;

    static size_t bitSizeOf(size_t, type value, uint8_t) { return getBitSizeOfVarUInt32(value); }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarUInt32();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarUInt32(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t, type value, uint8_t) { return getBitSizeOfVarUInt64(value); }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarUInt64();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarUInt64(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

struct var_int_array_traits
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t , type value, uint8_t) { return getBitSizeOfVarInt(value); };

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarInt();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarInt(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

struct var_uint_array_traits
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t , type value, uint8_t) { return getBitSizeOfVarUInt(value); };

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readVarUInt();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarUInt(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

struct float16_array_traits
{
    typedef float type;

    static size_t bitSizeOf(size_t, type, uint8_t) { return BIT_SIZE; }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readFloat16();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeFloat16(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 16;
};

struct float32_array_traits
{
    typedef float type;

    static size_t bitSizeOf(size_t, type, uint8_t) { return BIT_SIZE; }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readFloat32();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeFloat32(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 32;
};

struct float64_array_traits
{
    typedef double type;

    static size_t bitSizeOf(size_t, type, uint8_t) { return BIT_SIZE; }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = in.readFloat64();
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeFloat64(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 64;
};

struct bool_array_traits
{
    typedef uint8_t type;

    static size_t bitSizeOf(size_t, type, uint8_t) { return BIT_SIZE; }

    static size_t initializeOffsets(size_t bitPosition, type value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(type* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        *storage = static_cast<type>(in.readBool());
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        if (value > 1)
            throw CppRuntimeException("bool value '" + convertToString(value) + "' is out of range");
        out.writeBool((value != 0) ? true : false);
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

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 1;
};

struct string_array_traits
{
    typedef std::string type;

    static size_t bitSizeOf(size_t, const type& value, uint8_t) { return getBitSizeOfString(value); }

    static size_t initializeOffsets(size_t bitPosition, const type& value, uint8_t numBits)
    {
        return bitPosition + bitSizeOf(bitPosition, value, numBits);
    }

    template <typename ELEMENT_FACTORY>
    static void read(void* storage, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        new (storage) type(in.readString());
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t, ELEMENT_FACTORY, uint8_t)
    {
        node.getValue().get(*storage);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeString(value);
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

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

} // namespace detail

typedef BasicArray<detail::var_int_nn_array_traits<uint16_t> >  VarUInt16Array;
typedef BasicArray<detail::var_int_nn_array_traits<uint32_t> >  VarUInt32Array;
typedef BasicArray<detail::var_int_nn_array_traits<uint64_t> >  VarUInt64Array;

typedef BasicArray<detail::var_int_nn_array_traits<int16_t> >   VarInt16Array;
typedef BasicArray<detail::var_int_nn_array_traits<int32_t> >   VarInt32Array;
typedef BasicArray<detail::var_int_nn_array_traits<int64_t> >   VarInt64Array;

typedef BasicArray<detail::var_int_array_traits>                VarIntArray;
typedef BasicArray<detail::var_uint_array_traits>               VarUIntArray;

typedef BasicArray<detail::float16_array_traits>                Float16Array;
typedef BasicArray<detail::float32_array_traits>                Float32Array;
typedef BasicArray<detail::float64_array_traits>                Float64Array;

typedef BasicArray<detail::bool_array_traits>                   BoolArray;
typedef BasicArray<detail::string_array_traits>                 StringArray;

} // namespace zserio

#endif // ZSERIO_BASIC_ARRAY_H_INC
