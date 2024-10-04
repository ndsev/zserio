#include "gtest/gtest.h"

class DocCommentMissingWarningTest : public ::testing::Test
{
protected:
    DocCommentMissingWarningTest() = default;
};

TEST_F(DocCommentMissingWarningTest, dummy)
{}
