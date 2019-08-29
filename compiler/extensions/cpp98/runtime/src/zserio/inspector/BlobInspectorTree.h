#ifndef ZSERIO_BLOB_INSPECTOR_TREE_H_INC
#define ZSERIO_BLOB_INSPECTOR_TREE_H_INC

#include <string>

#include "../Container.h"
#include "../StringHolder.h"

#include "BlobInspectorValue.h"

namespace zserio
{

class BlobInspectorNode
{
public:
    enum NodeType
    {
        NT_CONTAINER,   // m_value = VT_UNDEFINED, m_children.size() > 0
        NT_VALUE,       // m_value = value, m_children.size() = 0
        NT_ARRAY        // m_value = VT_UNDEFINED, m_children.size() = array size
    };

    struct ZserioDescriptor
    {
        ZserioDescriptor(size_t startPos = UNDEFINED_BIT_POSITION, size_t endPos = UNDEFINED_BIT_POSITION) :
            startBitPosition(startPos), endBitPosition(endPos) {}

        bool operator==(const ZserioDescriptor& other) const
        {
            return (startBitPosition == other.startBitPosition && endBitPosition == other.endBitPosition);
        }

        size_t              startBitPosition;
        size_t              endBitPosition;

        static const size_t UNDEFINED_BIT_POSITION = static_cast<size_t>(-1);
    };

    struct ZserioFunction
    {
        ZserioFunction(const StringHolder& returnTypeName, const StringHolder& functionName) :
                        zserioReturnTypeName(returnTypeName), zserioFunctionName(functionName) {}
        ZserioFunction(const StringHolder& returnTypeName, const StringHolder& functionName,
                const BlobInspectorValue& value) :
                        zserioReturnTypeName(returnTypeName), zserioFunctionName(functionName), returnValue(value) {}

        bool operator==(const ZserioFunction& other) const
        {
            return (zserioReturnTypeName == other.zserioReturnTypeName && zserioFunctionName == other.zserioFunctionName &&
                    returnValue == other.returnValue);
        }

        StringHolder        zserioReturnTypeName;
        StringHolder        zserioFunctionName;
        BlobInspectorValue  returnValue;
    };

    explicit BlobInspectorNode(NodeType nodeType = NT_CONTAINER);
    BlobInspectorNode(NodeType nodeType, const StringHolder& zserioTypeName, const StringHolder& zserioName);

    // default destructor, copy constructor and copy assignment operator is fine

    void setZserioTypeName(const StringHolder& zserioTypeName);
    void setZserioName(const StringHolder& zserioName);

    void setValue(const BlobInspectorValue& value);
    BlobInspectorValue& getValue();

    void setZserioDescriptor(const ZserioDescriptor& zserioDescriptor);
    void setZserioDescriptor(size_t startBitPosition, size_t endBitPosition);

    void reserveChildren(size_t numChildren);
    BlobInspectorNode& createChild(NodeType nodeType, const StringHolder& zserioTypeName,
            const StringHolder& zserioName);
    Container<BlobInspectorNode>& getChildren();

    void reserveZserioFunctions(size_t numZserioFunctions);
    ZserioFunction& createZserioFunction(const StringHolder& returnTypeName, const StringHolder& functionName);
    Container<ZserioFunction>& getZserioFunctions();

    NodeType getNodeType() const;
    const StringHolder& getZserioTypeName() const;
    const StringHolder& getZserioName() const;
    const BlobInspectorValue& getValue() const;
    const ZserioDescriptor& getZserioDescriptor() const;
    const Container<BlobInspectorNode>& getChildren() const;
    const Container<ZserioFunction>& getZserioFunctions() const;

    static const char* convertNodeTypeToString(NodeType nodeType);

private:
    NodeType                        m_nodeType;
    StringHolder                    m_zserioTypeName;
    StringHolder                    m_zserioName;
    BlobInspectorValue              m_value;
    Container<BlobInspectorNode>    m_children;
    ZserioDescriptor                m_zserioDescriptor;
    Container<ZserioFunction>       m_zserioFunctions;
};

typedef BlobInspectorNode BlobInspectorTree;

} // namespace zserio

#endif // ifndef ZSERIO_BLOB_INSPECTOR_TREE_H_INC
