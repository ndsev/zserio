#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "structure_types/empty_structure/EmptyStructure.h"

namespace structure_types
{
namespace empty_structure
{

TEST(EmptyStructureTest, emptyConstructor)
{
    EmptyStructure emptyStructure;
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
}

TEST(EmptyStructureTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(NULL, 0);

    EmptyStructure emptyStructure(reader);
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
}

TEST(EmptyStructureTest, copyConstructor)
{
    EmptyStructure emptyStructure;
    EmptyStructure emptyStructureCopy(emptyStructure);
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
    ASSERT_EQ(0, emptyStructureCopy.bitSizeOf());
}

TEST(EmptyStructureTest, assignmentOperator)
{
    EmptyStructure emptyStructure;
    EmptyStructure emptyStructureCopy;
    emptyStructureCopy = emptyStructure;
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
    ASSERT_EQ(0, emptyStructureCopy.bitSizeOf());
}

TEST(EmptyStructureTest, moveConstructor)
{
    EmptyStructure emptyStructure;
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
    EmptyStructure emptyStructureMoved(std::move(emptyStructure));
    ASSERT_EQ(0, emptyStructureMoved.bitSizeOf());
}

TEST(EmptyStructureTest, moveAssignmentOperator)
{
    EmptyStructure emptyStructure;
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
    EmptyStructure emptyStructureMoved;
    emptyStructureMoved = std::move(emptyStructure);
    ASSERT_EQ(0, emptyStructureMoved.bitSizeOf());
}

TEST(EmptyStructureTest, bitSizeOf)
{
    EmptyStructure emptyStructure;
    const size_t bitPosition = 1;
    ASSERT_EQ(0, emptyStructure.bitSizeOf(bitPosition));
}

TEST(EmptyStructureTest, initializeOffsets)
{
    EmptyStructure emptyStructure;
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition, emptyStructure.initializeOffsets(bitPosition));
}

TEST(EmptyStructureTest, operatorEquality)
{
    EmptyStructure emptyStructure1;
    EmptyStructure emptyStructure2;
    ASSERT_TRUE(emptyStructure1 == emptyStructure2);
}

TEST(EmptyStructureTest, hashCode)
{
    EmptyStructure emptyStructure1;
    EmptyStructure emptyStructure2;
    ASSERT_EQ(emptyStructure1.hashCode(), emptyStructure2.hashCode());
}

TEST(EmptyStructureTest, read)
{
    zserio::BitStreamReader reader(NULL, 0);
    const EmptyStructure emptyStructure(reader);
    ASSERT_EQ(0, emptyStructure.bitSizeOf());
}

TEST(EmptyStructureTest, write)
{
    EmptyStructure emptyStructure;

    zserio::BitStreamWriter writer;
    emptyStructure.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    ASSERT_EQ(0, writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    EmptyStructure readEmptyStructure(reader);
    ASSERT_TRUE(emptyStructure == readEmptyStructure);
}

} // namespace empty_structure
} // namespace structure_types
