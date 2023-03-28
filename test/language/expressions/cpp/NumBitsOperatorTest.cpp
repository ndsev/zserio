#include <cmath>

#include "gtest/gtest.h"

#include "expressions/numbits_operator/NumBitsFunctions.h"

namespace expressions
{
namespace numbits_operator
{

namespace
{
    uint8_t calculateExpectedNumBits(uint64_t value)
    {
        if (value == 0)
            return 0;

        if (value == 1)
            return 1;

        return static_cast<uint8_t>(log2(static_cast<double>(value - 1)) + 1);
    }
}

TEST(NumBitsOperatorTest, GetNumBits8)
{
    NumBitsFunctions numBitsFunctions;
    for (uint8_t value8 = 1; value8 < 255; ++value8)
    {
        numBitsFunctions.setValue8(value8);
        ASSERT_EQ(calculateExpectedNumBits(value8), numBitsFunctions.funcGetNumBits8()) <<
                "value8=" << static_cast<uint32_t>(value8);
    }
}

TEST(NumBitsOperatorTest, GetNumBits16)
{
    NumBitsFunctions numBitsFunctions;
    for (uint16_t value16 = 1; value16 < 65535; ++value16)
    {
        numBitsFunctions.setValue16(value16);
        ASSERT_EQ(calculateExpectedNumBits(value16), numBitsFunctions.funcGetNumBits16()) <<
                "value16=" << value16;
    }
}

TEST(NumBitsOperatorTest, GetNumBits32)
{
    NumBitsFunctions numBitsFunctions;
    for (uint32_t value32 = 1; value32 < (1U << 31U); value32 <<= 1U)
    {
        numBitsFunctions.setValue32(value32);
        ASSERT_EQ(calculateExpectedNumBits(value32), numBitsFunctions.funcGetNumBits32()) <<
                "value32=" << value32;
    }
}

TEST(NumBitsOperatorTest, GetNumBits64)
{
    NumBitsFunctions numBitsFunctions;
    for (uint64_t value64 = 1; value64 < (UINT64_C(1) << 48U); value64 <<= 1U)
    {
        numBitsFunctions.setValue64(value64);
        ASSERT_EQ(calculateExpectedNumBits(value64), numBitsFunctions.funcGetNumBits64())
                << "value64=" << value64;
    }
}

} // namespace numbits_operator
} // namespace expressions
