#include "zserio/CppRuntimeException.h"
#include "zserio/BitPositionUtil.h"

namespace zserio
{

size_t alignTo(size_t alignmentValue, size_t bitPosition)
{
    return (bitPosition > 0 && alignmentValue != 0)
            ? (((bitPosition - 1) / alignmentValue) + 1) * alignmentValue
            : bitPosition;
}

} // namespace zserio
