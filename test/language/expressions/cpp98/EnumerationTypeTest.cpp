#include "math.h"

#include "gtest/gtest.h"

#include "expressions/enumeration_type/EnumerationTypeExpression.h"

namespace expressions
{
namespace enumeration_type
{

TEST(EnumerationTypeTest, bitSizeOfWithOptional)
{
    EnumerationTypeExpression enumerationTypeExpression;
    enumerationTypeExpression.setColor(Color::RED);
    enumerationTypeExpression.setIsColorRed(true);

    const size_t enumerationTypeExpressionBitSizeWithOptional = 9;
    ASSERT_EQ(enumerationTypeExpressionBitSizeWithOptional, enumerationTypeExpression.bitSizeOf());
}

TEST(EnumerationTypeTest, bitSizeOfWithoutOptional)
{
    EnumerationTypeExpression enumerationTypeExpression;
    enumerationTypeExpression.setColor(Color::BLUE);

    const size_t enumerationTypeExpressionBitSizeWithoutOptional = 8;
    ASSERT_EQ(enumerationTypeExpressionBitSizeWithoutOptional, enumerationTypeExpression.bitSizeOf());
}

} // namespace enumeration_type
} // namespace expressions
