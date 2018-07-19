#ifndef ZSERIO_VARUINT64_UTIL_H_INC
#define ZSERIO_VARUINT64_UTIL_H_INC

#include "Types.h"

namespace zserio
{
    /**
     * Converts uint64_t read from varuint64 to signed integer. Used e.g. for conversion to enum values.
     *
     * \param value Value stored in varuint64 to convert.
     *
     * \return int Signed integer converted from varuint64.
     *
     * \throw CppRuntimeException when input value is not convertible to int.
     */
    int convertVarUInt64ToInt(uint64_t value);

    /**
     * Converts uint64_t read from varuint64 to array size of type size_t.
     *
     * \param value Value stored in varuint64 to convert.
     *
     * \return size_t Array size converted from varuint64.
     *
     * \throw CppRuntimeException when input value is not convertible to size_t.
     */
    size_t convertVarUInt64ToArraySize(uint64_t value);
} // namespace zserio

#endif // ZSERIO_VARUINT64_UTIL_H_INC
