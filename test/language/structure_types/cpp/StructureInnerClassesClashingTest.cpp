#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "structure_types/structure_inner_classes_clashing/ArrayType_array.h"
#include "structure_types/structure_inner_classes_clashing/OffsetChecker_array.h"
#include "structure_types/structure_inner_classes_clashing/OffsetInitializer_array.h"

namespace structure_types
{
namespace structure_inner_classes_clashing
{

using allocator_type = ArrayType_array::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class StructureInnerClassesClashingTest : public ::testing::Test
{
protected:
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(StructureInnerClassesClashingTest, writeReadArrayType)
{
    ArrayType_array testStructure{vector_type<uint32_t>{1, 2, 3, 4}};

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition());
    ArrayType_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

TEST_F(StructureInnerClassesClashingTest, writeReadOffsetChecker)
{
    OffsetChecker_array testStructure;
    testStructure.setArray(vector_type<uint32_t>{1, 2, 3, 4});
    testStructure.getOffsets().resize(testStructure.getArray().size());

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition());
    OffsetChecker_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

TEST_F(StructureInnerClassesClashingTest, writeReadOffsetInitializer)
{
    OffsetInitializer_array testStructure;
    testStructure.setArray(vector_type<uint32_t>{1, 2, 3, 4});
    testStructure.getOffsets().resize(testStructure.getArray().size());

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition());
    OffsetInitializer_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

} // namespace structure_inner_classes_clashing
} // namespace structure_types