#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_pubsub_code
{

class WithoutPubsubCode : public ::testing::Test
{
protected:
    bool isFilePresent(const char* fileName)
    {
        std::ifstream file(fileName);
        return file.is_open();
    }
};

TEST_F(WithoutPubsubCode, checkService)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Service.cpp"));
    ASSERT_TRUE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Service.h"));
}

TEST_F(WithoutPubsubCode, checkResponse)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Response.cpp"));
    ASSERT_TRUE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Response.h"));
}

TEST_F(WithoutPubsubCode, checkRequest)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Request.cpp"));
    ASSERT_TRUE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Request.h"));
}

TEST_F(WithoutPubsubCode, checkPubsub)
{
    ASSERT_FALSE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Pubsub.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/without_pubsub_code/gen/without_pubsub_code/Pubsub.h"));
}

} // namespace without_pubsub_code
