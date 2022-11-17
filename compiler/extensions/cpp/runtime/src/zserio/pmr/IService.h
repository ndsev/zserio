#ifndef ZSERIO_I_SERVICE_H_INC
#define ZSERIO_I_SERVICE_H_INC

#include "zserio/IService.h"
#include "zserio/pmr/PolymorphicAllocator.h"

// needed to have proper pmr typedefs
#include "zserio/pmr/IReflectable.h"

namespace zserio
{
namespace pmr
{

/** Typedef to service interface provided for convenience - using PropagatingPolymorphicAllocator<uint8_t>. */
/** \{ */
using IServiceData = IBasicServiceData<PropagatingPolymorphicAllocator<uint8_t>>;
using IServiceDataPtr = IBasicServiceDataPtr<PropagatingPolymorphicAllocator<uint8_t>>;
using IService = IBasicService<PropagatingPolymorphicAllocator<uint8_t>>;
using IServiceClient = IBasicServiceClient<PropagatingPolymorphicAllocator<uint8_t>>;
/** \} */

/**
 * Typedef to service data implementation provided for convenience - using
 * PropagatingPolymorphicAllocator<uint8_t>.
 */
using ReflectableServiceData = BasicReflectableServiceData<PropagatingPolymorphicAllocator<uint8_t>>;
using ObjectServiceData = BasicObjectServiceData<PropagatingPolymorphicAllocator<uint8_t>>;
using RawServiceDataHolder = BasicRawServiceDataHolder<PropagatingPolymorphicAllocator<uint8_t>>;
using RawServiceDataView = BasicRawServiceDataView<PropagatingPolymorphicAllocator<uint8_t>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_I_SERVICE_H_INC
