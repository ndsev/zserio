#include "gtest/gtest.h"

class NotHandledWarningTest : public ::testing::Test
{
protected:
    NotHandledWarningTest() = default;
};

TEST_F(NotHandledWarningTest, dummy)
{}
