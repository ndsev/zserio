#include <cstddef>
#include <limits>

#include "zserio/CppRuntimeException.h"
#include "zserio/RuntimeArch.h"
#include "zserio/SizeConvertUtil.h"

namespace zserio
{

uint32_t convertSizeToUInt32(size_t value)
{
#ifdef ZSERIO_RUNTIME_64BIT
    if (value > static_cast<size_t>(std::numeric_limits<uint32_t>::max()))
    {
        throw CppRuntimeException("SizeConvertUtil: size_t value '")
                << value << "' is out of bounds for conversion to uint32_t type!";
    }
#endif

    return static_cast<uint32_t>(value);
}

size_t convertUInt64ToSize(uint64_t value)
{
#ifndef ZSERIO_RUNTIME_64BIT
    if (value > static_cast<uint64_t>(std::numeric_limits<size_t>::max()))
    {
        throw CppRuntimeException("SizeConvertUtil: uint64_t value '")
                << value << "' is out of bounds for conversion to size_t type!";
    }
#endif

    return static_cast<size_t>(value);
}

} // namespace zserio
