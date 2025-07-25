/**
 * Automatically generated by Zserio C++ generator version 1.2.2 using Zserio core 2.17.0.
 * Generator setup: writerCode, settersCode, pubsubCode, serviceCode, sqlCode, typeInfoCode, reflectionCode, parsingInfoCode, polymorphicAllocator.
 */

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>
#include <zserio/TypeInfo.h>
#include <zserio/pmr/AnyHolder.h>
#include <zserio/pmr/Reflectable.h>

#include <test_object/polymorphic_allocator/ArrayObject.h>

namespace test_object
{
namespace polymorphic_allocator
{

ArrayObject::ArrayObject(const allocator_type&) noexcept :
        m_parsingInfo(),
        m_value_(uint32_t())
{
}

ArrayObject::ArrayObject(::zserio::BitStreamReader& in, const allocator_type&) :
        m_parsingInfo(in.getBitPosition()),
        m_value_(readValue(in))
{
    m_parsingInfo.initializeBitSize(in.getBitPosition());
}

ArrayObject::ArrayObject(ArrayObject::ZserioPackingContext& context, ::zserio::BitStreamReader& in, const allocator_type&) :
        m_parsingInfo(in.getBitPosition()),
        m_value_(readValue(context, in))
{
    m_parsingInfo.initializeBitSize(in.getBitPosition());
}

ArrayObject::ArrayObject(::zserio::PropagateAllocatorT,
        const ArrayObject& other, const allocator_type& allocator) :
        m_parsingInfo(other.m_parsingInfo),
        m_value_(::zserio::allocatorPropagatingCopy(other.m_value_, allocator))
{
}

const ::zserio::pmr::ITypeInfo& ArrayObject::typeInfo()
{
    static const ::zserio::StringView templateName;
    static const ::zserio::Span<::zserio::BasicTemplateArgumentInfo<allocator_type>> templateArguments;

    static const ::std::array<::zserio::BasicFieldInfo<allocator_type>, 1> fields = {
        ::zserio::BasicFieldInfo<allocator_type>{
            ::zserio::makeStringView("value"), // schemaName
            ::zserio::BuiltinTypeInfo<allocator_type>::getFixedUnsignedBitField(UINT8_C(31)), // typeInfo
            {}, // typeArguments
            false, // isExtended
            {}, // alignment
            {}, // offset
            {}, // initializer
            false, // isOptional
            {}, // optionalClause
            {}, // constraint
            false, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        }
    };

    static const ::zserio::Span<::zserio::BasicParameterInfo<allocator_type>> parameters;

    static const ::zserio::Span<::zserio::BasicFunctionInfo<allocator_type>> functions;

    static const ::zserio::StructTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("test_object.polymorphic_allocator.ArrayObject"),
        [](const allocator_type& allocator) -> ::zserio::pmr::IReflectablePtr
        {
            return std::allocate_shared<::zserio::ReflectableOwner<ArrayObject>>(allocator, allocator);
        },
        templateName, templateArguments,
        fields, parameters, functions
    };

