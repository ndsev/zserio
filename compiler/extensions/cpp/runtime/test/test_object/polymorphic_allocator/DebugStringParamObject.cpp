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
#include <zserio/StringView.h>

#include <test_object/polymorphic_allocator/DebugStringParamObject.h>

namespace test_object
{
namespace polymorphic_allocator
{

DebugStringParamObject::DebugStringParamObject(const allocator_type& allocator) noexcept :
        m_isInitialized(false),
        m_parsingInfo(),
        m_text_(::zserio::stringViewToString(::zserio::makeStringView("test"), allocator))
{
}

DebugStringParamObject::DebugStringParamObject(::zserio::BitStreamReader& in,
        int32_t param_, const allocator_type& allocator) :
        m_param_(param_),
        m_isInitialized(true),
        m_parsingInfo(in.getBitPosition()),
        m_text_(readText(in, allocator))
{
    m_parsingInfo.initializeBitSize(in.getBitPosition());
}

DebugStringParamObject::DebugStringParamObject(const DebugStringParamObject& other) :
        m_parsingInfo(other.m_parsingInfo),
        m_text_(other.m_text_)
{
    if (other.m_isInitialized)
    {
        initialize(other.m_param_);
    }
    else
    {
        m_isInitialized = false;
    }
}

DebugStringParamObject& DebugStringParamObject::operator=(const DebugStringParamObject& other)
{
    m_parsingInfo = other.m_parsingInfo;
    m_text_ = other.m_text_;
    if (other.m_isInitialized)
    {
        initialize(other.m_param_);
    }
    else
    {
        m_isInitialized = false;
    }

    return *this;
}

DebugStringParamObject::DebugStringParamObject(DebugStringParamObject&& other) :
        m_parsingInfo(other.m_parsingInfo),
        m_text_(::std::move(other.m_text_))
{
    if (other.m_isInitialized)
    {
        initialize(other.m_param_);
    }
    else
    {
        m_isInitialized = false;
    }
}

DebugStringParamObject& DebugStringParamObject::operator=(DebugStringParamObject&& other)
{
    m_parsingInfo = other.m_parsingInfo;
    m_text_ = ::std::move(other.m_text_);
    if (other.m_isInitialized)
    {
        initialize(other.m_param_);
    }
    else
    {
        m_isInitialized = false;
    }

    return *this;
}

DebugStringParamObject::DebugStringParamObject(::zserio::NoInitT,
        const DebugStringParamObject& other) :
        m_isInitialized(false),
        m_parsingInfo(other.m_parsingInfo),
        m_text_(other.m_text_)
{
}

DebugStringParamObject& DebugStringParamObject::assign(::zserio::NoInitT,
        const DebugStringParamObject& other)
{
    m_isInitialized = false;
    m_parsingInfo = other.m_parsingInfo;
    m_text_ = other.m_text_;

    return *this;
}

DebugStringParamObject::DebugStringParamObject(::zserio::NoInitT,
        DebugStringParamObject&& other) :
        m_isInitialized(false),
        m_parsingInfo(other.m_parsingInfo),
        m_text_(::std::move(other.m_text_))
{
}

DebugStringParamObject& DebugStringParamObject::assign(::zserio::NoInitT,
        DebugStringParamObject&& other)
{
    m_isInitialized = false;
    m_parsingInfo = other.m_parsingInfo;
    m_text_ = ::std::move(other.m_text_);

    return *this;
}

DebugStringParamObject::DebugStringParamObject(::zserio::PropagateAllocatorT,
        const DebugStringParamObject& other, const allocator_type& allocator) :
        m_parsingInfo(other.m_parsingInfo),
        m_text_(::zserio::allocatorPropagatingCopy(other.m_text_, allocator))
{
    if (other.m_isInitialized)
    {
        initialize(other.m_param_);
    }
    else
    {
        m_isInitialized = false;
    }
}

DebugStringParamObject::DebugStringParamObject(::zserio::PropagateAllocatorT, ::zserio::NoInitT,
        const DebugStringParamObject& other, const allocator_type& allocator) :
        m_isInitialized(false),
        m_parsingInfo(other.m_parsingInfo),
        m_text_(::zserio::allocatorPropagatingCopy(other.m_text_, allocator))
{
}

const ::zserio::pmr::ITypeInfo& DebugStringParamObject::typeInfo()
{
    static const ::zserio::StringView templateName;
    static const ::zserio::Span<::zserio::BasicTemplateArgumentInfo<allocator_type>> templateArguments;

    static const ::std::array<::zserio::BasicFieldInfo<allocator_type>, 1> fields = {
        ::zserio::BasicFieldInfo<allocator_type>{
            ::zserio::makeStringView("text"), // schemaName
            ::zserio::BuiltinTypeInfo<allocator_type>::getString(), // typeInfo
            {}, // typeArguments
            false, // isExtended
            {}, // alignment
            {}, // offset
            ::zserio::makeStringView("::zserio::makeStringView(\"test\")"), // initializer
            false, // isOptional
            {}, // optionalClause
            {}, // constraint
            false, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        }
    };

    static const ::std::array<::zserio::BasicParameterInfo<allocator_type>, 1> parameters = {
        ::zserio::BasicParameterInfo<allocator_type>{
            ::zserio::makeStringView("param"),
            ::zserio::BuiltinTypeInfo<allocator_type>::getInt32()
        }
    };

    static const ::zserio::Span<::zserio::BasicFunctionInfo<allocator_type>> functions;

    static const ::zserio::StructTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("test_object.polymorphic_allocator.DebugStringParamObject"),
        [](const allocator_type& allocator) -> ::zserio::pmr::IReflectablePtr
        {
            return std::allocate_shared<::zserio::ReflectableOwner<DebugStringParamObject>>(allocator, allocator);
        },
        templateName, templateArguments,
        fields, parameters, functions
    };

