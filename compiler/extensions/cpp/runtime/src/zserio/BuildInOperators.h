#ifndef ZSERIO_BUILD_IN_OPERATORS_H_INC
#define ZSERIO_BUILD_IN_OPERATORS_H_INC

#include "Types.h"

namespace zserio
{

/**
 * Gets the minimum number of bits required to encode <tt>value-1</tt>.
 *
 * This method implements Zserio build-in operator <tt>numbits</tt>.
 *
 * <b>Note:</b> Please note that this method returns 1 if value is zero.
 *
 * Examples:
 * <tt>numbits(0) = 1</tt>
 * <tt>numbits(1) = 1</tt>
 * <tt>numbits(2) = 1</tt>
 * <tt>numbits(3) = 2</tt>
 * <tt>numbits(4) = 2</tt>
 * <tt>numbits(8) = 3</tt>
 * <tt>numbits(16) = 4</tt>
 *
 * @param value The value from which to calculate number of bits.
 *
 * @return Number of bis required to encode <tt>value-1</tt> or <tt>1</tt>
 *         if <tt>value</tt> is <tt>0</tt>.
 */
uint8_t getNumBits(uint64_t value);

} // namespace zserio

#endif // ifndef ZSERIO_BUILD_IN_OPERATORS_H_INC
