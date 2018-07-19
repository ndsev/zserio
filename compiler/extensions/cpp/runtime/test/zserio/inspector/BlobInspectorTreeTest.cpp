#include "zserio/inspector/BlobInspectorTree.h"

#include "gtest/gtest.h"

namespace zserio
{

class BlobInspectorTreeTest : public ::testing::Test
{
protected:
    void fillTree(BlobInspectorTree& tree)
    {
        tree.setZserioTypeName(TREE_ZSERIO_TYPE_NAME);
        tree.setZserioName(TREE_ZSERIO_NAME);
        tree.setZserioDescriptor(TREE_START_BIT_POSITION, TREE_END_BIT_POSITION);
        BlobInspectorNode::ZserioFunction& zserioFunction = tree.createZserioFunction(TREE_FUNC_ZSERIO_RETURN_TYPE_NAME,
                TREE_FUNC_ZSERIO_FUNCTION_NAME);
        zserioFunction.returnValue.set(TREE_FUNC_VALUE);

        BlobInspectorNode& child1 = tree.createChild(BlobInspectorNode::NT_CONTAINER, CHILD1_ZSERIO_TYPE_NAME,
                CHILD1_ZSERIO_NAME);
        child1.setZserioDescriptor(BlobInspectorNode::ZserioDescriptor(CHILD1_START_BIT_POSITION,
                CHILD1_END_BIT_POSITION));
        BlobInspectorNode& childOfChild1 = child1.createChild(BlobInspectorNode::NT_VALUE,
                CHILD_OF_CHILD1_ZSERIO_TYPE_NAME, CHILD_OF_CHILD1_ZSERIO_NAME);
        childOfChild1.setValue(BlobInspectorValue(CHILD_OF_CHILD1_VALUE));
        childOfChild1.setZserioDescriptor(CHILD_OF_CHILD1_START_BIT_POSITION, CHILD_OF_CHILD1_END_BIT_POSITION);

        BlobInspectorNode& child2 = tree.createChild(BlobInspectorNode::NT_VALUE, CHILD2_ZSERIO_TYPE_NAME,
                CHILD2_ZSERIO_NAME);
        child2.setValue(BlobInspectorValue(CHILD2_VALUE));
        child2.setZserioDescriptor(CHILD2_START_BIT_POSITION, CHILD2_END_BIT_POSITION);

        BlobInspectorNode& child3 = tree.createChild(BlobInspectorNode::NT_ARRAY, CHILD3_ZSERIO_TYPE_NAME,
                CHILD3_ZSERIO_NAME);
        child3.setZserioDescriptor(CHILD3_START_BIT_POSITION, CHILD3_END_BIT_POSITION);
        BlobInspectorNode& childOfChild3 = child3.createChild(BlobInspectorNode::NT_VALUE,
                CHILD_OF_CHILD3_ZSERIO_TYPE_NAME, CHILD_OF_CHILD3_ZSERIO_NAME);
        childOfChild3.setValue(BlobInspectorValue(CHILD_OF_CHILD3_VALUE));
        childOfChild3.setZserioDescriptor(CHILD_OF_CHILD3_START_BIT_POSITION, CHILD_OF_CHILD3_END_BIT_POSITION);
    }

    void checkTree(const BlobInspectorTree& tree)
    {
        // tree
        BlobInspectorNode::ZserioDescriptor treeDescriptor(TREE_START_BIT_POSITION, TREE_END_BIT_POSITION);
        Container<BlobInspectorNode::ZserioFunction> treeFunctions;
        treeFunctions.push_back(BlobInspectorNode::ZserioFunction(TREE_FUNC_ZSERIO_RETURN_TYPE_NAME,
                TREE_FUNC_ZSERIO_FUNCTION_NAME, BlobInspectorValue(TREE_FUNC_VALUE)));
        checkBlobInspectorContainerNode(tree, TREE_ZSERIO_TYPE_NAME, TREE_ZSERIO_NAME, 3, treeDescriptor,
                treeFunctions);

        const Container<BlobInspectorNode>& children = tree.getChildren();

        // child1
        checkChild1(children[0]);

        // child2
        checkBlobInspectorValueNode(children[1], CHILD2_ZSERIO_TYPE_NAME, CHILD2_ZSERIO_NAME,
                BlobInspectorValue(CHILD2_VALUE),
                BlobInspectorNode::ZserioDescriptor(CHILD2_START_BIT_POSITION, CHILD2_END_BIT_POSITION));

        // child3
        checkChild3(children[2]);
    }

