/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, stdAllocator.
 */

#ifndef TEST_OBJECT_STD_ALLOCATOR_SERIALIZE_NESTED_H
#define TEST_OBJECT_STD_ALLOCATOR_SERIALIZE_NESTED_H

#include <zserio/Traits.h>
#include <zserio/NoInit.h>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
#include <zserio/ParsingInfo.h>
#include <zserio/ITypeInfo.h>
#include <zserio/IReflectable.h>
#include <memory>
#include <zserio/OptionalHolder.h>
#include <zserio/ArrayTraits.h>
#include <zserio/Types.h>

namespace test_object
{
namespace std_allocator
{

class SerializeNested
{
public:
    using allocator_type = ::std::allocator<uint8_t>;

    SerializeNested() noexcept :
            SerializeNested(allocator_type())
    {}

    explicit SerializeNested(const allocator_type& allocator) noexcept;

    template <typename ZSERIO_T_optionalValue = uint32_t>
    SerializeNested(
            uint8_t offset_,
            ZSERIO_T_optionalValue&& optionalValue_,
            const allocator_type& allocator = allocator_type()) :
            SerializeNested(allocator)
    {
        m_offset_ = offset_;
        m_optionalValue_ = ::std::forward<ZSERIO_T_optionalValue>(optionalValue_);
    }

    explicit SerializeNested(::zserio::BitStreamReader& in,
            int8_t param_, const allocator_type& allocator = allocator_type());

    ~SerializeNested() = default;

    SerializeNested(const SerializeNested& other);
    SerializeNested& operator=(const SerializeNested& other);

    SerializeNested(SerializeNested&& other);
    SerializeNested& operator=(SerializeNested&& other);

    SerializeNested(::zserio::NoInitT,
            const SerializeNested& other);
    SerializeNested& assign(::zserio::NoInitT, const SerializeNested& other);

    SerializeNested(::zserio::NoInitT, SerializeNested&& other);
    SerializeNested& assign(::zserio::NoInitT,
            SerializeNested&& other);

    SerializeNested(::zserio::PropagateAllocatorT,
            const SerializeNested& other, const allocator_type& allocator);
    SerializeNested(::zserio::PropagateAllocatorT, ::zserio::NoInitT,
            const SerializeNested& other, const allocator_type& allocator);

    static const ::zserio::ITypeInfo& typeInfo();
    ::zserio::IReflectableConstPtr reflectable(const allocator_type& allocator = allocator_type()) const;
    ::zserio::IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    void initialize(
            int8_t param_);
    bool isInitialized() const;

    int8_t getParam() const;

    uint8_t getOffset() const;
    void setOffset(uint8_t offset_);

    uint32_t getOptionalValue() const;
    void setOptionalValue(uint32_t optionalValue_);
    bool isOptionalValueUsed() const;
    bool isOptionalValueSet() const;
    void resetOptionalValue();

    size_t bitSizeOf(size_t bitPosition = 0) const;

    size_t initializeOffsets(size_t bitPosition = 0);

    bool operator==(const SerializeNested& other) const;

    bool operator<(const SerializeNested& other) const;

    uint32_t hashCode() const;

    void write(::zserio::BitStreamWriter& out) const;

    const ::zserio::ParsingInfo& parsingInfo() const;

private:
    uint8_t readOffset(::zserio::BitStreamReader& in);
    ::zserio::InplaceOptionalHolder<uint32_t> readOptionalValue(::zserio::BitStreamReader& in);

    int8_t m_param_;
    bool m_isInitialized;
    ::zserio::ParsingInfo m_parsingInfo;
    uint8_t m_offset_;
    ::zserio::InplaceOptionalHolder<uint32_t> m_optionalValue_;
};

} // namespace std_allocator
} // namespace test_object

#endif // TEST_OBJECT_STD_ALLOCATOR_SERIALIZE_NESTED_H
