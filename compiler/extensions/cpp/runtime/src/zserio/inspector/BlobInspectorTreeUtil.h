#ifndef ZSERIO_BLOB_INSPECTOR_NODE_UTIL_H_INC
#define ZSERIO_BLOB_INSPECTOR_NODE_UTIL_H_INC

#include <string>

#include "BlobInspectorTree.h"

namespace zserio
{

const BlobInspectorNode& getBlobInspectorNode(const BlobInspectorTree& tree, size_t childIndex,
        BlobInspectorNode::NodeType expectedNodeType);

const BlobInspectorNode& getBlobInspectorNode(const BlobInspectorTree& tree, size_t childIndex);

void findBlobInspectorNodes(const BlobInspectorTree& tree, bool recursiveSearch,
        const std::string& expectedZserioTypeName, const std::string& expectedZserioName,
        Container<BlobInspectorNode>& searchedNodes);

} // namespace zserio

#endif // ifndef ZSERIO_BLOB_INSPECTOR_NODE_UTIL_H_INC
