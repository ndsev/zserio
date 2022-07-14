#include "ZserioTreeCreatorTestObject.h"

namespace zserio
{

constexpr ::std::array<const char*, 2> EnumTraits<DummyEnum>::names;
constexpr ::std::array<DummyEnum, 2> EnumTraits<DummyEnum>::values;

template <>
size_t enumToOrdinal(DummyEnum value)
{
    switch (value)
    {
    case DummyEnum::ONE:
        return 0;
    case DummyEnum::TWO:
        return 1;
    default:
        throw ::zserio::CppRuntimeException("Unknown value for enumeration DarkColor: ") +
                static_cast<typename ::std::underlying_type<DummyEnum>::type>(value) + "!";
    }
}

template <>
DummyEnum valueToEnum(uint8_t rawValue)
{
    switch (rawValue)
    {
    case UINT8_C(0):
        return DummyEnum::ONE;
    default:
        return DummyEnum::TWO;
    }
}

template <>
const ITypeInfo& enumTypeInfo<DummyEnum, std::allocator<uint8_t>>()
{
    static const Span<StringView> underlyingTypeArguments;

    static const ::std::array<ItemInfo, 2> items = {
        ItemInfo{ makeStringView("ONE"), makeStringView("UINT8_C(0)") },
        ItemInfo{ makeStringView("TWO"), makeStringView("UINT8_C(1)") }
    };

    static const EnumTypeInfo<std::allocator<uint8_t>> typeInfo = {
        makeStringView("DummyEnum"),
        BuiltinTypeInfo<>::getUInt8(), underlyingTypeArguments, items
    };

    return typeInfo;
}

template <>
IReflectablePtr enumReflectable(DummyEnum value, const ::std::allocator<uint8_t>& allocator)
{
    class Reflectable : public ReflectableBase<::std::allocator<uint8_t>>
    {
    public:
        explicit Reflectable(DummyEnum value) :
                ReflectableBase<::std::allocator<uint8_t>>(enumTypeInfo<DummyEnum>()),
                m_value(value)
        {}

        virtual uint8_t getUInt8() const override
        {
            return static_cast<typename ::std::underlying_type<DummyEnum>::type>(m_value);
        }

        virtual void write(BitStreamWriter&) const override
        {
        }

        virtual size_t bitSizeOf(size_t) const override
        {
            return 8;
        }

        virtual uint64_t toUInt() const override
        {
            return getUInt8();
        }

    private:
        DummyEnum m_value;
    };

    return std::allocate_shared<Reflectable>(allocator, value);
}


const ITypeInfo& DummyBitmask::typeInfo()
{
    static const Span<StringView> underlyingTypeArguments;

    static const ::std::array<ItemInfo, 2> values = {
        ItemInfo{ makeStringView("READ"), makeStringView("UINT8_C(1)") },
        ItemInfo{ makeStringView("WRITE"), makeStringView("UINT8_C(2)") }
    };

    static const BitmaskTypeInfo<std::allocator<uint8_t>> typeInfo = {
        makeStringView("DummyBitmask"),
        BuiltinTypeInfo<>::getUInt8(), underlyingTypeArguments, values
    };

    return typeInfo;
}

IReflectablePtr DummyBitmask::reflectable(const ::std::allocator<uint8_t>& allocator) const
{
    class Reflectable : public ReflectableBase<::std::allocator<uint8_t>>
    {
    public:
        explicit Reflectable(DummyBitmask bitmask) :
                ReflectableBase<::std::allocator<uint8_t>>(DummyBitmask::typeInfo()),
                m_bitmask(bitmask)
        {}

        virtual uint8_t getUInt8() const override
        {
            return m_bitmask.getValue();
        }

        virtual void write(BitStreamWriter&) const override
        {
        }

        virtual size_t bitSizeOf(size_t) const override
        {
            return 8;
        }

        virtual uint64_t toUInt() const override
        {
            return getUInt8();
        }

    private:
        DummyBitmask m_bitmask;
    };

    return ::std::allocate_shared<Reflectable>(allocator, *this);
}

DummyNested::DummyNested(const allocator_type& allocator) noexcept :
        m_isInitialized(false),
        m_value_(uint32_t()),
        m_text_(allocator),
        m_dummyEnum_(DummyEnum()),
        m_dummyBitmask_(DummyBitmask())
{
}

const ITypeInfo& DummyNested::typeInfo()
{
    static const StringView templateName;
    static const Span<TemplateArgumentInfo> templateArguments;

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
        },
        FieldInfo{
            makeStringView("data"), // schemaName
            BuiltinTypeInfo<>::getBitBuffer(), // typeInfo
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
            makeStringView("dummyEnum"), // schemaName
            enumTypeInfo<DummyEnum>(), // typeInfo
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
            makeStringView("dummyBitmask"), // schemaName
            DummyBitmask::typeInfo(), // typeInfo
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
            if (name == makeStringView("data"))
            {
                return ReflectableFactory::getBitBuffer(m_object.getData(), get_allocator());
            }
            if (name == makeStringView("dummyEnum"))
            {
                return enumReflectable(m_object.getDummyEnum(), get_allocator());
            }
            if (name == makeStringView("dummyBitmask"))
            {
                return m_object.getDummyBitmask().reflectable(get_allocator());
            }

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
        }

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
            if (name == makeStringView("data"))
            {
                return ReflectableFactory::getBitBuffer(m_object.getData(), get_allocator());
            }
            if (name == makeStringView("dummyEnum"))
            {
                return enumReflectable(m_object.getDummyEnum(), get_allocator());
            }
            if (name == makeStringView("dummyBitmask"))
            {
                return m_object.getDummyBitmask().reflectable(get_allocator());
            }

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
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
            if (name == makeStringView("data"))
            {
                m_object.setData(value.get<BitBuffer>());
                return;
            }
            if (name == makeStringView("dummyEnum"))
            {
                if (value.isType<DummyEnum>())
                {
                    m_object.setDummyEnum(value.get<DummyEnum>());
                }
                else
                {
                    m_object.setDummyEnum(valueToEnum<DummyEnum>(
                            value.get<typename std::underlying_type<DummyEnum>::type>()));
                }
                return;
            }
            if (name == makeStringView("dummyBitmask"))
            {
                if (value.isType<DummyBitmask>())
                    m_object.setDummyBitmask(value.get<DummyBitmask>());
                else
                    m_object.setDummyBitmask(DummyBitmask(value.get<DummyBitmask::underlying_type>()));
                return;
            }

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
        }

        virtual IReflectablePtr getParameter(StringView name) override
        {
            if (name == makeStringView("param"))
            {
                return ReflectableFactory::getUInt32(m_object.getParam(), get_allocator());
            }

            throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyNested'!";
        }

        virtual IReflectableConstPtr getParameter(StringView name) const override
        {
            if (name == makeStringView("param"))
            {
                return ReflectableFactory::getUInt32(m_object.getParam(), get_allocator());
            }

            throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyNested'!";
        }

        virtual void initialize(const vector<AnyHolder<>>& typeArguments) override
        {
            if (typeArguments.size() != 1)
            {
                throw CppRuntimeException("No enough arguments to DummyNested::initialize, expecting 1, got") +
                        typeArguments.size();
            }
            m_object.initialize(typeArguments[0].get<uint32_t>());
        }

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

