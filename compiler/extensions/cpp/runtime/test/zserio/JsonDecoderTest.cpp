#include "gtest/gtest.h"
#include "zserio/JsonDecoder.h"

namespace zserio
{

using JsonDecoder = BasicJsonDecoder<>;

class JsonDecoderTest : public ::testing::Test
{
protected:
    template <typename T>
    void checkDecoderSuccess(const char* input, size_t expectedNumRead, T expectedValue)
    {
        const auto decoderResult = m_decoder.decodeValue(input);
        ASSERT_EQ(expectedNumRead, decoderResult.numReadChars) << input;
        ASSERT_TRUE(decoderResult.value.isType<T>()) << input;
        ASSERT_EQ(expectedValue, decoderResult.value.get<T>()) << input;
    }

    void checkDecoderFailure(const char* input, size_t expectedNumRead)
    {
        const auto decoderResult = m_decoder.decodeValue(input);
        ASSERT_EQ(expectedNumRead, decoderResult.numReadChars) << input;
        ASSERT_FALSE(decoderResult.value.hasValue()) << input;
    }

    JsonDecoder m_decoder;
};

TEST_F(JsonDecoderTest, decodeInvalid)
{
    checkDecoderFailure("invalid", 1);
}

TEST_F(JsonDecoderTest, decodeNull)
{
    checkDecoderSuccess("null", 4, nullptr);
    checkDecoderSuccess("null {}", 4, nullptr);
    checkDecoderSuccess("null{}", 4, nullptr);
    checkDecoderSuccess("nullix", 4, nullptr);

    checkDecoderFailure("nvalid", 2);
    checkDecoderFailure("nul", 3);
}

TEST_F(JsonDecoderTest, decodeTrue)
{
    checkDecoderSuccess("true", 4, true);
    checkDecoderSuccess("true {}", 4, true);

    checkDecoderFailure("trust", 4);
    checkDecoderFailure("tru", 3);
}

TEST_F(JsonDecoderTest, decodeFalse)
{
    checkDecoderSuccess("false", 5, false);
    checkDecoderSuccess("false {}", 5, false);

    checkDecoderFailure("ffalse", 2);
    checkDecoderFailure("fa", 2);
}

TEST_F(JsonDecoderTest, decodeNan)
{
    {
        const auto decoderResult = m_decoder.decodeValue("NaN");
        ASSERT_EQ(3, decoderResult.numReadChars);
        ASSERT_TRUE(decoderResult.value.isType<double>());
        ASSERT_TRUE(std::isnan(decoderResult.value.get<double>()));
    }

    {
        const auto decoderResult = m_decoder.decodeValue("NaN {}");
        ASSERT_EQ(3, decoderResult.numReadChars);
        ASSERT_TRUE(decoderResult.value.isType<double>());
        ASSERT_TRUE(std::isnan(decoderResult.value.get<double>()));
    }

    checkDecoderFailure("Nanic", 3);
    checkDecoderFailure("Na", 2);
}

TEST_F(JsonDecoderTest, decodePositiveInfinity)
{
    {
        const auto decoderResult = m_decoder.decodeValue("Infinity");
        ASSERT_EQ(8, decoderResult.numReadChars);
        ASSERT_TRUE(decoderResult.value.isType<double>());
        ASSERT_TRUE(std::isinf(decoderResult.value.get<double>()));
        ASSERT_LT(0.0, decoderResult.value.get<double>());
    }

    {
        const auto decoderResult = m_decoder.decodeValue("Infinity {}");
        ASSERT_EQ(8, decoderResult.numReadChars);
        ASSERT_TRUE(decoderResult.value.isType<double>());
        ASSERT_TRUE(std::isinf(decoderResult.value.get<double>()));
        ASSERT_LT(0.0, decoderResult.value.get<double>());
    }

    checkDecoderFailure("Infiniinfiny", 7);
    checkDecoderFailure("Inf", 3);
}

TEST_F(JsonDecoderTest, decodeNegativeInfinity)
{
    {
        const auto decoderResult = m_decoder.decodeValue("-Infinity");
        ASSERT_EQ(9, decoderResult.numReadChars);
        ASSERT_TRUE(decoderResult.value.isType<double>());
        ASSERT_TRUE(std::isinf(decoderResult.value.get<double>()));
        ASSERT_GT(0.0, decoderResult.value.get<double>());
    }

    {
        const auto decoderResult = m_decoder.decodeValue("-Infinity {}");
        ASSERT_EQ(9, decoderResult.numReadChars);
        ASSERT_TRUE(decoderResult.value.isType<double>());
        ASSERT_TRUE(std::isinf(decoderResult.value.get<double>()));
        ASSERT_GT(0.0, decoderResult.value.get<double>());
    }

    checkDecoderFailure("-Infinvalid", 7);
    checkDecoderFailure("-Infin", 6);
    checkDecoderFailure("-Infix", 6);
}

TEST_F(JsonDecoderTest, decodeSignedIntegral)
{
    checkDecoderSuccess("-0", 2, static_cast<int64_t>(0));
    checkDecoderSuccess("-1", 2, static_cast<int64_t>(-1));
    checkDecoderSuccess("-9223372036854775808", 20, INT64_MIN);

    checkDecoderFailure("--10", 1);
    checkDecoderFailure("-", 1);
    checkDecoderFailure("-A", 1);
}

TEST_F(JsonDecoderTest, decodeUnsignedIntegral)
{
    checkDecoderSuccess("0", 1, static_cast<uint64_t>(0));
    checkDecoderSuccess("1", 1, static_cast<uint64_t>(1));
    checkDecoderSuccess("9223372036854775807", 19, static_cast<uint64_t>(INT64_MAX));
    checkDecoderSuccess("18446744073709551615", 20, UINT64_MAX);

    checkDecoderFailure("+", 1);
    checkDecoderFailure("+10", 1);
    checkDecoderFailure("184467440737095516156", 21);
}

TEST_F(JsonDecoderTest, decodeDouble)
{
    checkDecoderSuccess("0.0", 3, 0.0);
    checkDecoderSuccess("-1.0", 4, -1.0);
    checkDecoderSuccess("1.0", 3, 1.0);
    checkDecoderSuccess("3.5", 3, 3.5);
    checkDecoderSuccess("9.875", 5, 9.875);
    checkDecoderSuccess("0.6171875", 9, 0.6171875);

    checkDecoderSuccess("1e+20", 5, 1e+20);
    checkDecoderSuccess("1E+20", 5, 1E+20);
    checkDecoderSuccess("1e-20", 5, 1e-20);
    checkDecoderSuccess("1E-20", 5, 1E-20);
    checkDecoderSuccess("-1e+20", 6, -1e+20);
    checkDecoderSuccess("-1E+20", 6, -1E+20);
    checkDecoderSuccess("-1e-20", 6, -1e-20);
    checkDecoderSuccess("-1E-20", 6, -1E-20);

    checkDecoderSuccess("1.0E-20", 7, 1.0E-20);
    checkDecoderSuccess("-1.0E-20", 8, -1.0E-20);
    checkDecoderSuccess("9.875E+3", 8, 9.875E+3);
    checkDecoderSuccess("-9.875E-3", 9, -9.875E-3);

    checkDecoderFailure("1EE20", 2);
    checkDecoderFailure("1E.E20", 2);
    checkDecoderFailure("1E++20", 3);
    checkDecoderFailure("1E--20", 3);

    checkDecoderFailure("1e", 2);
    checkDecoderFailure("1e+", 3);
    checkDecoderFailure("1E-", 3);
}

TEST_F(JsonDecoderTest, decodeString)
{
    checkDecoderSuccess("\"\"", 2, std::string(""));
    checkDecoderSuccess("\"test\"", 6, std::string("test"));
    checkDecoderSuccess("\"München\"", 10, std::string("München"));
    checkDecoderSuccess("\"€\"", 5, std::string("€"));

    // escapes
    checkDecoderSuccess("\"\\\\\"", 4, std::string("\\"));
    checkDecoderSuccess("\"\\\"\"", 4, std::string("\""));
    checkDecoderSuccess("\"\\b\"", 4, std::string("\b"));
    checkDecoderSuccess("\"\\f\"", 4, std::string("\f"));
    checkDecoderSuccess("\"\\n\"", 4, std::string("\n"));
    checkDecoderSuccess("\"\\r\"", 4, std::string("\r"));
    checkDecoderSuccess("\"\\t\"", 4, std::string("\t"));

    checkDecoderSuccess("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"", 62,
            std::string("\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2"));

    // <= 0x1F -> unicode escape
    checkDecoderSuccess("\"\\u001f\"", 8, std::string("\x1F"));

    checkDecoderFailure("\"\\u001x\"", 7);
    checkDecoderFailure("\"unterminated", 13);
    checkDecoderFailure("\"wrong escape \\", 15);
    checkDecoderFailure("\"wrong unicode escape - 0 char \\u", 33);
    checkDecoderFailure("\"wrong unicode escape - 1 char \\u0", 34);
    checkDecoderFailure("\"wrong unicode escape - 1 char \\u1", 34);
    checkDecoderFailure("\"wrong unicode escape - 2 chars \\u00", 36);
    checkDecoderFailure("\"wrong unicode escape - 2 chars \\u01", 36);
    checkDecoderFailure("\"wrong unicode escape - 3 chars \\u00-", 37);
    checkDecoderFailure("\"wrong unicode escape - 3 chars \\u00A", 37);
    checkDecoderFailure("\"wrong unicode escape - 3 chars \\u00G", 37);
    checkDecoderFailure("\"wrong unicode escape - 4 chars \\u000G", 38);
    checkDecoderFailure("\"unknown escape \\x", 18);
}

TEST_F(JsonDecoderTest, wrong_arguments)
{
    checkDecoderFailure("", 0);
}

} // namespace zserio
