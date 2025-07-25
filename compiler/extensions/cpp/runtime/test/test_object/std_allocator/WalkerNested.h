/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, stdAllocator.
 */

#ifndef TEST_OBJECT_STD_ALLOCATOR_WALKER_NESTED_H
#define TEST_OBJECT_STD_ALLOCATOR_WALKER_NESTED_H

#include <zserio/Traits.h>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
#include <zserio/ParsingInfo.h>
#include <zserio/ITypeInfo.h>
#include <zserio/IReflectable.h>
#include <memory>
#include <zserio/ArrayTraits.h>
#include <zserio/String.h>

namespace test_object
{
namespace std_allocator
{

class WalkerNested
{
public:
    using allocator_type = ::std::allocator<uint8_t>;

    WalkerNested() noexcept :
            WalkerNested(allocator_type())
    {}

    explicit WalkerNested(const allocator_type& allocator) noexcept;

    template <typename ZSERIO_T_text = ::zserio::string<>,
            ::zserio::is_field_constructor_enabled_t<ZSERIO_T_text, WalkerNested, allocator_type> = 0>
    explicit WalkerNested(
            ZSERIO_T_text&& text_,
            const allocator_type& allocator = allocator_type()) :
            WalkerNested(allocator)
    {
        m_text_ = ::std::forward<ZSERIO_T_text>(text_);
    }

    explicit WalkerNested(::zserio::BitStreamReader& in, const allocator_type& allocator = allocator_type());

    ~WalkerNested() = default;

    WalkerNested(const WalkerNested&) = default;
    WalkerNested& operator=(const WalkerNested&) = default;

    WalkerNested(WalkerNested&&) = default;
    WalkerNested& operator=(WalkerNested&&) = default;

    WalkerNested(::zserio::PropagateAllocatorT,
            const WalkerNested& other, const allocator_type& allocator);

    static const ::zserio::ITypeInfo& typeInfo();
    ::zserio::IReflectableConstPtr reflectable(const allocator_type& allocator = allocator_type()) const;
    ::zserio::IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    const ::zserio::string<>& getText() const;
    ::zserio::string<>& getText();
    void setText(const ::zserio::string<>& text_);
    void setText(::zserio::string<>&& text_);

    size_t bitSizeOf(size_t bitPosition = 0) const;

    size_t initializeOffsets(size_t bitPosition = 0);

    bool operator==(const WalkerNested& other) const;

    bool operator<(const WalkerNested& other) const;

    uint32_t hashCode() const;

    void write(::zserio::BitStreamWriter& out) const;

    const ::zserio::ParsingInfo& parsingInfo() const;

private:
    ::zserio::string<> readText(::zserio::BitStreamReader& in,
            const allocator_type& allocator);

    ::zserio::ParsingInfo m_parsingInfo;
    ::zserio::string<> m_text_;
};

} // namespace std_allocator
} // namespace test_object

#endif // TEST_OBJECT_STD_ALLOCATOR_WALKER_NESTED_H
