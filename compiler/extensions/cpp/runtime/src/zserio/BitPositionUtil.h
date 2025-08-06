#ifndef ZSERIO_BITPOSITION_UTIL_H_INC
#define ZSERIO_BITPOSITION_UTIL_H_INC

#include "zserio/Types.h"

namespace zserio
{

/**
 * Aligns the bit size to the given alignment value.
 *
 * \param alignmentValue Value to align.
 * \param bitPosition Current bit position where to apply alignment.
 *
 * \return Aligned bit position.
 */
inline constexpr size_t alignTo(size_t alignmentValue, size_t bitPosition)
{
    return (bitPosition > 0 && alignmentValue != 0)
            ? (((bitPosition - 1) / alignmentValue) + 1) * alignmentValue
            : bitPosition;
}

} // namespace zserio

#endif // ifndef ZSERIO_BITPOSITION_UTIL_H_INC
