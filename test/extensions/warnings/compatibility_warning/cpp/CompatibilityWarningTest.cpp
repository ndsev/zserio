#include <string>

#include "gtest/gtest.h"
#include "test_utils/ZserioErrorOutput.h"

class CompatibilityWarningTest : public ::testing::Test
{
protected:
    CompatibilityWarningTest() :
            zserioWarningsRootWith("warnings/compatibility_warning", "zserio_log_root_with.txt"),
            zserioWarningsRootWithout("warnings/compatibility_warning", "zserio_log_root_without.txt")
    {}

    const test_utils::ZserioErrorOutput zserioWarningsRootWith;
    const test_utils::ZserioErrorOutput zserioWarningsRootWithout;
};

TEST_F(CompatibilityWarningTest, rootWithDiffCompatibility)
{
    const std::string error =
            "subpackage.zs:1:30: "
            "Package compatibility version '2.4.2' doesn't match to '2.5.0' specified in root package!";

    ASSERT_TRUE(zserioWarningsRootWith.isPresent(error));
}

TEST_F(CompatibilityWarningTest, rootWithoutCompatibility)
{
    const std::string error =
            "subpackage.zs:1:30: "
            "Package specifies compatibility version '2.4.2' while root package specifies nothing!";

    ASSERT_TRUE(zserioWarningsRootWithout.isPresent(error));
}
