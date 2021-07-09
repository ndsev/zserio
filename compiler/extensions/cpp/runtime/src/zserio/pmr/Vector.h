#ifndef ZSERIO_PMR_VECTOR_H_INC
#define ZSERIO_PMR_VECTOR_H_INC

#include <vector>
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

template <typename T>
using vector = std::vector<T, PropagatingPolymorphicAllocator<T>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_VECTOR_H_INC
