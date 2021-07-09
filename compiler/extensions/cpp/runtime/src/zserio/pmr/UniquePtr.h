#ifndef ZSERIO_PMR_UNIQUE_PTR_H_INC
#define ZSERIO_PMR_UNIQUE_PTR_H_INC

#include "zserio/UniquePtr.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

template <typename T>
using unique_ptr = zserio::unique_ptr<T, PropagatingPolymorphicAllocator<T>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_UNIQUE_PTR_H_INC
