#include <limits>

#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/VarSizeUtil.h"

namespace zserio
{

uint32_t convertSizeToUInt32(size_t value)
{
    if (value > static_cast<size_t>(std::numeric_limits<uint32_t>::max()))
        throw CppRuntimeException("VarSizeUtil: Size value '" + convertToString(value) +
                "' is out of bounds for conversion to uint32_t type!");

    return static_cast<uint32_t>(value);
}

} // namespace zserio
