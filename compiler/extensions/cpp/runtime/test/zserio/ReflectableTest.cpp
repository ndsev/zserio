#include <string>
#include <functional>
#include <type_traits>

#include "gtest/gtest.h"

#include "zserio/Reflectable.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/ArrayTraits.h"

using namespace zserio::literals;
using namespace std::placeholders;

namespace zserio
{

namespace
{

class DummyBitmask
{
public:
    typedef uint8_t underlying_type;

    struct Values
    {
        static const DummyBitmask CREATE;
        static const DummyBitmask READ;
        static const DummyBitmask WRITE;
    };

    DummyBitmask() :
            m_value(0)
    {}

    explicit DummyBitmask(BitStreamReader& in) :
        m_value(readValue(in))
    {}

    explicit DummyBitmask(underlying_type value) :
        m_value(value)
    {}

    underlying_type getValue() const
    {
        return m_value;
    }

    string<> toString(const std::allocator<char>& allocator = std::allocator<char>()) const
    {
        // simplified implementation
        string<> result(allocator);
        if (*this == DummyBitmask::Values::CREATE)
            result = "CREATE";
        else if (*this == DummyBitmask::Values::READ)
            result = "READ";
        else if (*this == DummyBitmask::Values::WRITE)
            result = "WRITE";

        return result;
    }

    static const ITypeInfo& typeInfo()
    {
        static const Span<StringView> underlyingTypeArguments;

        static const std::array<ItemInfo, 3> values = {
            ItemInfo{ makeStringView("CREATE"), makeStringView("UINT8_C(1)") },
            ItemInfo{ makeStringView("READ"), makeStringView("UINT8_C(2)") },
            ItemInfo{ makeStringView("WRITE"), makeStringView("UINT8_C(4)") }
        };

        static const BitmaskTypeInfo<std::allocator<uint8_t>> typeInfo = {
            makeStringView("DummyBitmask"),
            BuiltinTypeInfo<>::getUInt8(), underlyingTypeArguments, values
        };

        return typeInfo;
    }

    IReflectablePtr reflectable(const std::allocator<uint8_t>& allocator = std::allocator<uint8_t>()) const
    {
        class Reflectable : public ReflectableBase<std::allocator<uint8_t>>
        {
        public:
            explicit Reflectable(DummyBitmask bitmask) :
                    ReflectableBase<std::allocator<uint8_t>>(DummyBitmask::typeInfo()),
                    m_bitmask(bitmask)
            {}

            virtual uint8_t getUInt8() const override
            {
                return m_bitmask.getValue();
            }

            virtual uint64_t toUInt() const override
            {
                return m_bitmask.getValue();
            }

            virtual double toDouble() const override
            {
                return static_cast<double>(toUInt());
            }

            virtual string<> toString(
                    const std::allocator<uint8_t>& allocator = std::allocator<uint8_t>()) const override
            {
                return m_bitmask.toString(allocator);
            }

            virtual void write(BitStreamWriter& writer) const override
            {
                m_bitmask.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition) const override
            {
                return m_bitmask.bitSizeOf(bitPosition);
            }

        private:
            DummyBitmask m_bitmask;
        };

        return std::allocate_shared<Reflectable>(allocator, *this);
    }

    bool operator==(const DummyBitmask& other) const
    {
        return m_value == other.m_value;
    }

    void write(BitStreamWriter& out) const
    {
        out.writeBits(m_value, UINT8_C(8));
    }

    size_t bitSizeOf(size_t) const
    {
        return UINT8_C(8);
    }

private:
    static underlying_type readValue(BitStreamReader& in)
    {
        return static_cast<underlying_type>(in.readBits(UINT8_C(8)));
    }

    underlying_type m_value;
};

const DummyBitmask DummyBitmask::Values::CREATE = DummyBitmask(UINT8_C(1));
const DummyBitmask DummyBitmask::Values::READ = DummyBitmask(UINT8_C(2));
const DummyBitmask DummyBitmask::Values::WRITE = DummyBitmask(UINT8_C(4));

enum class DummyEnum : int8_t
{
    VALUE1 = UINT8_C(-1),
    VALUE2 = UINT8_C(0),
    VALUE3 = UINT8_C(1)
};

class DummyChild
{
public:
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyChild(const allocator_type& = allocator_type()) :
            m_isInitialized(false), m_value(uint32_t())
    {}

    explicit DummyChild(uint32_t value) :
            m_isInitialized(false), m_value(value)
    {}

    explicit DummyChild(BitStreamReader& in, int32_t dummyParam_, string<>& stringParam_) :
            m_dummyParam_(dummyParam_), m_stringParam_(&stringParam_), m_isInitialized(true),
            m_value(readValue(in))
    {}

    DummyChild(const DummyChild& other) :
            m_value(other.m_value)
    {
        if (other.m_isInitialized)
            initialize(other.m_dummyParam_, *(other.m_stringParam_));
        else
            m_isInitialized = false;
    }

    DummyChild& operator=(const DummyChild& other)
    {
        m_value = other.m_value;
        if (other.m_isInitialized)
            initialize(other.m_dummyParam_, *(other.m_stringParam_));
        else
            m_isInitialized = false;

        return *this;
    }

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

