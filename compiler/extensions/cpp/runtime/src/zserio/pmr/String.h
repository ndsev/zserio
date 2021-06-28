#ifndef ZSERIO_PMR_STRING_H_INC
#define ZSERIO_PMR_STRING_H_INC

#include "zserio/String.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

using string = zserio::string<PropagatingPolymorphicAllocator<char>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_STRING_H_INC
