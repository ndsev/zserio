#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/ObjectArray.h"

#include "functions/structure_parent_child_value/ParentValue.h"

namespace functions
{
namespace structure_parent_child_value
{

class FunctionsStructureParentChildValueTest : public ::testing::Test
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

const uint32_t FunctionsStructureParentChildValueTest::CHILD_VALUE = 0xABCD;

TEST_F(FunctionsStructureParentChildValueTest, checkParentValue)
{
    ParentValue parentValue;
    createParentValue(parentValue);
    ASSERT_EQ(CHILD_VALUE, parentValue.funcGetValue());

    zserio::BitStreamWriter writtenWriter;
    parentValue.write(writtenWriter);
    size_t writtenWriterBufferByteSize;
    const uint8_t* writtenWriterBuffer = writtenWriter.getWriteBuffer(writtenWriterBufferByteSize);

    zserio::BitStreamWriter expectedWriter;
    writeParentValueToByteArray(expectedWriter);
    size_t expectedWriterBufferByteSize;
    const uint8_t* expectedWriterBuffer = expectedWriter.getWriteBuffer(expectedWriterBufferByteSize);

    std::vector<uint8_t> writtenWriterVector(writtenWriterBuffer,
                                             writtenWriterBuffer + writtenWriterBufferByteSize);
    std::vector<uint8_t> expectedWriterVector(expectedWriterBuffer,
                                              expectedWriterBuffer + expectedWriterBufferByteSize);
    ASSERT_EQ(expectedWriterVector, writtenWriterVector);

    zserio::BitStreamReader reader(writtenWriterBuffer, writtenWriterBufferByteSize);
    const ParentValue readParentValue(reader);
    ASSERT_EQ(parentValue, readParentValue);
}

} // namespace structure_parent_child_value
} // namespace functions
