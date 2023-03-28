#include "gtest/gtest.h"

#include "expressions/full_enumeration_type/FullEnumerationTypeExpression.h"

namespace expressions
{
namespace full_enumeration_type
{

TEST(FullEnumerationTypeTest, bitSizeOfWithOptional)
{
    FullEnumerationTypeExpression fullEnumerationTypeExpression;
    fullEnumerationTypeExpression.setColor(Color::RED);
    fullEnumerationTypeExpression.setIsColorRed(true);

    const size_t fullEnumerationTypeExpressionBitSizeWithOptional = 9;
    ASSERT_EQ(fullEnumerationTypeExpressionBitSizeWithOptional, fullEnumerationTypeExpression.bitSizeOf());
}

TEST(FullEnumerationTypeTest, bitSizeOfWithoutOptional)
{
    FullEnumerationTypeExpression fullEnumerationTypeExpression;
    fullEnumerationTypeExpression.setColor(Color::BLUE);

    const size_t fullEnumerationTypeExpressionBitSizeWithoutOptional = 8;
    ASSERT_EQ(fullEnumerationTypeExpressionBitSizeWithoutOptional, fullEnumerationTypeExpression.bitSizeOf());
}

} // namespace full_enumeration_type
} // namespace expressions
