#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_grpc_code
{

class WithoutGrpcCode : public ::testing::Test
{
protected:
    bool isFilePresent(const char* fileName)
    {
        std::ifstream file(fileName);
        return file.is_open();
    }
};

TEST_F(WithoutGrpcCode, checkGrpcSerializationTraits)
{
    ASSERT_FALSE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/GrpcSerializationTraits.h"));
}

TEST_F(WithoutGrpcCode, checkService)
{
    ASSERT_FALSE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/Service.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/Service.h"));
}

TEST_F(WithoutGrpcCode, checkResponse)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/Response.cpp"));
    ASSERT_TRUE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/Response.h"));
}

TEST_F(WithoutGrpcCode, checkRequest)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/Request.cpp"));
    ASSERT_TRUE(isFilePresent(
            "arguments/without_grpc_code/gen/without_grpc_code/Request.h"));
}

} // namespace without_writer_code
