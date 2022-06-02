#ifndef ZSERIO_PMR_I_TYPE_INFO_H_INC
#define ZSERIO_PMR_I_TYPE_INFO_H_INC

#include "zserio/ITypeInfo.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

/** Typedef provided for convenience - using default PropagatingPolymorphicAllocator<uint8_t>. */
/** \{ */
using ITypeInfo = IBasicTypeInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using FieldInfo = BasicFieldInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using ParameterInfo = BasicParameterInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using FunctionInfo = BasicFunctionInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using CaseInfo = BasicCaseInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using ColumnInfo = BasicColumnInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using TableInfo = BasicTableInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using TemplateArgumentInfo = BasicTemplateArgumentInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using MessageInfo = BasicMessageInfo<PropagatingPolymorphicAllocator<uint8_t>>;
using MethodInfo = BasicMethodInfo<PropagatingPolymorphicAllocator<uint8_t>>;
/** \} */

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_I_TYPE_INFO_H_INC