void DummyNested::setText(const string<>& text_)
{
    m_text_ = text_;
}

BitBuffer& DummyNested::getData()
{
    return m_data_;
}

void DummyNested::setData(const BitBuffer& data_)
{
    m_data_ = data_;
}

DummyEnum DummyNested::getDummyEnum() const
{
    return m_dummyEnum_;
}

void DummyNested::setDummyEnum(DummyEnum dummyEnum_)
{
    m_dummyEnum_ = dummyEnum_;
}

DummyBitmask DummyNested::getDummyBitmask() const
{
    return m_dummyBitmask_;
}

void DummyNested::setDummyBitmask(DummyBitmask dummyBitmask_)
{
    m_dummyBitmask_ = dummyBitmask_;
}

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
    static const ::std::array<FieldInfo, 8> fields = {
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
        },
        FieldInfo{
            makeStringView("externArray"), // schemaName
            BuiltinTypeInfo<>::getBitBuffer(), // typeInfo
            {}, // typeArguments
            {}, // alignment
            {}, // offset
            {}, // initializer
            true, // isOptional
            {}, // optionalClause
            {}, // constraint
            true, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        },
        FieldInfo{
            makeStringView("optionalBool"), // schemaName
            BuiltinTypeInfo<>::getBool(), // typeInfo
            {}, // typeArguments
            {}, // alignment
            {}, // offset
            {}, // initializer
            true, // isOptional
            {}, // optionalClause
            {}, // constraint
            false, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        },
        FieldInfo{
            makeStringView("optionalNested"), // schemaName
            DummyNested::typeInfo(), // typeInfo
            {}, // typeArguments
            {}, // alignment
            {}, // offset
            {}, // initializer
            true, // isOptional
            {}, // optionalClause
            {}, // constraint
            false, // isArray
            {}, // arrayLength
            false, // isPacked
            false // isImplicit
        },
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
            if (name == makeStringView("externArray"))
            {
                if (!m_object.isExternArraySet())
                    return nullptr;

                return ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getBitBuffer(),
                        m_object.getExternArray(), get_allocator());
            }
            if (name == makeStringView("optionalBool"))
            {
                if (!m_object.isOptionalBoolSet())
                    return nullptr;

                return ReflectableFactory::getBool(m_object.getOptionalBool(), get_allocator());
            }
            if (name == makeStringView("optionalNested"))
            {
                if (!m_object.isOptionalNestedSet())
                    return nullptr;

                return m_object.getOptionalNested().reflectable(get_allocator());
            }

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
        }

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
            if (name == makeStringView("externArray"))
            {
                if (!m_object.isExternArraySet())
                    return nullptr;

                return ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getBitBuffer(),
                        m_object.getExternArray(), get_allocator());
            }
            if (name == makeStringView("optionalBool"))
            {
                if (!m_object.isOptionalBoolSet())
                    return nullptr;

                return ReflectableFactory::getBool(m_object.getOptionalBool(), get_allocator());
            }
            if (name == makeStringView("optionalNested"))
            {
                if (!m_object.isOptionalNestedSet())
                    return nullptr;

                return m_object.getOptionalNested().reflectable(get_allocator());
            }

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
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
            if (name == makeStringView("optionalBool"))
            {
                if (value.isType<std::nullptr_t>())
                    m_object.resetOptionalBool();
                else
                    m_object.setOptionalBool(value.get<bool>());
                return;
            }
            if (name == makeStringView("textArray"))
            {
                m_object.setTextArray(value.get<vector<string<>>>());
                return;
            }

            // note that unused setters are omitted!

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
        }

        virtual IReflectablePtr createField(StringView name) override
        {
            if (name == makeStringView("text"))
            {
                m_object.setText(string<>(get_allocator()));
                return ReflectableFactory::getString(m_object.getText(), get_allocator());
            }

            if (name == makeStringView("externArray"))
            {
                m_object.setExternArray(std::vector<BitBuffer>(get_allocator()));
                return ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getBitBuffer(),
                        m_object.getExternArray(), get_allocator());
            }

            if (name == makeStringView("optionalNested"))
            {
                m_object.setOptionalNested(DummyNested(get_allocator()));
                return m_object.getOptionalNested().reflectable(get_allocator());
            }

            throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
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

    if (isOptionalNestedSet())
        m_optionalNested_.value().initialize(static_cast<uint32_t>(getValue()));

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

