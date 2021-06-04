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
    SimpleStructure simpleStructure;
    const size_t expectedBitSize = SIMPLE_STRUCTURE_BIT_SIZE;
    simpleStructure.initializeChildren();
    ASSERT_EQ(expectedBitSize, simpleStructure.bitSizeOf());
}

} // namespace simple_structure
} // namespace appl
} // namespace company