        static const std::array<FieldInfo, 1> fields = {
            FieldInfo{
                makeStringView("value"), // schemaName
                BuiltinTypeInfo<>::getFixedUnsignedBitField(31), // typeInfo
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

        static const std::array<ParameterInfo, 2> parameters = {
            ParameterInfo{
                makeStringView("dummyParam"),
                BuiltinTypeInfo<>::getFixedSignedBitField(31)
            },
            ParameterInfo{ // non-simple parameter to check reference_wrapper
                makeStringView("stringParam"),
                BuiltinTypeInfo<>::getString()
            }
        };

        static const std::array<FunctionInfo, 2> functions = {
            FunctionInfo{
                makeStringView("getValue"),
                BuiltinTypeInfo<>::getFixedSignedBitField(31),
                makeStringView("getValue()")
            },
            FunctionInfo{
                makeStringView("throwingFunction"),
                BuiltinTypeInfo<>::getFixedSignedBitField(31),
                makeStringView("getValue()")
            }
        };

        static const StructTypeInfo<std::allocator<uint8_t>> typeInfo = {
            makeStringView("DummyChild"),
            [](const std::allocator<uint8_t>& allocator) -> IReflectablePtr {
                return std::allocate_shared<ReflectableOwner<DummyChild>>(allocator, allocator);
            },
            templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IReflectableConstPtr reflectable(const allocator_type& allocator = allocator_type()) const
    {
        class Reflectable : public ReflectableConstAllocatorHolderBase<allocator_type>
        {
        private:
            using Base = ReflectableConstAllocatorHolderBase<allocator_type>;

        public:
            using Base::getField;
            using Base::getParameter;
            using Base::callFunction;

            explicit Reflectable(const DummyChild& object, const allocator_type& allocator) :
                    Base(DummyChild::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IReflectableConstPtr getField(StringView name) const override
            {
                if (name == makeStringView("value"))
                {
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.getValue(), get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IReflectableConstPtr getParameter(StringView name) const override
            {
                if (name == makeStringView("dummyParam"))
                {
                    return ReflectableFactory::getFixedSignedBitField(
                            31, m_object.getDummyParam(), get_allocator());
                }
                if (name == makeStringView("stringParam"))
                {
                    return ReflectableFactory::getString(m_object.getStringParam(), get_allocator());
                }

                throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyChild'!";
            }

            virtual IReflectableConstPtr callFunction(StringView name) const override
            {
                if (name == makeStringView("getValue"))
                {
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.funcGetValue(), get_allocator());
                }
                else if (name == makeStringView("throwingFunction"))
                {
                    throw CppRuntimeException("Testing throw from function call");
                }
                else
                {
                    throw CppRuntimeException("Function '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual void write(BitStreamWriter& writer) const override
            {
                m_object.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition) const override
            {
                return m_object.bitSizeOf(bitPosition);
            }

        private:
            const DummyChild& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            explicit Reflectable(DummyChild& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(DummyChild::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual void initializeChildren() override
            {}

            virtual void initialize(const vector<AnyHolder<>>& typeArguments) override
            {
                if (typeArguments.size() != 2)
                    throw CppRuntimeException("Wrong number of type arguments!");

                m_object.initialize(typeArguments[0].get<int32_t>(),
                        typeArguments[1].get<std::reference_wrapper<string<>>>().get());
            }

            virtual IReflectableConstPtr getField(StringView name) const override
            {
                if (name == makeStringView("value"))
                {
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.getValue(), get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IReflectablePtr getField(StringView name) override
            {
                if (name == makeStringView("value"))
                {
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.getValue(), get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IReflectableConstPtr getParameter(StringView name) const override
            {
                if (name == makeStringView("dummyParam"))
                {
                    return ReflectableFactory::getFixedSignedBitField(
                            31, m_object.getDummyParam(), get_allocator());
                }
                if (name == makeStringView("stringParam"))
                {
                    return ReflectableFactory::getString(m_object.getStringParam(), get_allocator());
                }

                throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyChild'!";
            }

            virtual IReflectablePtr getParameter(StringView name) override
            {
                if (name == makeStringView("dummyParam"))
                {
                    return ReflectableFactory::getFixedSignedBitField(
                            31, m_object.getDummyParam(), get_allocator());
                }
                if (name == makeStringView("stringParam"))
                {
                    return ReflectableFactory::getString(m_object.getStringParam(), get_allocator());
                }

                throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyChild'!";
            }

            virtual IReflectableConstPtr callFunction(StringView name) const override
            {
                if (name == makeStringView("getValue"))
                {
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.funcGetValue(), get_allocator());
                }
                else if (name == makeStringView("throwingFunction"))
                {
                    throw CppRuntimeException("Testing throw from function call");
                }
                else
                {
                    throw CppRuntimeException("Function '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IReflectablePtr callFunction(StringView name) override
            {
                if (name == makeStringView("getValue"))
                {
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.funcGetValue(), get_allocator());
                }
                else if (name == makeStringView("throwingFunction"))
                {
                    throw CppRuntimeException("Testing throw from function call");
                }
                else
                {
                    throw CppRuntimeException("Function '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual void setField(StringView name,
                    const AnyHolder<allocator_type>& value) override
            {
                if (name == makeStringView("value"))
                {
                    m_object.setValue(value.get<uint32_t>());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual void write(BitStreamWriter& writer) const override
            {
                m_object.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition) const override
            {
                return m_object.bitSizeOf(bitPosition);
            }

        private:
            DummyChild& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    void initialize(int32_t dummyParam_, string<>& stringParam_)
    {
        m_dummyParam_ = dummyParam_;
        m_stringParam_ = &stringParam_;
        m_isInitialized = true;
    }

    int32_t getDummyParam() const
    {
        if (!m_isInitialized)
            throw CppRuntimeException("Not initialized!");
        return m_dummyParam_;
    }

    string<>& getStringParam()
    {
        if (!m_isInitialized)
            throw CppRuntimeException("Not initialized!");
        return *m_stringParam_;
    }

    const string<>& getStringParam() const
    {
        if (!m_isInitialized)
            throw CppRuntimeException("Not initialized!");
        return *m_stringParam_;
    }

    bool operator==(const DummyChild& other) const
    {
        return m_value == other.m_value;
    }

    uint32_t getValue() const
    {
        return m_value;
    }

    uint32_t funcGetValue() const
    {
        return getValue();
    }

    void setValue(uint32_t value)
    {
        m_value = value;
    }

    void write(BitStreamWriter& writer) const
    {
        writer.writeBits(m_value, 31);
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return 31;
    }

private:
    uint32_t readValue(BitStreamReader& in)
    {
        return in.readBits(31);
    }

    int32_t m_dummyParam_;
    string<>* m_stringParam_;
    bool m_isInitialized;
    uint32_t m_value;
};

class DummyParent
{
public:
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyParent(const allocator_type& allocator = allocator_type()) :
            m_areChildrenInitialized(false), m_dummyChild(allocator) {}
    explicit DummyParent(const string<>& stringField, const DummyChild& dummyChild) :
            m_areChildrenInitialized(false), m_stringField(stringField), m_dummyChild(dummyChild) {}
    explicit DummyParent(BitStreamReader& in):
            m_areChildrenInitialized(true),
            m_stringField(readStringField(in)),
            m_dummyChild(readDummyChild(in))
    {}

    DummyParent(const DummyParent& other) :
            m_stringField(other.m_stringField),
            m_dummyChild(other.m_dummyChild)
    {
        if (other.m_areChildrenInitialized)
            initializeChildren();
        else
            m_areChildrenInitialized = false;
    }

    DummyParent& operator=(const DummyParent& other)
    {
        m_stringField = other.m_stringField;
        m_dummyChild = other.m_dummyChild;

        if (other.m_areChildrenInitialized)
            initializeChildren();
        else
            m_areChildrenInitialized = false;

        return *this;
    }

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

        static const std::array<FieldInfo, 2> fields = {
            FieldInfo{
                makeStringView("stringField"), // schemaName
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
                makeStringView("dummyChild"), // schemaName
                DummyChild::typeInfo(), // typeInfo
                {}, // typeArguments (omitted since not needed in test)
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

        static const Span<ParameterInfo> parameters;

        static const Span<FunctionInfo> functions;

        static const StructTypeInfo<std::allocator<uint8_t>> typeInfo = {
            makeStringView("DummyParent"),
            [](const std::allocator<uint8_t>& allocator) -> IReflectablePtr {
                return std::allocate_shared<ReflectableOwner<DummyParent>>(allocator, allocator);
            },
            templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IReflectableConstPtr reflectable(const allocator_type& allocator = allocator_type()) const
    {
        class Reflectable : public ReflectableConstAllocatorHolderBase<allocator_type>
        {
        private:
            using Base = ReflectableConstAllocatorHolderBase<allocator_type>;

        public:
            using Base::getField;
            using Base::getParameter;
            using Base::callFunction;

            explicit Reflectable(const DummyParent& object, const allocator_type& allocator) :
                    Base(DummyParent::typeInfo(), allocator), m_object(object)
            {}

            virtual IReflectableConstPtr getField(StringView name) const override
            {
                if (name == makeStringView("dummyChild"))
                {
                    return m_object.getDummyChild().reflectable(get_allocator());
                }

                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
            }

            virtual void write(BitStreamWriter& writer) const override
            {
                m_object.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition) const override
            {
                return m_object.bitSizeOf(bitPosition);
            }

        private:
            const DummyParent& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            explicit Reflectable(DummyParent& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(DummyParent::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual void initializeChildren() override
            {
                m_object.initializeChildren();
            }

            virtual IReflectableConstPtr getField(StringView name) const override
            {
                if (name == makeStringView("dummyChild"))
                {
                    return m_object.getDummyChild().reflectable(get_allocator());
                }

                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
            }

            virtual IReflectablePtr getField(StringView name) override
            {
                if (name == makeStringView("dummyChild"))
                {
                    return m_object.getDummyChild().reflectable(get_allocator());
                }

                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
            }

            virtual IReflectablePtr createField(StringView name) override
            {
                if (name == makeStringView("dummyChild"))
                {
                    m_object.setDummyChild(DummyChild());
                    return m_object.getDummyChild().reflectable(get_allocator());
                }

                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
            }

            virtual void setField(StringView name, const AnyHolder<allocator_type>& value) override
            {
                if (name == makeStringView("dummyChild"))
                {
                    m_object.setDummyChild(value.get<DummyChild>());
                    return;
                }

                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
            }

            virtual void write(BitStreamWriter& writer) const override
            {
                m_object.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition) const override
            {
                return m_object.bitSizeOf(bitPosition);
            }

        private:
            DummyParent& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    void initializeChildren()
    {
        m_dummyChild.initialize(static_cast<int32_t>(13), m_stringField);
        m_areChildrenInitialized = true;
    }

    bool operator==(const DummyParent& other) const
    {
        return m_dummyChild == other.m_dummyChild;
    }

    const string<>& getStringField() const
    {
        return m_stringField;
    }

    string<>& getStringField()
    {
        return m_stringField;
    }

    const DummyChild& getDummyChild() const
    {
        return m_dummyChild;
    }

    DummyChild& getDummyChild()
    {
        return m_dummyChild;
    }

    void setDummyChild(const DummyChild& dummyChild)
    {
        m_dummyChild = dummyChild;
    }

    void write(BitStreamWriter& out) const
    {
        out.writeString(m_stringField);
        m_dummyChild.write(out);
    }

    size_t bitSizeOf(size_t bitPosition = 0) const
    {
        return bitSizeOfString(m_stringField) + m_dummyChild.bitSizeOf(bitPosition);
    }

private:
    string<> readStringField(::zserio::BitStreamReader& in)
    {
        return in.readString();
    }

    DummyChild readDummyChild(BitStreamReader& in)
    {
        return DummyChild(in, static_cast<int32_t>(13), getStringField());
    }

    bool m_areChildrenInitialized;
    string<> m_stringField;
    DummyChild m_dummyChild;
};

DummyParent createInitializedDummyParent(const string<>& stringField, uint32_t dummyChildValue)
{
    DummyParent dummyParent(stringField, DummyChild{dummyChildValue});
    dummyParent.initializeChildren();
    return dummyParent;
}

} // namespace

template <>
struct EnumTraits<DummyEnum>
{
    static constexpr std::array<const char*, 3> names = {{"VALUE1", "VALUE2", "VALUE3"}};
};

constexpr std::array<const char*, 3> EnumTraits<DummyEnum>::names;

template <>
inline size_t enumToOrdinal<DummyEnum>(DummyEnum value)
{
    switch (value)
    {
    case DummyEnum::VALUE1:
        return 0;
    case DummyEnum::VALUE2:
        return 1;
    default: // DummyEnum::VALUE3:
        return 2;
    }
}

template <>
inline DummyEnum valueToEnum(typename std::underlying_type<DummyEnum>::type rawValue)
{
    return DummyEnum(rawValue);
}

template <>
inline DummyEnum read<DummyEnum>(BitStreamReader& in)
{
    return valueToEnum<DummyEnum>(
            static_cast<typename std::underlying_type<DummyEnum>::type>(in.readSignedBits(UINT8_C(8))));
}

template <>
inline void write<DummyEnum>(BitStreamWriter& out, DummyEnum value)
{
    out.writeSignedBits(enumToValue(value), INT8_C(8));
}

template <>
inline size_t bitSizeOf<DummyEnum>(DummyEnum)
{
    return INT8_C(8);
}

template <>
const ITypeInfo& enumTypeInfo<DummyEnum>()
{
    static const Span<StringView> underlyingTypeArguments;

    static const std::array<ItemInfo, 3> items = {
        ItemInfo{ makeStringView("VALUE1"), makeStringView("INT8_C(-1)") },
        ItemInfo{ makeStringView("VALUE2"), makeStringView("INT8_C(0)") },
        ItemInfo{ makeStringView("VALUE3"), makeStringView("INT8_C(1)") }
    };

    static const EnumTypeInfo<std::allocator<uint8_t>> typeInfo = {
        makeStringView("DummyEnum"),
        BuiltinTypeInfo<>::getInt8(), underlyingTypeArguments, items
    };

    return typeInfo;
}

template <>
IReflectablePtr enumReflectable(DummyEnum value, const std::allocator<uint8_t>& allocator)
{
    class Reflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        explicit Reflectable(DummyEnum value) :
                ReflectableBase<std::allocator<uint8_t>>(enumTypeInfo<DummyEnum>()),
                m_value(value)
        {}

        virtual int8_t getInt8() const override
        {
            return static_cast<typename std::underlying_type<DummyEnum>::type>(m_value);
        }

        virtual int64_t toInt() const override
        {
            return static_cast<typename std::underlying_type<DummyEnum>::type>(m_value);
        }

        virtual double toDouble() const override
        {
            return static_cast<double>(toInt());
        }

        virtual string<> toString(
                const std::allocator<uint8_t>& allocator = std::allocator<uint8_t>()) const override
        {
            return string<>(enumToString(m_value), allocator);
        }

        virtual void write(BitStreamWriter& writer) const override
        {
            zserio::write(writer, m_value);
        }

        virtual size_t bitSizeOf(size_t) const override
        {
            return zserio::bitSizeOf(m_value);
        }

    private:
        DummyEnum m_value;
    };

    return std::allocate_shared<Reflectable>(allocator, value);
}

class ReflectableTest : public ::testing::Test
{
protected:
    template <typename RAW_ARRAY, typename REFLECTABLE_PTR, typename ELEMENT_CHECKER>
    void checkArray(const RAW_ARRAY& rawArray, const REFLECTABLE_PTR& reflectable,
            const ELEMENT_CHECKER& elementChecker)
    {
        ASSERT_TRUE(reflectable->isArray());
        ASSERT_EQ(rawArray.size(), reflectable->size());
        for (size_t i = 0; i < rawArray.size(); ++i)
        {
            if (i % 2 == 0)
                elementChecker(rawArray[i], reflectable->at(i));
            else
                elementChecker(rawArray[i], (*reflectable)[i]);
        }

        ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException);

        ASSERT_THROW(reflectable->bitSizeOf(0), CppRuntimeException);

        BitBuffer bitBuffer(0);
        BitStreamWriter writer(bitBuffer);
        ASSERT_THROW(reflectable->write(writer), CppRuntimeException);

        checkNonCompound(reflectable);
    }

    template <typename T, typename REFLECTABLE_PTR, typename READ_FUNC>
    void checkWriteRead(T value, const REFLECTABLE_PTR& reflectable, const READ_FUNC& readFunc,
            size_t bitBufferSize)
    {
        BitBuffer bitBuffer(bitBufferSize);
        BitStreamWriter writer(bitBuffer);
        reflectable->write(writer);
        const size_t bitSizeOfValue = reflectable->bitSizeOf();
        ASSERT_EQ(bitSizeOfValue, writer.getBitPosition());

        BitStreamReader reader(bitBuffer);
        ASSERT_EQ(value, readFunc(reader));
        ASSERT_EQ(bitSizeOfValue, reader.getBitPosition());
    }

    void checkNonArray(const IReflectableConstPtr& reflectable)
    {
        ASSERT_FALSE(reflectable->isArray());
        ASSERT_THROW(reflectable->size(), CppRuntimeException);
        ASSERT_THROW(reflectable->at(0), CppRuntimeException);
        ASSERT_THROW((*reflectable)[0], CppRuntimeException);
    }

    void checkNonArray(const IReflectablePtr& reflectable)
    {
        checkNonArray(static_cast<IReflectableConstPtr>(reflectable));

        ASSERT_THROW(reflectable->at(0), CppRuntimeException);
        ASSERT_THROW((*reflectable)[0], CppRuntimeException);
        ASSERT_THROW(reflectable->resize(1), CppRuntimeException);
        ASSERT_THROW(reflectable->setAt(AnyHolder<>(), 0), CppRuntimeException);
    }

    template <typename REFLECTABLE_PTR>
    void checkNonCompoundConstMethods(const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_THROW(reflectable->getField("field"), CppRuntimeException);
        ASSERT_THROW(reflectable->getParameter("parameter"), CppRuntimeException);
        ASSERT_THROW(reflectable->callFunction("function"), CppRuntimeException);
        ASSERT_THROW(reflectable->getChoice(), CppRuntimeException);

        ASSERT_EQ(nullptr, reflectable->find("some.field"));
        ASSERT_EQ(nullptr, (*reflectable)["some.field"]);
    }

    void checkNonCompound(const IReflectablePtr& reflectable)
    {
        ASSERT_THROW(reflectable->initializeChildren(), CppRuntimeException);
        ASSERT_THROW(reflectable->initialize(vector<AnyHolder<>>()), CppRuntimeException);
        ASSERT_THROW(reflectable->setField("field", AnyHolder<>{}), CppRuntimeException);
        ASSERT_THROW(reflectable->createField("field"), CppRuntimeException);

        checkNonCompoundConstMethods(reflectable);
    }

    void checkNonCompound(const IReflectableConstPtr& reflectable)
    {
        checkNonCompoundConstMethods(reflectable);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER>
    void checkArithmeticCppTypeGetter(T value, const REFLECTABLE_PTR& reflectable,
            CppType cppType, const GETTER& getter, bool& match)
    {
        if (reflectable->getTypeInfo().getCppType() == cppType)
        {
            ASSERT_EQ(value, ((*reflectable).*getter)());
            match = true;
        }
        else
        {
            ASSERT_THROW(((*reflectable).*getter)(), CppRuntimeException);
        }
    }

    template <typename T, typename REFLECTABLE_PTR>
    void checkArithmeticCppTypeGetters(T value, const REFLECTABLE_PTR& reflectable)
    {
        const ITypeInfo& typeInfo = reflectable->getTypeInfo();
        bool match = false;
        if (TypeInfoUtil::isFloatingPoint(typeInfo.getCppType()))
        {
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::FLOAT, &IReflectable::getFloat, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::DOUBLE, &IReflectable::getDouble, match);
        }
        else if (TypeInfoUtil::isSigned(typeInfo.getCppType()))
        {
            ASSERT_EQ(static_cast<int64_t>(value), reflectable->toInt());

            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::INT8, &IReflectable::getInt8, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::INT16, &IReflectable::getInt16, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::INT32, &IReflectable::getInt32, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::INT64, &IReflectable::getInt64, match);
        }
        else
        {
            ASSERT_EQ(static_cast<uint64_t>(value), reflectable->toUInt());

            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::BOOL, &IReflectable::getBool, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::UINT8, &IReflectable::getUInt8, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::UINT16, &IReflectable::getUInt16, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::UINT32, &IReflectable::getUInt32, match);
            checkArithmeticCppTypeGetter(value, reflectable,
                    CppType::UINT64, &IReflectable::getUInt64, match);
        }

        ASSERT_TRUE(match);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkFloatingPoint(T value, const REFLECTABLE_PTR& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, ((*reflectable).*getter)());

        ASSERT_EQ(value, reflectable->toDouble());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException); // NOT IMPLEMENTED!

        ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        checkArithmeticCppTypeGetters(value, reflectable);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable, readFunc, bitSize);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkIntegral(T value, const REFLECTABLE_PTR& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize)
    {
        ASSERT_EQ(value, ((*reflectable).*getter)());

        ASSERT_EQ(value, reflectable->toDouble());
        ASSERT_EQ(zserio::toString(value), reflectable->toString());

        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        checkArithmeticCppTypeGetters(value, reflectable);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable, readFunc, bitSize);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkSignedIntegral(T value, const REFLECTABLE_PTR& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, reflectable->toInt());
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBool(), CppRuntimeException); // bool is unsigned integral type

        checkIntegral(value, reflectable, getter, readFunc, bitSize);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkUnsignedIntegral(T value, const REFLECTABLE_PTR& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, reflectable->toUInt());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);

        checkIntegral(value, reflectable, getter, readFunc, bitSize);
    }

    template <typename REFLECTABLE_PTR>
    void checkString(StringView value, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(value, reflectable->getStringView());

        ASSERT_EQ(toString(value), reflectable->toString());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);

        ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(toString(value), reflectable,
                std::bind(&BitStreamReader::readString<>, _1, std::allocator<uint8_t>()),
                bitSizeOfVarSize(convertSizeToUInt32(value.size())) + value.size() * 8);
    }

    template <typename REFLECTABLE_PTR>
    void checkBitmask(DummyBitmask bitmask, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(bitmask.getValue(), reflectable->getUInt8());

        ASSERT_EQ(bitmask.getValue(), reflectable->toUInt());
        ASSERT_EQ(bitmask.getValue(), reflectable->toDouble());
        ASSERT_EQ(bitmask.toString(), reflectable->toString());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(bitmask, reflectable,
                [](BitStreamReader& reader) {
                    return DummyBitmask(reader);
                }, 8
        );
    }

    template <typename REFLECTABLE_PTR>
    void checkEnum(DummyEnum enumeration, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(enumToValue(enumeration), reflectable->getInt8());

        ASSERT_EQ(enumToValue(enumeration), reflectable->toInt());
        ASSERT_EQ(enumToValue(enumeration), reflectable->toDouble());
        ASSERT_EQ(enumToString(enumeration), reflectable->toString());
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(enumeration, reflectable,
                [](BitStreamReader& reader) {
                    return zserio::read<DummyEnum>(reader);
                }, 8
        );
    }

    template <typename REFLECTABLE_PTR>
    void checkCompoundConstMethods(const DummyParent& dummyParent, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_TRUE(TypeInfoUtil::isCompound(reflectable->getTypeInfo().getSchemaType()));

        // field getter
        ASSERT_EQ(dummyParent.getDummyChild().getValue(),
                reflectable->getField("dummyChild")->getField("value")->getUInt32());
        ASSERT_THROW(reflectable->getField("nonexistent"), CppRuntimeException);
        ASSERT_THROW(reflectable->getField("dummyChild")->getField("nonexistent"), CppRuntimeException);

        // find field
        ASSERT_EQ(dummyParent.getDummyChild().getValue(), reflectable->find("dummyChild.value")->toUInt());
        ASSERT_EQ(dummyParent.getDummyChild().getValue(), (*reflectable)["dummyChild.value"]->toDouble());

        // find parameter
        ASSERT_NO_THROW(reflectable->getField("dummyChild")->getParameter("dummyParam"));
        ASSERT_EQ(13, (*reflectable)["dummyChild.dummyParam"]->getInt32());
        ASSERT_NO_THROW(reflectable->getField("dummyChild")->getParameter("stringParam"));
        ASSERT_EQ(dummyParent.getStringField(), (*reflectable)["dummyChild.stringParam"]->toString());
        ASSERT_THROW(reflectable->getField("dummyChild")->getParameter("nonexistent"), CppRuntimeException);

        // find function
        ASSERT_EQ(dummyParent.getDummyChild().getValue(), (*reflectable)["dummyChild.getValue"]->getUInt32());
        ASSERT_THROW(reflectable->getField("dummyChild")->callFunction("nonexistent"), CppRuntimeException);

        // find failed
        ASSERT_EQ(nullptr, reflectable->find("dummyChild.nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("dummyChild.value.nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("dummyChild.dummyParam.nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("dummyChild.getValue.nonexistent"));
        // find failed because the underlying code throws
        ASSERT_EQ(nullptr, reflectable->find("dummyChild.throwingFunction.nonexistent"));

        ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException);

        checkNonArray(reflectable);

        checkWriteRead(dummyParent, reflectable,
                [](BitStreamReader& reader) {
                    return DummyParent(reader);
                }, dummyParent.bitSizeOf()
        );

        string<> stringParam = "stringParam";
        checkWriteRead(dummyParent.getDummyChild(), reflectable->getField("dummyChild"),
                [&stringParam](BitStreamReader& reader) {
                    return DummyChild(reader, static_cast<int32_t>(13), stringParam);
                }, dummyParent.getDummyChild().bitSizeOf()
        );
    }

    void checkCompound(const DummyParent& dummyParent, const IReflectableConstPtr& reflectable)
    {
        checkCompoundConstMethods(dummyParent, reflectable);
    }

    void checkCompound(const DummyParent& dummyParent, const IReflectablePtr& reflectable)
    {
        checkCompoundConstMethods(dummyParent, reflectable);
        checkCompoundConstMethods(dummyParent, static_cast<IReflectableConstPtr>(reflectable));

        // setter
        reflectable->getField("dummyChild")->setField("value", AnyHolder<>(static_cast<uint32_t>(11)));
        ASSERT_EQ(11, dummyParent.getDummyChild().getValue());
        ASSERT_THROW(reflectable->setField("nonexistent", AnyHolder<>()), CppRuntimeException);
        ASSERT_THROW(reflectable->find("dummyChild")->setField("nonexistent", AnyHolder<>()),
                CppRuntimeException);

        reflectable->createField("dummyChild");
        ASSERT_EQ(uint32_t(), dummyParent.getDummyChild().getValue());

        reflectable->setField("dummyChild", AnyHolder<>(DummyChild{42}));
        ASSERT_EQ(42, dummyParent.getDummyChild().getValue());
        reflectable->initializeChildren(); // keep the reflectable initialized for following tests
    }
};

TEST_F(ReflectableTest, boolReflectable)
{
    const bool value = true;
    auto reflectable = ReflectableFactory::getBool(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getBool,
            std::bind(&BitStreamReader::readBool, _1));
}

TEST_F(ReflectableTest, int8Reflectable)
{
    const int8_t value = -12;
    auto reflectable = ReflectableFactory::getInt8(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, 8));
}

TEST_F(ReflectableTest, int16Reflectable)
{
    const int16_t value = -1234;
    auto reflectable = ReflectableFactory::getInt16(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, 16));
}

TEST_F(ReflectableTest, int32Reflectable)
{
    const int32_t value = -123456;
    auto reflectable = ReflectableFactory::getInt32(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, 32));
}

TEST_F(ReflectableTest, int64Reflectable)
{
    const int64_t value = -1234567890;
    auto reflectable = ReflectableFactory::getInt64(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, 64));
}

TEST_F(ReflectableTest, uint8Reflectable)
{
    const uint8_t value = 0xFF;
    auto reflectable = ReflectableFactory::getUInt8(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, 8));
}

TEST_F(ReflectableTest, uint16Reflectable)
{
    const uint16_t value = 0xFFFF;
    auto reflectable = ReflectableFactory::getUInt16(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, 16));
}

TEST_F(ReflectableTest, uint32Reflectable)
{
    const uint32_t value = 0xFFFFFFFF;
    auto reflectable = ReflectableFactory::getUInt32(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, 32));
}

TEST_F(ReflectableTest, uint64Reflectable)
{
    const uint64_t value = 0xFFFFFFFFFFFF;
    auto reflectable = ReflectableFactory::getUInt64(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, 64));
}

TEST_F(ReflectableTest, fixedSignedBitField5) // mapped to int8_t
{
    const uint8_t numBits = 5;
    const int8_t value = 15;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(10, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedSignedBitField15) // mapped to int16_t
{
    const uint8_t numBits = 15;
    const int16_t value = -15;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(5, value), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(17, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedSignedBitField31) // mapped to int32_t
{
    const uint8_t numBits = 31;
    const int32_t value = -12345678;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(16, value), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(33, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedSignedBitField60) // mapped to int64_t
{
    const uint8_t numBits = 60;
    const int64_t value = 1234567890;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(31, value), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(65, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField7) // mapped to uint8_t
{
    const uint8_t numBits = 7;
    const uint8_t value = 0x2F;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(9, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField9) // mapped to uint16_t
{
    const uint8_t numBits = 9;
    const uint16_t value = 0x1FF;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(8, value), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(17, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField31) // mapped to uint32_t
{
    const uint8_t numBits = 31;
    const uint32_t value = UINT32_MAX >> 1;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(16, value), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(33, value), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField33) // mapped to uint64_t
{
    const uint8_t numBits = 33;
    const uint64_t value = static_cast<uint64_t>(UINT32_MAX) << 1;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(32, value), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(65, value), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField5) // mapped to int8_t
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    const int8_t value = 15;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(9, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField15) // mapped to int16_t
{
    const uint8_t maxBitSize = 16;
    const uint8_t numBits = 15;
    const int16_t value = -15;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(8, value, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(17, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField31) // mapped to int32_t
{
    const uint8_t maxBitSize = 32;
    const uint8_t numBits = 31;
    const int32_t value = -12345678;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(16, value, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(33, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField60) // mapped to int64_t
{
    const uint8_t maxBitSize = 64;
    const uint8_t numBits = 60;
    const int64_t value = 1234567890;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(32, value, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(65, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField7) // mapped to uint8_t
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 7;
    const uint8_t value = 0x2F;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(9, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField9) // mapped to uint16_t
{
    const uint8_t maxBitSize = 16;
    const uint8_t numBits = 9;
    const uint16_t value = 0x1FF;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(8, value, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(17, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField31) // mapped to uint32_t
{
    const uint8_t maxBitSize = 32;
    const uint8_t numBits = 31;
    const uint32_t value = UINT32_MAX >> 1;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(16, value, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(33, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField33) // mapped to uint64_t
{
    const uint8_t maxBitSize = 64;
    const uint8_t numBits = 33;
    const uint64_t value = static_cast<uint64_t>(UINT32_MAX) << 1;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(32, value, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(65, value, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, varint16Reflectable)
{
    const int16_t value = -1234;
    auto reflectable = ReflectableFactory::getVarInt16(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readVarInt16, _1), bitSizeOfVarInt16(value));
}

TEST_F(ReflectableTest, varint32Reflectable)
{
    const int32_t value = 54321;
    auto reflectable = ReflectableFactory::getVarInt32(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readVarInt32, _1), bitSizeOfVarInt32(value));
}

TEST_F(ReflectableTest, varint64Reflectable)
{
    const int64_t value = -87654321;
    auto reflectable = ReflectableFactory::getVarInt64(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readVarInt64, _1), bitSizeOfVarInt64(value));
}

TEST_F(ReflectableTest, varintReflectable)
{
    const int64_t value = INT64_MAX;
    auto reflectable = ReflectableFactory::getVarInt(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readVarInt, _1), bitSizeOfVarInt(value));
}

TEST_F(ReflectableTest, varuint16Reflectable)
{
    const uint16_t value = 1234;
    auto reflectable = ReflectableFactory::getVarUInt16(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readVarUInt16, _1), bitSizeOfVarUInt16(value));
}

TEST_F(ReflectableTest, varuint32Reflectable)
{
    const uint32_t value = 0x1FFFFFFF;
    auto reflectable = ReflectableFactory::getVarUInt32(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readVarUInt32, _1), bitSizeOfVarUInt32(value));
}

TEST_F(ReflectableTest, varuint64Reflectable)
{
    const uint64_t value = 4242424242;
    auto reflectable = ReflectableFactory::getVarUInt64(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readVarUInt64, _1), bitSizeOfVarUInt64(value));
}

TEST_F(ReflectableTest, varuintReflectable)
{
    const uint64_t value = UINT64_MAX;
    auto reflectable = ReflectableFactory::getVarUInt(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readVarUInt, _1), bitSizeOfVarUInt(value));
}

TEST_F(ReflectableTest, varsizeReflectable)
{
    const uint32_t value = (UINT32_C(1) << (7+7+7+7+3)) - 1;
    auto reflectable = ReflectableFactory::getVarSize(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readVarSize, _1), bitSizeOfVarSize(value));
}

TEST_F(ReflectableTest, float16Reflectable)
{
    const float value = 2.0f;
    auto reflectable = ReflectableFactory::getFloat16(value);
    checkFloatingPoint(value, reflectable, &IReflectable::getFloat,
            std::bind(&BitStreamReader::readFloat16, _1));
}

TEST_F(ReflectableTest, float32Reflectable)
{
    const float value = 1.2f;
    auto reflectable = ReflectableFactory::getFloat32(value);
    checkFloatingPoint(value, reflectable, &IReflectable::getFloat,
            std::bind(&BitStreamReader::readFloat32, _1));
}

TEST_F(ReflectableTest, float64Reflectable)
{
    const double value = 1.2;
    auto reflectable = ReflectableFactory::getFloat64(value);
    checkFloatingPoint(value, reflectable, &IReflectable::getDouble,
            std::bind(&BitStreamReader::readFloat64, _1));
}

TEST_F(ReflectableTest, stringReflectable)
{
    const std::string value = "some longer string value to have a chance that some allocation hopefully occurs";
    auto reflectable = ReflectableFactory::getString(value);
    checkString(value, reflectable);
}

TEST_F(ReflectableTest, stringViewReflectable)
{
    auto view = makeStringView("some text as a string view");
    auto reflectable = ReflectableFactory::getString(view);
    checkString(view, reflectable);
}

TEST_F(ReflectableTest, bitBufferReflectable)
{
    const BitBuffer value = BitBuffer{std::vector<uint8_t>{{0xAB, 0xF0}}, 12};
    auto reflectable = ReflectableFactory::getBitBuffer(value);

    ASSERT_EQ(value, reflectable->getBitBuffer());

    ASSERT_THROW(reflectable->toString(), CppRuntimeException);
    ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
    ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
    ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);

    ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
    ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
    ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
    ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);

    checkNonCompound(reflectable);
    checkNonArray(reflectable);

    checkWriteRead(value, reflectable,
            std::bind(&BitStreamReader::readBitBuffer<>, _1, std::allocator<uint8_t>()),
            bitSizeOfVarSize(convertSizeToUInt32(value.getBitSize())) + value.getBitSize());
}

TEST_F(ReflectableTest, uint8ConstArray)
{
    const auto rawArray = std::vector<uint8_t>{{10, 20, 30, 40}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo<>::getUInt8();
    auto reflectable = ReflectableFactory::getBuiltinArray(typeInfo, rawArray);
    checkArray(rawArray, reflectable,
            [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
                checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                        std::bind(&BitStreamReader::readBits, _1, 8));
            }
    );

    // call version with dynamic bit size
    ASSERT_THROW(ReflectableFactory::getBuiltinArray(typeInfo, rawArray, 8), CppRuntimeException);
}

TEST_F(ReflectableTest, uint8Array)
{
    auto rawArray = std::vector<uint8_t>{{10, 20, 30, 40}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo<>::getUInt8();
    auto reflectable = ReflectableFactory::getBuiltinArray(typeInfo, rawArray);
    checkArray(rawArray, reflectable,
            [&](uint8_t value, const IReflectablePtr& elementReflectable) {
                checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                        std::bind(&BitStreamReader::readBits, _1, 8));
            }
    );
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
                checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                        std::bind(&BitStreamReader::readBits, _1, 8));
            }
    );

    // call version with dynamic bit size
    ASSERT_THROW(ReflectableFactory::getBuiltinArray(typeInfo, rawArray, 8), CppRuntimeException);

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(static_cast<uint8_t>(13)));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(13, reflectable->at(0)->getUInt8());
    reflectable->setAt(AnyHolder<>(static_cast<uint8_t>(42)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(42, reflectable->at(0)->getUInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());
}

TEST_F(ReflectableTest, dynamicSignedBitField5ConstArray)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    const auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo<>::getDynamicSignedBitField(maxBitSize);
    auto reflectable = ReflectableFactory::getBuiltinArray(typeInfo, rawArray, numBits);
    checkArray(rawArray, reflectable,
            [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            }
    );

    // call version without dynamic bit size
    ASSERT_THROW(ReflectableFactory::getBuiltinArray(typeInfo, rawArray), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField5Array)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo<>::getDynamicSignedBitField(maxBitSize);
    auto reflectable = ReflectableFactory::getBuiltinArray(typeInfo, rawArray, numBits);
    checkArray(rawArray, reflectable,
            [&](int8_t value, const IReflectablePtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            }
    );
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            }
    );

    // call version without dynamic bit size
    ASSERT_THROW(ReflectableFactory::getBuiltinArray(typeInfo, rawArray), CppRuntimeException);

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(static_cast<int8_t>(13)));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(13, reflectable->at(0)->getInt8());
    reflectable->setAt(AnyHolder<>(static_cast<int8_t>(42)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(42, reflectable->at(0)->getInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());
}

TEST_F(ReflectableTest, stringConstArray)
{
    const auto rawArray = std::vector<std::string>{{"one", "two", "three"}};
    auto reflectable = ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getString(), rawArray);
    checkArray(rawArray, reflectable,
            [&](StringView value, const IReflectableConstPtr& elementReflectable) {
                checkString(value, elementReflectable);
            }
    );
}

TEST_F(ReflectableTest, stringArray)
{
    auto rawArray = std::vector<std::string>{{"one", "two", "three"}};
    auto reflectable = ReflectableFactory::getBuiltinArray(BuiltinTypeInfo<>::getString(), rawArray);
    checkArray(rawArray, reflectable,
            [&](StringView value, const IReflectablePtr& elementReflectable) {
                checkString(value, elementReflectable);
            }
    );
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](StringView value, const IReflectableConstPtr& elementReflectable) {
                checkString(value, elementReflectable);
            }
    );

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(std::string("appended")));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ("appended"_sv, reflectable->at(0)->getStringView());
    reflectable->setAt(AnyHolder<>(std::string("set")), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ("set"_sv, reflectable->at(0)->getStringView());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());
}

TEST_F(ReflectableTest, bitmaskConst)
{
    const DummyBitmask bitmask = DummyBitmask::Values::WRITE;
    auto reflectable = bitmask.reflectable();
    checkBitmask(bitmask, reflectable);
}

TEST_F(ReflectableTest, bitmask)
{
    DummyBitmask bitmask = DummyBitmask::Values::WRITE;
    auto reflectable = bitmask.reflectable();
    checkBitmask(bitmask, reflectable);
}

TEST_F(ReflectableTest, bitmaskConstArray)
{
    const auto rawArray = std::vector<DummyBitmask>{{
            DummyBitmask::Values::WRITE, DummyBitmask::Values::CREATE, DummyBitmask::Values::READ
    }};
    auto reflectable = ReflectableFactory::getBitmaskArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](DummyBitmask value, const IReflectableConstPtr& elementReflectable) {
                checkBitmask(value, elementReflectable);
            }
    );
}

TEST_F(ReflectableTest, bitmaskArray)
{
    auto rawArray = std::vector<DummyBitmask>{{
            DummyBitmask::Values::WRITE, DummyBitmask::Values::CREATE, DummyBitmask::Values::READ
    }};
    auto reflectable = ReflectableFactory::getBitmaskArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](DummyBitmask value, const IReflectablePtr& elementReflectable) {
                checkBitmask(value, elementReflectable);
            }
    );
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](DummyBitmask value, const IReflectableConstPtr& elementReflectable) {
                checkBitmask(value, elementReflectable);
            }
    );

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(DummyBitmask::Values::READ));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(DummyBitmask::Values::READ, DummyBitmask(reflectable->at(0)->getUInt8()));
    reflectable->setAt(AnyHolder<>(DummyBitmask::Values::CREATE), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(DummyBitmask::Values::CREATE, DummyBitmask(reflectable->at(0)->getUInt8()));
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());
}

TEST_F(ReflectableTest, enumeration)
{
    const DummyEnum enumeration = DummyEnum::VALUE1;
    auto reflectable = enumReflectable(enumeration);
    checkEnum(enumeration, reflectable);
}

TEST_F(ReflectableTest, enumConstArray)
{
    const auto rawArray = std::vector<DummyEnum>{{DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3}};
    auto reflectable = ReflectableFactory::getEnumArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](DummyEnum value, const IReflectableConstPtr& elementReflectable) {
                checkEnum(value, elementReflectable);
            }
    );
}

TEST_F(ReflectableTest, enumArray)
{
    auto rawArray = std::vector<DummyEnum>{{DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3}};
    auto reflectable = ReflectableFactory::getEnumArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](DummyEnum value, const IReflectablePtr& elementReflectable) {
                checkEnum(value, elementReflectable);
            }
    );
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](DummyEnum value, const IReflectableConstPtr& elementReflectable) {
                checkEnum(value, elementReflectable);
            }
    );

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(DummyEnum::VALUE3));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(enumToValue(DummyEnum::VALUE3), reflectable->at(0)->getInt8());
    reflectable->setAt(AnyHolder<>(DummyEnum::VALUE2), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(enumToValue(DummyEnum::VALUE2), reflectable->at(0)->getInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());
}

