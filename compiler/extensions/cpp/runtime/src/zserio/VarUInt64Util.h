#ifndef ZSERIO_VARUINT64_UTIL_H_INC
#define ZSERIO_VARUINT64_UTIL_H_INC

#include "Types.h"

namespace zserio
{
    /**
     * Converts uint64_t read from varuint64 to int32_t. Used for conversion to ChoiceTag enum in unions.
     *
     * \param value Value stored in varuint64 to convert.
     *
     * \return int32_t Signed 32-bit integer converted from varuint64.
     *
     * \throw CppRuntimeException when input value is not convertible to int32_t.
     */
    int32_t convertVarUInt64ToInt32(uint64_t value);

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
