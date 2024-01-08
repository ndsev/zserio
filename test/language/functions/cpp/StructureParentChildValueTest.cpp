#include <vector>

#include "functions/structure_parent_child_value/ParentValue.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace functions
{
namespace structure_parent_child_value
{

class StructureParentChildValueTest : public ::testing::Test
{
protected:
    void writeParentValueToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(CHILD_VALUE, 32);
    }

    void createParentValue(ParentValue& parentValue)
    {
        ChildValue& childValue = parentValue.getChildValue();
        childValue.setVal(CHILD_VALUE);
        parentValue.setChildValue(childValue);
    }

    static const uint32_t CHILD_VALUE;
};

const uint32_t StructureParentChildValueTest::CHILD_VALUE = 0xABCD;

TEST_F(StructureParentChildValueTest, checkParentValue)
{
    ParentValue parentValue;
    createParentValue(parentValue);
    ASSERT_EQ(CHILD_VALUE, parentValue.funcGetValue());

    zserio::BitBuffer writtenBitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writtenWriter(writtenBitBuffer);
    parentValue.write(writtenWriter);

    zserio::BitBuffer expectedBitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter expectedWriter(expectedBitBuffer);
    writeParentValueToByteArray(expectedWriter);

    ASSERT_EQ(expectedBitBuffer, writtenBitBuffer);

    zserio::BitStreamReader reader(writtenBitBuffer);
    const ParentValue readParentValue(reader);
    ASSERT_EQ(parentValue, readParentValue);
}

} // namespace structure_parent_child_value
} // namespace functions
