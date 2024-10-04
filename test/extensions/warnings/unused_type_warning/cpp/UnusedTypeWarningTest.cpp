#include "gtest/gtest.h"

class UnusedTypeWarningTest : public ::testing::Test
{
protected:
    UnusedTypeWarningTest() = default;
};

TEST_F(UnusedTypeWarningTest, dummy)
{}
