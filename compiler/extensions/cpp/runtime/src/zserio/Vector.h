#ifndef ZSERIO_VECTOR_H_INC
#define ZSERIO_VECTOR_H_INC

#include <vector>
#include "zserio/RebindAlloc.h"

namespace zserio
{

/**
 * Typedef to std::vector provided for convenience - using std::allocator<uint8_t>.
 *
 * Automatically rebinds the given allocator.
 */
template <typename T, typename ALLOC = std::allocator<T>>
using vector = std::vector<T, RebindAlloc<ALLOC, T>>;

} // namespace zserio

#endif // ZSERIO_VECTOR_H_INC
