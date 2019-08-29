#ifndef ZSERIO_OBJECT_ARRAY_H_INC
#define ZSERIO_OBJECT_ARRAY_H_INC

#include "ArrayBase.h"
#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "PreWriteAction.h"

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    #include "inspector/BlobTreeArrayHandler.h"
    #include "inspector/BlobInspectorTree.h"
#endif

namespace zserio
{

namespace detail
{

template <class OBJECT>
struct object_array_traits
{
    typedef OBJECT type;

    static size_t bitSizeOf(size_t bitPosition, const type& value, uint8_t)
    {
        return value.bitSizeOf(bitPosition);
    }

    static size_t initializeOffsets(size_t bitPosition, type& value, uint8_t)
    {
        return value.initializeOffsets(bitPosition);
    }

    template <typename ELEMENT_FACTORY>
    static void read(void* storage, BitStreamReader& in, size_t index, ELEMENT_FACTORY elementFactory, uint8_t)
    {
        elementFactory.create(storage, in, index);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    static void read(type* storage, const zserio::BlobInspectorNode& node, size_t index,
            ELEMENT_FACTORY elementFactory, uint8_t)
    {
        elementFactory.create(storage, node, index);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    static void write(BitStreamWriter& out, type& value, uint8_t)
    {
        value.write(out, NO_PRE_WRITE_ACTION);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename BLOB_TREE_HANDLER>
    static void write(BitStreamWriter& out, BLOB_TREE_HANDLER blobTreeHandler, type& value, uint8_t)
    {
        BlobInspectorNode& elementNode = blobTreeHandler.createElementNode(out);
        value.write(out, elementNode, NO_PRE_WRITE_ACTION);
        blobTreeHandler.fillElementNode(elementNode, out, value);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR
};

} // namespace detail

template <class OBJECT>
class ObjectArray : public ArrayBase<detail::object_array_traits<OBJECT> >
{
public:
    // constructors
    ObjectArray() {}
    explicit ObjectArray(size_t size) : ArrayBase<ARRAY_TRAITS>(size) {}

    template <typename ELEMENT_FACTORY>
    ObjectArray(zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory)
    {
        read(in, size, elementFactory);
    }

    template <typename ELEMENT_FACTORY>
    ObjectArray(zserio::BitStreamReader& in, AutoLength autoLength, ELEMENT_FACTORY elementFactory)
    {
        read(in, autoLength, elementFactory);
    }

    template <typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
    ObjectArray(zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory,
            OFFSET_CHECKER checker)
    {
        read(in, size, elementFactory, checker);
    }

    template <typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
    ObjectArray(zserio::BitStreamReader& in, AutoLength autoLength, ELEMENT_FACTORY elementFactory,
            OFFSET_CHECKER checker)
    {
        read(in, autoLength, elementFactory, checker);
    }

    template <typename ELEMENT_FACTORY>
    ObjectArray(zserio::BitStreamReader& in, ImplicitLength implicitLength, ELEMENT_FACTORY elementFactory)
    {
        read(in, implicitLength, elementFactory);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    ObjectArray(const zserio::BlobInspectorNode& arrayNode, ELEMENT_FACTORY elementFactory)
    {
        read(arrayNode, elementFactory);
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

    // Zserio interface
    template <typename ELEMENT_INITIALIZER>
    void initializeElements(ELEMENT_INITIALIZER elementInitializer)
    {
        ArrayBase<ARRAY_TRAITS>::initializeElementsImpl(elementInitializer);
    }

    int hashCode() const
    {
        return ArrayBase<ARRAY_TRAITS>::hashCodeImpl();
    }

    size_t bitSizeOf(size_t bitPosition) const
    {
        return ArrayBase<ARRAY_TRAITS>::bitSizeOfImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::DummyBitSizeOfAligner());
    }

    size_t bitSizeOf(size_t bitPosition, AutoLength) const
    {
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
        return ArrayBase<ARRAY_TRAITS>::initializeOffsetsImpl(bitPosition, detail::DummyAutoLengthWrapper(),
                detail::DummyOffsetSetterWrapper());
    }

    size_t initializeOffsets(size_t bitPosition, AutoLength)
    {
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

    template <typename ELEMENT_FACTORY>
    void read(zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, elementFactory,
                detail::DummyOffsetCheckerWrapper());
    }

    template <typename ELEMENT_FACTORY>
    void read(zserio::BitStreamReader& in, AutoLength autoLength, ELEMENT_FACTORY elementFactory)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, autoLength, elementFactory,
                detail::DummyOffsetCheckerWrapper());
    }

    template <typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
    void read(zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory,
            OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, elementFactory,
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }

    template <typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
    void read(zserio::BitStreamReader& in, AutoLength autoLength, ELEMENT_FACTORY elementFactory,
            OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, autoLength, elementFactory,
                detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }

    template <typename ELEMENT_FACTORY>
    void read(zserio::BitStreamReader& in, ImplicitLength size, ELEMENT_FACTORY elementFactory)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(in, size, elementFactory);
    }

#ifdef ZSERIO_RUNTIME_INCLUDE_INSPECTOR
    template <typename ELEMENT_FACTORY>
    void read(const zserio::BlobInspectorNode& arrayNode, ELEMENT_FACTORY elementFactory)
    {
        ArrayBase<ARRAY_TRAITS>::readImpl(arrayNode, elementFactory);
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
            detail::BlobTreeObjectArrayHandler(arrayNode, ArrayBase<ARRAY_TRAITS>::size(), elementZserioTypeName),
            detail::DummyOffsetCheckerWrapper());
    }

    template <typename OFFSET_CHECKER>
    void write(zserio::BitStreamWriter& out, zserio::BlobInspectorNode& arrayNode,
            const zserio::StringHolder& elementZserioTypeName, OFFSET_CHECKER checker)
    {
        ArrayBase<ARRAY_TRAITS>::writeImpl(out,
            detail::BlobTreeObjectArrayHandler(arrayNode, ArrayBase<ARRAY_TRAITS>::size(), elementZserioTypeName),
            detail::OffsetCheckerWrapper<OFFSET_CHECKER>(checker));
    }
#endif // ZSERIO_RUNTIME_INCLUDE_INSPECTOR

private:
    typedef detail::object_array_traits<OBJECT> ARRAY_TRAITS;
};

} // namespace zserio

#endif // ZSERIO_OBJECT_ARRAY_H_INC
