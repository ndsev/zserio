#include <string>
#include <fstream>

#include "gtest/gtest.h"

namespace
{
    bool isTextInFilePresent(const char* fileName, const char* text)
    {
        std::ifstream file(fileName);
        bool isPresent = false;
        std::string line;
        while (std::getline(file, line))
        {
            if (line.find(text) != std::string::npos)
            {
                isPresent = true;
                break;
            }
        }
        file.close();

        return isPresent;
    }

    bool isWarningPresent(const char* warningText)
    {
        const char* logFileName = "warnings/service_types_warning/zserio.log";
        return isTextInFilePresent(logFileName, warningText);
    }
}

namespace service_types
{

TEST(ServiceWarningTest, mainWarning)
{
    ASSERT_TRUE(isWarningPresent("GRPC services are not supported by the legacy C++98 emitter!"));
}

TEST(ServiceWarningTest, simpleServiceWarning)
{
    ASSERT_TRUE(isWarningPresent("service_types_warning.zs:13:9: Ignoring GRPC service 'Math'!"));
}

TEST(ServiceWarningTest, streamingServiceWarning)
{
    ASSERT_TRUE(isWarningPresent("service_types_warning.zs:18:9: Ignoring GRPC service 'Accumulator'!"));
}

} // namespace service_types
