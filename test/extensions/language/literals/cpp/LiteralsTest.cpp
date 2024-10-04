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
    static_assert(BOOLEAN_TRUE, "shall be constexpr");

    bool expectedBoolean = true;
    ASSERT_EQ(expectedBoolean, BOOLEAN_TRUE);

    expectedBoolean = false;
    ASSERT_EQ(expectedBoolean, BOOLEAN_FALSE);
}

TEST(LiteralsTest, Decimal)
{
    static_assert(DECIMAL_NEGATIVE == -255, "shall be constexpr");

    ASSERT_EQ(static_cast<int32_t>(255), DECIMAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), DECIMAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), DECIMAL_NEGATIVE);
    ASSERT_EQ(static_cast<int32_t>(0), DECIMAL_ZERO);
}

TEST(LiteralsTest, Hexadecimal)
{
    static_assert(255 == HEXADECIMAL_POSITIVE, "shall be constexpr");

    ASSERT_EQ(static_cast<int32_t>(255), HEXADECIMAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), HEXADECIMAL_POSITIVE_WITH_CAPITAL_X);
    ASSERT_EQ(static_cast<int32_t>(255), HEXADECIMAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), HEXADECIMAL_NEGATIVE);
}

TEST(LiteralsTest, Octal)
{
    static_assert(255 == HEXADECIMAL_POSITIVE, "shall be constexpr");

    ASSERT_EQ(static_cast<int32_t>(255), OCTAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), OCTAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), OCTAL_NEGATIVE);
    ASSERT_EQ(static_cast<int32_t>(0), OCTAL_ZERO);
}

TEST(LiteralsTest, Binary)
{
    static_assert(BINARY_POSITIVE == 0xff, "shall be constexpr");

    ASSERT_EQ(static_cast<int32_t>(255), BINARY_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), BINARY_POSITIVE_WITH_CAPITAL_B);
    ASSERT_EQ(static_cast<int32_t>(255), BINARY_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), BINARY_NEGATIVE);
}

TEST(LiteralsTest, float16Literal)
{
    constexpr float diff = 15.2F - FLOAT16 < 0.0F ? FLOAT16 - 15.2F : 15.2F - FLOAT16;
    static_assert(diff <= std::numeric_limits<float>::epsilon(), "shall be constexpr");

    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(LiteralsTest, float32Literal)
{
    constexpr float diff = (15.23F - FLOAT32) < 0.0F ? FLOAT32 - 15.23F : 15.23F - FLOAT32;
    static_assert(diff <= std::numeric_limits<float>::epsilon(), "shall be constexpr");

    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(LiteralsTest, float64Literal)
{
    constexpr double diff = 15.234 - FLOAT64 < 0.0 ? FLOAT64 - 15.234 : 15.234 - FLOAT64;
    static_assert(diff <= std::numeric_limits<double>::epsilon(), "shall be constexpr");

    ASSERT_TRUE(diff <= std::numeric_limits<double>::epsilon());
}

TEST(LiteralsTest, String)
{
    static_assert(STRING.size() == 44, "shall be constexpr");

    ASSERT_EQ("String with escaped values \x31 \x32 \063 \n \t \f \r \\ \""_sv, STRING);
}

} // namespace literals
