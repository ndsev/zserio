#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "test_utils/ZserioErrorOutput.h"

class FileEncodingWarningTest : public ::testing::Test
{
protected:
    FileEncodingWarningTest() :
            zserioWarnings("warnings/file_encoding_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(FileEncodingWarningTest, nonUtf8Characters)
{
    const std::string warning = "file_encoding_warning.zs:1:1: Found non-UTF8 encoded characters.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(FileEncodingWarningTest, tabCharacters)
{
    const std::string warning = "file_encoding_warning.zs:1:1: Found tab characters.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(FileEncodingWarningTest, nonPrintableAsciiCharacters)
{
    const std::string warning = "file_encoding_warning.zs:1:1: Found non-printable ASCII characters.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
