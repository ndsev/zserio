#include "zserio/CppRuntimeException.h"

#include "zserio/inspector/BlobInspectorTreeUtil.h"

#include "gtest/gtest.h"

namespace zserio
{

class BlobInspectorTreeUtilTest : public ::testing::Test
{
protected:
    void fillTree(BlobInspectorTree& tree)
    {
        tree.setZserioTypeName(TREE_ZSERIO_TYPE_NAME);
        tree.setZserioName(TREE_ZSERIO_NAME);
        tree.setZserioDescriptor(BlobInspectorNode::ZserioDescriptor(TREE_START_BIT_POSITION, TREE_END_BIT_POSITION));
        BlobInspectorNode::ZserioFunction& zserioFunction = tree.createZserioFunction(TREE_FUNC_ZSERIO_RETURN_TYPE_NAME,
                TREE_FUNC_ZSERIO_FUNCTION_NAME);
        zserioFunction.returnValue.set(TREE_FUNC_VALUE);

        BlobInspectorNode& child1 = tree.createChild(BlobInspectorNode::NT_CONTAINER, CHILD1_ZSERIO_TYPE_NAME,
                CHILD1_ZSERIO_NAME);
        child1.setZserioDescriptor(CHILD1_START_BIT_POSITION, CHILD1_END_BIT_POSITION);
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

const zserio::StringHolder BlobInspectorTreeUtilTest::TREE_ZSERIO_TYPE_NAME("TreeZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::TREE_ZSERIO_NAME("TreeZserioName");
const size_t BlobInspectorTreeUtilTest::TREE_START_BIT_POSITION = 0;
const size_t BlobInspectorTreeUtilTest::TREE_END_BIT_POSITION = 41;
const zserio::StringHolder BlobInspectorTreeUtilTest::TREE_FUNC_ZSERIO_RETURN_TYPE_NAME("FunctionZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::TREE_FUNC_ZSERIO_FUNCTION_NAME("FunctionZserioName");
const uint16_t BlobInspectorTreeUtilTest::TREE_FUNC_VALUE = 0xDEAD;

const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD1_ZSERIO_TYPE_NAME("Child1ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD1_ZSERIO_NAME("Child1ZserioName");
const size_t BlobInspectorTreeUtilTest::CHILD1_START_BIT_POSITION = 0;
const size_t BlobInspectorTreeUtilTest::CHILD1_END_BIT_POSITION = 8;

const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD_OF_CHILD1_ZSERIO_TYPE_NAME(
        "ChildOfChild1ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD_OF_CHILD1_ZSERIO_NAME("ChildOfChild1ZserioName");
const size_t BlobInspectorTreeUtilTest::CHILD_OF_CHILD1_START_BIT_POSITION = 0;
const size_t BlobInspectorTreeUtilTest::CHILD_OF_CHILD1_END_BIT_POSITION = 8;
const uint8_t BlobInspectorTreeUtilTest::CHILD_OF_CHILD1_VALUE = 0xAB;

const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD2_ZSERIO_TYPE_NAME("Child2ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD2_ZSERIO_NAME("Child2ZserioName");
const size_t BlobInspectorTreeUtilTest::CHILD2_START_BIT_POSITION = 8;
const size_t BlobInspectorTreeUtilTest::CHILD2_END_BIT_POSITION = 40;
const uint32_t BlobInspectorTreeUtilTest::CHILD2_VALUE = 0xABCDDCBA;

const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD3_ZSERIO_TYPE_NAME("Child3ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD3_ZSERIO_NAME("Child3ZserioName");
const size_t BlobInspectorTreeUtilTest::CHILD3_START_BIT_POSITION = 40;
const size_t BlobInspectorTreeUtilTest::CHILD3_END_BIT_POSITION = 41;

const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD_OF_CHILD3_ZSERIO_TYPE_NAME("Child3ZserioTypeName");
const zserio::StringHolder BlobInspectorTreeUtilTest::CHILD_OF_CHILD3_ZSERIO_NAME("ChildOfChild3ZserioName");
const size_t BlobInspectorTreeUtilTest::CHILD_OF_CHILD3_START_BIT_POSITION = 40;
const size_t BlobInspectorTreeUtilTest::CHILD_OF_CHILD3_END_BIT_POSITION = 41;
const bool BlobInspectorTreeUtilTest::CHILD_OF_CHILD3_VALUE = true;

TEST_F(BlobInspectorTreeUtilTest, GetBlobInspectorNode_Unchecked)
{
    BlobInspectorTree tree;
    fillTree(tree);
    ASSERT_NO_THROW(getBlobInspectorNode(tree, 0));
    ASSERT_NO_THROW(getBlobInspectorNode(tree, 1));
    ASSERT_NO_THROW(getBlobInspectorNode(tree, 2));
}

TEST_F(BlobInspectorTreeUtilTest, GetBlobInspectorNode_ExpectedChild)
{
    BlobInspectorTree tree;
    fillTree(tree);
    ASSERT_NO_THROW(getBlobInspectorNode(tree, 0, BlobInspectorNode::NT_CONTAINER));
    ASSERT_NO_THROW(getBlobInspectorNode(tree, 1, BlobInspectorNode::NT_VALUE));
    ASSERT_NO_THROW(getBlobInspectorNode(tree, 2, BlobInspectorNode::NT_ARRAY));
}

TEST_F(BlobInspectorTreeUtilTest, GetBlobInspectorNode_UnExpectedChild)
{
    BlobInspectorTree tree;
    fillTree(tree);
    ASSERT_THROW(getBlobInspectorNode(tree, 3, BlobInspectorNode::NT_CONTAINER), CppRuntimeException);
    ASSERT_THROW(getBlobInspectorNode(tree, 2, BlobInspectorNode::NT_VALUE), CppRuntimeException);
    ASSERT_THROW(getBlobInspectorNode(tree, 1, BlobInspectorNode::NT_ARRAY), CppRuntimeException);
}

TEST_F(BlobInspectorTreeUtilTest, GetBlobInspectorNode_WrongIndexedChild)
{
    BlobInspectorTree tree;
    fillTree(tree);
    ASSERT_THROW(getBlobInspectorNode(tree, 4, BlobInspectorNode::NT_CONTAINER), CppRuntimeException);
}

TEST_F(BlobInspectorTreeUtilTest, FindBlobInspectorNodes_NoRecursion_Expected)
{
    BlobInspectorTree tree;
    fillTree(tree);
    Container<BlobInspectorNode> searchedNodes;
    findBlobInspectorNodes(tree, false, CHILD2_ZSERIO_TYPE_NAME.get(), CHILD2_ZSERIO_NAME.get(), searchedNodes);

    ASSERT_EQ(1, searchedNodes.size());
    ASSERT_EQ(CHILD2_ZSERIO_TYPE_NAME, searchedNodes[0].getZserioTypeName());
    ASSERT_EQ(CHILD2_ZSERIO_NAME, searchedNodes[0].getZserioName());
}

TEST_F(BlobInspectorTreeUtilTest, FindBlobInspectorNodes_NoRecursion_Unexpected)
{
    BlobInspectorTree tree;
    fillTree(tree);
    Container<BlobInspectorNode> searchedNodes;
    findBlobInspectorNodes(tree, false, CHILD_OF_CHILD1_ZSERIO_TYPE_NAME.get(), CHILD_OF_CHILD1_ZSERIO_NAME.get(),
            searchedNodes);

    ASSERT_EQ(0, searchedNodes.size());
}

TEST_F(BlobInspectorTreeUtilTest, FindBlobInspectorNodes_Recursion_Expected)
{
    BlobInspectorTree tree;
    fillTree(tree);
    Container<BlobInspectorNode> searchedNodes;
    findBlobInspectorNodes(tree, true, CHILD_OF_CHILD1_ZSERIO_TYPE_NAME.get(), CHILD_OF_CHILD1_ZSERIO_NAME.get(),
            searchedNodes);

    ASSERT_EQ(1, searchedNodes.size());
    ASSERT_EQ(CHILD_OF_CHILD1_ZSERIO_TYPE_NAME, searchedNodes[0].getZserioTypeName());
    ASSERT_EQ(CHILD_OF_CHILD1_ZSERIO_NAME, searchedNodes[0].getZserioName());
}

TEST_F(BlobInspectorTreeUtilTest, FindBlobInspectorNodes_Recursion_Expected_Multiple)
{
    BlobInspectorTree tree;
    fillTree(tree);
    Container<BlobInspectorNode> searchedNodes;
    findBlobInspectorNodes(tree, true, CHILD3_ZSERIO_TYPE_NAME.get(), "", searchedNodes);

    ASSERT_EQ(2, searchedNodes.size());
    ASSERT_EQ(CHILD3_ZSERIO_TYPE_NAME, searchedNodes[0].getZserioTypeName());
    ASSERT_EQ(CHILD3_ZSERIO_NAME, searchedNodes[0].getZserioName());
    ASSERT_EQ(CHILD3_ZSERIO_TYPE_NAME, searchedNodes[1].getZserioTypeName());
    ASSERT_EQ(CHILD_OF_CHILD3_ZSERIO_NAME, searchedNodes[1].getZserioName());
}

TEST_F(BlobInspectorTreeUtilTest, FindBlobInspectorNodes_Recursion_Unexpected)
{
    BlobInspectorTree tree;
    fillTree(tree);
    Container<BlobInspectorNode> searchedNodes;
    findBlobInspectorNodes(tree, true, "ChildOfChild2ZserioTypeName", "ChildOfChild2ZserioName", searchedNodes);

    ASSERT_EQ(0, searchedNodes.size());
}

} // namespace zserio
