#include "gtest/gtest.h"

#include "DefaultPackageStructure.h"

TEST(DefaultPackageTest, defaultPackageStructure)
{
    // just test that DefaultPackageStructure is available in global namespace
    DefaultPackageStructure structure;
    structure.initialize(4);
    structure.setValue(10);
    ASSERT_EQ(10, structure.getValue());
}
