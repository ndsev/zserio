#include <cstdio>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "with_inspector_code/parameterized_structure/ParameterizedStructureDatabase.h"

namespace with_inspector_code
{
namespace parameterized_structure
{

class ParameterizedStructureTest : public ::testing::Test
{
protected:
    void fillRootStructure(RootStructure& rootStructure)
    {
        rootStructure.setName(ROOT_STRUCTURE_NAME);
        rootStructure.setSimpleEnum(SimpleEnum::EnumBar);

        const uint16_t id = rootStructure.getId();
        if (id == ROOT_STRUCTURE_ID_OPTIONAL)
        {
            rootStructure.setExtra(ROOT_STRUCTURE_EXTRA);
            rootStructure.setAutoOptional(ROOT_STRUCTURE_AUTO_OPTIONAL);
        }

        fillTestAlign(rootStructure.getTestAlign());
        fillTestArray(rootStructure.getTestArray());
        fillTestUnion(rootStructure.getTestUnion(), rootStructure.getRootArg());
        fillTestChoice(rootStructure.getTestChoice());
    }

    void fillTestAlign(TestAlign& testAlign)
    {
        testAlign.setAligned32(TEST_ALIGN_ALIGNED32);
        testAlign.setOffsetField(TEST_ALIGN_OFFSET_FIELD);
    }

    void fillTestArray(TestArray& testArray)
    {
        testArray.setArraySize(TEST_ARRAY_SIZE);

        zserio::Int16Array& arrayInt16 = testArray.getArrayInt16();
        arrayInt16.reserve(TEST_ARRAY_SIZE);
        for (uint8_t i = 0; i < TEST_ARRAY_SIZE; ++i)
            arrayInt16.push_back(i);

        SimpleStructure simpleStructure;
        simpleStructure.setA(SIMPLE_STRUCTURE_A);
        simpleStructure.setB(SIMPLE_STRUCTURE_B);
        zserio::ObjectArray<SimpleStructure>& arraySimpleStructure = testArray.getArraySimpleStructure();
        arraySimpleStructure.reserve(TEST_ARRAY_SIZE);
        for (uint8_t i = 0; i < TEST_ARRAY_SIZE; ++i)
            arraySimpleStructure.push_back(simpleStructure);

        const SimpleEnum simpleEnum(SimpleEnum::EnumBaz);
        zserio::ObjectArray<SimpleEnum>& arrayEnum = testArray.getArrayEnum();
        arrayEnum.reserve(TEST_ARRAY_SIZE);
        for (uint8_t i = 0; i < TEST_ARRAY_SIZE; ++i)
            arrayEnum.push_back(simpleEnum);
    }

    void fillTestUnion(TestUnion& testUnion, uint8_t arg)
    {
        if (arg == 0)
            testUnion.setArg8(TEST_UNION_ARG8);
        else
            testUnion.setArg16(TEST_UNION_ARG16);
    }

    void fillTestChoice(TestChoice& testChoice)
    {
        const uint8_t arg = testChoice.getArg();
        if (arg == 0)
            testChoice.setArg8(TEST_CHOICE_ARG8);
        else
            testChoice.setArg16(TEST_CHOICE_ARG16);
    }

    class RootStructureInspectorParameterProvider : public IInspectorParameterProvider
    {
    public:
        explicit RootStructureInspectorParameterProvider(uint16_t rootStructureId) :
                m_rootStructureId(rootStructureId)
        {
        }

        virtual uint8_t getTestTable_rootArg()
        {
            return ROOT_STRUCTURE_ROOT_ARG;
        }

        virtual uint16_t getTestTable_rootStructure_id()
        {
            return m_rootStructureId;
        }

        virtual uint16_t getTestTable_extraRootStructure_id()
        {
            return m_rootStructureId;
        }

        virtual uint8_t getTestTable_extraRootStructure_rootArg()
        {
            return ROOT_STRUCTURE_ROOT_ARG;
        }

    private:
        uint16_t    m_rootStructureId;
    };

