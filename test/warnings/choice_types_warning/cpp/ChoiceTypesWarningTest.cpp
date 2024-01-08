#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "test_utils/ZserioErrorOutput.h"

class ChoiceTypesWarningTest : public ::testing::Test
{
protected:
    ChoiceTypesWarningTest() :
            zserioWarnings("warnings/choice_types_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(ChoiceTypesWarningTest, optionalReferencesInSelector)
{
    const std::string warning =
            "optional_references_in_selector.zs:8:41: Choice 'TestChoice' selector "
            "contains reference to optional field 'numBits'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
