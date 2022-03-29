#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class NotHandledWarningTest : public ::testing::Test
{
protected:
    NotHandledWarningTest()
    :   zserioWarnings("warnings/not_handled_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(NotHandledWarningTest, notHandledWhite)
{
    const std::string warning = "not_handled_warning.zs:15:8: "
            "Enumeration value 'WHITE' is not handled in choice 'EnumParamChoice'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(NotHandledWarningTest, notHandledRed)
{
    const std::string warning = "not_handled_warning.zs:15:8: "
            "Enumeration value 'RED' is not handled in choice 'EnumParamChoice'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
