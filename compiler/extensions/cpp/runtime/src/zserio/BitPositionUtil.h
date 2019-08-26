#ifndef ZSERIO_BITPOSITION_UTIL_H_INC
#define ZSERIO_BITPOSITION_UTIL_H_INC

#include <cstddef>

namespace zserio
{

/**
 * Constant defining num bits per a single byte (8).
 */
static const size_t NUM_BITS_PER_BYTE = 8;

size_t alignTo(size_t alignmentValue, size_t bitPosition);
size_t bitsToBytes(size_t numBits);
size_t bytesToBits(size_t numBytes);

} // namespace zserio

#endif // ifndef ZSERIO_BITPOSITION_UTIL_H_INC
