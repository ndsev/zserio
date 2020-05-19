#include <limits>

#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/VarUInt64Util.h"

namespace zserio
{

namespace
{
    template <typename T>
    T convertVarUInt64(uint64_t value)
    {
        if (value > static_cast<uint64_t>(std::numeric_limits<T>::max()))
            throw CppRuntimeException("VarUInt64 value (" + convertToString(value) +
                    ") is out of bounds for conversion!");
        return static_cast<T>(value);
    }
}

size_t convertVarUInt64ToArraySize(uint64_t value)
{
    return convertVarUInt64<size_t>(value);
}

} // namespace zserio
