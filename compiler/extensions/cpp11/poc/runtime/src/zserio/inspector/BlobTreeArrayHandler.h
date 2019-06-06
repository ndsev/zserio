#ifndef ZSERIO_BLOB_TREE_ARRAY_HANDLER_H_INC
#define ZSERIO_BLOB_TREE_ARRAY_HANDLER_H_INC

#include "../BitStreamWriter.h"
#include "../StringConvertUtil.h"
#include "../PreWriteAction.h"

#include "BlobInspectorTree.h"

namespace zserio
{
namespace detail
{

class BlobTreeArrayHandlerBase
{
public:
    BlobTreeArrayHandlerBase(BlobInspectorNode& arrayNode, size_t arraySize,
            const StringHolder& elementZserioTypeName, BlobInspectorNode::NodeType elementNodeType) :
                m_arrayNode(arrayNode),
                m_elementZserioTypeName(elementZserioTypeName),
                m_elementNodeType(elementNodeType),
                m_startBitPosition(BlobInspectorNode::ZserioDescriptor::UNDEFINED_BIT_POSITION)
    {
        arrayNode.reserveChildren(arraySize);
    }

    BlobInspectorNode& createElementNode(const BitStreamWriter& out)
    {
        m_startBitPosition = out.getBitPosition();

        return m_arrayNode.createChild(m_elementNodeType, m_elementZserioTypeName, m_arrayNode.getZserioName());
    }

protected:
    void fillZserioDescriptor(BlobInspectorNode& elementNode, const BitStreamWriter& out)
    {
        elementNode.setZserioDescriptor(BlobInspectorNode::ZserioDescriptor(m_startBitPosition,
                out.getBitPosition()));
    }

private:
    BlobInspectorNode&          m_arrayNode;
    StringHolder                m_elementZserioTypeName;
    BlobInspectorNode::NodeType m_elementNodeType;
    size_t                      m_startBitPosition;
};

class BlobTreeNumericArrayHandler : public BlobTreeArrayHandlerBase
{
public:
    BlobTreeNumericArrayHandler(BlobInspectorNode& arrayNode,
            size_t arraySize,
            const StringHolder& elementZserioTypeName) : BlobTreeArrayHandlerBase(arrayNode, arraySize,
                    elementZserioTypeName, BlobInspectorNode::NT_VALUE)
    {
    }

    template<typename VALUE_TYPE>
    void fillElementNode(BlobInspectorNode& elementNode, const BitStreamWriter& out, const VALUE_TYPE& value)
    {
        elementNode.setValue(BlobInspectorValue(value));
        fillZserioDescriptor(elementNode, out);
    }
};

class BlobTreeObjectArrayHandler : public BlobTreeArrayHandlerBase
{
public:
    BlobTreeObjectArrayHandler(BlobInspectorNode& arrayNode,
            size_t arraySize,
            const StringHolder& elementZserioTypeName) : BlobTreeArrayHandlerBase(arrayNode, arraySize,
                    elementZserioTypeName, BlobInspectorNode::NT_CONTAINER)
    {
    }

    template<typename VALUE_TYPE>
    void fillElementNode(BlobInspectorNode& elementNode, const BitStreamWriter& out, const VALUE_TYPE&)
    {
        fillZserioDescriptor(elementNode, out);
    }
};

} // namespace detail
} // namespace zserio

#endif // ifndef ZSERIO_BLOB_TREE_ARRAY_HANDLER_H_INC
