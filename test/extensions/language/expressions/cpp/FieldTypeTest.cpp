#include "expressions/field_type/FieldTypeExpression.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace field_type
{

TEST(FieldTypeTest, bitSizeOfWithOptional)
{
    ContainedType containedType;
    containedType.setNeedsExtraValue(true);
    FieldTypeExpression fieldTypeExpression;
    fieldTypeExpression.setContainedType(containedType);
    fieldTypeExpression.setExtraValue(0x02);

    const size_t fieldTypeExpressionBitSizeWithOptional = 4;
    ASSERT_EQ(fieldTypeExpressionBitSizeWithOptional, fieldTypeExpression.bitSizeOf());
}

TEST(FieldTypeTest, bitSizeOfWithoutOptional)
{
    ContainedType containedType;
    containedType.setNeedsExtraValue(false);
    FieldTypeExpression fieldTypeExpression;
    fieldTypeExpression.setContainedType(containedType);

    const size_t fieldTypeExpressionBitSizeWithoutOptional = 1;
    ASSERT_EQ(fieldTypeExpressionBitSizeWithoutOptional, fieldTypeExpression.bitSizeOf());
}

} // namespace field_type
} // namespace expressions
