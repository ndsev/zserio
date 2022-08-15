#ifndef ZSERIO_BUILTIN_BUILT_IN_OPERATORS_H_INC
#define ZSERIO_BUILTIN_BUILT_IN_OPERATORS_H_INC

#include "zserio/Types.h"

namespace zserio
{

namespace builtin
{

/**
 * Checks whether the requiredMask is set within the bitmaskValue.
 *
 * This method implements zserio built-in operator isset.
 *
 * \param bitmaskValue Bitmask value to check.
 * \param requiredMask Mask to use.
 *
 * \return True when the requiredMask is set within the bitmaskValue, false otherwise.
 */
template <typename BITMASK1, typename BITMASK2>
bool isSet(BITMASK1 bitmaskValue, BITMASK2 requiredMask)
{
    return (bitmaskValue & requiredMask) == requiredMask;
}

/**
 * Gets the minimum number of bits required to encode <tt>numValues</tt> different values.
 *
 * This method implements Zserio build-in operator <tt>numbits</tt>.
 *
 * <b>Note:</b> Please note that this method returns 0 if <tt>numValues</tt> is zero.
 *
 * Examples:
 * <tt>numbits(0) = 0</tt>
 * <tt>numbits(1) = 1</tt>
 * <tt>numbits(2) = 1</tt>
 * <tt>numbits(3) = 2</tt>
 * <tt>numbits(4) = 2</tt>
 * <tt>numbits(8) = 3</tt>
 * <tt>numbits(16) = 4</tt>
 *
 * \param numValues The number of different values from which to calculate number of bits.
 *
 * \return Number of bis required to encode <tt>numValues</tt> different values.
 */
uint8_t numBits(uint64_t numValues);

} // namespace builtin

} // namespace zserio

#endif // ifndef ZSERIO_BUILTIN_BUILT_IN_OPERATORS_H_INC
