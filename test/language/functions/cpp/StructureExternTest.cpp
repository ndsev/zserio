#include "gtest/gtest.h"

#include "functions/structure_extern/TestStructure.h"

namespace functions
{
namespace structure_extern
{

class StructureExternTest : public ::testing::Test
{
protected:
    static const zserio::BitBuffer FIELD;
    static const zserio::BitBuffer CHILD_FIELD;
};

const zserio::BitBuffer StructureExternTest::FIELD = zserio::BitBuffer{std::vector<uint8_t>{{0xAB, 0xE0}}, 11};
const zserio::BitBuffer StructureExternTest::CHILD_FIELD =
        zserio::BitBuffer{std::vector<uint8_t>{{0xCA, 0xFE}}, 15};

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
