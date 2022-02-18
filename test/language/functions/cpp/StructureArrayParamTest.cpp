#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "functions/structure_array_param/ChildStructure.h"
#include "functions/structure_array_param/ParentStructure.h"

namespace functions
{
namespace structure_array_param
{

class StructureArrayParamTest : public ::testing::Test
{
protected:
    void writeParentStructureToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(NUM_CHILDREN, 8);

        for (uint8_t i = 0; i < NUM_CHILDREN; ++i)
            writer.writeBits(static_cast<uint32_t>(VALUES[i]), CHILD_BIT_SIZE);

        writer.writeBits(NUM_ANOTHER_CHILDREN, 8);

        for (uint8_t i = 0; i < NUM_ANOTHER_CHILDREN; ++i)
            writer.writeBits(static_cast<uint32_t>(ANOTHER_VALUES[i]), ANOTHER_CHILD_BIT_SIZE);
    }

    void createParentStructure(ParentStructure& parentStructure)
    {
        parentStructure.setNumChildren(NUM_CHILDREN);

        auto& children = parentStructure.getChildren();
        for (uint8_t i = 0; i < NUM_CHILDREN; ++i)
        {
            ChildStructure child;
            child.setValue(VALUES[i]);
            children.push_back(child);
        }

        parentStructure.setNumAnotherChildren(NUM_ANOTHER_CHILDREN);

        auto& anotherChildren = parentStructure.getAnotherChildren();
        for (uint8_t i = 0; i < NUM_ANOTHER_CHILDREN; ++i)
        {
            ChildStructure child;
            child.setValue(ANOTHER_VALUES[i]);
            anotherChildren.push_back(child);
        }

        parentStructure.initializeChildren();
    }

protected:
    static const uint8_t CHILD_BIT_SIZE = 19;
    static const uint8_t ANOTHER_CHILD_BIT_SIZE = 17;

private:
    static const uint8_t NUM_CHILDREN = 2;
    static const uint64_t VALUES[NUM_CHILDREN];
    static const uint8_t NUM_ANOTHER_CHILDREN = 2;
    static const uint64_t ANOTHER_VALUES[NUM_ANOTHER_CHILDREN];
};

const uint64_t StructureArrayParamTest::VALUES[StructureArrayParamTest::NUM_CHILDREN] =
{
    0xAABB, 0xCCDD
};

const uint64_t StructureArrayParamTest::ANOTHER_VALUES[
        StructureArrayParamTest::NUM_ANOTHER_CHILDREN] =
{
    0xAABB, 0xCCDD
};

TEST_F(StructureArrayParamTest, checkParentStructure)
{
    ParentStructure parentStructure;
    createParentStructure(parentStructure);
    const uint8_t expectedChildBitSize = CHILD_BIT_SIZE;
    ASSERT_EQ(expectedChildBitSize, parentStructure.funcGetChildBitSize());
    const uint8_t expectedAnotherChildBitSize = ANOTHER_CHILD_BIT_SIZE;
    ASSERT_EQ(expectedAnotherChildBitSize, parentStructure.getNotLeftMost().funcGetAnotherChildBitSize());

    zserio::BitBuffer writtenBitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writtenWriter(writtenBitBuffer);
    parentStructure.write(writtenWriter);

    zserio::BitBuffer expectedBitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter expectedWriter(expectedBitBuffer);
    writeParentStructureToByteArray(expectedWriter);

    ASSERT_EQ(expectedBitBuffer, writtenBitBuffer);

    zserio::BitStreamReader reader(writtenBitBuffer);
    const ParentStructure readParentStructure(reader);
    ASSERT_EQ(parentStructure, readParentStructure);
}

} // namespace structure_array_param
} // namespace functions
