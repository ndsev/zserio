#include "../CppRuntimeException.h"
#include "../StringConvertUtil.h"

#include "BlobInspectorTreeUtil.h"

namespace zserio
{

const BlobInspectorNode& getBlobInspectorNode(const BlobInspectorTree& tree, size_t childIndex,
        BlobInspectorNode::NodeType expectedNodeType)
{
    const BlobInspectorNode& child = getBlobInspectorNode(tree, childIndex);
    const BlobInspectorNode::NodeType childNodeType = child.getNodeType();
    if (childNodeType != expectedNodeType)
        throw CppRuntimeException("Unexpected blob inspector node " + convertToString(childIndex) +
                " (zserio type name: " + std::string(child.getZserioTypeName().get()) + ", zserio name: " +
                std::string(child.getZserioName().get()) + ", node type: " +
                std::string(BlobInspectorNode::convertNodeTypeToString(childNodeType)));

    return child;
}

const BlobInspectorNode& getBlobInspectorNode(const BlobInspectorTree& tree, size_t childIndex)
{
    const Container<BlobInspectorNode>& children = tree.getChildren();
    if (childIndex >= children.size())
        throw CppRuntimeException("Wrong blob inspector node child index " + convertToString(childIndex) +
                " (zserio type name: " + std::string(tree.getZserioTypeName().get()) + ", zserio name: " +
                std::string(tree.getZserioName().get()) + ", number of children: " +
                convertToString(children.size()));

    return children[childIndex];
}

void findBlobInspectorNodes(const BlobInspectorTree& tree, bool recursiveSearch,
        const std::string& expectedZserioTypeName, const std::string& expectedZserioName,
        Container<BlobInspectorNode>& searchedNodes)
{
    const Container<BlobInspectorNode>& children = tree.getChildren();
    for (Container<BlobInspectorNode>::const_iterator it = children.begin(); it != children.end(); ++it)
    {
        if ((expectedZserioTypeName.empty() || it->getZserioTypeName() == expectedZserioTypeName) &&
            (expectedZserioName.empty() || it->getZserioName() == expectedZserioName))
        {
            BlobInspectorNode* searchedNode = new (searchedNodes.get_next_storage()) BlobInspectorNode(*it);
            searchedNodes.commit_storage(searchedNode);
        }

        if (recursiveSearch)
            findBlobInspectorNodes(*it, recursiveSearch, expectedZserioTypeName, expectedZserioName, searchedNodes);
    }
}

} // namespace zserio
