#ifndef ZSERIO_PMR_UNIQUE_PTR_H_INC
#define ZSERIO_PMR_UNIQUE_PTR_H_INC

#include "zserio/UniquePtr.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/**
 * Typedef to std::unique_ptr provided for convenience - using PropagatingPolymorphicAllocator<uint8_t>.
 */
template <typename T>
using unique_ptr = zserio::unique_ptr<T, PropagatingPolymorphicAllocator<T>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_UNIQUE_PTR_H_INC
