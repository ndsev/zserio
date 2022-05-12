#ifndef ZSERIO_PMR_I_REFLECTABLE_H_INC
#define ZSERIO_PMR_I_REFLECTABLE_H_INC

#include "zserio/IReflectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

// needed to have proper typedefs
#include "zserio/pmr/AnyHolder.h"
#include "zserio/pmr/String.h"
#include "zserio/pmr/BitBuffer.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to reflectable interface provided for convenience - using PropagatingPolymorphicAllocator<uint8_t>.
 */
/** \{ */
using IReflectable = IBasicReflectable<PropagatingPolymorphicAllocator<uint8_t>>;
using IReflectablePtr = IBasicReflectablePtr<PropagatingPolymorphicAllocator<uint8_t>>;
using IReflectableConstPtr = IBasicReflectableConstPtr<PropagatingPolymorphicAllocator<uint8_t>>;
/** \} */

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_I_REFLECTABLE_H_INC
