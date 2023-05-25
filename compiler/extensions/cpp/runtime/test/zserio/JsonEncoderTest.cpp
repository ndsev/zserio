#include "gtest/gtest.h"

#include <cmath>
#include <sstream>

#include "zserio/JsonEncoder.h"

namespace zserio
{

TEST(JsonEncoderTest, encodeNull)
{
    std::ostringstream os;
    JsonEncoder::encodeNull(os);
    ASSERT_EQ("null", os.str());
}

TEST(JsonEncoderTest, encodeBool)
{
    std::ostringstream os;
    JsonEncoder::encodeBool(os, true);
    ASSERT_EQ("true", os.str());

    os.str("");
    JsonEncoder::encodeBool(os, false);
    ASSERT_EQ("false", os.str());
}

TEST(JsonEncoderTest, encodeIntegral)
{
    std::ostringstream os;
    JsonEncoder::encodeIntegral(os, static_cast<uint8_t>(UINT8_MAX));
    ASSERT_EQ(std::to_string(UINT8_MAX), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<uint16_t>(UINT16_MAX));
    ASSERT_EQ(std::to_string(UINT16_MAX), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<uint32_t>(UINT32_MAX));
    ASSERT_EQ(std::to_string(UINT32_MAX), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<uint64_t>(UINT64_MAX));
    ASSERT_EQ(std::to_string(UINT64_MAX), os.str());

    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int8_t>(INT8_MIN));
    ASSERT_EQ(std::to_string(INT8_MIN), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int8_t>(INT8_MAX));
    ASSERT_EQ(std::to_string(INT8_MAX), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int16_t>(INT16_MIN));
    ASSERT_EQ(std::to_string(INT16_MIN), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int16_t>(INT16_MAX));
    ASSERT_EQ(std::to_string(INT16_MAX), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int32_t>(INT32_MIN));
    ASSERT_EQ(std::to_string(INT32_MIN), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int32_t>(INT32_MAX));
    ASSERT_EQ(std::to_string(INT32_MAX), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int64_t>(INT64_MIN));
    ASSERT_EQ(std::to_string(INT64_MIN), os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, static_cast<int64_t>(INT64_MAX));
    ASSERT_EQ(std::to_string(INT64_MAX), os.str());

    os.str("");
    JsonEncoder::encodeIntegral(os, INT64_MIN);
    ASSERT_EQ("-9223372036854775808", os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, -1000);
    ASSERT_EQ("-1000", os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, 0);
    ASSERT_EQ("0", os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, 1000);
    ASSERT_EQ("1000", os.str());
    os.str("");
    JsonEncoder::encodeIntegral(os, UINT64_MAX);
    ASSERT_EQ("18446744073709551615", os.str());
}

TEST(JsonEncoderTest, encodeFloatingPoint)
{
    std::ostringstream os;
    JsonEncoder::encodeFloatingPoint(os, -1.0);
    ASSERT_EQ("-1.0", os.str());
    os.str("");
    JsonEncoder::encodeFloatingPoint(os, 0.0);
    ASSERT_EQ("0.0", os.str());
    os.str("");
    JsonEncoder::encodeFloatingPoint(os, 1.0);
    ASSERT_EQ("1.0", os.str());

    os.str("");
    JsonEncoder::encodeFloatingPoint(os, 3.5);
    ASSERT_EQ("3.5", os.str());
    os.str("");
    JsonEncoder::encodeFloatingPoint(os, 9.875);
    ASSERT_EQ("9.875", os.str());
    os.str("");
    JsonEncoder::encodeFloatingPoint(os, 0.6171875);
    ASSERT_EQ("0.6171875", os.str());

    os.str("");
    JsonEncoder::encodeFloatingPoint(os, 1e20);
    ASSERT_TRUE("1e+20" == os.str() || "1e+020" == os.str())
            << "Value '" << os.str() << "' does not match to neither '1e+20' nor '1e+020'";

    os.str("");
    JsonEncoder::encodeFloatingPoint(os, -1e+16);
    ASSERT_TRUE("-1e+16" == os.str() || "-1e+016" == os.str())
            << "Value '" << os.str() << "' does not match to neither '-1e+16' nor '-1e+016'";

    os.str("");
    JsonEncoder::encodeFloatingPoint(os, static_cast<double>(NAN));
    ASSERT_EQ("NaN", os.str());
    os.str("");
    JsonEncoder::encodeFloatingPoint(os, static_cast<double>(INFINITY));
    ASSERT_EQ("Infinity", os.str());
    os.str("");
    JsonEncoder::encodeFloatingPoint(os, -static_cast<double>(INFINITY));
    ASSERT_EQ("-Infinity", os.str());
}

TEST(JsonEncoderTest, encodeString)
{
    std::ostringstream os;
    JsonEncoder::encodeString(os, "");
    ASSERT_EQ("\"\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "test");
    ASSERT_EQ("\"test\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "München");
    ASSERT_EQ("\"München\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "€");
    ASSERT_EQ("\"€\"", os.str());

    // escapes
    os.str("");
    JsonEncoder::encodeString(os, "\\");
    ASSERT_EQ("\"\\\\\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "\"");
    ASSERT_EQ("\"\\\"\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "\b");
    ASSERT_EQ("\"\\b\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "\f");
    ASSERT_EQ("\"\\f\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "\n");
    ASSERT_EQ("\"\\n\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "\r");
    ASSERT_EQ("\"\\r\"", os.str());
    os.str("");
    JsonEncoder::encodeString(os, "\t");
    ASSERT_EQ("\"\\t\"", os.str());

    os.str("");
    JsonEncoder::encodeString(os, "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2");
    ASSERT_EQ("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"", os.str());

    // <= 0x1F -> unicode escape
    os.str("");
    JsonEncoder::encodeString(os, "\x1F");
    ASSERT_EQ("\"\\u001f\"", os.str());
}

} // namespace zserio

