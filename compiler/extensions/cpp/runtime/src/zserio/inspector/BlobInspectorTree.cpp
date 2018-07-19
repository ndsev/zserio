#include "../CppRuntimeException.h"

#include "BlobInspectorTree.h"

namespace zserio
{

BlobInspectorNode::BlobInspectorNode(NodeType nodeType) : m_nodeType(nodeType)
{
}

BlobInspectorNode::BlobInspectorNode(NodeType nodeType, const StringHolder& zserioTypeName,
        const StringHolder& zserioName) : m_nodeType(nodeType), m_zserioTypeName(zserioTypeName), m_zserioName(zserioName)
{
}

void BlobInspectorNode::setZserioTypeName(const StringHolder& zserioTypeName)
{
    m_zserioTypeName = zserioTypeName;
}

void BlobInspectorNode::setZserioName(const StringHolder& zserioName)
{
    m_zserioName = zserioName;
}

void BlobInspectorNode::setValue(const BlobInspectorValue& value)
{
    if (m_nodeType != NT_VALUE)
        throw CppRuntimeException("setValue is supported only for Blob Inspector Value nodes!");

    m_value = value;
}

BlobInspectorValue& BlobInspectorNode::getValue()
{
    if (m_nodeType != NT_VALUE)
        throw CppRuntimeException("setValue is supported only for Blob Inspector Value nodes!");

    return m_value;
}

void BlobInspectorNode::setZserioDescriptor(const ZserioDescriptor& nodeDescriptor)
{
    m_zserioDescriptor = nodeDescriptor;
}

void BlobInspectorNode::setZserioDescriptor(size_t startBitPosition, size_t endBitPosition)
{
    m_zserioDescriptor.startBitPosition = startBitPosition;
    m_zserioDescriptor.endBitPosition = endBitPosition;
}

void BlobInspectorNode::reserveChildren(size_t numChildren)
{
    if (m_nodeType != NT_CONTAINER && m_nodeType != NT_ARRAY)
        throw CppRuntimeException("reserveChildren is supported only for Blob Inspector Container and Array "
                "nodes!");

    m_children.reserve(numChildren);
}

BlobInspectorNode& BlobInspectorNode::createChild(NodeType nodeType, const StringHolder& zserioTypeName,
        const StringHolder& zserioName)
{
    if (m_nodeType != NT_CONTAINER && m_nodeType != NT_ARRAY)
        throw CppRuntimeException("createChild is supported only for Blob Inspector Container and Array nodes!");

    BlobInspectorNode* child = new (m_children.get_next_storage()) BlobInspectorNode(nodeType);
    child->setZserioTypeName(zserioTypeName);
    child->setZserioName(zserioName);
    m_children.commit_storage(child);

    return *child;
}

Container<BlobInspectorNode>& BlobInspectorNode::getChildren()
{
    if (m_nodeType != NT_CONTAINER && m_nodeType != NT_ARRAY)
        throw CppRuntimeException("getChildren is supported only for Blob Inspector Container and Array "
                                  "nodes!");

    return m_children;
}

void BlobInspectorNode::reserveZserioFunctions(size_t numZserioFunctions)
{
    if (m_nodeType != NT_CONTAINER)
        throw CppRuntimeException("reserveZserioFunctions is supported only for Blob Inspector Container nodes!");

    m_zserioFunctions.reserve(numZserioFunctions);
}

BlobInspectorNode::ZserioFunction& BlobInspectorNode::createZserioFunction(const StringHolder& returnTypeName,
        const StringHolder& functionName)
{
    if (m_nodeType != NT_CONTAINER)
        throw CppRuntimeException("createZserioFunction is supported only for Blob Inspector Container nodes!");

    ZserioFunction* zserioFunction = new (m_zserioFunctions.get_next_storage()) ZserioFunction(returnTypeName,
            functionName);
    m_zserioFunctions.commit_storage(zserioFunction);

    return *zserioFunction;
}

Container<BlobInspectorNode::ZserioFunction>& BlobInspectorNode::getZserioFunctions()
{
    if (m_nodeType != NT_CONTAINER)
        throw CppRuntimeException("getZserioFunctions is supported only for Blob Inspector Container nodes!");

    return m_zserioFunctions;
}

BlobInspectorNode::NodeType BlobInspectorNode::getNodeType() const
{
    return m_nodeType;
}

const StringHolder& BlobInspectorNode::getZserioTypeName() const
{
    return m_zserioTypeName;
}

const StringHolder& BlobInspectorNode::getZserioName() const
{
    return m_zserioName;
}

const BlobInspectorValue& BlobInspectorNode::getValue() const
{
    return m_value;
}

const BlobInspectorNode::ZserioDescriptor& BlobInspectorNode::getZserioDescriptor() const
{
    return m_zserioDescriptor;
}

const Container<BlobInspectorNode>& BlobInspectorNode::getChildren() const
{
    return m_children;
}

const Container<BlobInspectorNode::ZserioFunction>& BlobInspectorNode::getZserioFunctions() const
{
    return m_zserioFunctions;
}

const char* BlobInspectorNode::convertNodeTypeToString(NodeType nodeType)
{
    switch (nodeType)
    {
    case NT_CONTAINER:
        return "NT_CONTAINER";

    case NT_VALUE:
        return "NT_VALUE";

    case NT_ARRAY:
        return "NT_ARRAY";

    default:
        return "NT_INVALID";
    }
}

} // namespace zserio
