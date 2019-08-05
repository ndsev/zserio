#ifndef ZSERIO_BITSIZEOF_CALCULATOR_H_INC
#define ZSERIO_BITSIZEOF_CALCULATOR_H_INC

#include <cstddef>
#include <string>

#include "Types.h"

namespace zserio
{

size_t bitSizeOfVarInt16(int16_t value);
size_t bitSizeOfVarInt32(int32_t value);
size_t bitSizeOfVarInt64(int64_t value);

size_t bitSizeOfVarUInt16(uint16_t value);
size_t bitSizeOfVarUInt32(uint32_t value);
size_t bitSizeOfVarUInt64(uint64_t value);

size_t bitSizeOfVarInt(int64_t value);
size_t bitSizeOfVarUInt(uint64_t value);

size_t bitSizeOfString(const std::string& value);

} // namespace zserio

#endif // ifndef ZSERIO_BITSIZEOF_CALCULATOR_H_INC
