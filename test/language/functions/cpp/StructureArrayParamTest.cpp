#include <array>
#include <vector>

#include "functions/structure_array_param/ChildStructure.h"
#include "functions/structure_array_param/ParentStructure.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace functions
{
namespace structure_array_param
{

class StructureArrayParamTest : public ::testing::Test
{
protected:
    void writeParentStructureToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(VALUES.size(), 8);

        for (uint64_t value : VALUES)
            writer.writeBits(static_cast<uint32_t>(value), CHILD_BIT_SIZE);

        writer.writeBits(ANOTHER_VALUES.size(), 8);

        for (uint64_t anotherValue : ANOTHER_VALUES)
            writer.writeBits(static_cast<uint32_t>(anotherValue), ANOTHER_CHILD_BIT_SIZE);
    }

    void createParentStructure(ParentStructure& parentStructure)
    {
        parentStructure.setNumChildren(VALUES.size());

        auto& children = parentStructure.getChildren();
        for (uint64_t value : VALUES)
        {
            ChildStructure child;
            child.setValue(value);
            children.push_back(child);
        }

        parentStructure.setNumAnotherChildren(ANOTHER_VALUES.size());

        auto& anotherChildren = parentStructure.getAnotherChildren();
        for (uint64_t anotherValue : ANOTHER_VALUES)
        {
            ChildStructure child;
            child.setValue(anotherValue);
            anotherChildren.push_back(child);
        }

        parentStructure.initializeChildren();
    }

    static const uint8_t CHILD_BIT_SIZE = 19;
    static const uint8_t ANOTHER_CHILD_BIT_SIZE = 17;

private:
    static const std::array<uint64_t, 2> VALUES;
    static const std::array<uint64_t, 2> ANOTHER_VALUES;
};

const std::array<uint64_t, 2> StructureArrayParamTest::VALUES = {0xAABB, 0xCCDD};

const std::array<uint64_t, 2> StructureArrayParamTest::ANOTHER_VALUES = {0xAABB, 0xCCDD};

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
