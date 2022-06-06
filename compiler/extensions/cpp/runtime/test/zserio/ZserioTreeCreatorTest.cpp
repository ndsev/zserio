#include "gtest/gtest.h"

#include "zserio/Array.h"
#include "zserio/TypeInfo.h"
#include "zserio/Reflectable.h"
#include "zserio/ZserioTreeCreator.h"

using namespace zserio::literals;

namespace zserio
{

namespace
{

using allocator_type = std::allocator<uint8_t>;

class DummyNested
{
public:
    using allocator_type = ::std::allocator<uint8_t>;

    explicit DummyNested(const allocator_type& allocator = allocator_type()) noexcept;

    ~DummyNested() = default;

    static const ITypeInfo& typeInfo();
    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    void initialize(
            uint32_t param_);
    bool isInitialized() const;

    uint32_t getParam() const;

    uint32_t getValue() const;
    void setValue(uint32_t value_);

    string<>& getText();
    const string<>& getText() const;
    void setText(const string<>& text_);
    void setText(string<>&& text_);

private:
    uint32_t m_param_;
    bool m_isInitialized;
    uint32_t m_value_;
    string<> m_text_;
};

class DummyObject
{
private:
    class ZserioElementFactory_nestedArray
    {
    public:
        explicit ZserioElementFactory_nestedArray(DummyObject& owner) :
                m_owner(owner)
        {}

        void create(vector<DummyNested>&, BitStreamReader&, size_t) const
        {}

        void create(PackingContextNode&, vector<DummyNested>&,
                BitStreamReader&, size_t) const
        {}

    private:
        DummyObject& m_owner;
    };

    class ZserioElementInitializer_nestedArray
    {
    public:
        explicit ZserioElementInitializer_nestedArray(DummyObject& owner) :
                m_owner(owner)
        {}

        void initialize(DummyNested& element, size_t index) const;

    private:
        DummyObject& m_owner;
    };

    using ZserioArrayType_nestedArray = Array<vector<DummyNested>, ObjectArrayTraits<DummyNested, ZserioElementFactory_nestedArray>, ArrayType::AUTO>;
    using ZserioArrayType_textArray = Array<vector<string<>>, StringArrayTraits, ArrayType::AUTO>;

public:
    using allocator_type = ::std::allocator<uint8_t>;

    explicit DummyObject(const allocator_type& allocator = allocator_type()) noexcept;
    ~DummyObject() = default;

    static const ITypeInfo& typeInfo();
    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    void initializeChildren();

    uint32_t getValue() const;
    void setValue(uint32_t value_);

    DummyNested& getNested();
    const DummyNested& getNested() const;
    void setNested(const DummyNested& nested_);
    void setNested(DummyNested&& nested_);

    string<>& getText();
    const string<>& getText() const;
    void setText(const string<>& text_);
    void setText(string<>&& text_);

    vector<DummyNested>& getNestedArray();
    const vector<DummyNested>& getNestedArray() const;
    void setNestedArray(const vector<DummyNested>& nestedArray_);
    void setNestedArray(vector<DummyNested>&& nestedArray_);

