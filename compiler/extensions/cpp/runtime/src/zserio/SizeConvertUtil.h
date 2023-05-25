#ifndef ZSERIO_SIZE_CONVERT_UTIL_H_INC
#define ZSERIO_SIZE_CONVERT_UTIL_H_INC

#include <cstddef>

#include "zserio/Types.h"

namespace zserio
{
    /**
     * Converts size (array size, string size or bit buffer size) of type size_t to uint32_t value.
     *
     * \param value Size of type size_t to convert.
     *
     * \return uint32_t value converted from size.
     *
     * \throw CppRuntimeException when input value is not convertible to uint32_t value.
     */
    uint32_t convertSizeToUInt32(size_t value);

    /**
     * Converts uint64_t value to size (array size, string size of bit buffer size).
     *
     * \param value uint64_t value to convert.
     *
     * \return size_t value converted from uint64_t value.
     *
     * \throw CppRuntimeException when input value is not convertible to size_t value.
     */
    size_t convertUInt64ToSize(uint64_t value);
} // namespace zserio

#endif // ZSERIO_SIZE_CONVERT_UTIL_H_INC
