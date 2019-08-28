#ifndef ZSERIO_BITPOSITION_UTIL_H_INC
#define ZSERIO_BITPOSITION_UTIL_H_INC

#include <cstddef>

namespace zserio
{

/**
 * Constant defining num bits per a single byte (8).
 */
static const size_t NUM_BITS_PER_BYTE = 8;

/**
 * Aligns the bit size to the given alignment value.
 *
 * \param alignmentValue Value to align.
 * \param bitPosition Current bit position where to apply alignment.
 *
 * \return Aligned bit position.
 */
size_t alignTo(size_t alignmentValue, size_t bitPosition);

/**
 * Converts number of bits to bytes.
 *
 * \param numBits The number of bits to convert.
 *
 * \return Number of bytes.
 *
 * \throw CppRuntimeException if number of bits to convert is not divisible by 8.
 */
size_t bitsToBytes(size_t numBits);

/**
 * Converts number of bytes to bits.
 *
 * \param numBytes The n number of bytes to convert.
 *
 * \return Number of bits.
 */
size_t bytesToBits(size_t numBytes);

} // namespace zserio

#endif // ifndef ZSERIO_BITPOSITION_UTIL_H_INC
