#include "zserio/BitFieldUtil.h"
#include "zserio/CppRuntimeException.h"

namespace zserio
{

static void checkBitFieldLength(size_t length)
{
    if (length == 0 || length > 64)
    {
        throw CppRuntimeException("Asking for bound of bitfield with invalid length ") << length << "!";
    }
}

int64_t getBitFieldLowerBound(size_t length, bool isSigned)
{
    checkBitFieldLength(length);

    if (isSigned)
    {
        return -static_cast<int64_t>((UINT64_C(1) << (length - 1)) - 1) - 1;
    }
    else
    {
        return 0;
    }
}

uint64_t getBitFieldUpperBound(size_t length, bool isSigned)
{
    checkBitFieldLength(length);

    if (isSigned)
    {
        return (UINT64_C(1) << (length - 1)) - 1;
    }
    else
    {
        return length == 64 ? UINT64_MAX : ((UINT64_C(1) << length) - 1);
    }
}

} // namespace zserio
