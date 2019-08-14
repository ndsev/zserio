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

class FunctionsStructureArrayParamTest : public ::testing::Test
{
protected:
    void writeParentStructureToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(NUM_CHILDREN, 8);

        for (uint8_t i = 0; i < NUM_CHILDREN; ++i)
            writer.writeBits(static_cast<uint32_t>(VALUES[i]), CHILD_BIT_SIZE);
    }

    void createParentStructure(ParentStructure& parentStructure)
    {
        parentStructure.setNumChildren(NUM_CHILDREN);

        std::vector<ChildStructure>& children = parentStructure.getChildren();
        for (uint8_t i = 0; i < NUM_CHILDREN; ++i)
        {
            ChildStructure child;
            child.setValue(VALUES[i]);
            children.push_back(child);
        }
    }

protected:
    static const uint8_t    CHILD_BIT_SIZE = 19;

private:
    static const uint8_t    NUM_CHILDREN = 2;
    static const uint64_t   VALUES[NUM_CHILDREN];
};

const uint64_t FunctionsStructureArrayParamTest::VALUES[FunctionsStructureArrayParamTest::NUM_CHILDREN] =
{
    0xAABB, 0xCCDD
};

TEST_F(FunctionsStructureArrayParamTest, checkParentStructure)
{
    ParentStructure parentStructure;
    createParentStructure(parentStructure);
    const uint8_t expectedChildBitSize = CHILD_BIT_SIZE;
    ASSERT_EQ(expectedChildBitSize, parentStructure.funcGetChildBitSize());

    zserio::BitStreamWriter writtenWriter;
    parentStructure.write(writtenWriter);
    size_t writtenWriterBufferByteSize;
    const uint8_t* writtenWriterBuffer = writtenWriter.getWriteBuffer(writtenWriterBufferByteSize);

    zserio::BitStreamWriter expectedWriter;
    writeParentStructureToByteArray(expectedWriter);
    size_t expectedWriterBufferByteSize;
    const uint8_t* expectedWriterBuffer = expectedWriter.getWriteBuffer(expectedWriterBufferByteSize);

    std::vector<uint8_t> writtenWriterVector(writtenWriterBuffer,
                                             writtenWriterBuffer + writtenWriterBufferByteSize);
    std::vector<uint8_t> expectedWriterVector(expectedWriterBuffer,
                                              expectedWriterBuffer + expectedWriterBufferByteSize);
    ASSERT_EQ(expectedWriterVector, writtenWriterVector);

    zserio::BitStreamReader reader(writtenWriterBuffer, writtenWriterBufferByteSize);
    const ParentStructure readParentStructure(reader);
    ASSERT_EQ(parentStructure, readParentStructure);
}

} // namespace structure_array_param
} // namespace functions
