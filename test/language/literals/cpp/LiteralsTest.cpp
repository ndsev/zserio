#include "gtest/gtest.h"
#include "literals/BINARY_NEGATIVE.h"
#include "literals/BINARY_POSITIVE.h"
#include "literals/BINARY_POSITIVE_WITH_CAPITAL_B.h"
#include "literals/BINARY_POSITIVE_WITH_SIGN.h"
#include "literals/BOOLEAN_FALSE.h"
#include "literals/BOOLEAN_TRUE.h"
#include "literals/DECIMAL_NEGATIVE.h"
#include "literals/DECIMAL_POSITIVE.h"
#include "literals/DECIMAL_POSITIVE_WITH_SIGN.h"
#include "literals/DECIMAL_ZERO.h"
#include "literals/FLOAT16.h"
#include "literals/FLOAT32.h"
#include "literals/FLOAT64.h"
#include "literals/HEXADECIMAL_NEGATIVE.h"
#include "literals/HEXADECIMAL_POSITIVE.h"
#include "literals/HEXADECIMAL_POSITIVE_WITH_CAPITAL_X.h"
#include "literals/HEXADECIMAL_POSITIVE_WITH_SIGN.h"
#include "literals/OCTAL_NEGATIVE.h"
#include "literals/OCTAL_POSITIVE.h"
#include "literals/OCTAL_POSITIVE_WITH_SIGN.h"
#include "literals/OCTAL_ZERO.h"
#include "literals/STRING.h"

using namespace zserio::literals;

namespace literals
{

TEST(LiteralsTest, Boolean)
{
    bool expectedBoolean = true;
    ASSERT_EQ(expectedBoolean, BOOLEAN_TRUE);

    expectedBoolean = false;
    ASSERT_EQ(expectedBoolean, BOOLEAN_FALSE);
}

TEST(LiteralsTest, Decimal)
{
    ASSERT_EQ(static_cast<int32_t>(255), DECIMAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), DECIMAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), DECIMAL_NEGATIVE);
    ASSERT_EQ(static_cast<int32_t>(0), DECIMAL_ZERO);
}

TEST(LiteralsTest, Hexadecimal)
{
    ASSERT_EQ(static_cast<int32_t>(255), HEXADECIMAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), HEXADECIMAL_POSITIVE_WITH_CAPITAL_X);
    ASSERT_EQ(static_cast<int32_t>(255), HEXADECIMAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), HEXADECIMAL_NEGATIVE);
}

TEST(LiteralsTest, Octal)
{
    ASSERT_EQ(static_cast<int32_t>(255), OCTAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), OCTAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), OCTAL_NEGATIVE);
    ASSERT_EQ(static_cast<int32_t>(0), OCTAL_ZERO);
}

TEST(LiteralsTest, Binary)
{
    ASSERT_EQ(static_cast<int32_t>(255), BINARY_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), BINARY_POSITIVE_WITH_CAPITAL_B);
    ASSERT_EQ(static_cast<int32_t>(255), BINARY_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), BINARY_NEGATIVE);
}

TEST(LiteralsTest, float16Literal)
{
    float diff = 15.2F - FLOAT16;
    if (diff < 0.0F)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(LiteralsTest, float32Literal)
{
    float diff = 15.23F - FLOAT32;
    if (diff < 0.0F)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(LiteralsTest, float64Literal)
{
    double diff = 15.234 - FLOAT64;
    if (diff < 0.0)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<double>::epsilon());
}

TEST(LiteralsTest, String)
{
    ASSERT_EQ("String with escaped values \x31 \x32 \063 \n \t \f \r \\ \""_sv, STRING);
}

} // namespace literals
