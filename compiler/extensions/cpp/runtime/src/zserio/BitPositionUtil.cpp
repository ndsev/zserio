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
    const size_t numBytes = numBits / 8;
    if (numBytes * 8 != numBits)
    {
        throw CppRuntimeException("bitsToBytes: " + convertToString(numBits) + " is not a multiple of 8");
    }

    return numBytes;
}

size_t bytesToBits(size_t numBytes)
{
    return numBytes * 8;
}

} // namespace zserio