TEST_F(ReflectableTest, compoundConst)
{
    {
        const DummyParent dummyParentUninitialized = DummyParent{"test", DummyChild{13}};
        auto reflectable = dummyParentUninitialized.reflectable();
        ASSERT_FALSE(reflectable->find("dummyChild.stringParam"));
    }

    const DummyParent dummyParent = createInitializedDummyParent("test", 13);
    auto reflectable = dummyParent.reflectable();
    checkCompound(dummyParent, reflectable);

    IReflectablePtr nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->initializeChildren(), CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->initialize(vector<AnyHolder<>>()), CppRuntimeException);
    ASSERT_NO_THROW(reflectable->getField("dummyChild"));
    ASSERT_THROW(nonConstReflectable->getField("dummyChild"), CppRuntimeException);
    IReflectableConstPtr childReflectable = reflectable->getField("dummyChild");
    IReflectablePtr nonConstChildReflectable =std::const_pointer_cast<IReflectable>(childReflectable);
    ASSERT_THROW(nonConstChildReflectable->setField("value", AnyHolder<>(static_cast<uint32_t>(11))),
            CppRuntimeException);
    ASSERT_NO_THROW(childReflectable->getParameter("dummyParam"));
    ASSERT_THROW(nonConstChildReflectable->getParameter("dummyParam"), CppRuntimeException);
    ASSERT_NO_THROW(childReflectable->callFunction("getValue"));
    ASSERT_THROW(nonConstChildReflectable->callFunction("getValue"), CppRuntimeException);
}

