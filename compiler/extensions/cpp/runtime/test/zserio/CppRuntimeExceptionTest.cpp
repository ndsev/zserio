#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include <algorithm>

using namespace zserio::literals;

namespace zserio
{

TEST(CppRuntimeExceptionTest, emptyConstructor)
{
    CppRuntimeException exception;
    ASSERT_EQ(std::string(), exception.what());
}

TEST(CppRuntimeExceptionTest, cStringConstructor)
{
    CppRuntimeException noDescriptionException("");
    ASSERT_EQ(std::string(), noDescriptionException.what());

    const std::string testMessage = "this is the test message";
    CppRuntimeException exception(testMessage.c_str());
    ASSERT_EQ(testMessage, exception.what());
}

TEST(CppRuntimeExceptionTest, stringViewConstructor)
{
    CppRuntimeException noDescriptionException{StringView()};
    ASSERT_EQ(std::string(), noDescriptionException.what());

    const StringView testMessage = "this is the test message"_sv;
    CppRuntimeException exception(testMessage);
    ASSERT_EQ(testMessage, StringView(exception.what()));
}

TEST(CppRuntimeExceptionTest, appendCString)
{
    std::string testMessage = "1234567890123456";
    const std::string appendix = "1234567890123456";
    CppRuntimeException exception(testMessage.c_str());

    exception + appendix.c_str();
    testMessage += appendix;
    ASSERT_EQ(testMessage, exception.what());

    static const size_t max_len = CppRuntimeException::BUFFER_SIZE - 1;
    for (int i = 0; i < 100; ++i)
    {
        exception + appendix.c_str();
        testMessage += appendix;
        const size_t len = std::min(testMessage.size(), max_len);
        ASSERT_EQ(testMessage.substr(0, len), exception.what());
    }
}

TEST(CppRuntimeExceptionTest, appendBool)
{
    std::string testMessage = "test true: ";
    CppRuntimeException exception(testMessage.c_str());

    exception + true;
    testMessage += "true";
    ASSERT_EQ(testMessage, exception.what());

    exception + ", and false: " + false;
    testMessage += ", and false: false";
    ASSERT_EQ(testMessage, exception.what());
}

TEST(CppRuntimeExceptionTest, appendInt)
{
    int value = 42;
    CppRuntimeException exception = CppRuntimeException("") + value;
    ASSERT_EQ(std::to_string(value), exception.what());
}

} // namespace zserio
