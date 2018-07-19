#include "BuildInOperators.h"

namespace zserio
{

uint8_t getNumBits(uint64_t value)
{
    uint8_t result = 1;
    if (value > 0)
    {
        uint64_t current = (value - 1) >> 1;
        while (current > 0)
        {
            result++;
            current >>= 1;
        }
    }

    return result;
}

} // namespace zserio