TEST_F(ReflectableTest, compound)
{
    DummyParent dummyParent = DummyParent{"test", DummyChild{13}};
    auto reflectable = dummyParent.reflectable();

    // not initialized
    ASSERT_THROW(reflectable->getField("dummyChild")->getParameter("dummyParam"), CppRuntimeException);
    ASSERT_THROW(reflectable->getField("dummyChild")->getParameter("stringParam"), CppRuntimeException);
    ASSERT_FALSE(static_cast<IReflectableConstPtr>(reflectable)->find("dummyChild.stringParam"));

    reflectable->initializeChildren();
    checkCompound(dummyParent, reflectable);
}

TEST_F(ReflectableTest, compoundConstArray)
{
    DummyParent dummyParent1;
    dummyParent1 = createInitializedDummyParent("1", 13); // to cover assignment operator
    const auto rawArray = std::vector<DummyParent>{{
        dummyParent1,
        createInitializedDummyParent("2", 42)
    }};
    auto reflectable = ReflectableFactory::getCompoundArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const DummyParent& value, const IReflectableConstPtr& elementReflectable) {
                checkCompound(value, elementReflectable);
            }
    );

    IReflectablePtr nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->at(0), CppRuntimeException);
    ASSERT_THROW((*nonConstReflectable)[0], CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->resize(nonConstReflectable->size() + 1), CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->setAt(AnyHolder<>(DummyParent{"test", DummyChild{0}}), 0),
            CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->append(AnyHolder<>(DummyParent{"test", DummyChild{0}})),
            CppRuntimeException);
}

