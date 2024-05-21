#include "zserio/BuiltInOperators.h"

namespace zserio
{

namespace builtin
{

uint8_t numBits(uint64_t numValues)
{
    if (numValues == 0)
    {
        return 0;
    }

    uint8_t result = 1;
    uint64_t current = (numValues - 1U) >> 1U;
    while (current > 0)
    {
        result++;
        current >>= 1U;
    }

    return result;
}

} // namespace builtin

} // namespace zserio
