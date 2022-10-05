#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class TemplatesWarningTest : public ::testing::Test
{
protected:
    TemplatesWarningTest()
    :   zserioWarnings("warnings/templates_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(TemplatesWarningTest, defaultInitialization)
{
    const std::string warning1 = "default_instantiation_warning.zs:15:5: "
            "Default instantiation of 'Template' as 'Template_uint32.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 = "default_instantiation_warning.zs:17:5: "
            "Default instantiation of 'Subpackage1Template' as 'Subpackage1Template_string.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));

    const std::string warning3 = "default_instantiation_warning.zs:19:5: "
            "Default instantiation of 'Subpackage2Template' as 'Subpackage2Template_string.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning3));

    const std::vector<std::string> warnings4 =
    {
        "default_instantiation_warning.zs:20:5: "
                "    In instantiation of 'Subpackage3Template' required from here",
        "default_instantiation_subpackage3.zs:10:5: "
                "Default instantiation of 'Subpackage3InnerTemplate' as 'Subpackage3InnerTemplate_uint32."
    };
    ASSERT_TRUE(zserioWarnings.isPresent(warnings4));
}