TEST_F(ReflectableTest, compoundArray)
{
    auto rawArray = std::vector<DummyParent>{{
        createInitializedDummyParent("1", 13),
        createInitializedDummyParent("2", 42)
    }};
    auto reflectable = ReflectableFactory::getCompoundArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const DummyParent& value, const IReflectablePtr& elementReflectable) {
                checkCompound(value, elementReflectable);
            }
    );
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](const DummyParent& value, const IReflectableConstPtr& elementReflectable) {
                checkCompound(value, elementReflectable);
            }
    );

    reflectable->resize(reflectable->size() + 1);
    IReflectablePtr newCompound = reflectable->at(reflectable->size() - 1);
    ASSERT_TRUE(newCompound);

    reflectable->setAt(AnyHolder<>(DummyParent{"test", DummyChild{0}}), 0);
    ASSERT_EQ(0, reflectable->at(0)->find("dummyChild.value")->getUInt32());
    reflectable->append(AnyHolder<>(DummyParent{"test|", DummyChild{1}}));
    ASSERT_EQ(1, reflectable->at(reflectable->size() - 1)->find("dummyChild.value")->getUInt32());
}

TEST_F(ReflectableTest, reflectableOwner)
{
    auto reflectable = DummyParent::typeInfo().createInstance();
    IReflectableConstPtr constReflectable = reflectable;

    ASSERT_FALSE(reflectable->isArray());
    reflectable->setField("dummyChild", AnyHolder<>(DummyChild{42}));
    ASSERT_EQ(42, reflectable->getField("dummyChild")->getField("value")->getUInt32());
    ASSERT_EQ(42, constReflectable->getField("dummyChild")->getField("value")->getUInt32());
    ASSERT_THROW(reflectable->createField("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->getParameter("nonexistent"), CppRuntimeException);
    ASSERT_THROW(constReflectable->getParameter("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->callFunction("nonexistent"), CppRuntimeException);
    ASSERT_THROW(constReflectable->callFunction("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->getChoice(), CppRuntimeException);
    ASSERT_THROW(constReflectable->getChoice(), CppRuntimeException);
    ASSERT_FALSE(reflectable->find("nonexistent"));
    ASSERT_FALSE(constReflectable->find("nonexistent"));
    ASSERT_FALSE((*reflectable)["nonexistent"]);
    ASSERT_FALSE((*constReflectable)["nonexistent"]);

    ASSERT_THROW(reflectable->size(), CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->resize(0), CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->at(0), CppRuntimeException); // not an array
    ASSERT_THROW(constReflectable->at(0), CppRuntimeException); // not an array
    ASSERT_THROW((*reflectable)[0], CppRuntimeException); // not an array
    ASSERT_THROW((*constReflectable)[0], CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(), 0), CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->append(AnyHolder<>()), CppRuntimeException); // not an array

    ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
    ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
    ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
    ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
    ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

    ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
    ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
    ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
    ASSERT_THROW(reflectable->toString(), CppRuntimeException);

    ASSERT_NO_THROW(reflectable->initializeChildren());

    const size_t bitSizeOfValue = reflectable->bitSizeOf();
    BitBuffer bitBuffer(bitSizeOfValue);
    BitStreamWriter writer(bitBuffer);
    reflectable->write(writer);
    ASSERT_EQ(bitSizeOfValue, writer.getBitPosition());

    // for better coverage
    auto dummyChildReflectable = DummyChild::typeInfo().createInstance();
    ASSERT_NO_THROW(dummyChildReflectable->initializeChildren());

    ASSERT_THROW(dummyChildReflectable->initialize( // wrong number of arguments
            vector<AnyHolder<>>{AnyHolder<>{static_cast<int32_t>(13)}}), CppRuntimeException);
    string<> stringParam = "stringParam";
    ASSERT_THROW(dummyChildReflectable->initialize( // wrong type of first argument
            {{ AnyHolder<>{static_cast<uint32_t>(13)}, AnyHolder<>{std::ref(stringParam)} }}),
            CppRuntimeException);
    ASSERT_NO_THROW(dummyChildReflectable->initialize(
            {{ AnyHolder<>{static_cast<int32_t>(13)}, AnyHolder<>{std::ref(stringParam)} }}));
}

} // namespace zserio
