#include "gtest/gtest.h"

#include <sstream>

#include "zserio/CppRuntimeException.h"
#include "zserio/StringView.h"
#include "zserio/DebugStringUtil.h"
#include "zserio/Reflectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{

namespace
{

using std_alloc = std::allocator<uint8_t>;
using pmr_alloc = pmr::PolymorphicAllocator<uint8_t>;

template <typename ALLOC = std_alloc>
struct DummyObject
{
    using allocator_type = ALLOC;

    explicit DummyObject(const ALLOC& allocator = ALLOC()) :
            m_text_("test", allocator)
    {}

    explicit DummyObject(const string<ALLOC>& text_, const ALLOC& = ALLOC()) :
            m_text_(text_)
    {}

    static const IBasicTypeInfo<ALLOC>& typeInfo()
    {
        static const std::array<BasicFieldInfo<ALLOC>, 1> fields{BasicFieldInfo<ALLOC>{
            "text"_sv, BuiltinTypeInfo<ALLOC>::getString(),
            {}, {}, {}, {}, false, {}, {}, false, {}, false, false
        }};

        static const StructTypeInfo<ALLOC> typeInfo{
            "Dummy"_sv,
            [](const ALLOC& allocator) -> IBasicReflectablePtr<ALLOC>
            {
                return std::allocate_shared<ReflectableOwner<DummyObject>>(allocator, allocator);
            },
            {}, {}, fields, {}, {}
        };

        return typeInfo;
    }

