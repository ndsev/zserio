#include "gtest/gtest.h"

#include "functions/structure_extern/TestStructure.h"

#include "zserio/RebindAlloc.h"

namespace functions
{
namespace structure_extern
{

using allocator_type = TestStructure::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;
using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class StructureExternTest : public ::testing::Test
{
protected:
    static const BitBuffer FIELD;
    static const BitBuffer CHILD_FIELD;
};

const BitBuffer StructureExternTest::FIELD = BitBuffer{vector_type<uint8_t>{{0xAB, 0xE0}}, 11};
const BitBuffer StructureExternTest::CHILD_FIELD =
        BitBuffer{vector_type<uint8_t>{{0xCA, 0xFE}}, 15};

TEST_F(StructureExternTest, getField)
{
    TestStructure testStructure = TestStructure{FIELD, Child{CHILD_FIELD}};
    ASSERT_EQ(FIELD, testStructure.funcGetField());

    // check that non-const getter works
    testStructure.funcGetField().getBuffer()[0] = 0x00;
    ASSERT_FALSE(FIELD == testStructure.funcGetField());
    ASSERT_EQ(0x00, testStructure.funcGetField().getBuffer()[0]);
}

TEST_F(StructureExternTest, getChildField)
{
    TestStructure testStructure = TestStructure{FIELD, Child{CHILD_FIELD}};
    ASSERT_EQ(CHILD_FIELD, testStructure.funcGetChildField());

    // check that non-const getter works
    testStructure.funcGetChildField().getBuffer()[0] = 0xEF;
    ASSERT_FALSE(CHILD_FIELD == testStructure.funcGetChildField());
    ASSERT_EQ(0xEF, testStructure.funcGetChildField().getBuffer()[0]);
}

} // namespace structure_extern
} // namespace functions
