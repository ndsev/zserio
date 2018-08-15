#include "gtest/gtest.h"

#include "literals/ConstType.h"

namespace literals
{

TEST(LiteralsTest, Boolean)
{
    bool expectedBoolean = true;
    ASSERT_EQ(expectedBoolean, ConstType::BOOLEAN_TRUE);

    expectedBoolean = false;
    ASSERT_EQ(expectedBoolean, ConstType::BOOLEAN_FALSE);
}

TEST(LiteralsTest, Decimal)
{
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::DECIMAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::DECIMAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), ConstType::DECIMAL_NEGATIVE);
}

TEST(LiteralsTest, Hexadecimal)
{
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::HEXADECIMAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::HEXADECIMAL_POSITIVE_WITH_CAPITAL_X);
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::HEXADECIMAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), ConstType::HEXADECIMAL_NEGATIVE);
}

TEST(LiteralsTest, Octal)
{
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::OCTAL_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::OCTAL_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), ConstType::OCTAL_NEGATIVE);
}

TEST(LiteralsTest, Binary)
{
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::BINARY_POSITIVE);
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::BINARY_POSITIVE_WITH_CAPITAL_B);
    ASSERT_EQ(static_cast<int32_t>(255), ConstType::BINARY_POSITIVE_WITH_SIGN);
    ASSERT_EQ(static_cast<int32_t>(-255), ConstType::BINARY_NEGATIVE);
}

TEST(LiteralsTest, float16Literal)
{
    float diff = 15.2f - ConstType::FLOAT16;
    if (diff < 0.0f)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(LiteralsTest, float32Literal)
{
    float diff = 15.23f - ConstType::FLOAT32;
    if (diff < 0.0f)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(LiteralsTest, float64Literal)
{
    float diff = 15.234 - ConstType::FLOAT64;
    if (diff < 0.0)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<double>::epsilon());
}

TEST(LiteralsTest, String)
{
    ASSERT_EQ("String", ConstType::STRING);
}

} // namespace literals
