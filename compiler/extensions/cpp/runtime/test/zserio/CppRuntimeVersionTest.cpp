#include "zserio/CppRuntimeVersion.h"

#include "gtest/gtest.h"

TEST(CppRuntimeVersionTest, checkVersion)
{
    // this test just uses macro CPP_EXTENSION_RUNTIME_VERSION_NUMBER to check clang-tidy warnings
    #if CPP_EXTENSION_RUNTIME_VERSION_NUMBER < 2011000
        #error Zserio runtime library version is too old!
    #endif
}
