/**
 * Automatically generated by Zserio C++ extension version 2.9.0-pre1.
 * Generator setup: writerCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, stdAllocator.
 */

#ifndef TEST_OBJECT_STD_ALLOC_WALKER_NESTED_H
#define TEST_OBJECT_STD_ALLOC_WALKER_NESTED_H

#include <zserio/Traits.h>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
#include <zserio/ITypeInfo.h>
#include <zserio/IReflectable.h>
#include <memory>
#include <zserio/PackingContext.h>
#include <zserio/ArrayTraits.h>
#include <zserio/String.h>

namespace test_object
{
namespace std_alloc
{

class WalkerNested
{
public:
    using allocator_type = ::std::allocator<uint8_t>;

    explicit WalkerNested(const allocator_type& allocator = allocator_type()) noexcept;

    template <typename ZSERIO_T_text,
            ::zserio::is_field_constructor_enabled_t<ZSERIO_T_text, WalkerNested, allocator_type> = 0>
    explicit WalkerNested(
            ZSERIO_T_text&& text_,
            const allocator_type& allocator = allocator_type()) :
            WalkerNested(allocator)
    {
        m_text_ = ::std::forward<ZSERIO_T_text>(text_);
    }

    explicit WalkerNested(::zserio::BitStreamReader& in, const allocator_type& allocator = allocator_type());
    explicit WalkerNested(::zserio::PackingContextNode& contextNode,
            ::zserio::BitStreamReader& in, const allocator_type& allocator = allocator_type());

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

    static void createPackingContext(::zserio::PackingContextNode& contextNode);
    void initPackingContext(::zserio::PackingContextNode& contextNode) const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t bitSizeOf(::zserio::PackingContextNode& contextNode, size_t bitPosition) const;

    size_t initializeOffsets(size_t bitPosition = 0);
    size_t initializeOffsets(::zserio::PackingContextNode& contextNode, size_t bitPosition);

    bool operator==(const WalkerNested& other) const;
    uint32_t hashCode() const;

    void write(::zserio::BitStreamWriter& out) const;
    void write(::zserio::PackingContextNode& contextNode, ::zserio::BitStreamWriter& out) const;

private:
    ::zserio::string<> readText(::zserio::BitStreamReader& in,
            const allocator_type& allocator);

    ::zserio::string<> m_text_;
};

} // namespace std_alloc
} // namespace test_object

#endif // TEST_OBJECT_STD_ALLOC_WALKER_NESTED_H