    void checkChild1(const BlobInspectorNode& node)
    {
        checkBlobInspectorContainerNode(node, CHILD1_ZSERIO_TYPE_NAME, CHILD1_ZSERIO_NAME, 1,
            BlobInspectorNode::ZserioDescriptor(CHILD1_START_BIT_POSITION, CHILD1_END_BIT_POSITION));
        const Container<BlobInspectorNode>& children = node.getChildren();

        // childOfChild1
        checkBlobInspectorValueNode(children[0], CHILD_OF_CHILD1_ZSERIO_TYPE_NAME, CHILD_OF_CHILD1_ZSERIO_NAME,
            BlobInspectorValue(CHILD_OF_CHILD1_VALUE),
            BlobInspectorNode::ZserioDescriptor(CHILD_OF_CHILD1_START_BIT_POSITION,
                CHILD_OF_CHILD1_END_BIT_POSITION));
    }

    void checkChild3(const BlobInspectorNode& node)
    {
        checkBlobInspectorArrayNode(node, CHILD3_ZSERIO_TYPE_NAME, CHILD3_ZSERIO_NAME, 1,
            BlobInspectorNode::ZserioDescriptor(CHILD3_START_BIT_POSITION, CHILD3_END_BIT_POSITION));
        const Container<BlobInspectorNode>& children = node.getChildren();

        // childOfChild3
        checkBlobInspectorValueNode(children[0], CHILD_OF_CHILD3_ZSERIO_TYPE_NAME, CHILD_OF_CHILD3_ZSERIO_NAME,
            BlobInspectorValue(CHILD_OF_CHILD3_VALUE),
            BlobInspectorNode::ZserioDescriptor(CHILD_OF_CHILD3_START_BIT_POSITION,
                CHILD_OF_CHILD3_END_BIT_POSITION));
    }

    void checkBlobInspectorNode(const BlobInspectorNode& node, const StringHolder& expectedZserioTypeName,
            const StringHolder& expectedZserioName, const BlobInspectorValue& expectedValue,
            size_t expectedNumChildren, const BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        ASSERT_EQ(expectedZserioTypeName, node.getZserioTypeName());
        ASSERT_EQ(expectedZserioName, node.getZserioName());
        ASSERT_EQ(expectedValue, node.getValue());
        ASSERT_EQ(expectedNumChildren, node.getChildren().size());
        ASSERT_EQ(expectedZserioDescriptor, node.getZserioDescriptor());
    }

    void checkBlobInspectorContainerNode(const BlobInspectorNode& node, const StringHolder& expectedZserioTypeName,
            const StringHolder& expectedZserioName, size_t expectedNumChildren,
            const BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        checkBlobInspectorContainerNode(node, expectedZserioTypeName, expectedZserioName, expectedNumChildren,
                    expectedZserioDescriptor, Container<BlobInspectorNode::ZserioFunction>());
    }

    void checkBlobInspectorContainerNode(const BlobInspectorNode& node, const StringHolder& expectedZserioTypeName,
            const StringHolder& expectedZserioName, size_t expectedNumChildren,
            const BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor,
            const Container<BlobInspectorNode::ZserioFunction>& expectedZserioFunctions)
    {
        ASSERT_EQ(BlobInspectorNode::NT_CONTAINER, node.getNodeType());
        checkBlobInspectorNode(node, expectedZserioTypeName, expectedZserioName, BlobInspectorValue(),
            expectedNumChildren, expectedZserioDescriptor);
        ASSERT_EQ(expectedZserioFunctions, node.getZserioFunctions());
    }

    void checkBlobInspectorValueNode(const BlobInspectorNode& node, const StringHolder& expectedZserioTypeName,
            const StringHolder& expectedZserioName, const BlobInspectorValue& expectedValue,
            const BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        ASSERT_EQ(BlobInspectorNode::NT_VALUE, node.getNodeType());
        checkBlobInspectorNode(node, expectedZserioTypeName, expectedZserioName, expectedValue, 0,
            expectedZserioDescriptor);
    }

    void checkBlobInspectorArrayNode(const BlobInspectorNode& node, const StringHolder& expectedZserioTypeName,
            const StringHolder& expectedZserioName, size_t expectedNumElements,
            const BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        ASSERT_EQ(BlobInspectorNode::NT_ARRAY, node.getNodeType());
        checkBlobInspectorNode(node, expectedZserioTypeName, expectedZserioName, BlobInspectorValue(),
            expectedNumElements, expectedZserioDescriptor);
    }

private:
    static const zserio::StringHolder TREE_ZSERIO_TYPE_NAME;
    static const zserio::StringHolder TREE_ZSERIO_NAME;
    static const size_t TREE_START_BIT_POSITION;
    static const size_t TREE_END_BIT_POSITION;
    static const zserio::StringHolder TREE_FUNC_ZSERIO_RETURN_TYPE_NAME;
    static const zserio::StringHolder TREE_FUNC_ZSERIO_FUNCTION_NAME;
    static const uint16_t TREE_FUNC_VALUE;

    static const zserio::StringHolder CHILD1_ZSERIO_TYPE_NAME;
    static const zserio::StringHolder CHILD1_ZSERIO_NAME;
    static const size_t CHILD1_START_BIT_POSITION;
    static const size_t CHILD1_END_BIT_POSITION;

