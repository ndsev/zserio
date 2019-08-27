#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/BitFieldUtil.h"

namespace zserio
{

static void checkBitFieldLength(size_t length, bool isSigned)
{
    const size_t maxSignedBitFieldLength = 64;
    const size_t maxUnsignedBitFieldLength = 63;
    const size_t maxBitFieldLength = isSigned ? maxSignedBitFieldLength : maxUnsignedBitFieldLength;

    if (length == 0 || length > maxBitFieldLength)
    {
        throw CppRuntimeException("Asking for bound of bitfield with invalid length " +
                convertToString(length));
    }
}

int64_t getBitFieldLowerBound(size_t length, bool isSigned)
{
    checkBitFieldLength(length, isSigned);

    if (isSigned)
        return -(INT64_C(1) << (length - 1));
    else
        return 0;
}

uint64_t getBitFieldUpperBound(size_t length, bool isSigned)
{
    checkBitFieldLength(length, isSigned);

    if (isSigned)
        return (UINT64_C(1) << (length - 1)) - 1;
    else
        return (UINT64_C(1) << length) - 1;
}

} // namespace zserio
