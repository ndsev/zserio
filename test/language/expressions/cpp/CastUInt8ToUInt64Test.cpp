#include "expressions/cast_uint8_to_uint64/CastUInt8ToUInt64Expression.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace cast_uint8_to_uint64
{

TEST(CastUInt8ToUInt64Test, uint64ValueUsingUInt8Value)
{
    CastUInt8ToUInt64Expression castUInt8ToUInt64Expression;
    const uint8_t uint8Value = 0xBA;
    castUInt8ToUInt64Expression.setUint8Value(uint8Value);
    castUInt8ToUInt64Expression.setUseConstant(false);
    const uint64_t expectedUInt64Value = static_cast<uint64_t>(uint8Value);
    ASSERT_EQ(expectedUInt64Value, castUInt8ToUInt64Expression.funcUint64Value());
}

TEST(CastUInt8ToUInt64Test, uint64ValueUsingConstant)
{
    CastUInt8ToUInt64Expression castUInt8ToUInt64Expression;
    const uint8_t uint8Value = 0xBA;
    castUInt8ToUInt64Expression.setUint8Value(uint8Value);
    castUInt8ToUInt64Expression.setUseConstant(true);
    const uint64_t expectedUInt64Value = static_cast<uint64_t>(1);
    ASSERT_EQ(expectedUInt64Value, castUInt8ToUInt64Expression.funcUint64Value());
}

} // namespace cast_uint8_to_uint64
} // namespace expressions
