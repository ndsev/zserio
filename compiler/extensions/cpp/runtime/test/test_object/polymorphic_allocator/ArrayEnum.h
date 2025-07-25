/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, polymorphicAllocator.
 */

#ifndef TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_ENUM_H
#define TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_ENUM_H

#include <array>

#include <zserio/Enums.h>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/DeltaContext.h>
#include <zserio/pmr/ITypeInfo.h>
#include <zserio/pmr/IReflectable.h>
#include <zserio/ArrayTraits.h>
#include <zserio/Types.h>

namespace test_object
{
namespace polymorphic_allocator
{

enum class ArrayEnum : int8_t
{
    VALUE1 = INT8_C(0),
    VALUE2 = INT8_C(1),
    VALUE3 = INT8_C(2)
};

} // namespace polymorphic_allocator
} // namespace test_object

namespace zserio
{

// This is full specialization of enumeration traits and methods for ArrayEnum enumeration.
template <>
struct EnumTraits<::test_object::polymorphic_allocator::ArrayEnum>
{
    static constexpr ::std::array<const char*, 3> names =
    {{
        "VALUE1",
        "VALUE2",
        "VALUE3"
    }};

    static constexpr ::std::array<::test_object::polymorphic_allocator::ArrayEnum, 3> values =
    {{
        ::test_object::polymorphic_allocator::ArrayEnum::VALUE1,
        ::test_object::polymorphic_allocator::ArrayEnum::VALUE2,
        ::test_object::polymorphic_allocator::ArrayEnum::VALUE3
    }};

    static constexpr const char* enumName = "ArrayEnum";
};

template <>
const ::zserio::pmr::ITypeInfo& enumTypeInfo<::test_object::polymorphic_allocator::ArrayEnum, ::zserio::pmr::PropagatingPolymorphicAllocator<>>();

template <>
::zserio::pmr::IReflectablePtr enumReflectable(::test_object::polymorphic_allocator::ArrayEnum value, const ::zserio::pmr::PropagatingPolymorphicAllocator<>& allocator);

template <>
size_t enumToOrdinal<::test_object::polymorphic_allocator::ArrayEnum>(::test_object::polymorphic_allocator::ArrayEnum value);

template <>
::test_object::polymorphic_allocator::ArrayEnum valueToEnum<::test_object::polymorphic_allocator::ArrayEnum>(
        typename ::std::underlying_type<::test_object::polymorphic_allocator::ArrayEnum>::type rawValue);

template <>
uint32_t enumHashCode<::test_object::polymorphic_allocator::ArrayEnum>(::test_object::polymorphic_allocator::ArrayEnum value);

template <>
void initPackingContext<::zserio::DeltaContext, ::test_object::polymorphic_allocator::ArrayEnum>(::zserio::DeltaContext& context, ::test_object::polymorphic_allocator::ArrayEnum value);

template <>
size_t bitSizeOf<::test_object::polymorphic_allocator::ArrayEnum>(::test_object::polymorphic_allocator::ArrayEnum value);

template <>
size_t bitSizeOf<::zserio::DeltaContext, ::test_object::polymorphic_allocator::ArrayEnum>(::zserio::DeltaContext& context, ::test_object::polymorphic_allocator::ArrayEnum value);

template <>
size_t initializeOffsets<::test_object::polymorphic_allocator::ArrayEnum>(size_t bitPosition, ::test_object::polymorphic_allocator::ArrayEnum value);

template <>
size_t initializeOffsets<::zserio::DeltaContext, ::test_object::polymorphic_allocator::ArrayEnum>(::zserio::DeltaContext& context, size_t bitPosition,
        ::test_object::polymorphic_allocator::ArrayEnum value);

template <>
::test_object::polymorphic_allocator::ArrayEnum read<::test_object::polymorphic_allocator::ArrayEnum>(::zserio::BitStreamReader& in);

template <>
::test_object::polymorphic_allocator::ArrayEnum read<::test_object::polymorphic_allocator::ArrayEnum, ::zserio::DeltaContext>(::zserio::DeltaContext& context, ::zserio::BitStreamReader& in);

template <>
void write<::test_object::polymorphic_allocator::ArrayEnum>(::zserio::BitStreamWriter& out, ::test_object::polymorphic_allocator::ArrayEnum value);

template <>
void write<::zserio::DeltaContext, ::test_object::polymorphic_allocator::ArrayEnum>(::zserio::DeltaContext& context, ::zserio::BitStreamWriter& out,
        ::test_object::polymorphic_allocator::ArrayEnum value);

} // namespace zserio

#endif // TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_ENUM_H
