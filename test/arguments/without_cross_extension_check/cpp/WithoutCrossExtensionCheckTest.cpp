#include "gtest/gtest.h"

#include "invalid_in_java/Test.h"
#include "invalid_in_python/Test.h"

TEST(WithoutCrossExtensionCheck, invalidInJava)
{
    ::invalid_in_java::Test test;
    ASSERT_EQ(13, test.funcAbstract());
}

TEST(WithoutCrossExtensionCheck, invalidInPython)
{
    ::invalid_in_python::Test test(13, 42, "def");
    ASSERT_EQ(13, test.getSomeField());
    ASSERT_EQ(42, test.getSome_field());
    ASSERT_EQ("def", test.getDef());
}
