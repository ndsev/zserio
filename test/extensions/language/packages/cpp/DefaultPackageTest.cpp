#include "gtest/gtest.h"

#include "DefaultPackageStructure.h"

TEST(DefaultPackageTest, defaultPackageStructure)
{
    // just test that DefaultPackageStructure is available in global namespace
    DefaultPackageStructure structure;
    structure.initialize(4);
    structure.setValue(10);
    default_package_import::top::TopStructure topStructure;
    topStructure.setType(1);
    topStructure.setData(1234);
    structure.setTopStructure(topStructure);
    Child child;
    child.setValue(0xdeadbeef);
    structure.setChildStructure(child);
    ASSERT_EQ(10, structure.getValue());
    ASSERT_EQ(1, structure.getTopStructure().getType());
    ASSERT_EQ(1234, structure.getTopStructure().getData());
    ASSERT_EQ(0xdeadbeef, structure.getChildStructure().getValue());
}
