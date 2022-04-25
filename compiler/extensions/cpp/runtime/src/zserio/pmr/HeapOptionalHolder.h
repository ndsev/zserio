#ifndef ZSERIO_PMR_HEAP_OPTIONAL_HOLDER_H_INC
#define ZSERIO_PMR_HEAP_OPTIONAL_HOLDER_H_INC

#include "zserio/OptionalHolder.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to HeapOptionalHolder provided for convenience - using PropagatingPolymorphicAllocator.
 */
template <typename T>
using HeapOptionalHolder = zserio::HeapOptionalHolder<T, PropagatingPolymorphicAllocator<T>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_HEAP_OPTIONAL_HOLDER_H_INC
