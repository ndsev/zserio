#ifndef ZSERIO_PMR_ARRAY_TRAITS_H_INC
#define ZSERIO_PMR_ARRAY_TRAITS_H_INC

#include "zserio/ArrayTraits.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

using StringArrayTraits = BasicStringArrayTraits<PropagatingPolymorphicAllocator>;

using BitBufferArrayTraits = BasicBitBufferArrayTraits<PropagatingPolymorphicAllocator>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_ARRAY_TRAITS_H_INC
