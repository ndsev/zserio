#include <limits>
#include <type_traits>

#include "zserio/CppRuntimeException.h"
#include "zserio/VarSizeUtil.h"

namespace zserio
{

uint32_t convertSizeToUInt32(size_t value)
{
#ifdef ZSERIO_RUNTIME_64BIT
    if (value > static_cast<size_t>(std::numeric_limits<uint32_t>::max()))
    {
        throw CppRuntimeException("VarSizeUtil: Size value '") << value <<
                "' is out of bounds for conversion to uint32_t type!";
    }
#endif

    return static_cast<uint32_t>(value);
}

} // namespace zserio
