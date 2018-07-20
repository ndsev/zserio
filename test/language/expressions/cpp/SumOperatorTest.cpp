#include "math.h"

#include "gtest/gtest.h"

#include "expressions/sum_operator/SumFunction.h"

namespace expressions
{
namespace sum_operator
{

TEST(SumOperatorTest, GetSumFixedArray)
{
    SumFunction sumFunction;
    zserio::UInt8Array fixedArray;
    uint16_t expectedSum = 0;
    for (uint8_t i = 1; i <= 10; ++i)
    {
        fixedArray.push_back(i);
        expectedSum += i;
    }
    sumFunction.setFixedArray(fixedArray);
    ASSERT_EQ(expectedSum, sumFunction.getSumFixedArray());
}

} // namespace sum_operator
} // namespace expressions
