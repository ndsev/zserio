#ifndef ZSERIO_VARSIZE_UTIL_H_INC
#define ZSERIO_VARSIZE_UTIL_H_INC

#include <cstddef>

#include "zserio/Types.h"

namespace zserio
{
    /**
     * Converts size (array size, string size or bit buffer size) of type size_t to uint32_t value.
     *
     * \param value Size of type size_t to convert.
     *
     * \return uint32_t uint32_t value converted from size.
     *
     * \throw CppRuntimeException when input value is not convertible to uint32_t value.
     */
    uint32_t convertSizeToUInt32(size_t value);
} // namespace zserio

#endif // ZSERIO_VARSIZE_UTIL_H_INC
