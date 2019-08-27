#ifndef ZSERIO_BITFIELD_UTIL_H_INC
#define ZSERIO_BITFIELD_UTIL_H_INC

#include <cstddef>

#include "zserio/Types.h"

namespace zserio
{

/**
 * Calculates lower bound for the given bit field.
 *
 * \param length Length of the bit field.
 * \param isSigned Whether the bit field is signed.
 *
 * \return Lower bound for the bit field.
 */
int64_t getBitFieldLowerBound(size_t length, bool isSigned);

/**
 * Calculates lower bound for the given bit field.
 *
 * \param length Length of the bit field.
 * \param isSigned Whether the bit field is signed.
 *
 * \return Upper bound for the bit field.
 */
uint64_t getBitFieldUpperBound(size_t length, bool isSigned);

} // namespace zserio

#endif // ifndef ZSERIO_BITFIELD_UTIL_H_INC
