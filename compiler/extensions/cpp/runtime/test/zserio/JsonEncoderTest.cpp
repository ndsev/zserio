#include <cmath>
#include <sstream>

#include "gtest/gtest.h"
#include "zserio/JsonEncoder.h"

namespace zserio
{

TEST(JsonEncoderTest, encodeNull)
{
    std::ostringstream stream;
    JsonEncoder::encodeNull(stream);
    ASSERT_EQ("null", stream.str());
}

TEST(JsonEncoderTest, encodeBool)
{
    std::ostringstream stream;
    JsonEncoder::encodeBool(stream, true);
    ASSERT_EQ("true", stream.str());

    stream.str("");
    JsonEncoder::encodeBool(stream, false);
    ASSERT_EQ("false", stream.str());
}

TEST(JsonEncoderTest, encodeIntegral)
{
    std::ostringstream stream;
    JsonEncoder::encodeIntegral(stream, static_cast<uint8_t>(UINT8_MAX));
    ASSERT_EQ(std::to_string(UINT8_MAX), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<uint16_t>(UINT16_MAX));
    ASSERT_EQ(std::to_string(UINT16_MAX), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<uint32_t>(UINT32_MAX));
    ASSERT_EQ(std::to_string(UINT32_MAX), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<uint64_t>(UINT64_MAX));
    ASSERT_EQ(std::to_string(UINT64_MAX), stream.str());

    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int8_t>(INT8_MIN));
    ASSERT_EQ(std::to_string(INT8_MIN), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int8_t>(INT8_MAX));
    ASSERT_EQ(std::to_string(INT8_MAX), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int16_t>(INT16_MIN));
    ASSERT_EQ(std::to_string(INT16_MIN), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int16_t>(INT16_MAX));
    ASSERT_EQ(std::to_string(INT16_MAX), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int32_t>(INT32_MIN));
    ASSERT_EQ(std::to_string(INT32_MIN), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int32_t>(INT32_MAX));
    ASSERT_EQ(std::to_string(INT32_MAX), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int64_t>(INT64_MIN));
    ASSERT_EQ(std::to_string(INT64_MIN), stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, static_cast<int64_t>(INT64_MAX));
    ASSERT_EQ(std::to_string(INT64_MAX), stream.str());

    stream.str("");
    JsonEncoder::encodeIntegral(stream, INT64_MIN);
    ASSERT_EQ("-9223372036854775808", stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, -1000);
    ASSERT_EQ("-1000", stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, 0);
    ASSERT_EQ("0", stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, 1000);
    ASSERT_EQ("1000", stream.str());
    stream.str("");
    JsonEncoder::encodeIntegral(stream, UINT64_MAX);
    ASSERT_EQ("18446744073709551615", stream.str());
}

TEST(JsonEncoderTest, encodeFloatingPoint)
{
    std::ostringstream stream;
    JsonEncoder::encodeFloatingPoint(stream, -1.0);
    ASSERT_EQ("-1.0", stream.str());
    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, 0.0);
    ASSERT_EQ("0.0", stream.str());
    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, 1.0);
    ASSERT_EQ("1.0", stream.str());

    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, 3.5);
    ASSERT_EQ("3.5", stream.str());
    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, 9.875);
    ASSERT_EQ("9.875", stream.str());
    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, 0.6171875);
    ASSERT_EQ("0.6171875", stream.str());

    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, 1e20);
    ASSERT_TRUE("1e+20" == stream.str() || "1e+020" == stream.str())
            << "Value '" << stream.str() << "' does not match to neither '1e+20' nor '1e+020'";

    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, -1e+16);
    ASSERT_TRUE("-1e+16" == stream.str() || "-1e+016" == stream.str())
            << "Value '" << stream.str() << "' does not match to neither '-1e+16' nor '-1e+016'";

    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, static_cast<double>(NAN));
    ASSERT_EQ("NaN", stream.str());
    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, static_cast<double>(INFINITY));
    ASSERT_EQ("Infinity", stream.str());
    stream.str("");
    JsonEncoder::encodeFloatingPoint(stream, -static_cast<double>(INFINITY));
    ASSERT_EQ("-Infinity", stream.str());
}

TEST(JsonEncoderTest, encodeString)
{
    std::ostringstream stream;
    JsonEncoder::encodeString(stream, "");
    ASSERT_EQ("\"\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "test");
    ASSERT_EQ("\"test\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "München");
    ASSERT_EQ("\"München\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "€");
    ASSERT_EQ("\"€\"", stream.str());

    // escapes
    stream.str("");
    JsonEncoder::encodeString(stream, "\\");
    ASSERT_EQ("\"\\\\\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "\"");
    ASSERT_EQ("\"\\\"\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "\b");
    ASSERT_EQ("\"\\b\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "\f");
    ASSERT_EQ("\"\\f\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "\n");
    ASSERT_EQ("\"\\n\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "\r");
    ASSERT_EQ("\"\\r\"", stream.str());
    stream.str("");
    JsonEncoder::encodeString(stream, "\t");
    ASSERT_EQ("\"\\t\"", stream.str());

    stream.str("");
    JsonEncoder::encodeString(stream, "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2");
    ASSERT_EQ("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"", stream.str());

    // <= 0x1F -> unicode escape
    stream.str("");
    JsonEncoder::encodeString(stream, "\x1F");
    ASSERT_EQ("\"\\u001f\"", stream.str());
}

} // namespace zserio