    return typeInfo;
}

::zserio::pmr::IReflectableConstPtr ArrayObject::reflectable(const allocator_type& allocator) const
{
    class Reflectable : public ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>
    {
    public:
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getField;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getParameter;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::callFunction;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getAnyValue;

        explicit Reflectable(const ::test_object::polymorphic_allocator::ArrayObject& object, const allocator_type& alloc) :
                ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>(::test_object::polymorphic_allocator::ArrayObject::typeInfo(), alloc),
                m_object(object)
        {}

        size_t bitSizeOf(size_t bitPosition) const override
        {
            return m_object.bitSizeOf(bitPosition);
        }

        void write(::zserio::BitStreamWriter& writer) const override
        {
            m_object.write(writer);
        }

        ::zserio::pmr::IReflectableConstPtr getField(::zserio::StringView name) const override
        {
            if (name == ::zserio::makeStringView("value"))
            {
                return ::zserio::pmr::ReflectableFactory::getFixedUnsignedBitField(m_object.getValue(), UINT8_C(31), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'ArrayObject'!";
        }

        ::zserio::pmr::AnyHolder getAnyValue(const allocator_type& alloc) const override
        {
            return ::zserio::pmr::AnyHolder(::std::cref(m_object), alloc);
        }

        const ::zserio::ParsingInfo& parsingInfo() const override
        {
            return m_object.parsingInfo();
        }

    private:
        const ::test_object::polymorphic_allocator::ArrayObject& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}

::zserio::pmr::IReflectablePtr ArrayObject::reflectable(const allocator_type& allocator)
{
    class Reflectable : public ::zserio::ReflectableAllocatorHolderBase<allocator_type>
    {
    public:
        explicit Reflectable(::test_object::polymorphic_allocator::ArrayObject& object, const allocator_type& alloc) :
                ::zserio::ReflectableAllocatorHolderBase<allocator_type>(::test_object::polymorphic_allocator::ArrayObject::typeInfo(), alloc),
                m_object(object)
        {}

        void initializeChildren() override
        {
        }

        size_t initializeOffsets(size_t bitPosition) override
        {
            return m_object.initializeOffsets(bitPosition);
        }

        size_t bitSizeOf(size_t bitPosition) const override
        {
            return m_object.bitSizeOf(bitPosition);
        }

        void write(::zserio::BitStreamWriter& writer) const override
        {
            m_object.write(writer);
        }

        ::zserio::pmr::IReflectableConstPtr getField(::zserio::StringView name) const override
        {
            if (name == ::zserio::makeStringView("value"))
            {
                return ::zserio::pmr::ReflectableFactory::getFixedUnsignedBitField(m_object.getValue(), UINT8_C(31), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'ArrayObject'!";
        }

        ::zserio::pmr::IReflectablePtr getField(::zserio::StringView name) override
        {
            if (name == ::zserio::makeStringView("value"))
            {
                return ::zserio::pmr::ReflectableFactory::getFixedUnsignedBitField(m_object.getValue(), UINT8_C(31), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'ArrayObject'!";
        }

        void setField(::zserio::StringView name,
                const ::zserio::AnyHolder<allocator_type>& value) override
        {
            if (name == ::zserio::makeStringView("value"))
            {
                m_object.setValue(value.get<uint32_t>());
                return;
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'ArrayObject'!";
        }

        ::zserio::pmr::IReflectablePtr createField(::zserio::StringView name) override
        {
            if (name == ::zserio::makeStringView("value"))
            {
                m_object.setValue(uint32_t());
                return ::zserio::pmr::ReflectableFactory::getFixedUnsignedBitField(m_object.getValue(), UINT8_C(31), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'ArrayObject'!";
        }

        ::zserio::pmr::AnyHolder getAnyValue(const allocator_type& alloc) const override
        {
            return ::zserio::pmr::AnyHolder(::std::cref(m_object), alloc);
        }

        ::zserio::pmr::AnyHolder getAnyValue(const allocator_type& alloc) override
        {
            return ::zserio::pmr::AnyHolder(::std::ref(m_object), alloc);
        }

        const ::zserio::ParsingInfo& parsingInfo() const override
        {
            return m_object.parsingInfo();
        }

    private:
        ::test_object::polymorphic_allocator::ArrayObject& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}

uint32_t ArrayObject::getValue() const
{
    return m_value_;
}

void ArrayObject::setValue(uint32_t value_)
{
    m_value_ = value_;
}

void ArrayObject::initPackingContext(ArrayObject::ZserioPackingContext& context) const
{
    context.getValue().init<::zserio::BitFieldArrayTraits<uint32_t, UINT8_C(31)>>(m_value_);
}

size_t ArrayObject::bitSizeOf(size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    endBitPosition += UINT8_C(31);

    return endBitPosition - bitPosition;
}

size_t ArrayObject::bitSizeOf(ArrayObject::ZserioPackingContext& context, size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    endBitPosition += context.getValue().bitSizeOf<::zserio::BitFieldArrayTraits<uint32_t, UINT8_C(31)>>(m_value_);

    return endBitPosition - bitPosition;
}

size_t ArrayObject::initializeOffsets(size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    endBitPosition += UINT8_C(31);

    return endBitPosition;
}

size_t ArrayObject::initializeOffsets(ArrayObject::ZserioPackingContext& context, size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    endBitPosition += context.getValue().bitSizeOf<::zserio::BitFieldArrayTraits<uint32_t, UINT8_C(31)>>(m_value_);

    return endBitPosition;
}

bool ArrayObject::operator==(const ArrayObject& other) const
{
    if (this != &other)
    {
        return
                (m_value_ == other.m_value_);
    }

    return true;
}

bool ArrayObject::operator<(const ArrayObject& other) const
{
    if (m_value_ < other.m_value_)
    {
        return true;
    }
    if (other.m_value_ < m_value_)
    {
        return false;
    }

    return false;
}

uint32_t ArrayObject::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;

    result = ::zserio::calcHashCode(result, m_value_);

    return result;
}

void ArrayObject::write(::zserio::BitStreamWriter& out) const
{
    out.writeBits(m_value_, UINT8_C(31));
}

void ArrayObject::write(ArrayObject::ZserioPackingContext& context, ::zserio::BitStreamWriter& out) const
{
    context.getValue().write<::zserio::BitFieldArrayTraits<uint32_t, UINT8_C(31)>>(out, m_value_);
}

const ::zserio::ParsingInfo& ArrayObject::parsingInfo() const
{
    return m_parsingInfo;
}

uint32_t ArrayObject::readValue(::zserio::BitStreamReader& in)
{
    return static_cast<uint32_t>(in.readBits(UINT8_C(31)));
}

uint32_t ArrayObject::readValue(ArrayObject::ZserioPackingContext& context, ::zserio::BitStreamReader& in)
{
    return context.getValue().read<::zserio::BitFieldArrayTraits<uint32_t, UINT8_C(31)>>(in);
}


} // namespace polymorphic_allocator
} // namespace test_object
