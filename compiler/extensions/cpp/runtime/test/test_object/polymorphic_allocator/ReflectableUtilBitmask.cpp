/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, polymorphicAllocator.
 */

#include <zserio/HashCodeUtil.h>
#include <zserio/StringConvertUtil.h>
#include <zserio/TypeInfo.h>
#include <zserio/pmr/AnyHolder.h>
#include <zserio/pmr/Reflectable.h>

#include <test_object/polymorphic_allocator/ReflectableUtilBitmask.h>

namespace test_object
{
namespace polymorphic_allocator
{

ReflectableUtilBitmask::ReflectableUtilBitmask(::zserio::BitStreamReader& in) :
        m_value(readValue(in))
{}

const ::zserio::pmr::ITypeInfo& ReflectableUtilBitmask::typeInfo()
{
    using allocator_type = ::zserio::pmr::PropagatingPolymorphicAllocator<>;

    static const ::zserio::Span<::zserio::StringView> underlyingTypeArguments;

    static const ::std::array<::zserio::ItemInfo, 2> values = {
        ::zserio::ItemInfo{ ::zserio::makeStringView("READ"), static_cast<uint64_t>(UINT8_C(1)), false, false},
        ::zserio::ItemInfo{ ::zserio::makeStringView("WRITE"), static_cast<uint64_t>(UINT8_C(2)), false, false}
    };

    static const ::zserio::BitmaskTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("test_object.polymorphic_allocator.ReflectableUtilBitmask"),
        ::zserio::BuiltinTypeInfo<allocator_type>::getUInt8(), underlyingTypeArguments, values
    };

    return typeInfo;
}

::zserio::pmr::IReflectablePtr ReflectableUtilBitmask::reflectable(const ::zserio::pmr::PropagatingPolymorphicAllocator<>& allocator) const
{
    class Reflectable : public ::zserio::ReflectableBase<::zserio::pmr::PropagatingPolymorphicAllocator<>>
    {
    public:
        explicit Reflectable(::test_object::polymorphic_allocator::ReflectableUtilBitmask bitmask) :
                ::zserio::ReflectableBase<::zserio::pmr::PropagatingPolymorphicAllocator<>>(::test_object::polymorphic_allocator::ReflectableUtilBitmask::typeInfo()),
                m_bitmask(bitmask)
        {}

        size_t bitSizeOf(size_t bitPosition) const override
        {
            return m_bitmask.bitSizeOf(bitPosition);
        }

        void write(::zserio::BitStreamWriter& writer) const override
        {
            m_bitmask.write(writer);
        }

        ::zserio::pmr::AnyHolder getAnyValue(const ::zserio::pmr::PropagatingPolymorphicAllocator<>& alloc) const override
        {
            return ::zserio::pmr::AnyHolder(m_bitmask, alloc);
        }

        ::zserio::pmr::AnyHolder getAnyValue(const ::zserio::pmr::PropagatingPolymorphicAllocator<>& alloc) override
        {
            return ::zserio::pmr::AnyHolder(m_bitmask, alloc);
        }

        uint8_t getUInt8() const override
        {
            return m_bitmask.getValue();
        }

        uint64_t toUInt() const override
        {
            return m_bitmask.getValue();
        }

        double toDouble() const override
        {
            return static_cast<double>(toUInt());
        }

        ::zserio::pmr::string toString(const ::zserio::pmr::PropagatingPolymorphicAllocator<>& alloc) const override
        {
            return m_bitmask.toString(alloc);
        }

    private:
        ::test_object::polymorphic_allocator::ReflectableUtilBitmask m_bitmask;
    };

    return ::std::allocate_shared<Reflectable>(allocator, *this);
}

size_t ReflectableUtilBitmask::bitSizeOf(size_t) const
{
    return UINT8_C(8);
}

size_t ReflectableUtilBitmask::initializeOffsets(size_t bitPosition) const
{
    return bitPosition + bitSizeOf(bitPosition);
}

uint32_t ReflectableUtilBitmask::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;
    result = ::zserio::calcHashCode(result, m_value);
    return result;
}

void ReflectableUtilBitmask::write(::zserio::BitStreamWriter& out) const
{
    out.writeBits(m_value, UINT8_C(8));
}

::zserio::pmr::string ReflectableUtilBitmask::toString(const ::zserio::pmr::string::allocator_type& allocator) const
{
    ::zserio::pmr::string result(allocator);
    if ((*this & ReflectableUtilBitmask::Values::READ) == ReflectableUtilBitmask::Values::READ)
    {
        result += result.empty() ? "READ" : " | READ";
    }
    if ((*this & ReflectableUtilBitmask::Values::WRITE) == ReflectableUtilBitmask::Values::WRITE)
    {
        result += result.empty() ? "WRITE" : " | WRITE";
    }

    return ::zserio::toString<::zserio::pmr::string::allocator_type>(m_value, allocator) + "[" + result + "]";
}

ReflectableUtilBitmask::underlying_type ReflectableUtilBitmask::readValue(::zserio::BitStreamReader& in)
{
    return static_cast<underlying_type>(in.readBits(UINT8_C(8)));
}

} // namespace polymorphic_allocator
} // namespace test_object
