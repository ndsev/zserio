#include "gtest/gtest.h"

#include "company/appl/SimpleStructure.h"

namespace company
{
namespace appl
{
namespace simple_structure
{

class SetTopLevelPackageTest : public ::testing::Test
{
protected:
    static const size_t SIMPLE_STRUCTURE_BIT_SIZE;
};

const size_t SetTopLevelPackageTest::SIMPLE_STRUCTURE_BIT_SIZE = 32;

TEST_F(SetTopLevelPackageTest, emptyConstructor)
{
    {
        SimpleStructure simpleStructure;
        simpleStructure.initializeChildren();
        ASSERT_EQ(SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf());
    }

    {
        SimpleStructure simpleStructure = {};
        simpleStructure.initializeChildren();
        ASSERT_EQ(SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf());
    }
}

TEST_F(SetTopLevelPackageTest, fieldConstructor)
{
    SimpleStructure simpleStructure2 = SimpleStructure({}, {}, {}, {}, {}, {});
    simpleStructure2.initializeChildren();
    ASSERT_EQ(SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure2.bitSizeOf());
}

} // namespace simple_structure
} // namespace appl
} // namespace company