    IBasicReflectableConstPtr<ALLOC> reflectable(const ALLOC& allocator = ALLOC()) const
    {
        class Reflectable : public ReflectableConstAllocatorHolderBase<ALLOC>
        {
        public:
            using ReflectableConstAllocatorHolderBase<ALLOC>::get_allocator;
            using ReflectableConstAllocatorHolderBase<ALLOC>::getField;
            using ReflectableConstAllocatorHolderBase<ALLOC>::getParameter;
            using ReflectableConstAllocatorHolderBase<ALLOC>::callFunction;
            using ReflectableConstAllocatorHolderBase<ALLOC>::getAnyValue;

            explicit Reflectable(const DummyObject& owner, const ALLOC& allocator) :
                    ReflectableConstAllocatorHolderBase<ALLOC>(typeInfo(), allocator),
                    m_owner(owner)
            {}

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

            virtual void write(BitStreamWriter&) const override
            {
            }

            virtual IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override
            {
                if (name == makeStringView("text"))
                {
                    return BasicReflectableFactory<ALLOC>::getString(m_owner.getText(), get_allocator());
                }
                throw CppRuntimeException("Field '") << name << "' doesn't exist in 'DummyNested'!";
            }

            virtual AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
            {
                return AnyHolder<ALLOC>(std::cref(m_owner), allocator);
            }

        private:
            const DummyObject& m_owner;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    IBasicReflectablePtr<ALLOC> reflectable(const ALLOC& allocator = ALLOC())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<ALLOC>
        {
        public:
            using ReflectableAllocatorHolderBase<ALLOC>::get_allocator;
            using ReflectableAllocatorHolderBase<ALLOC>::getField;
            using ReflectableAllocatorHolderBase<ALLOC>::getParameter;
            using ReflectableAllocatorHolderBase<ALLOC>::callFunction;

            explicit Reflectable(DummyObject& owner, const ALLOC& allocator) :
                    ReflectableAllocatorHolderBase<ALLOC>(typeInfo(), allocator),
                    m_owner(owner)
            {}

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

            virtual void write(BitStreamWriter&) const override
            {
            }

            virtual IBasicReflectablePtr<ALLOC> getField(StringView name) override
            {
                if (name == makeStringView("text"))
                {
                    return BasicReflectableFactory<ALLOC>::getString(m_owner.getText(), get_allocator());
                }
                throw CppRuntimeException("Field '") << name << "' doesn't exist in 'DummyNested'!";
            }

            virtual void setField(StringView name, const AnyHolder<ALLOC>& any) override
            {
                if (name == makeStringView("text"))
                {
                    m_owner.setText(any.template get<string<ALLOC>>());
                    return;
                }
                throw CppRuntimeException("Field '") << name << "' doesn't exist in 'DummyNested'!";
            }

            virtual AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
            {
                return AnyHolder<ALLOC>(std::cref(m_owner), allocator);
            }

            virtual AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
            {
                return AnyHolder<ALLOC>(std::ref(m_owner), allocator);
            }

        private:
            DummyObject& m_owner;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    void setText(const string<ALLOC>& text_)
    {
        m_text_ = text_;
    }

    const string<ALLOC>& getText() const
    {
        return m_text_;
    }

private:
    string<ALLOC> m_text_;
};

template <typename ALLOC = std_alloc>
class ParameterizedDummyObject
{
public:
    using allocator_type = ALLOC;

    explicit ParameterizedDummyObject(const allocator_type& allocator = allocator_type()) noexcept :
            m_isInitialized(false),
            m_text_(allocator)
    {}

    explicit ParameterizedDummyObject(const string<ALLOC>& text_,
            const allocator_type& = allocator_type()) noexcept :
            m_isInitialized(false),
            m_text_(text_)
    {}

    static const IBasicTypeInfo<ALLOC>& typeInfo()
    {
        static const StringView templateName;
        static const Span<BasicTemplateArgumentInfo<allocator_type>> templateArguments;

        static const ::std::array<BasicFieldInfo<allocator_type>, 1> fields = {
            BasicFieldInfo<allocator_type>{
                makeStringView("text"), // schemaName
                BuiltinTypeInfo<allocator_type>::getString(), // typeInfo
                {}, // typeArguments
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

        static const ::std::array<BasicParameterInfo<allocator_type>, 1> parameters = {
            BasicParameterInfo<allocator_type>{
                makeStringView("param"),
                BuiltinTypeInfo<allocator_type>::getInt32()
            }
        };

        static const Span<BasicFunctionInfo<allocator_type>> functions;

        static const StructTypeInfo<allocator_type> typeInfo = {
            makeStringView("ParameterizedDummyObject"),
            [](const allocator_type& allocator) -> IBasicReflectablePtr<allocator_type>
            {
                return std::allocate_shared<ReflectableOwner<ParameterizedDummyObject>>(allocator, allocator);
            },
            templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IBasicReflectableConstPtr<ALLOC> reflectable(const allocator_type& allocator) const
    {
        class Reflectable : public ReflectableConstAllocatorHolderBase<allocator_type>
        {
        private:
            using Base = ReflectableConstAllocatorHolderBase<allocator_type>;

        public:
            using Base::get_allocator;
            using Base::getField;
            using Base::getParameter;
            using Base::callFunction;
            using Base::getAnyValue;

            explicit Reflectable(const ParameterizedDummyObject& object, const allocator_type& allocator) :
                    Base(ParameterizedDummyObject::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

            virtual void write(BitStreamWriter&) const override
            {}

            virtual IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override
            {
                if (name == makeStringView("text"))
                {
                    return BasicReflectableFactory<ALLOC>::getString(m_object.getText(), get_allocator());
                }
                throw CppRuntimeException("Field '") << name <<
                        "' doesn't exist in 'ParameterizedDummyObject'!";
            }

            virtual IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override
            {
                if (name == makeStringView("param"))
                {
                    return BasicReflectableFactory<ALLOC>::getInt32(m_object.getParam(), get_allocator());
                }
                throw CppRuntimeException("Parameter '") << name <<
                        "' doesn't exist in 'ParameterizedDummyObject'!";
            }

            virtual AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const
            {
                return AnyHolder<ALLOC>(std::cref(m_object), allocator);
            }

        private:
            const ParameterizedDummyObject& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    IBasicReflectablePtr<ALLOC> reflectable(const allocator_type& allocator)
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            using ReflectableAllocatorHolderBase<allocator_type>::get_allocator;
            using ReflectableAllocatorHolderBase<allocator_type>::getField;
            using ReflectableAllocatorHolderBase<allocator_type>::getParameter;
            using ReflectableAllocatorHolderBase<allocator_type>::callFunction;

            explicit Reflectable(ParameterizedDummyObject& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(
                            ParameterizedDummyObject::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual void initialize(
                    const vector<AnyHolder<allocator_type>, allocator_type>& typeArguments) override
            {
                if (typeArguments.size() != 1)
                {
                    throw CppRuntimeException("No enough arguments to ParameterizedDummyObject::initialize, "
                            "expecting 1, got ") << typeArguments.size();
                }

                m_object.initialize(
                    typeArguments[0].template get<int32_t>()
                );
            }

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

            virtual void write(BitStreamWriter&) const override
            {}

            virtual IBasicReflectablePtr<ALLOC> getField(StringView name) override
            {
                if (name == makeStringView("text"))
                {
                    return BasicReflectableFactory<ALLOC>::getString(m_object.getText(), get_allocator());
                }
                throw CppRuntimeException("Field '") << name <<
                        "' doesn't exist in 'ParameterizedDummyObject'!";
            }

            virtual void setField(StringView name,
                    const AnyHolder<allocator_type>& value) override
            {
                if (name == makeStringView("text"))
                {
                    m_object.setText(value.template get<string<ALLOC>>());
                    return;
                }
                throw CppRuntimeException("Field '") << name <<
                        "' doesn't exist in 'ParameterizedDummyObject'!";
            }

            virtual IBasicReflectablePtr<ALLOC> getParameter(StringView name) override
            {
                if (name == makeStringView("param"))
                {
                    return BasicReflectableFactory<ALLOC>::getInt32(m_object.getParam(), get_allocator());
                }
                throw CppRuntimeException("Parameter '") << name <<
                        "' doesn't exist in 'ParameterizedDummyObject'!";
            }

            virtual AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
            {
                return AnyHolder<ALLOC>(std::cref(m_object), allocator);
            }

            virtual AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
            {
                return AnyHolder<ALLOC>(std::ref(m_object), allocator);
            }

        private:
            ParameterizedDummyObject& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    void initialize(int32_t param_)
    {
        m_param_ = param_;
        m_isInitialized = true;
    }

    int32_t getParam() const
    {
        if (!m_isInitialized)
        {
            throw CppRuntimeException(
                    "Parameter 'param' of compound 'ParameterizedDummyObject' is not initialized!");
        }

        return m_param_;
    }

    const string<ALLOC>& getText() const
    {
        return m_text_;
    }

    void setText(const string<ALLOC>& text_)
    {
        m_text_ = text_;
    }

private:
    int32_t m_param_;
    bool m_isInitialized;
    string<ALLOC> m_text_;
};

} // namespace

TEST(DebugStringUtilTest, toJsonStreamDefault)
{
    std::ostringstream os;
    DummyObject<> dummyObject;
    toJsonStream(dummyObject, os);
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());

    // improve coverage
    IReflectablePtr reflectable = dummyObject.reflectable();
    ASSERT_TRUE(reflectable);
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, toJsonStreamDefaultWithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, std_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamDefaultWithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr_alloc> dummyObject;
    toJsonStream(dummyObject, os, pmr_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2);
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2WithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2, std_alloc());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2WithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr_alloc> dummyObject;
    toJsonStream(dummyObject, os, 2, pmr_alloc());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilter)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, DepthWalkFilter(0));
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilterWithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, DefaultWalkFilter(), std_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr_alloc> dummyObject;
    toJsonStream(dummyObject, os, BasicDefaultWalkFilter<pmr_alloc>(),
            pmr_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2Filter)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2, DefaultWalkFilter());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2FilterWithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2, DepthWalkFilter(0), std_alloc());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2FilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr_alloc> dummyObject;
    toJsonStream(dummyObject, os, 2, BasicDepthWalkFilter<pmr_alloc>(0),
            pmr_alloc());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStringDefault)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject));
}

TEST(DebugStringUtilTest, toJsonStringDefaultWithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject, std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringDefaultWithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject, pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toJsonString(dummyObject, 2));
}

TEST(DebugStringUtilTest, toJsonStringIndent2WithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toJsonString(dummyObject, 2, std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2WithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(dummyObject, 2, pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringFilter)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject, DefaultWalkFilter()));
}

TEST(DebugStringUtilTest, toJsonStringFilterWithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toJsonString(dummyObject, DefaultWalkFilter(), std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringFilterWithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toJsonString(dummyObject, BasicDefaultWalkFilter<pmr_alloc>(),
                    pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2Filter)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n}", toJsonString(dummyObject, 2, DepthWalkFilter(0)));
}

TEST(DebugStringUtilTest, toJsonStringIndent2FilterWithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(dummyObject, 2, DefaultWalkFilter(), std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2FilterWithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(dummyObject, 2, BasicDefaultWalkFilter<pmr_alloc>(),
                    pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonFileDefault)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileDefault.json";
    toJsonFile(dummyObject, fileName);

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileDefaultWithAlloc)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileDefaultWithAlloc.json";
    toJsonFile(dummyObject, fileName, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileDefaultWithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    const string<pmr::PolymorphicAllocator<char>> fileName =
            "DebugStringUtilTest_toJsonFileDefaultWithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2.json";
    toJsonFile(dummyObject, fileName, 2);

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2WithAlloc)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2WithAlloc.json";
    toJsonFile(dummyObject, fileName, 2, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2WithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    const string<pmr::PolymorphicAllocator<char>> fileName =
            "DebugStringUtilTest_toJsonFileIndent2WithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, 2, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilter)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileFilter.json";
    toJsonFile(dummyObject, fileName, DefaultWalkFilter());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilterWithAlloc)
{
    const DummyObject<> dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_toJsonFileFilterWithAlloc.json";
    toJsonFile(dummyObject, fileName, defaultWalkFilter, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilterWithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    BasicDefaultWalkFilter<pmr_alloc> defaultWalkFilter;
    const string<pmr::PolymorphicAllocator<char>> fileName =
            "DebugStringUtilTest_toJsonFileFilterWithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, defaultWalkFilter, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2Filter)
{
    const DummyObject<> dummyObject;
    DepthWalkFilter depthWalkFilter(0);
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2Filter.json";
    toJsonFile(dummyObject, fileName, 2, depthWalkFilter);

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2FilterWithAlloc)
{
    const DummyObject<> dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2FilterWithAlloc.json";
    toJsonFile(dummyObject, fileName, 2, defaultWalkFilter, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2FilterWithPolymorphicAlloc)
{
    const DummyObject<pmr_alloc> dummyObject;
    BasicDefaultWalkFilter<pmr_alloc> defaultWalkFilter;
    const string<pmr::PolymorphicAllocator<char>> fileName =
            "DebugStringUtilTest_toJsonFileIndent2FilterWithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, 2, defaultWalkFilter, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfo)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream(DummyObject<>::typeInfo(), ss);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamParameterizedTypeInfo)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream(ParameterizedDummyObject<>::typeInfo(), ss);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());
    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfoWithAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream(DummyObject<>::typeInfo(), ss, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfoWithPolymorphicAllocDefault)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonStream(DummyObject<pmr_alloc>::typeInfo(), ss);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfoWithPolymorphicAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonStream(DummyObject<pmr_alloc>::typeInfo(), ss, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamType)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream<DummyObject<>>(ss);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamParameterizedType)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream<ParameterizedDummyObject<>>(ss);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeWithAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream<DummyObject<>>(ss, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeWithPolymorphicAllocDefault)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable = fromJsonStream<DummyObject<pmr_alloc>>(ss);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeWithPolymorphicAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonStream<DummyObject<pmr_alloc>>(ss, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfo)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString(DummyObject<>::typeInfo(), jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringParameterizedTypeInfo)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString(ParameterizedDummyObject<>::typeInfo(), jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfoWithAlloc)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable =
            fromJsonString(DummyObject<>::typeInfo(), jsonString, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfoWithPolymorphicAllocDefault)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonString(DummyObject<pmr_alloc>::typeInfo(), jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfoWithPolymorphicAlloc)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonString(DummyObject<pmr_alloc>::typeInfo(), jsonString, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringType)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString<DummyObject<>>(jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringParameterizedType)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString<ParameterizedDummyObject<>>(jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeWithAlloc)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString<DummyObject<>>(jsonString, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeWithPolymorphicAllocDefault)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable = fromJsonString<DummyObject<pmr_alloc>>(jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeWithPolymorphicAlloc)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonString<DummyObject<pmr_alloc>>(jsonString, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfo)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfo.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile(DummyObject<>::typeInfo(), fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedTypeInfo)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameterizedTypeInfo.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile(ParameterizedDummyObject<>::typeInfo(), fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfoWithAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfoWithAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile(DummyObject<>::typeInfo(), fileName, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfoWithPolymorphicAllocDefault)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfoWithPolymorphicAllocDefault.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonFile(DummyObject<pmr_alloc>::typeInfo(), fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfoWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfoWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonFile(DummyObject<pmr_alloc>::typeInfo(), fileName, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedTypeInfoWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameterizedTypeInfoWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable = fromJsonFile(
            ParameterizedDummyObject<pmr_alloc>::typeInfo(), fileName, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<pmr_alloc>, pmr_alloc>{AnyHolder<pmr_alloc>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileType)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileType.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile<DummyObject<>>(fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedType)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameterizedType.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile<ParameterizedDummyObject<>>(fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeWithAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeWithAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile<DummyObject<>>(fileName, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeWithPolymorphicAllocDefault)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeWithPolymorphicAllocDefault.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable = fromJsonFile<DummyObject<pmr_alloc>>(fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable = fromJsonFile<DummyObject<pmr_alloc>>(fileName, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedTypeWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameteriezedTypeWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonFile<ParameterizedDummyObject<pmr_alloc>>(fileName, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<pmr_alloc>, pmr_alloc>{AnyHolder<pmr_alloc>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

} // namespace zserio
