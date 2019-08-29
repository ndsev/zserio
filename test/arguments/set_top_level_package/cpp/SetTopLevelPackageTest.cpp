#include "gtest/gtest.h"

#include "company/appl/set_top_level_package/SimpleStructure.h"

namespace company
{
namespace appl
{
namespace set_top_level_package
{
namespace simple_structure
{

class SetTopLevelPackageTest : public ::testing::Test
{
protected:
    static const size_t SIMPLE_STRUCTURE_BIT_SIZE;
};

const size_t SetTopLevelPackageTest::SIMPLE_STRUCTURE_BIT_SIZE = 18;

TEST_F(SetTopLevelPackageTest, emptyConstructor)
{
    SimpleStructure simpleStructure;
    const size_t expectedBitSize = SIMPLE_STRUCTURE_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, simpleStructure.bitSizeOf());
}

} // namespace simple_structure
} // namespace set_top_level_package
} // namespace appl
} // namespace company
