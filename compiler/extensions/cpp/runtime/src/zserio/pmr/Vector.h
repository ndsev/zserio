#ifndef ZSERIO_PMR_VECTOR_H_INC
#define ZSERIO_PMR_VECTOR_H_INC

#include "zserio/Vector.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to std::vector provided for convenience - using PropagatingPolymorphicAllocator.
 */
template <typename T>
using vector = zserio::vector<T, PropagatingPolymorphicAllocator<T>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_VECTOR_H_INC
