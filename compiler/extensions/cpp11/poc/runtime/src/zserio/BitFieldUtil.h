#ifndef ZSERIO_BITFIELD_UTIL_H_INC
#define ZSERIO_BITFIELD_UTIL_H_INC

#include <cstddef>

#include "Types.h"

namespace zserio
{

int64_t getBitFieldLowerBound(size_t length, bool isSigned);
uint64_t getBitFieldUpperBound(size_t length, bool isSigned);

} // namespace zserio

#endif // ifndef ZSERIO_BITFIELD_UTIL_H_INC
