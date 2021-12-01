#ifndef ZSERIO_PMR_I_REFLECTABLE_H_INC
#define ZSERIO_PMR_I_REFLECTABLE_H_INC

#include "zserio/IReflectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to reflectable interface provided for convenience -
 * using PropagatingPolymorphicAllocator<uint8_t>.
 */
/** \{ */
using IReflectable = zserio::IReflectable<PropagatingPolymorphicAllocator<uint8_t>>;
using IReflectablePtr = IReflectable::IReflectablePtr;
/** \} */

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_I_REFLECTABLE_H_INC