string<>& DummyObject::getText()
{
    return m_text_;
}

void DummyObject::setText(const string<>& text_)
{
    m_text_ = text_;
}

vector<DummyNested>& DummyObject::getNestedArray()
{
    return m_nestedArray_.getRawArray();
}

vector<string<>>& DummyObject::getTextArray()
{
    return m_textArray_.getRawArray();
}

void DummyObject::setTextArray(const vector<string<>>& textArray_)
{
    m_textArray_ = ZserioArrayType_textArray(textArray_, StringArrayTraits());
}

bool DummyObject::isExternArraySet() const
{
    return m_externArray_.hasValue();
}

vector<BitBuffer>& DummyObject::getExternArray()
{
    return m_externArray_.value().getRawArray();
}

void DummyObject::setExternArray(const vector<BitBuffer>& externArray_)
{
    m_externArray_ = ZserioArrayType_externArray(externArray_, BitBufferArrayTraits());
}

bool DummyObject::isOptionalBoolSet() const
{
    return m_optionalBool_.hasValue();
}

bool DummyObject::getOptionalBool() const
{
    return m_optionalBool_.value();
}

void DummyObject::setOptionalBool(bool optionalBool_)
{
    m_optionalBool_ = optionalBool_;
}

void DummyObject::resetOptionalBool()
{
    m_optionalBool_.reset();
}

bool DummyObject::isOptionalNestedSet() const
{
    return m_optionalNested_.hasValue();
}

DummyNested& DummyObject::getOptionalNested()
{
    return m_optionalNested_.value();
}

void DummyObject::setOptionalNested(const DummyNested& optionalNested_)
{
    m_optionalNested_ = optionalNested_;
}

} // namespace zserio
