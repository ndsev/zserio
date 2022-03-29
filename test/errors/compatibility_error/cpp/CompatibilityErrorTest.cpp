#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class CompatibilityErrorTest : public ::testing::Test
{
protected:

    CompatibilityErrorTest()
    :   zserioErrors("errors/compatibility_error")
    {}

    const test_utils::ZserioErrorOutput zserioErrors;
};

TEST_F(CompatibilityErrorTest, packedArrayInTemplate240)
{
    const std::vector<std::string> errors{{
            "packed_array_in_template_240_error.zs:1:30: "
                    "Root package requires compatibility with version '2.4.0'!",
            "packed_array_in_template_240_error.zs:7:14: "
                    "Packed arrays binary encoding has been changed in version '2.5.0'!",
            "C++11 Generator: Compatibility check failed!"
    }};

    ASSERT_TRUE(zserioErrors.isPresent(errors));
}

TEST_F(CompatibilityErrorTest, packedCompoundArray242)
{
    const std::vector<std::string> errors{{
        "packed_compound_array_242_error.zs:1:30: "
                "Root package requires compatibility with version '2.4.2'!",
        "packed_compound_array_242_error.zs:16:25: "
                "Packed arrays binary encoding has been changed in version '2.5.0'!",
        "C++11 Generator: Compatibility check failed!"
    }};

    ASSERT_TRUE(zserioErrors.isPresent(errors));
}

TEST_F(CompatibilityErrorTest, packedUInt32Array241)
{
    const std::vector<std::string> errors{{
        "packed_uint32_array_241_error.zs:1:30: "
                "Root package requires compatibility with version '2.4.1'!",
        "packed_uint32_array_241_error.zs:7:19: "
                "Packed arrays binary encoding has been changed in version '2.5.0'!",
        "C++11 Generator: Compatibility check failed!"
    }};

    ASSERT_TRUE(zserioErrors.isPresent(errors));
}

TEST_F(CompatibilityErrorTest, versionLessThanMinSupported)
{
    ASSERT_TRUE(zserioErrors.isPresent(
            "version_less_than_min_supported_error.zs:1:30: "
                    "Package specifies unsupported compatibility version '2.3.2', "
                    "minimum supported version is '2.4.0'!"));
}

TEST_F(CompatibilityErrorTest, wrongCompatibilityVersionFormat)
{
    ASSERT_TRUE(zserioErrors.isPresent(
            "wrong_compatibility_version_format_error.zs:2:30: "
                    "Failed to parse version string: '2.5.0-rc1' as a version!"));
}
