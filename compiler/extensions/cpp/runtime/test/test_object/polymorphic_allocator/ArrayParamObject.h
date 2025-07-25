/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, polymorphicAllocator.
 */

#ifndef TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_PARAM_OBJECT_H
#define TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_PARAM_OBJECT_H

#include <zserio/Traits.h>
#include <zserio/NoInit.h>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
#include <zserio/DeltaContext.h>
#include <zserio/ParsingInfo.h>
#include <zserio/pmr/ITypeInfo.h>
#include <zserio/pmr/IReflectable.h>
#include <zserio/pmr/PolymorphicAllocator.h>
#include <zserio/ArrayTraits.h>
#include <zserio/Types.h>

#include <test_object/polymorphic_allocator/ArrayObject.h>

namespace test_object
{
namespace polymorphic_allocator
{

class ArrayParamObject
{
public:
    class ZserioPackingContext
    {
    public:
        ::zserio::DeltaContext& getValue()
        {
            return m_value_;
        }

    private:
        ::zserio::DeltaContext m_value_;
    };

    using allocator_type = ::zserio::pmr::PropagatingPolymorphicAllocator<>;

    ArrayParamObject() noexcept :
            ArrayParamObject(allocator_type())
    {}

    explicit ArrayParamObject(const allocator_type& allocator) noexcept;

    explicit ArrayParamObject(
            uint32_t value_,
            const allocator_type& allocator = allocator_type()) :
            ArrayParamObject(allocator)
    {
        m_value_ = value_;
    }

    explicit ArrayParamObject(::zserio::BitStreamReader& in,
            ::test_object::polymorphic_allocator::ArrayObject& param_, const allocator_type& allocator = allocator_type());
    explicit ArrayParamObject(ZserioPackingContext& context,
            ::zserio::BitStreamReader& in,
            ::test_object::polymorphic_allocator::ArrayObject& param_, const allocator_type& allocator = allocator_type());

    ~ArrayParamObject() = default;

    ArrayParamObject(const ArrayParamObject& other);
    ArrayParamObject& operator=(const ArrayParamObject& other);

    ArrayParamObject(ArrayParamObject&& other);
    ArrayParamObject& operator=(ArrayParamObject&& other);

    ArrayParamObject(::zserio::NoInitT,
            const ArrayParamObject& other);
    ArrayParamObject& assign(::zserio::NoInitT, const ArrayParamObject& other);

    ArrayParamObject(::zserio::NoInitT, ArrayParamObject&& other);
    ArrayParamObject& assign(::zserio::NoInitT,
            ArrayParamObject&& other);

    ArrayParamObject(::zserio::PropagateAllocatorT,
            const ArrayParamObject& other, const allocator_type& allocator);
    ArrayParamObject(::zserio::PropagateAllocatorT, ::zserio::NoInitT,
            const ArrayParamObject& other, const allocator_type& allocator);

    static const ::zserio::pmr::ITypeInfo& typeInfo();
    ::zserio::pmr::IReflectableConstPtr reflectable(const allocator_type& allocator = allocator_type()) const;
    ::zserio::pmr::IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    void initialize(
            ::test_object::polymorphic_allocator::ArrayObject& param_);
    bool isInitialized() const;

    ::test_object::polymorphic_allocator::ArrayObject& getParam();
    const ::test_object::polymorphic_allocator::ArrayObject& getParam() const;

    uint32_t getValue() const;
    void setValue(uint32_t value_);

    void initPackingContext(ZserioPackingContext& context) const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t bitSizeOf(ZserioPackingContext& context, size_t bitPosition) const;

    size_t initializeOffsets(size_t bitPosition = 0);
    size_t initializeOffsets(ZserioPackingContext& context, size_t bitPosition);

    bool operator==(const ArrayParamObject& other) const;

    bool operator<(const ArrayParamObject& other) const;

    uint32_t hashCode() const;

    void write(::zserio::BitStreamWriter& out) const;
    void write(ZserioPackingContext& context, ::zserio::BitStreamWriter& out) const;

    const ::zserio::ParsingInfo& parsingInfo() const;

private:
    uint32_t readValue(::zserio::BitStreamReader& in);
    uint32_t readValue(ZserioPackingContext& context,
            ::zserio::BitStreamReader& in);

    ::test_object::polymorphic_allocator::ArrayObject* m_param_;
    bool m_isInitialized;
    ::zserio::ParsingInfo m_parsingInfo;
    uint32_t m_value_;
};

} // namespace polymorphic_allocator
} // namespace test_object

#endif // TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_PARAM_OBJECT_H
