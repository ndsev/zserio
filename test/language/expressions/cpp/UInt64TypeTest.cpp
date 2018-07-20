#include "math.h"

#include "gtest/gtest.h"

#include "expressions/uint64_type/UInt64TypeExpression.h"

namespace expressions
{
namespace uint64_type
{

TEST(UInt64TypeTest, bitSizeOfWithOptional)
{
    UInt64TypeExpression uint64TypeExpression;
    const uint32_t uint32Value = 8;
    const uint64_t uint64ValueWithOptional = 2;
    const bool boolValue = true;
    const uint8_t additionalValue = 0x03;
    uint64TypeExpression.setUint32Value(uint32Value);
    uint64TypeExpression.setUint64Value(uint64ValueWithOptional);
    uint64TypeExpression.setBoolValue(boolValue);
    uint64TypeExpression.setAdditionalValue(additionalValue);

    const size_t uint64TypeExpressionBitSizeWithOptional = 100;
    ASSERT_EQ(uint64TypeExpressionBitSizeWithOptional, uint64TypeExpression.bitSizeOf());
}

TEST(UInt64TypeTest, bitSizeOfWithoutOptional)
{
    UInt64TypeExpression uint64TypeExpression;
    const uint32_t uint32Value = 8;
    const uint64_t uint64ValueWithoutOptional = 1;
    const bool boolValue = true;
    uint64TypeExpression.setUint32Value(uint32Value);
    uint64TypeExpression.setUint64Value(uint64ValueWithoutOptional);
    uint64TypeExpression.setBoolValue(boolValue);

    const size_t uint64TypeExpressionBitSizeWithoutOptional = 97;
    ASSERT_EQ(uint64TypeExpressionBitSizeWithoutOptional, uint64TypeExpression.bitSizeOf());
}

} // namespace uint64_type
} // namespace expressions
