#include "gtest/gtest.h"
#include "zserio/CppRuntimeVersion.h"

TEST(CppRuntimeVersionTest, checkVersion)
{
// this test just uses macro CPP_EXTENSION_RUNTIME_VERSION_NUMBER to check clang-tidy warnings
#if CPP_EXTENSION_RUNTIME_VERSION_NUMBER < 1000000
    #error Zserio runtime library version is too old!
#endif
}
