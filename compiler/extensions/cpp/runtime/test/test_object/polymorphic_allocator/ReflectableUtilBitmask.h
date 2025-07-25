/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, polymorphicAllocator.
 */

#ifndef TEST_OBJECT_POLYMORPHIC_ALLOCATOR_REFLECTABLE_UTIL_BITMASK_H
#define TEST_OBJECT_POLYMORPHIC_ALLOCATOR_REFLECTABLE_UTIL_BITMASK_H

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/pmr/ITypeInfo.h>
#include <zserio/pmr/IReflectable.h>
#include <zserio/pmr/ArrayTraits.h>
#include <zserio/pmr/String.h>
#include <zserio/ArrayTraits.h>
#include <zserio/Types.h>

namespace test_object
{
namespace polymorphic_allocator
{

class ReflectableUtilBitmask
{
public:
    using underlying_type = uint8_t;

    enum class Values : underlying_type
    {
        READ = UINT8_C(1),
        WRITE = UINT8_C(2)
    };

    constexpr ReflectableUtilBitmask() noexcept :
            m_value(0)
    {}

    explicit ReflectableUtilBitmask(::zserio::BitStreamReader& in);
    constexpr ReflectableUtilBitmask(Values value) noexcept :
            m_value(static_cast<underlying_type>(value))
    {}

    constexpr explicit ReflectableUtilBitmask(underlying_type value) noexcept :
            m_value(value)
    {}

    ~ReflectableUtilBitmask() = default;

    ReflectableUtilBitmask(const ReflectableUtilBitmask&) = default;
    ReflectableUtilBitmask& operator=(const ReflectableUtilBitmask&) = default;

    ReflectableUtilBitmask(ReflectableUtilBitmask&&) = default;
    ReflectableUtilBitmask& operator=(ReflectableUtilBitmask&&) = default;

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

    size_t bitSizeOf(size_t bitPosition = 0) const;

    size_t initializeOffsets(size_t bitPosition = 0) const;

    uint32_t hashCode() const;

    void write(::zserio::BitStreamWriter& out) const;

    ::zserio::pmr::string toString(const ::zserio::pmr::string::allocator_type& allocator =
            ::zserio::pmr::string::allocator_type()) const;

private:
    static underlying_type readValue(::zserio::BitStreamReader& in);

    underlying_type m_value;
};

inline constexpr bool operator==(const ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

inline constexpr bool operator!=(const ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    return lhs.getValue() != rhs.getValue();
}

inline constexpr bool operator<(const ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    return lhs.getValue() < rhs.getValue();
}

inline constexpr ReflectableUtilBitmask operator|(ReflectableUtilBitmask::Values lhs, ReflectableUtilBitmask::Values rhs)
{
    return ReflectableUtilBitmask(static_cast<ReflectableUtilBitmask::underlying_type>(lhs) | static_cast<ReflectableUtilBitmask::underlying_type>(rhs));
}

inline constexpr ReflectableUtilBitmask operator|(const ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    return ReflectableUtilBitmask(lhs.getValue() | rhs.getValue());
}

inline constexpr ReflectableUtilBitmask operator&(ReflectableUtilBitmask::Values lhs, ReflectableUtilBitmask::Values rhs)
{
    return ReflectableUtilBitmask(static_cast<ReflectableUtilBitmask::underlying_type>(lhs) & static_cast<ReflectableUtilBitmask::underlying_type>(rhs));
}

inline constexpr ReflectableUtilBitmask operator&(const ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    return ReflectableUtilBitmask(lhs.getValue() & rhs.getValue());
}

inline constexpr ReflectableUtilBitmask operator^(ReflectableUtilBitmask::Values lhs, ReflectableUtilBitmask::Values rhs)
{
    return ReflectableUtilBitmask(static_cast<ReflectableUtilBitmask::underlying_type>(lhs) ^ static_cast<ReflectableUtilBitmask::underlying_type>(rhs));
}

inline constexpr ReflectableUtilBitmask operator^(const ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    return ReflectableUtilBitmask(lhs.getValue() ^ rhs.getValue());
}

inline constexpr ReflectableUtilBitmask operator~(ReflectableUtilBitmask::Values lhs)
{
    return ReflectableUtilBitmask(static_cast<ReflectableUtilBitmask::underlying_type>(static_cast<ReflectableUtilBitmask::underlying_type>(~static_cast<ReflectableUtilBitmask::underlying_type>(lhs))));
}

inline constexpr ReflectableUtilBitmask operator~(const ReflectableUtilBitmask& lhs)
{
    return ReflectableUtilBitmask(static_cast<ReflectableUtilBitmask::underlying_type>(static_cast<ReflectableUtilBitmask::underlying_type>(~lhs.getValue())));
}

inline ReflectableUtilBitmask operator|=(ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    lhs = ReflectableUtilBitmask(lhs.getValue() | rhs.getValue());
    return lhs;
}

inline ReflectableUtilBitmask operator&=(ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    lhs = ReflectableUtilBitmask(lhs.getValue() & rhs.getValue());
    return lhs;
}

inline ReflectableUtilBitmask operator^=(ReflectableUtilBitmask& lhs, const ReflectableUtilBitmask& rhs)
{
    lhs = ReflectableUtilBitmask(lhs.getValue() ^ rhs.getValue());
    return lhs;
}

} // namespace polymorphic_allocator
} // namespace test_object

#endif // TEST_OBJECT_POLYMORPHIC_ALLOCATOR_REFLECTABLE_UTIL_BITMASK_H
