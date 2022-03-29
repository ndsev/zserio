#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class FunctionsWarningTest : public ::testing::Test
{
protected:
    FunctionsWarningTest()
    :   zserioWarnings("warnings/functions_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(FunctionsWarningTest, optionalReferencesInFunction)
{
    const std::string warning1 = "optional_references_in_function.zs:11:16: Function "
            "'suspicionFunction' contains reference to optional field 'additionalValue'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 = "optional_references_in_function.zs:16:16: Function "
            "'autoSuspicionFunction' contains reference to optional field 'autoAdditionalValue'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));
}
