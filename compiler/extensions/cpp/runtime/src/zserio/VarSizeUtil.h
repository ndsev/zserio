#ifndef ZSERIO_VARSIZE_UTIL_H_INC
#define ZSERIO_VARSIZE_UTIL_H_INC

#include <cstddef>

#include "zserio/Types.h"

namespace zserio
{
    /**
     * Converts array size of type size_t to uint32_t value.
     *
     * \param value Array size of type size_t to convert.
     *
     * \return uint32_t uint32_t value converted from array size.
     *
     * \throw CppRuntimeException when input value is not convertible to uint32_t value.
     */
    uint32_t convertArraySizeToUInt32(size_t value);
} // namespace zserio

#endif // ZSERIO_VARSIZE_UTIL_H_INC
