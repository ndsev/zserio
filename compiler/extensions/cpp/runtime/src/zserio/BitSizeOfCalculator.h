#ifndef ZSERIO_BITSIZEOF_CALCULATOR_H_INC
#define ZSERIO_BITSIZEOF_CALCULATOR_H_INC

#include <cstddef>
#include <string>

#include "Types.h"

namespace zserio
{

size_t getBitSizeOfVarInt16(int16_t value);
size_t getBitSizeOfVarInt32(int32_t value);
size_t getBitSizeOfVarInt64(int64_t value);

size_t getBitSizeOfVarUInt16(uint16_t value);
size_t getBitSizeOfVarUInt32(uint32_t value);
size_t getBitSizeOfVarUInt64(uint64_t value);

size_t getBitSizeOfVarInt(int64_t value);
size_t getBitSizeOfVarUInt(uint64_t value);

size_t getBitSizeOfString(const std::string& value);

} // namespace zserio

#endif // ifndef ZSERIO_BITSIZEOF_CALCULATOR_H_INC
