#ifndef ZSERIO_PMR_INTROSPECTABLE_H_INC
#define ZSERIO_PMR_INTROSPECTABLE_H_INC

#include "zserio/Introspectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

using Int8Introspectable = BasicInt8Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using Int16Introspectable = BasicInt16Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using Int32Introspectable = BasicInt32Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using Int64Introspectable = BasicInt64Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using UInt8Introspectable = BasicUInt8Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using UInt16Introspectable = BasicUInt16Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using UInt32Introspectable = BasicUInt32Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using UInt64Introspectable = BasicUInt64Introspectable<PropagatingPolymorphicAllocator<uint8_t>>;
using StringIntrospectable = BasicStringIntrospectable<PropagatingPolymorphicAllocator<uint8_t>>;
using BitBufferIntrospectable = BasicBitBufferIntrospectable<PropagatingPolymorphicAllocator<uint8_t>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_INTROSPECTABLE_H_INC
