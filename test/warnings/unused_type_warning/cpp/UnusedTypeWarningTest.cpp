#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class UnusedTypeWarningTest : public ::testing::Test
{
protected:
    UnusedTypeWarningTest()
    :   zserioWarnings("warnings/unused_type_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(UnusedTypeWarningTest, unusedEnumeration)
{
    const std::string warning = "unused_type_warning.zs:4:12: "
            "Type 'unused_type_warning.UnusedEnumeration' is not used.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(UnusedTypeWarningTest, unusedSubtype)
{
    const std::string warning = "unused_type_warning.zs:18:15: "
            "Type 'unused_type_warning.UnusedSubtype' is not used.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(UnusedTypeWarningTest, unusedChoice)
{
    const std::string warning = "unused_type_warning.zs:28:8: "
            "Type 'unused_type_warning.UnusedChoice' is not used.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(UnusedTypeWarningTest, unusedUnion)
{
    const std::string warning = "unused_type_warning.zs:48:7: "
            "Type 'unused_type_warning.UnusedUnion' is not used.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(UnusedTypeWarningTest, unusedStructure)
{
    const std::string warning = "unused_type_warning.zs:62:8: "
            "Type 'unused_type_warning.UnusedStructure' is not used.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(UnusedTypeWarningTest, unusedTable)
{
    const std::string warning = "unused_type_warning.zs:76:11: "
            "Type 'unused_type_warning.UnusedTable' is not used.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