    vector<string<>>& getTextArray();
    const vector<string<>>& getTextArray() const;
    void setTextArray(const vector<string<>>& textArray_);
    void setTextArray(vector<string<>>&& textArray_);

private:
    bool m_areChildrenInitialized;
    uint32_t m_value_;
    DummyNested m_nested_;
    string<> m_text_;
    ZserioArrayType_nestedArray m_nestedArray_;
    ZserioArrayType_textArray m_textArray_;
};

void DummyObject::ZserioElementInitializer_nestedArray::initialize(DummyNested& element, size_t index) const
{
    (void)index;
    element.initialize(static_cast<uint32_t>(m_owner.getValue()));
}

DummyObject::DummyObject(const allocator_type& allocator) noexcept :
        m_areChildrenInitialized(false),
        m_value_(uint32_t()),
        m_nested_(allocator),
        m_text_(allocator),
        m_nestedArray_(ObjectArrayTraits<DummyNested, ZserioElementFactory_nestedArray>(), allocator),
        m_textArray_(StringArrayTraits(), allocator)
{
}

const ITypeInfo& DummyObject::typeInfo()
{
    static const StringView templateName;
    static const Span<TemplateArgumentInfo> templateArguments;

    static const std::array<StringView, 1> nestedTypeArguments = { "getValue"_sv };

    static const std::array<StringView, 1> nestedArrayTypeArguments = { "getValue"_sv };
    static const ::std::array<FieldInfo, 5> fields = {
        FieldInfo{
            makeStringView("value"), // schemaName
            BuiltinTypeInfo<>::getUInt32(), // typeInfo
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
        },
        FieldInfo{
            makeStringView("nested"), // schemaName
            DummyNested::typeInfo(), // typeInfo
            nestedTypeArguments, // typeArguments
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
        },
        FieldInfo{
            makeStringView("text"), // schemaName
            BuiltinTypeInfo<>::getString(), // typeInfo
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
        },
        FieldInfo{
            makeStringView("nestedArray"), // schemaName
            DummyNested::typeInfo(), // typeInfo
            nestedArrayTypeArguments, // typeArguments
            {}, // alignment
            {}, // offset
            {}, // initializer
            false, // isOptional
            {}, // optionalClause
            {}, // constraint
            true, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        },
        FieldInfo{
            makeStringView("textArray"), // schemaName
            BuiltinTypeInfo<>::getString(), // typeInfo
            {}, // typeArguments
            {}, // alignment
            {}, // offset
            {}, // initializer
            false, // isOptional
            {}, // optionalClause
            {}, // constraint
            true, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        }
    };

    static const Span<ParameterInfo> parameters;

    static const Span<FunctionInfo> functions;

    static const StructTypeInfo<std::allocator<uint8_t>> typeInfo = {
        makeStringView("DummyObject"),
        [](const std::allocator<uint8_t>& allocator) -> IReflectablePtr {
            return std::allocate_shared<ReflectableOwner<DummyObject>>(allocator, allocator);
        },
        templateName, templateArguments,
        fields, parameters, functions
    };

    return typeInfo;
}

IReflectablePtr DummyObject::reflectable(const allocator_type& allocator)
{
    class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
    {
    public:
        explicit Reflectable(DummyObject& object, const allocator_type& allocator) :
                ReflectableAllocatorHolderBase<allocator_type>(DummyObject::typeInfo(), allocator),
                m_object(object)
        {}

        virtual IReflectableConstPtr getField(StringView name) const override
        {
            if (name == makeStringView("value"))
            {
                return ReflectableFactory::getUInt32(m_object.getValue(), get_allocator());
            }
            if (name == makeStringView("nested"))
            {
                return m_object.getNested().reflectable(get_allocator());
            }
            if (name == makeStringView("text"))
            {
                return ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            if (name == makeStringView("nestedArray"))
            {
                return ReflectableFactory::getCompoundArray(m_object.getNestedArray(), get_allocator());
            }
            if (name == makeStringView("textArray"))
            {
                return ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getString(),
                        m_object.getTextArray(), get_allocator());
            }
            else
            {
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
            }
        }

        virtual IReflectablePtr getField(StringView name) override
        {
            if (name == makeStringView("value"))
            {
                return ReflectableFactory::getUInt32(m_object.getValue(), get_allocator());
            }
            if (name == makeStringView("nested"))
            {
                return m_object.getNested().reflectable(get_allocator());
            }
            if (name == makeStringView("text"))
            {
                return ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            if (name == makeStringView("nestedArray"))
            {
                return ReflectableFactory::getCompoundArray(m_object.getNestedArray(), get_allocator());
            }
            if (name == makeStringView("textArray"))
            {
                return ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getString(),
                        m_object.getTextArray(), get_allocator());
            }
            else
            {
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
            }
        }

        virtual void setField(StringView name,
                const AnyHolder<allocator_type>& value) override
        {
            if (name == makeStringView("value"))
            {
                m_object.setValue(value.get<uint32_t>());
                return;
            }
            if (name == makeStringView("nested"))
            {
                m_object.setNested(value.get<DummyNested>());
                return;
            }
            if (name == makeStringView("text"))
            {
                m_object.setText(value.get<string<>>());
                return;
            }
            if (name == makeStringView("nestedArray"))
            {
                m_object.setNestedArray(value.get<vector<DummyNested>>());
                return;
            }
            if (name == makeStringView("textArray"))
            {
                m_object.setTextArray(value.get<vector<string<>>>());
                return;
            }
            else
            {
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
            }
        }

        virtual void initializeChildren() override
        {
            m_object.initializeChildren();
        }

        virtual void write(BitStreamWriter&) const override
        {}

        virtual size_t bitSizeOf(size_t) const override
        {
            return 0;
        }

    private:
        DummyObject& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}

void DummyObject::initializeChildren()
{
    m_nested_.initialize(static_cast<uint32_t>(getValue()));
    m_nestedArray_.initializeElements(ZserioElementInitializer_nestedArray(*this));

    m_areChildrenInitialized = true;
}

uint32_t DummyObject::getValue() const
{
    return m_value_;
}

void DummyObject::setValue(uint32_t value_)
{
    m_value_ = value_;
}

DummyNested& DummyObject::getNested()
{
    return m_nested_;
}

const DummyNested& DummyObject::getNested() const
{
    return m_nested_;
}

void DummyObject::setNested(const DummyNested& nested_)
{
    m_nested_ = nested_;
}

void DummyObject::setNested(DummyNested&& nested_)
{
    m_nested_ = ::std::move(nested_);
}

string<>& DummyObject::getText()
{
    return m_text_;
}

const string<>& DummyObject::getText() const
{
    return m_text_;
}

void DummyObject::setText(const string<>& text_)
{
    m_text_ = text_;
}

void DummyObject::setText(string<>&& text_)
{
    m_text_ = ::std::move(text_);
}

vector<DummyNested>& DummyObject::getNestedArray()
{
    return m_nestedArray_.getRawArray();
}

const vector<DummyNested>& DummyObject::getNestedArray() const
{
    return m_nestedArray_.getRawArray();
}

void DummyObject::setNestedArray(const vector<DummyNested>& nestedArray_)
{
    m_nestedArray_ = ZserioArrayType_nestedArray(nestedArray_, ObjectArrayTraits<DummyNested, ZserioElementFactory_nestedArray>());
}

void DummyObject::setNestedArray(vector<DummyNested>&& nestedArray_)
{
    m_nestedArray_ = ZserioArrayType_nestedArray(std::move(nestedArray_), ObjectArrayTraits<DummyNested, ZserioElementFactory_nestedArray>());
}

vector<string<>>& DummyObject::getTextArray()
{
    return m_textArray_.getRawArray();
}

const vector<string<>>& DummyObject::getTextArray() const
{
    return m_textArray_.getRawArray();
}

void DummyObject::setTextArray(const vector<string<>>& textArray_)
{
    m_textArray_ = ZserioArrayType_textArray(textArray_, StringArrayTraits());
}

void DummyObject::setTextArray(vector<string<>>&& textArray_)
{
    m_textArray_ = ZserioArrayType_textArray(std::move(textArray_), StringArrayTraits());
}

DummyNested::DummyNested(const allocator_type& allocator) noexcept :
        m_isInitialized(false),
        m_value_(uint32_t()),
        m_text_(allocator)
{
}

const ITypeInfo& DummyNested::typeInfo()
{
    static const StringView templateName;
    static const Span<TemplateArgumentInfo> templateArguments;

    static const ::std::array<FieldInfo, 2> fields = {
        FieldInfo{
            makeStringView("value"), // schemaName
            BuiltinTypeInfo<>::getUInt32(), // typeInfo
            {}, // typeArguments
            {}, // alignment
            {}, // offset
            {}, // initializer
            false, // isOptional
            {}, // optionalClause
            makeStringView("getValue() < getParam()"), // constraint
            false, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        },
        FieldInfo{
            makeStringView("text"), // schemaName
            BuiltinTypeInfo<>::getString(), // typeInfo
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

    static const ::std::array<ParameterInfo, 1> parameters = {
        ParameterInfo{
            makeStringView("param"),
            BuiltinTypeInfo<>::getUInt32()
        }
    };

    static const Span<FunctionInfo> functions;

    static const StructTypeInfo<std::allocator<uint8_t>> typeInfo = {
        makeStringView("DummyNested"),
        [](const std::allocator<uint8_t>& allocator) -> IReflectablePtr {
            return std::allocate_shared<ReflectableOwner<DummyNested>>(allocator, allocator);
        },
        templateName, templateArguments, fields, parameters, functions
    };

    return typeInfo;
}

IReflectablePtr DummyNested::reflectable(const allocator_type& allocator)
{
    class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
    {
    public:
        explicit Reflectable(DummyNested& object, const allocator_type& allocator) :
                ReflectableAllocatorHolderBase<allocator_type>(DummyNested::typeInfo(), allocator),
                m_object(object)
        {}

        virtual IReflectableConstPtr getField(StringView name) const override
        {
            if (name == makeStringView("value"))
            {
                return ReflectableFactory::getUInt32(m_object.getValue(), get_allocator());
            }
            if (name == makeStringView("text"))
            {
                return ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            else
            {
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
            }
        }

        virtual IReflectablePtr getField(StringView name) override
        {
            if (name == makeStringView("value"))
            {
                return ReflectableFactory::getUInt32(m_object.getValue(), get_allocator());
            }
            if (name == makeStringView("text"))
            {
                return ReflectableFactory::getString(m_object.getText(), get_allocator());
            }
            else
            {
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
            }
        }

        virtual void setField(StringView name,
                const AnyHolder<allocator_type>& value) override
        {
            if (name == makeStringView("value"))
            {
                m_object.setValue(value.get<uint32_t>());
                return;
            }
            if (name == makeStringView("text"))
            {
                m_object.setText(value.get<string<>>());
                return;
            }
            else
            {
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
            }
        }

        virtual IReflectableConstPtr getParameter(StringView name) const override
        {
            if (name == makeStringView("param"))
            {
                return ReflectableFactory::getUInt32(m_object.getParam(), get_allocator());
            }
            else
            {
                throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyNested'!";
            }
        }

        virtual IReflectablePtr getParameter(StringView name) override
        {
            if (name == makeStringView("param"))
            {
                return ReflectableFactory::getUInt32(m_object.getParam(), get_allocator());
            }
            else
            {
                throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyNested'!";
            }
        }

        virtual void initializeChildren() override
        {}

        virtual void write(BitStreamWriter&) const override
        {}

        virtual size_t bitSizeOf(size_t) const override
        {
            return 0;
        }

    private:
        DummyNested& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}

void DummyNested::initialize(
        uint32_t param_)
{
    m_param_ = param_;
    m_isInitialized = true;
}

bool DummyNested::isInitialized() const
{
    return m_isInitialized;
}

uint32_t DummyNested::getParam() const
{
    if (!m_isInitialized)
        throw CppRuntimeException("Parameter 'param' of compound 'DummyNested' is not initialized!");

    return m_param_;
}

uint32_t DummyNested::getValue() const
{
    return m_value_;
}

void DummyNested::setValue(uint32_t value_)
{
    m_value_ = value_;
}

string<>& DummyNested::getText()
{
    return m_text_;
}

const string<>& DummyNested::getText() const
{
    return m_text_;
}

void DummyNested::setText(const string<>& text_)
{
    m_text_ = text_;
}

void DummyNested::setText(string<>&& text_)
{
    m_text_ = ::std::move(text_);
}

} // namespace

TEST(ZserioTreeCreatorTest, testCreateObject)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());
    creator.beginRoot();
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);
    ASSERT_EQ(CppType::STRUCT, reflectable->getTypeInfo().getCppType());
}

TEST(ZserioTreeCreatorTest, testCreateObjectSetFields)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());
    creator.beginRoot();
    creator.setValue("value", 13);
    creator.setValue("text", "test");
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);

    ASSERT_EQ(13, reflectable->getField("value")->getUInt32());
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getString());
}

TEST(ZserioTreeCreatorTest, testCreateObjectFull)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());
    creator.beginRoot();
    creator.setValue("value", 13);
    creator.setValue("text", string<>("test"));
    creator.beginCompound("nested");
    creator.setValue("value", 10);
    creator.setValue("text", "nested"_sv);
    creator.endCompound();
    creator.beginArray("nestedArray");
    creator.beginCompoundElement();
    creator.setValue("value", 5);
    creator.setValue("text", "nestedArray");
    creator.endCompoundElement();
    creator.endArray();
    creator.beginArray("textArray");
    creator.addValueElement("this");
    creator.addValueElement("is");
    creator.addValueElement("text"_sv);
    creator.addValueElement("array");
    creator.endArray();
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);
}

} // namespace zserio
