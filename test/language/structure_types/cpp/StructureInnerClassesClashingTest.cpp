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
using vector_type = zserio::vector<T, allocator_type>;

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

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ArrayType_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

TEST_F(StructureInnerClassesClashingTest, writeReadOffsetChecker)
{
    OffsetChecker_array testStructure;
    testStructure.setArray(vector_type<uint32_t>{1, 2, 3, 4});
    testStructure.getOffsets().resize(testStructure.getArray().size());
    testStructure.initializeOffsets();

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OffsetChecker_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

TEST_F(StructureInnerClassesClashingTest, writeReadOffsetInitializer)
{
    OffsetInitializer_array testStructure;
    testStructure.setArray(vector_type<uint32_t>{1, 2, 3, 4});
    testStructure.getOffsets().resize(testStructure.getArray().size());
    testStructure.initializeOffsets();

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OffsetInitializer_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

} // namespace structure_inner_classes_clashing
} // namespace structure_types
