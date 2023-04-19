#ifndef ZSERIO_PMR_MAP_H_INC
#define ZSERIO_PMR_MAP_H_INC

#include <map>

#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to std::map provided for convenience - using PropagatingPolymorphicAllocator.
 */
template <typename KEY, typename T, typename COMPARE = std::less<KEY>>
using map = std::map<KEY, T, COMPARE, PropagatingPolymorphicAllocator<std::pair<const KEY, T>>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_MAP_H_INC
