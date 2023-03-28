#include "gtest/gtest.h"

#include "expressions/cast_uint64_to_uint8/CastUInt64ToUInt8Expression.h"

namespace expressions
{
namespace cast_uint64_to_uint8
{

TEST(CastUInt64ToUInt8Test, uint8ValueUsingUInt64Value)
{
    CastUInt64ToUInt8Expression castUInt64ToUInt8Expression;
    uint64_t uint64Value = 0xFFFFFFFFFFFFFFFEULL;
    castUInt64ToUInt8Expression.setUint64Value(uint64Value);
    castUInt64ToUInt8Expression.setUseConstant(false);
    const uint8_t expectedUInt8Value = static_cast<uint8_t>(uint64Value);
    ASSERT_EQ(expectedUInt8Value, castUInt64ToUInt8Expression.funcUint8Value());
}

TEST(CastUInt64ToUInt8Test, uint8ValueUsingConstant)
{
    CastUInt64ToUInt8Expression castUInt64ToUInt8Expression;
    const uint64_t uint64Value = 0xFFFFFFFFFFFFFFFEULL;
    castUInt64ToUInt8Expression.setUint64Value(uint64Value);
    castUInt64ToUInt8Expression.setUseConstant(true);
    const uint8_t expectedUInt8Value = 1;
    ASSERT_EQ(expectedUInt8Value, castUInt64ToUInt8Expression.funcUint8Value());
}

} // namespace cast_uint64_to_uint8
} // namespace expressions
