#ifndef ZSERIO_PMR_INTROSPECTABLE_H_INC
#define ZSERIO_PMR_INTROSPECTABLE_H_INC

#include "zserio/Introspectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

using IntrospectableFactory = BasicIntrospectableFactory<PropagatingPolymorphicAllocator<uint8_t>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_INTROSPECTABLE_H_INC
