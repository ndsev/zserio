#ifndef ZSERIO_PMR_I_INTROSPECTABLE_H_INC
#define ZSERIO_PMR_I_INTROSPECTABLE_H_INC

#include "zserio/IIntrospectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to introspectable interface provided for convenience -
 * using PropagatingPolymorphicAllocator<uint8_t>.
 */
/** \{ */
using IIntrospectable = zserio::IIntrospectable<PropagatingPolymorphicAllocator<uint8_t>>;
using IIntrospectablePtr = IIntrospectable::IIntrospectablePtr;
/** \} */

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_I_INTROSPECTABLE_H_INC
