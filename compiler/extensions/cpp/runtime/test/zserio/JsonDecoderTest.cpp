#include "gtest/gtest.h"

#include "zserio/JsonDecoder.h"

namespace zserio
{

namespace
{

class JsonDecoderTest : public ::testing::Test
{
protected:
    template <typename T>
    void checkDecoder(const char* input, size_t expectedNumRead, T expectedValue)
    {
        size_t numRead = 0;
        const AnyHolder<> jsonValue = m_decoder.decodeValue(input, numRead);
        ASSERT_EQ(expectedNumRead, numRead);
        ASSERT_TRUE(jsonValue.isType<T>());
        ASSERT_EQ(expectedValue, jsonValue.get<T>());
    }

    JsonDecoder m_decoder;
};


} // namespace

TEST_F(JsonDecoderTest, decodeNull)
{
    checkDecoder("null", 4, nullptr);
}

TEST_F(JsonDecoderTest, decodeBool)
{
    checkDecoder("true", 4, true);
    checkDecoder("false", 5, false);
}

TEST_F(JsonDecoderTest, decodeNan)
{
    size_t numRead = 0;
    const AnyHolder<> jsonValue = m_decoder.decodeValue("NaN", numRead);
    ASSERT_EQ(3, numRead);
    ASSERT_TRUE(jsonValue.isType<double>());
    ASSERT_TRUE(std::isnan(jsonValue.get<double>()));
}

TEST_F(JsonDecoderTest, decodeInfinity)
{
    {
        size_t numRead = 0;
        const AnyHolder<> jsonValue = m_decoder.decodeValue("Infinity", numRead);
        ASSERT_TRUE(jsonValue.isType<double>());
        ASSERT_EQ(8, numRead);
        ASSERT_TRUE(std::isinf(jsonValue.get<double>()));
        ASSERT_LT(0.0, jsonValue.get<double>());
    }

    {
        size_t numRead = 0;
        const AnyHolder<> jsonValue = m_decoder.decodeValue("-Infinity", numRead);
        ASSERT_TRUE(jsonValue.isType<double>());
        ASSERT_EQ(9, numRead);
        ASSERT_TRUE(std::isinf(jsonValue.get<double>()));
        ASSERT_GT(0.0, jsonValue.get<double>());
    }
}

TEST_F(JsonDecoderTest, decodeSignedIntegral)
{
    checkDecoder("-0", 2, static_cast<int64_t>(0));
    checkDecoder("-1", 2, static_cast<int64_t>(-1));
    checkDecoder("-9223372036854775808", 20, INT64_MIN);
}

TEST_F(JsonDecoderTest, decodeUnsignedIntegral)
{
    checkDecoder("0", 1, static_cast<uint64_t>(0));
    checkDecoder("1", 1, static_cast<uint64_t>(1));
    checkDecoder("9223372036854775807", 19, static_cast<uint64_t>(INT64_MAX));
    checkDecoder("18446744073709551615", 20, UINT64_MAX);
}

TEST_F(JsonDecoderTest, decodeDouble)
{
    checkDecoder("0.0", 3, 0.0);
    checkDecoder("-1.0", 4, -1.0);
    checkDecoder("1.0", 3, 1.0);
    checkDecoder("3.5", 3, 3.5);
    checkDecoder("9.875", 5, 9.875);
    checkDecoder("0.6171875", 9, 0.6171875);

    checkDecoder("1e+20", 5, 1e+20);
    checkDecoder("1E+20", 5, 1E+20);
}

TEST_F(JsonDecoderTest, decodeString)
{
    checkDecoder("\"\"", 2, std::string(""));
    checkDecoder("\"test\"", 6, std::string("test"));
    checkDecoder("\"München\"", 10, std::string("München"));
    checkDecoder("\"€\"", 5, std::string("€"));

    // escapes
    checkDecoder("\"\\\\\"", 4, std::string("\\"));
    checkDecoder("\"\\\"\"", 4, std::string("\""));
    checkDecoder("\"\\b\"", 4, std::string("\b"));
    checkDecoder("\"\\f\"", 4, std::string("\f"));
    checkDecoder("\"\\n\"", 4, std::string("\n"));
    checkDecoder("\"\\r\"", 4, std::string("\r"));
    checkDecoder("\"\\t\"", 4, std::string("\t"));

    checkDecoder("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"", 62,
           std::string("\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2"));

    // <= 0x1F -> unicode escape
    checkDecoder("\"\\u001f\"", 8, std::string("\x1F"));
}

} // namespace zserio