    void checkRootStructureTree(const zserio::BlobInspectorTree& tree)
    {
        size_t currentBitPosition = 0;

        // tree
        size_t bitSize = 168;
        size_t numChildren = 6;
        zserio::BlobInspectorNode::ZserioDescriptor treeDescriptor(currentBitPosition,
                currentBitPosition + bitSize);
        zserio::Container<zserio::BlobInspectorNode::ZserioFunction> treeFunctions;
        treeFunctions.push_back(zserio::BlobInspectorNode::ZserioFunction("bool", "isIdValid",
                zserio::BlobInspectorValue(ROOT_STRUCTURE_ID != 0)));
        checkBlobInspectorContainerNode(tree, "with_inspector_code.parameterized_structure.RootStructure()",
                "rootStructure", numChildren, treeDescriptor, treeFunctions);

        const zserio::Container<zserio::BlobInspectorNode>& children = tree.getChildren();

        // child1
        bitSize = 16;
        checkBlobInspectorValueNode(children[0], "string", "name",
                zserio::BlobInspectorValue(std::string(ROOT_STRUCTURE_NAME)),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        currentBitPosition += bitSize;

        // child2
        bitSize = 8;
        checkBlobInspectorValueNode(children[1], "with_inspector_code.parameterized_structure.SimpleEnum",
                "simpleEnum",
                zserio::BlobInspectorValue(SimpleEnum(SimpleEnum::EnumBar).getValue(),
                        SimpleEnum(SimpleEnum::EnumBar).toString()),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        currentBitPosition += bitSize;

        // child3
        bitSize = 32;
        checkTestAlign(children[2], currentBitPosition, bitSize);
        currentBitPosition += bitSize;

        // child4
        bitSize = 88;
        checkTestArray(children[3], currentBitPosition, bitSize);
        currentBitPosition += bitSize;

        // child5
        bitSize = 8 + 8;
        checkTestUnion(children[4], currentBitPosition, bitSize);
        currentBitPosition += bitSize;

        // child6
        bitSize = 8;
        checkTestChoice(children[5], currentBitPosition, bitSize);
    }

    void checkTestAlign(const zserio::BlobInspectorNode& node, size_t currentBitPosition, size_t bitSize)
    {
        // "with_inspector_code.TestAlign testAlign"
        size_t numChildren = 3;
        checkBlobInspectorContainerNode(node, "with_inspector_code.parameterized_structure.TestAlign",
                "testAlign", numChildren,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));

        const zserio::Container<zserio::BlobInspectorNode>& children = node.getChildren();

        // child1
        currentBitPosition += 8;
        bitSize = 8;
        checkBlobInspectorValueNode(children[0], "uint8", "aligned32",
                zserio::BlobInspectorValue(TEST_ALIGN_ALIGNED32),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        currentBitPosition += bitSize;

        // child2
        bitSize = 8;
        checkBlobInspectorValueNode(children[1], "uint8", "extraOffset",
                zserio::BlobInspectorValue(static_cast<uint8_t>((currentBitPosition + bitSize) / 8)),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        currentBitPosition += bitSize;

        // child3
        bitSize = 8;
        checkBlobInspectorValueNode(children[2], "uint8", "offsetField",
                zserio::BlobInspectorValue(TEST_ALIGN_OFFSET_FIELD),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
    }

    void checkTestArray(const zserio::BlobInspectorNode& node, size_t currentBitPosition, size_t bitSize)
    {
        // "with_inspector_code.TestArray testArray"
        size_t numChildren = 4;
        checkBlobInspectorContainerNode(node, "with_inspector_code.parameterized_structure.TestArray",
                "testArray", numChildren,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));

        const zserio::Container<zserio::BlobInspectorNode>& children = node.getChildren();

        // child1
        bitSize = 8;
        checkBlobInspectorValueNode(children[0], "uint8", "arraySize",
                zserio::BlobInspectorValue(TEST_ARRAY_SIZE),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        currentBitPosition += bitSize;

        // child2
        bitSize = 32;
        checkBlobInspectorArrayNode(children[1], "int16[]", "arrayInt16", TEST_ARRAY_SIZE,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        const zserio::Container<zserio::BlobInspectorNode>& child2Elements = children[1].getChildren();
        bitSize = 16;
        for (size_t i = 0; i < TEST_ARRAY_SIZE; ++i)
        {
            checkBlobInspectorValueNode(child2Elements[i], "int16", "arrayInt16",
                    zserio::BlobInspectorValue(static_cast<int16_t>(i)),
                    zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition,
                            currentBitPosition + bitSize));
            currentBitPosition += bitSize;
        }

        // child3
        bitSize = 32;
        checkBlobInspectorArrayNode(children[2],
                "with_inspector_code.parameterized_structure.SimpleStructure[]", "arraySimpleStructure",
                TEST_ARRAY_SIZE,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        const zserio::Container<zserio::BlobInspectorNode>& child3Elements = children[2].getChildren();
        for (size_t i = 0; i < TEST_ARRAY_SIZE; ++i)
        {
            bitSize = 16;
            checkBlobInspectorContainerNode(child3Elements[i],
                    "with_inspector_code.parameterized_structure.SimpleStructure", "arraySimpleStructure",
                    2, zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition,
                                currentBitPosition + bitSize));
            const zserio::Container<zserio::BlobInspectorNode>& simpleStructureChildren =
                    child3Elements[i].getChildren();

            bitSize = 8;
            checkBlobInspectorValueNode(simpleStructureChildren[0], "uint8", "a",
                    zserio::BlobInspectorValue(SIMPLE_STRUCTURE_A),
                    zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition,
                            currentBitPosition + bitSize));
            currentBitPosition += bitSize;

            checkBlobInspectorValueNode(simpleStructureChildren[1], "uint8", "b",
                    zserio::BlobInspectorValue(SIMPLE_STRUCTURE_B),
                    zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition,
                            currentBitPosition + bitSize));
            currentBitPosition += bitSize;
        }

        // child4
        bitSize = 16;
        checkBlobInspectorArrayNode(children[3], "with_inspector_code.parameterized_structure.SimpleEnum[]",
                "arrayEnum", TEST_ARRAY_SIZE,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        const zserio::Container<zserio::BlobInspectorNode>& child4Elements = children[3].getChildren();
        for (size_t i = 0; i < TEST_ARRAY_SIZE; ++i)
        {
            bitSize = 8;
            checkBlobInspectorContainerNode(child4Elements[i],
                    "with_inspector_code.parameterized_structure.SimpleEnum", "arrayEnum", 1,
                    zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition,
                            currentBitPosition + bitSize));
            const zserio::Container<zserio::BlobInspectorNode>& simpleEnumChildren =
                    child4Elements[i].getChildren();

            checkBlobInspectorValueNode(simpleEnumChildren[0],
                    "with_inspector_code.parameterized_structure.SimpleEnum", "SimpleEnum",
                    zserio::BlobInspectorValue(SimpleEnum(SimpleEnum::EnumBaz).getValue(),
                            SimpleEnum(SimpleEnum::EnumBaz).toString()),
                    zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition,
                            currentBitPosition + bitSize));
            currentBitPosition += bitSize;
        }
    }

    void checkTestUnion(const zserio::BlobInspectorNode& node, size_t currentBitPosition, size_t bitSize)
    {
        // "with_inspector_code.TestUnion testUnion"
        size_t numChildren = 1;
        checkBlobInspectorContainerNode(node, "with_inspector_code.parameterized_structure.TestUnion",
                "testUnion", numChildren,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
        currentBitPosition += 8; // choice tag

        const zserio::Container<zserio::BlobInspectorNode>& children = node.getChildren();

        // child1
        bitSize = 8;
        checkBlobInspectorValueNode(children[0], "uint8", "arg8",
                zserio::BlobInspectorValue(TEST_UNION_ARG8),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
    }

    void checkTestChoice(const zserio::BlobInspectorNode& node, size_t currentBitPosition, size_t bitSize)
    {
        // "with_inspector_code.TestChoice(rootArg) testChoice"
        size_t numChildren = 1;
        checkBlobInspectorContainerNode(node, "with_inspector_code.parameterized_structure.TestChoice()",
                "testChoice", numChildren,
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));

        const zserio::Container<zserio::BlobInspectorNode>& children = node.getChildren();

        // child1
        bitSize = 8;
        checkBlobInspectorValueNode(children[0], "uint8", "arg8",
                zserio::BlobInspectorValue(TEST_CHOICE_ARG8),
                zserio::BlobInspectorNode::ZserioDescriptor(currentBitPosition, currentBitPosition + bitSize));
    }

    void checkBlobInspectorNode(const zserio::BlobInspectorNode& node,
            const zserio::StringHolder& expectedZserioTypeName,
            const zserio::StringHolder& expectedZserioName,
            const zserio::BlobInspectorValue& expectedValue, size_t expectedNumChildren,
            const zserio::BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        ASSERT_EQ(expectedZserioTypeName, node.getZserioTypeName());
        ASSERT_EQ(expectedZserioName, node.getZserioName());
        ASSERT_EQ(expectedValue, node.getValue());
        ASSERT_EQ(expectedNumChildren, node.getChildren().size());
        ASSERT_EQ(expectedZserioDescriptor, node.getZserioDescriptor());
    }

    void checkBlobInspectorContainerNode(const zserio::BlobInspectorNode& node,
            const zserio::StringHolder& expectedZserioTypeName,
            const zserio::StringHolder& expectedZserioName,
            size_t expectedNumChildren,
            const zserio::BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        checkBlobInspectorContainerNode(node, expectedZserioTypeName, expectedZserioName, expectedNumChildren,
                    expectedZserioDescriptor, zserio::Container<zserio::BlobInspectorNode::ZserioFunction>());
    }

    void checkBlobInspectorContainerNode(const zserio::BlobInspectorNode& node,
            const zserio::StringHolder& expectedZserioTypeName,
            const zserio::StringHolder& expectedZserioName,
            size_t expectedNumChildren,
            const zserio::BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor,
            const zserio::Container<zserio::BlobInspectorNode::ZserioFunction>& expectedZserioFunctions)
    {
        ASSERT_EQ(zserio::BlobInspectorNode::NT_CONTAINER, node.getNodeType());
        checkBlobInspectorNode(node, expectedZserioTypeName, expectedZserioName, zserio::BlobInspectorValue(),
            expectedNumChildren, expectedZserioDescriptor);
        ASSERT_EQ(expectedZserioFunctions, node.getZserioFunctions());
    }

    void checkBlobInspectorValueNode(const zserio::BlobInspectorNode& node,
            const zserio::StringHolder& expectedZserioTypeName,
            const zserio::StringHolder& expectedZserioName,
            const zserio::BlobInspectorValue& expectedValue,
            const zserio::BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        ASSERT_EQ(zserio::BlobInspectorNode::NT_VALUE, node.getNodeType());
        checkBlobInspectorNode(node, expectedZserioTypeName, expectedZserioName, expectedValue, 0,
                expectedZserioDescriptor);
    }

    void checkBlobInspectorArrayNode(const zserio::BlobInspectorNode& node,
            const zserio::StringHolder& expectedZserioTypeName,
            const zserio::StringHolder& expectedZserioName,
            size_t expectedNumElements,
            const zserio::BlobInspectorNode::ZserioDescriptor& expectedZserioDescriptor)
    {
        ASSERT_EQ(zserio::BlobInspectorNode::NT_ARRAY, node.getNodeType());
        checkBlobInspectorNode(node, expectedZserioTypeName, expectedZserioName, zserio::BlobInspectorValue(),
                expectedNumElements, expectedZserioDescriptor);
    }

    static const uint16_t   ROOT_STRUCTURE_ID;
    static const uint16_t   ROOT_STRUCTURE_ID_OPTIONAL;
    static const uint8_t    ROOT_STRUCTURE_ROOT_ARG;
    static const char*      ROOT_STRUCTURE_NAME;
    static const uint8_t    ROOT_STRUCTURE_EXTRA;
    static const uint8_t    ROOT_STRUCTURE_AUTO_OPTIONAL;

    static const uint8_t    TEST_ALIGN_ALIGNED32;
    static const uint8_t    TEST_ALIGN_OFFSET_FIELD;

    static const uint8_t    TEST_ARRAY_SIZE;
    static const uint8_t    SIMPLE_STRUCTURE_A;
    static const uint8_t    SIMPLE_STRUCTURE_B;

    static const uint8_t    TEST_UNION_ARG8;
    static const uint16_t   TEST_UNION_ARG16;

    static const uint8_t    TEST_CHOICE_ARG8;
    static const uint16_t   TEST_CHOICE_ARG16;
};

const uint16_t   ParameterizedStructureTest::ROOT_STRUCTURE_ID = 0x01;
const uint16_t   ParameterizedStructureTest::ROOT_STRUCTURE_ID_OPTIONAL = 0x00;
const uint8_t    ParameterizedStructureTest::ROOT_STRUCTURE_ROOT_ARG = 0x00;
// This must be one character string because the expected length is hard-coded in checkRootStructureTree().
const char* ParameterizedStructureTest::ROOT_STRUCTURE_NAME = "X";
const uint8_t    ParameterizedStructureTest::ROOT_STRUCTURE_EXTRA = 0xAB;
const uint8_t    ParameterizedStructureTest::ROOT_STRUCTURE_AUTO_OPTIONAL = 0x34;

const uint8_t    ParameterizedStructureTest::TEST_ALIGN_ALIGNED32 = 0x04;
const uint8_t    ParameterizedStructureTest::TEST_ALIGN_OFFSET_FIELD = 0x3A;

const uint8_t    ParameterizedStructureTest::TEST_ARRAY_SIZE = 2;
const uint8_t    ParameterizedStructureTest::SIMPLE_STRUCTURE_A = 1;
const uint8_t    ParameterizedStructureTest::SIMPLE_STRUCTURE_B = 1;

const uint8_t    ParameterizedStructureTest::TEST_UNION_ARG8 = 10;
const uint16_t   ParameterizedStructureTest::TEST_UNION_ARG16 = 256;

const uint8_t    ParameterizedStructureTest::TEST_CHOICE_ARG8 = 9;
const uint16_t   ParameterizedStructureTest::TEST_CHOICE_ARG16 = 257;

TEST_F(ParameterizedStructureTest, convertBitStreamToBlobTree)
{
    RootStructure rootStructure;
    rootStructure.initialize(ROOT_STRUCTURE_ID, ROOT_STRUCTURE_ROOT_ARG);
    fillRootStructure(rootStructure);

    zserio::BitStreamWriter writer;
    rootStructure.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    RootStructureInspectorParameterProvider rootStructureInspectorParameterProvider(ROOT_STRUCTURE_ID);
    zserio::BlobInspectorTree tree;
    ParameterizedStructureDatabase parameterizedStructureDatabase;
    parameterizedStructureDatabase.convertBitStreamToBlobTree("testTable", "rootStructure", reader,
            rootStructureInspectorParameterProvider, tree);

    checkRootStructureTree(tree);
}

TEST_F(ParameterizedStructureTest, convertBlobTreeToBitStream)
{
    RootStructure rootStructure;
    rootStructure.initialize(ROOT_STRUCTURE_ID_OPTIONAL, ROOT_STRUCTURE_ROOT_ARG);
    fillRootStructure(rootStructure);

    zserio::BitStreamWriter writer;
    rootStructure.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    RootStructureInspectorParameterProvider rootStructureInspectorParameterProvider(ROOT_STRUCTURE_ID_OPTIONAL);
    zserio::BlobInspectorTree tree;
    const ParameterizedStructureDatabase parameterizedStructureDatabase;
    parameterizedStructureDatabase.convertBitStreamToBlobTree("testTable", "rootStructure", reader,
            rootStructureInspectorParameterProvider, tree);

    zserio::BitStreamWriter treeWriter;
    parameterizedStructureDatabase.convertBlobTreeToBitStream("testTable", "rootStructure", tree,
            rootStructureInspectorParameterProvider, treeWriter);

    size_t treeWriterBufferByteSize;
    const uint8_t* treeWriterBuffer = treeWriter.getWriteBuffer(treeWriterBufferByteSize);
    ASSERT_EQ(writerBufferByteSize, treeWriterBufferByteSize);
    for (size_t i = 0; i < treeWriterBufferByteSize; ++i)
        ASSERT_EQ(writerBuffer[i], treeWriterBuffer[i]);
}

TEST_F(ParameterizedStructureTest, doesBlobExist)
{
    const ParameterizedStructureDatabase parameterizedStructureDatabase;
    ASSERT_TRUE(parameterizedStructureDatabase.doesBlobExist("testTable", "rootStructure"));
    ASSERT_TRUE(parameterizedStructureDatabase.doesBlobExist("testTable", "extraRootStructure"));
    ASSERT_FALSE(parameterizedStructureDatabase.doesBlobExist("unknownTable", "rootStructure"));
    ASSERT_FALSE(parameterizedStructureDatabase.doesBlobExist("unknownTable", "extraRootStructure"));
    ASSERT_FALSE(parameterizedStructureDatabase.doesBlobExist("testTable", "unknownBlob"));
}

} // namespace parameterized_structure
} // namespace with_inspector_code
