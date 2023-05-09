#ifndef ZSERIO_WALKER_CONST_H_INC
#define ZSERIO_WALKER_CONST_H_INC

#include <cstddef>

namespace zserio
{

/**
 * Constant denoting that a walked value is not an element of an array.
 *
 * Note that in zserio the max usable array size is VARSIZE_MAX.
 */
static constexpr size_t WALKER_NOT_ELEMENT = static_cast<size_t>(-1);

} // namespace zserio

#endif // ZSERIO_WALKER_CONST_H_INC
