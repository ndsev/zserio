#ifndef ZSERIO_PMR_PACKING_CONTEXT_H_INC
#define ZSERIO_PMR_PACKING_CONTEXT_H_INC

#include "zserio/PackingContext.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

using PackingContextNode = BasicPackingContextNode<PropagatingPolymorphicAllocator<uint8_t>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_PACKING_CONTEXT_H_INC
