#ifndef ZSERIO_PMR_ARRAY_TRAITS_H_INC
#define ZSERIO_PMR_ARRAY_TRAITS_H_INC

#include "zserio/ArrayTraits.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to BytesArrayTraits provided for convenience - using PropagatingPolymorphicAllocator.
 */
using BytesArrayTraits = BasicBytesArrayTraits<PropagatingPolymorphicAllocator>;

/**
 * Typedef to StringArrayTraits provided for convenience - using PropagatingPolymorphicAllocator.
 */
using StringArrayTraits = BasicStringArrayTraits<PropagatingPolymorphicAllocator>;

/**
 * Typedef to BitBufferArrayTraits provided for convenience - using PropagatingPolymorphicAllocator.
 */
using BitBufferArrayTraits = BasicBitBufferArrayTraits<PropagatingPolymorphicAllocator>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_ARRAY_TRAITS_H_INC