    return typeInfo;
}

::zserio::pmr::IReflectableConstPtr DebugStringParamObject::reflectable(const allocator_type& allocator) const
{
    class Reflectable : public ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>
    {
    public:
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getField;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getParameter;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::callFunction;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getAnyValue;

        explicit Reflectable(const ::test_object::polymorphic_allocator::DebugStringParamObject& object, const allocator_type& alloc) :
                ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>(::test_object::polymorphic_allocator::DebugStringParamObject::typeInfo(), alloc),
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
            if (name == ::zserio::makeStringView("text"))
            {
                return ::zserio::pmr::ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'DebugStringParamObject'!";
        }

        ::zserio::pmr::IReflectableConstPtr getParameter(::zserio::StringView name) const override
        {
            if (name == ::zserio::makeStringView("param"))
            {
                return ::zserio::pmr::ReflectableFactory::getInt32(m_object.getParam(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Parameter '") << name << "' doesn't exist in 'DebugStringParamObject'!";
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
        const ::test_object::polymorphic_allocator::DebugStringParamObject& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}

::zserio::pmr::IReflectablePtr DebugStringParamObject::reflectable(const allocator_type& allocator)
{
    class Reflectable : public ::zserio::ReflectableAllocatorHolderBase<allocator_type>
    {
    public:
        explicit Reflectable(::test_object::polymorphic_allocator::DebugStringParamObject& object, const allocator_type& alloc) :
                ::zserio::ReflectableAllocatorHolderBase<allocator_type>(::test_object::polymorphic_allocator::DebugStringParamObject::typeInfo(), alloc),
                m_object(object)
        {}

        void initializeChildren() override
        {
        }

        void initialize(
                const ::zserio::vector<::zserio::AnyHolder<allocator_type>, allocator_type>& typeArguments) override
        {
            if (typeArguments.size() != 1)
            {
                throw ::zserio::CppRuntimeException("Not enough arguments to DebugStringParamObject::initialize, ") <<
                        "expecting 1, got " << typeArguments.size();
            }

            m_object.initialize(
                typeArguments[0].get<int32_t>()
            );
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
            if (name == ::zserio::makeStringView("text"))
            {
                return ::zserio::pmr::ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'DebugStringParamObject'!";
        }

        ::zserio::pmr::IReflectablePtr getField(::zserio::StringView name) override
        {
            if (name == ::zserio::makeStringView("text"))
            {
                return ::zserio::pmr::ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'DebugStringParamObject'!";
        }

        void setField(::zserio::StringView name,
                const ::zserio::AnyHolder<allocator_type>& value) override
        {
            if (name == ::zserio::makeStringView("text"))
            {
                m_object.setText(value.get<::zserio::pmr::string>());
                return;
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'DebugStringParamObject'!";
        }

        ::zserio::pmr::IReflectablePtr createField(::zserio::StringView name) override
        {
            if (name == ::zserio::makeStringView("text"))
            {
                m_object.setText(::zserio::pmr::string(get_allocator()));
                return ::zserio::pmr::ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in 'DebugStringParamObject'!";
        }

        ::zserio::pmr::IReflectableConstPtr getParameter(::zserio::StringView name) const override
        {
            if (name == ::zserio::makeStringView("param"))
            {
                return ::zserio::pmr::ReflectableFactory::getInt32(m_object.getParam(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Parameter '") << name << "' doesn't exist in 'DebugStringParamObject'!";
        }

        ::zserio::pmr::IReflectablePtr getParameter(::zserio::StringView name) override
        {
            if (name == ::zserio::makeStringView("param"))
            {
                return ::zserio::pmr::ReflectableFactory::getInt32(m_object.getParam(), get_allocator());
            }
            throw ::zserio::CppRuntimeException("Parameter '") << name << "' doesn't exist in 'DebugStringParamObject'!";
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
        ::test_object::polymorphic_allocator::DebugStringParamObject& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}

void DebugStringParamObject::initialize(
        int32_t param_)
{
    m_param_ = param_;
    m_isInitialized = true;
}

bool DebugStringParamObject::isInitialized() const
{
    return m_isInitialized;
}

int32_t DebugStringParamObject::getParam() const
{
    if (!m_isInitialized)
    {
        throw ::zserio::CppRuntimeException("Parameter 'param' of compound 'DebugStringParamObject' is not initialized!");
    }

    return m_param_;
}

::zserio::pmr::string& DebugStringParamObject::getText()
{
    return m_text_;
}

const ::zserio::pmr::string& DebugStringParamObject::getText() const
{
    return m_text_;
}

void DebugStringParamObject::setText(const ::zserio::pmr::string& text_)
{
    m_text_ = text_;
}

void DebugStringParamObject::setText(::zserio::pmr::string&& text_)
{
    m_text_ = ::std::move(text_);
}

size_t DebugStringParamObject::bitSizeOf(size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    endBitPosition += ::zserio::bitSizeOfString(m_text_);

    return endBitPosition - bitPosition;
}

size_t DebugStringParamObject::initializeOffsets(size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    endBitPosition += ::zserio::bitSizeOfString(m_text_);

    return endBitPosition;
}

bool DebugStringParamObject::operator==(const DebugStringParamObject& other) const
{
    if (this != &other)
    {
        return
                (getParam() == other.getParam()) &&
                (m_text_ == other.m_text_);
    }

    return true;
}

bool DebugStringParamObject::operator<(const DebugStringParamObject& other) const
{
    if (getParam() < other.getParam())
    {
        return true;
    }
    if (other.getParam() < getParam())
    {
        return false;
    }

    if (m_text_ < other.m_text_)
    {
        return true;
    }
    if (other.m_text_ < m_text_)
    {
        return false;
    }

    return false;
}

uint32_t DebugStringParamObject::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;

    result = ::zserio::calcHashCode(result, getParam());
    result = ::zserio::calcHashCode(result, m_text_);

    return result;
}

void DebugStringParamObject::write(::zserio::BitStreamWriter& out) const
{
    out.writeString(m_text_);
}

const ::zserio::ParsingInfo& DebugStringParamObject::parsingInfo() const
{
    return m_parsingInfo;
}

::zserio::pmr::string DebugStringParamObject::readText(::zserio::BitStreamReader& in,
        const allocator_type& allocator)
{
    return static_cast<::zserio::pmr::string>(in.readString(allocator));
}


} // namespace polymorphic_allocator
} // namespace test_object