    static const zserio::StringHolder CHILD_OF_CHILD1_ZSERIO_TYPE_NAME;
    static const zserio::StringHolder CHILD_OF_CHILD1_ZSERIO_NAME;
    static const size_t CHILD_OF_CHILD1_START_BIT_POSITION;
    static const size_t CHILD_OF_CHILD1_END_BIT_POSITION;
    static const uint8_t CHILD_OF_CHILD1_VALUE;

    static const zserio::StringHolder CHILD2_ZSERIO_TYPE_NAME;
    static const zserio::StringHolder CHILD2_ZSERIO_NAME;
    static const size_t CHILD2_START_BIT_POSITION;
    static const size_t CHILD2_END_BIT_POSITION;
    static const uint32_t CHILD2_VALUE;

    static const zserio::StringHolder CHILD3_ZSERIO_TYPE_NAME;
    static const zserio::StringHolder CHILD3_ZSERIO_NAME;
    static const size_t CHILD3_START_BIT_POSITION;
    static const size_t CHILD3_END_BIT_POSITION;

    static const zserio::StringHolder CHILD_OF_CHILD3_ZSERIO_TYPE_NAME;
    static const zserio::StringHolder CHILD_OF_CHILD3_ZSERIO_NAME;
    static const size_t CHILD_OF_CHILD3_START_BIT_POSITION;
    static const size_t CHILD_OF_CHILD3_END_BIT_POSITION;
    static const bool CHILD_OF_CHILD3_VALUE;
};

const zserio::StringHolder BlobInspectorTreeTest::TREE_ZSERIO_TYPE_NAME("TreeZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::TREE_ZSERIO_NAME("TreeZserioName");
const size_t BlobInspectorTreeTest::TREE_START_BIT_POSITION = 0;
const size_t BlobInspectorTreeTest::TREE_END_BIT_POSITION = 41;
const zserio::StringHolder BlobInspectorTreeTest::TREE_FUNC_ZSERIO_RETURN_TYPE_NAME("Child4ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::TREE_FUNC_ZSERIO_FUNCTION_NAME("Child4ZserioName");
const uint16_t BlobInspectorTreeTest::TREE_FUNC_VALUE = 0xDEAD;

const zserio::StringHolder BlobInspectorTreeTest::CHILD1_ZSERIO_TYPE_NAME("Child1ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::CHILD1_ZSERIO_NAME("Child1ZserioName");
const size_t BlobInspectorTreeTest::CHILD1_START_BIT_POSITION = 0;
const size_t BlobInspectorTreeTest::CHILD1_END_BIT_POSITION = 8;

const zserio::StringHolder BlobInspectorTreeTest::CHILD_OF_CHILD1_ZSERIO_TYPE_NAME("ChildOfChild1ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::CHILD_OF_CHILD1_ZSERIO_NAME("ChildOfChild1ZserioName");
const size_t BlobInspectorTreeTest::CHILD_OF_CHILD1_START_BIT_POSITION = 0;
const size_t BlobInspectorTreeTest::CHILD_OF_CHILD1_END_BIT_POSITION = 8;
const uint8_t BlobInspectorTreeTest::CHILD_OF_CHILD1_VALUE = 0xAB;

const zserio::StringHolder BlobInspectorTreeTest::CHILD2_ZSERIO_TYPE_NAME("Child2ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::CHILD2_ZSERIO_NAME("Child2ZserioName");
const size_t BlobInspectorTreeTest::CHILD2_START_BIT_POSITION = 8;
const size_t BlobInspectorTreeTest::CHILD2_END_BIT_POSITION = 40;
const uint32_t BlobInspectorTreeTest::CHILD2_VALUE = 0xABCDDCBA;

const zserio::StringHolder BlobInspectorTreeTest::CHILD3_ZSERIO_TYPE_NAME("Child3ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::CHILD3_ZSERIO_NAME("Child3ZserioName");
const size_t BlobInspectorTreeTest::CHILD3_START_BIT_POSITION = 40;
const size_t BlobInspectorTreeTest::CHILD3_END_BIT_POSITION = 41;

const zserio::StringHolder BlobInspectorTreeTest::CHILD_OF_CHILD3_ZSERIO_TYPE_NAME("ChildOfChild3ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeTest::CHILD_OF_CHILD3_ZSERIO_NAME("ChildOfChild3ZserioName");
const size_t BlobInspectorTreeTest::CHILD_OF_CHILD3_START_BIT_POSITION = 40;
const size_t BlobInspectorTreeTest::CHILD_OF_CHILD3_END_BIT_POSITION = 41;
const bool BlobInspectorTreeTest::CHILD_OF_CHILD3_VALUE = true;

TEST_F(BlobInspectorTreeTest, CheckSimpleTree)
{
    BlobInspectorTree tree;
    fillTree(tree);
    checkTree(tree);
}

} // namespace zserio
