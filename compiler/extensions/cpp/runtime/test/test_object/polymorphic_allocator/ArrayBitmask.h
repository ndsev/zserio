/**
 * Automatically generated by Zserio C++ extension version 2.11.0.
 * Generator setup: writerCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, polymorhpicAllocator.
 */

#ifndef TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_BITMASK_H
#define TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_BITMASK_H

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/pmr/ITypeInfo.h>
#include <zserio/pmr/IReflectable.h>
#include <zserio/pmr/ArrayTraits.h>
#include <zserio/pmr/String.h>
#include <zserio/pmr/PackingContext.h>
#include <zserio/ArrayTraits.h>
#include <zserio/Types.h>

namespace test_object
{
namespace polymorphic_allocator
{

class ArrayBitmask
{
public:
    using underlying_type = uint8_t;

    enum class Values : underlying_type
    {
        CREATE = UINT8_C(1),
        READ = UINT8_C(2),
        WRITE = UINT8_C(4)
    };

    constexpr ArrayBitmask() noexcept :
            m_value(0)
    {}

    explicit ArrayBitmask(::zserio::BitStreamReader& in);
    ArrayBitmask(::zserio::pmr::PackingContextNode& contextNode, ::zserio::BitStreamReader& in);
    constexpr ArrayBitmask(Values value) noexcept :
            m_value(static_cast<underlying_type>(value))
    {}

    constexpr explicit ArrayBitmask(underlying_type value) noexcept :
            m_value(value)
    {}

    ~ArrayBitmask() = default;

    ArrayBitmask(const ArrayBitmask&) = default;
    ArrayBitmask& operator=(const ArrayBitmask&) = default;

    ArrayBitmask(ArrayBitmask&&) = default;
    ArrayBitmask& operator=(ArrayBitmask&&) = default;

    static const ::zserio::pmr::ITypeInfo& typeInfo();
    ::zserio::pmr::IReflectablePtr reflectable(const ::zserio::pmr::PropagatingPolymorphicAllocator<>& allocator = ::zserio::pmr::PropagatingPolymorphicAllocator<>()) const;

    constexpr explicit operator underlying_type() const
    {
        return m_value;
    }

    constexpr underlying_type getValue() const
    {
        return m_value;
    }

    static void createPackingContext(::zserio::pmr::PackingContextNode& contextNode);
    void initPackingContext(::zserio::pmr::PackingContextNode& contextNode) const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t bitSizeOf(::zserio::pmr::PackingContextNode& contextNode, size_t bitPosition) const;

    size_t initializeOffsets(size_t bitPosition = 0) const;
    size_t initializeOffsets(::zserio::pmr::PackingContextNode& contextNode, size_t bitPosition) const;

    uint32_t hashCode() const;

    void write(::zserio::BitStreamWriter& out) const;
    void write(::zserio::pmr::PackingContextNode& contextNode, ::zserio::BitStreamWriter& out) const;

    ::zserio::pmr::string toString(const ::zserio::pmr::string::allocator_type& allocator =
            ::zserio::pmr::string::allocator_type()) const;

private:
    static underlying_type readValue(::zserio::BitStreamReader& in);
    static underlying_type readValue(::zserio::pmr::PackingContextNode& contextNode,
            ::zserio::BitStreamReader& in);

    underlying_type m_value;
};

inline bool operator==(const ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

inline bool operator!=(const ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    return lhs.getValue() != rhs.getValue();
}

inline ArrayBitmask operator|(ArrayBitmask::Values lhs, ArrayBitmask::Values rhs)
{
    return ArrayBitmask(static_cast<ArrayBitmask::underlying_type>(lhs) | static_cast<ArrayBitmask::underlying_type>(rhs));
}

inline ArrayBitmask operator|(const ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    return ArrayBitmask(lhs.getValue() | rhs.getValue());
}

inline ArrayBitmask operator&(ArrayBitmask::Values lhs, ArrayBitmask::Values rhs)
{
    return ArrayBitmask(static_cast<ArrayBitmask::underlying_type>(lhs) & static_cast<ArrayBitmask::underlying_type>(rhs));
}

inline ArrayBitmask operator&(const ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    return ArrayBitmask(lhs.getValue() & rhs.getValue());
}

inline ArrayBitmask operator^(ArrayBitmask::Values lhs, ArrayBitmask::Values rhs)
{
    return ArrayBitmask(static_cast<ArrayBitmask::underlying_type>(lhs) ^ static_cast<ArrayBitmask::underlying_type>(rhs));
}

inline ArrayBitmask operator^(const ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    return ArrayBitmask(lhs.getValue() ^ rhs.getValue());
}

inline ArrayBitmask operator~(ArrayBitmask::Values lhs)
{
    return ArrayBitmask(~static_cast<ArrayBitmask::underlying_type>(lhs));
}

inline ArrayBitmask operator~(const ArrayBitmask& lhs)
{
    return ArrayBitmask(~lhs.getValue());
}

inline ArrayBitmask operator|=(ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    lhs = ArrayBitmask(lhs.getValue() | rhs.getValue());
    return lhs;
}

inline ArrayBitmask operator&=(ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    lhs = ArrayBitmask(lhs.getValue() & rhs.getValue());
    return lhs;
}

inline ArrayBitmask operator^=(ArrayBitmask& lhs, const ArrayBitmask& rhs)
{
    lhs = ArrayBitmask(lhs.getValue() ^ rhs.getValue());
    return lhs;
}

} // namespace polymorphic_allocator
} // namespace test_object

#endif // TEST_OBJECT_POLYMORPHIC_ALLOCATOR_ARRAY_BITMASK_H