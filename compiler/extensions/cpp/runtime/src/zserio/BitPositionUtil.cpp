#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/BitPositionUtil.h"

namespace zserio
{

size_t alignTo(size_t alignmentValue, size_t bitPosition)
{
    return (bitPosition > 0 && alignmentValue != 0)
            ? (((bitPosition - 1) / alignmentValue) + 1) * alignmentValue
            : bitPosition;
}

size_t bitsToBytes(size_t numBits)
{
    const size_t numBytes = numBits / NUM_BITS_PER_BYTE;
    if (numBytes * NUM_BITS_PER_BYTE != numBits)
    {
        throw CppRuntimeException("bitsToBytes: " + convertToString(numBits) + " is not a multiple of " +
                convertToString(NUM_BITS_PER_BYTE));
    }

    return numBytes;
}

size_t bytesToBits(size_t numBytes)
{
    return numBytes * NUM_BITS_PER_BYTE;
}

} // namespace zserio
