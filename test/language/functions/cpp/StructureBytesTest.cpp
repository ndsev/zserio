#include "gtest/gtest.h"

#include "functions/structure_bytes/TestStructure.h"

#include "zserio/Vector.h"

namespace functions
{
namespace structure_bytes
{

using allocator_type = TestStructure::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class StructureBytesTest : public ::testing::Test
{
protected:
    static const vector_type<uint8_t> FIELD;
    static const vector_type<uint8_t> CHILD_FIELD;
};

const vector_type<uint8_t> StructureBytesTest::FIELD = vector_type<uint8_t>{{0xAB, 0xE0}};
const vector_type<uint8_t> StructureBytesTest::CHILD_FIELD = vector_type<uint8_t>{{0xCA, 0xFE}};

TEST_F(StructureBytesTest, getField)
{
    TestStructure testStructure = TestStructure{FIELD, Child{CHILD_FIELD}};
    ASSERT_EQ(FIELD, testStructure.funcGetField());

    // check that non-const getter works
    testStructure.funcGetField()[0] = 0x00;
    ASSERT_FALSE(FIELD == testStructure.funcGetField());
    ASSERT_EQ(0x00, testStructure.funcGetField()[0]);
}

TEST_F(StructureBytesTest, getChildField)
{
    TestStructure testStructure = TestStructure{FIELD, Child{CHILD_FIELD}};
    ASSERT_EQ(CHILD_FIELD, testStructure.funcGetChildField());

    // check that non-const getter works
    testStructure.funcGetChildField()[0] = 0xEF;
    ASSERT_FALSE(CHILD_FIELD == testStructure.funcGetChildField());
    ASSERT_EQ(0xEF, testStructure.funcGetChildField()[0]);
}

} // namespace structure_bytes
} // namespace functions
