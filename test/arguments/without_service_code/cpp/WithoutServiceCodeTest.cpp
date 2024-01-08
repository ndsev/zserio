#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_service_code
{

class WithoutServiceCode : public ::testing::Test
{
protected:
    bool isFilePresent(const char* fileName)
    {
        std::ifstream file(fileName);
        return file.is_open();
    }
};

TEST_F(WithoutServiceCode, checkService)
{
    ASSERT_FALSE(isFilePresent("arguments/without_service_code/gen/without_service_code/Service.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/without_service_code/gen/without_service_code/Service.h"));
}

TEST_F(WithoutServiceCode, checkResponse)
{
    ASSERT_TRUE(isFilePresent("arguments/without_service_code/gen/without_service_code/Response.cpp"));
    ASSERT_TRUE(isFilePresent("arguments/without_service_code/gen/without_service_code/Response.h"));
}

TEST_F(WithoutServiceCode, checkRequest)
{
    ASSERT_TRUE(isFilePresent("arguments/without_service_code/gen/without_service_code/Request.cpp"));
    ASSERT_TRUE(isFilePresent("arguments/without_service_code/gen/without_service_code/Request.h"));
}

TEST_F(WithoutServiceCode, checkPubsub)
{
    ASSERT_TRUE(isFilePresent("arguments/without_service_code/gen/without_service_code/Pubsub.cpp"));
    ASSERT_TRUE(isFilePresent("arguments/without_service_code/gen/without_service_code/Pubsub.h"));
}

} // namespace without_service_code
