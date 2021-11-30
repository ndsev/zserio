#include <string>
#include <functional>

#include "gtest/gtest.h"

#include "zserio/Introspectable.h"
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
        static const std::array<StringView, 0> underlyingTypeArguments;

        static const std::array<ItemInfo, 3> values = {
            ItemInfo{ makeStringView("CREATE"), makeStringView("UINT8_C(1)") },
            ItemInfo{ makeStringView("READ"), makeStringView("UINT8_C(2)") },
            ItemInfo{ makeStringView("WRITE"), makeStringView("UINT8_C(4)") }
        };

        static const BitmaskTypeInfo typeInfo = {
            makeStringView("DummyBitmask"),
            BuiltinTypeInfo::getUInt8(), underlyingTypeArguments, values
        };

        return typeInfo;
    }

    IIntrospectablePtr introspectable(const std::allocator<uint8_t>& allocator = std::allocator<uint8_t>())
    {
        class Introspectable : public IntrospectableBase<std::allocator<uint8_t>>
        {
        public:
            Introspectable(DummyBitmask bitmask) :
                    IntrospectableBase<std::allocator<uint8_t>>(DummyBitmask::typeInfo()),
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

            virtual void write(BitStreamWriter& writer) override
            {
                m_bitmask.write(writer);
            }

        private:
            DummyBitmask m_bitmask;
        };

        return std::allocate_shared<Introspectable>(allocator, *this);
    }

    bool operator==(const DummyBitmask& other) const
    {
        return m_value == other.m_value;
    }

    void write(BitStreamWriter& out,
            PreWriteAction = ALL_PRE_WRITE_ACTIONS) const
    {
        out.writeBits(m_value, UINT8_C(8));
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

    explicit DummyChild(uint32_t value) : m_value(value) {}
    explicit DummyChild(BitStreamReader& in) : m_value(in.readBits(31)) {}

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const std::array<TemplateArgumentInfo, 0> templateArguments;

        static const std::array<FieldInfo, 1> fields = {
            FieldInfo{
                makeStringView("value"), // schemaName
                BuiltinTypeInfo::getFixedUnsignedBitField(31), // typeInfo
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

        static const std::array<ParameterInfo, 1> parameters = {
            ParameterInfo{
                makeStringView("dummyParam"),
                BuiltinTypeInfo::getFixedSignedBitField(31)
            }
        };

        static const std::array<FunctionInfo, 2> functions = {
            FunctionInfo{
                makeStringView("getValue"),
                BuiltinTypeInfo::getFixedSignedBitField(31),
                makeStringView("getValue()")
            },
            FunctionInfo{
                makeStringView("throwingFunction"),
                BuiltinTypeInfo::getFixedSignedBitField(31),
                makeStringView("getValue()")
            }
        };

        static const StructTypeInfo typeInfo = {
            makeStringView("DummyChild"), templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IIntrospectablePtr introspectable(const allocator_type& allocator = allocator_type())
    {
        class Introspectable : public IntrospectableAllocatorHolderBase<allocator_type>
        {
        public:
            Introspectable(DummyChild& object, const allocator_type& allocator) :
                    IntrospectableAllocatorHolderBase<allocator_type>(DummyChild::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IIntrospectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("value"))
                {
                    return IntrospectableFactory::getFixedUnsignedBitField(
                            31, m_object.getValue(), get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IIntrospectablePtr getParameter(StringView name) const override
            {
                if (name == makeStringView("dummyParam"))
                {
                    // dummyParam is just a hack to test more code in runtime
                    return IntrospectableFactory::getFixedUnsignedBitField(
                            31, m_object.getValue(), get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IIntrospectablePtr callFunction(StringView name) const override
            {
                if (name == makeStringView("getValue"))
                {
                    return IntrospectableFactory::getFixedUnsignedBitField(
                            31, m_object.funcGetValue(), get_allocator());
                }
                else if (name == makeStringView("throwingFunction"))
                {
                    // TODO[Mi-L@]: Should we distinguish between this exception and the following one?
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

            virtual void write(BitStreamWriter& writer) override
            {
                m_object.write(writer);
            }

        private:
            DummyChild& m_object;
        };

        return std::allocate_shared<Introspectable>(allocator, *this, allocator);
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

    void write(BitStreamWriter& out, PreWriteAction = ALL_PRE_WRITE_ACTIONS)
    {
        out.writeBits(m_value, 31);
    }

private:
    uint32_t m_value;
};

class DummyParent
{
public:
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyParent(DummyChild dummyChild) : m_dummyChild(dummyChild) {}
    explicit DummyParent(BitStreamReader& in) : m_dummyChild(in) {}

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const std::array<TemplateArgumentInfo, 0> templateArguments;

        static const std::array<FieldInfo, 1> fields = {
            FieldInfo{
                makeStringView("dummyChild"), // schemaName
                DummyChild::typeInfo(), // typeInfo
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

        static const std::array<ParameterInfo, 0> parameters;

        static const std::array<FunctionInfo, 0> functions;

        static const StructTypeInfo typeInfo = {
            makeStringView("DummyParent"), templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IIntrospectablePtr introspectable(const allocator_type& allocator = allocator_type())
    {
        class Introspectable : public IntrospectableAllocatorHolderBase<allocator_type>
        {
        public:
            Introspectable(DummyParent& object, const allocator_type& allocator) :
                    IntrospectableAllocatorHolderBase<allocator_type>(DummyParent::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IIntrospectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("dummyChild"))
                {
                    return m_object.getDummyChild().introspectable(get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
                }
            }

            virtual void setField(StringView name,
                    const AnyHolder<allocator_type>& value) override
            {
                if (name == makeStringView("dummyChild"))
                {
                    m_object.setDummyChild(value.get<DummyChild>());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
                }
            }

            virtual void write(BitStreamWriter& writer) override
            {
                m_object.write(writer);
            }

        private:
            DummyParent& m_object;
        };

        return std::allocate_shared<Introspectable>(allocator, *this, allocator);
    }

    bool operator==(const DummyParent& other) const
    {
        return m_dummyChild == other.m_dummyChild;
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

    void write(BitStreamWriter& out, PreWriteAction = ALL_PRE_WRITE_ACTIONS)
    {
        m_dummyChild.write(out);
    }

private:
    DummyChild m_dummyChild;
};

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
    case DummyEnum::VALUE3:
        return 2;
    default:
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: ") +
                static_cast<uint8_t>(value) + "!";
    }
}

template <>
inline DummyEnum valueToEnum(typename std::underlying_type<DummyEnum>::type rawValue)
{
    switch (rawValue)
    {
    case INT8_C(-1):
    case INT8_C(0):
    case INT8_C(1):
        return DummyEnum(rawValue);
    default:
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: ") + rawValue + "!";
    }
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
const ITypeInfo& enumTypeInfo<DummyEnum>()
{
    static const std::array<StringView, 0> underlyingTypeArguments;

    static const std::array<ItemInfo, 3> items = {
        ItemInfo{ makeStringView("VALUE1"), makeStringView("INT8_C(-1)") },
        ItemInfo{ makeStringView("VALUE2"), makeStringView("INT8_C(0)") },
        ItemInfo{ makeStringView("VALUE3"), makeStringView("INT8_C(1)") }
    };

    static const EnumTypeInfo typeInfo = {
        makeStringView("DummyEnum"),
        BuiltinTypeInfo::getInt8(), underlyingTypeArguments, items
    };

    return typeInfo;
}

template <>
IIntrospectablePtr enumIntrospectable(DummyEnum value, const std::allocator<uint8_t>& allocator)
{
    class Introspectable : public IntrospectableBase<std::allocator<uint8_t>>
    {
    public:
        Introspectable(DummyEnum value) :
                IntrospectableBase<std::allocator<uint8_t>>(enumTypeInfo<DummyEnum>()),
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

        virtual void write(BitStreamWriter& writer) override
        {
            zserio::write(writer, m_value);
        }

    private:
        DummyEnum m_value;
    };

    return std::allocate_shared<Introspectable>(allocator, value);
}

class IntrospectableTest : public ::testing::Test
{
protected:
    template <typename RAW_ARRAY, typename ELEMENT_CHECKER>
    void checkArray(const RAW_ARRAY& rawArray, const IIntrospectablePtr& introspectable,
            const ELEMENT_CHECKER& elementChecker)
    {
        ASSERT_TRUE(introspectable->isArray());
        ASSERT_EQ(rawArray.size(), introspectable->size());
        for (size_t i = 0; i < rawArray.size(); ++i)
        {
            if (i % 2 == 0)
                elementChecker(rawArray[i], introspectable->at(i));
            else
                elementChecker(rawArray[i], (*introspectable)[i]);
        }

        ASSERT_THROW(introspectable->getBool(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(introspectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(introspectable->getString(), CppRuntimeException);
        ASSERT_THROW(introspectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(introspectable->toInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(introspectable->toString(), CppRuntimeException);

        BitBuffer bitBuffer(0);
        BitStreamWriter writer(bitBuffer);
        ASSERT_THROW(introspectable->write(writer), CppRuntimeException);

        checkNonCompound(introspectable);
    }

    template <typename T, typename READ_FUNC>
    void checkWriteRead(T value, const IIntrospectablePtr& introspectable, const READ_FUNC& readFunc,
            size_t bitBufferSize)
    {
        BitBuffer bitBuffer(bitBufferSize);
        BitStreamWriter writer(bitBuffer);
        introspectable->write(writer);

        BitStreamReader reader(bitBuffer);
        ASSERT_EQ(value, readFunc(reader));
    }

    void checkNonArray(const IIntrospectablePtr& introspectable)
    {
        ASSERT_FALSE(introspectable->isArray());
        ASSERT_THROW(introspectable->size(), CppRuntimeException);
        ASSERT_THROW(introspectable->at(0), CppRuntimeException);
        ASSERT_THROW((*introspectable)[0], CppRuntimeException);
    }

    void checkNonCompound(const IIntrospectablePtr& introspectable)
    {
        ASSERT_THROW(introspectable->getField("field"), CppRuntimeException);
        ASSERT_THROW(introspectable->setField("field", AnyHolder<>{}), CppRuntimeException);
        ASSERT_THROW(introspectable->getParameter("parameter"), CppRuntimeException);
        ASSERT_THROW(introspectable->callFunction("function"), CppRuntimeException);
        ASSERT_THROW(introspectable->getChoice(), CppRuntimeException);

        ASSERT_EQ(nullptr, introspectable->find("some.field"));
        ASSERT_EQ(nullptr, (*introspectable)["some.field"]);
    }

    template <typename T, typename GETTER>
    void checkArithmeticCppTypeGetter(T value, const IIntrospectablePtr& introspectable,
            CppType cppType, const GETTER& getter, bool& match)
    {
        if (introspectable->getTypeInfo().getCppType() == cppType)
        {
            ASSERT_EQ(value, ((*introspectable).*getter)());
            match = true;
        }
        else
        {
            ASSERT_THROW(((*introspectable).*getter)(), CppRuntimeException);
        }
    }

    template <typename T>
    void checkArithmeticCppTypeGetters(T value, const IIntrospectablePtr& introspectable)
    {
        const ITypeInfo& typeInfo = introspectable->getTypeInfo();
        bool match = false;
        if (TypeInfoUtil::isFloatingPoint(typeInfo.getCppType()))
        {
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::FLOAT, &IIntrospectable::getFloat, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::DOUBLE, &IIntrospectable::getDouble, match);
        }
        else if (TypeInfoUtil::isSigned(typeInfo.getCppType()))
        {
            ASSERT_EQ(static_cast<int64_t>(value), introspectable->toInt());

            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::INT8, &IIntrospectable::getInt8, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::INT16, &IIntrospectable::getInt16, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::INT32, &IIntrospectable::getInt32, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::INT64, &IIntrospectable::getInt64, match);
        }
        else
        {
            ASSERT_EQ(static_cast<uint64_t>(value), introspectable->toUInt());

            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::BOOL, &IIntrospectable::getBool, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::UINT8, &IIntrospectable::getUInt8, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::UINT16, &IIntrospectable::getUInt16, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::UINT32, &IIntrospectable::getUInt32, match);
            checkArithmeticCppTypeGetter(value, introspectable,
                    CppType::UINT64, &IIntrospectable::getUInt64, match);
        }

        ASSERT_TRUE(match);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkFloatingPoint(T value, const IIntrospectablePtr& introspectable,
            const GETTER& getter, const READ_FUNC& readFunc,
            size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, ((*introspectable).*getter)());

        ASSERT_EQ(value, introspectable->toDouble());
        ASSERT_THROW(introspectable->toInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toString(), CppRuntimeException); // NOT IMPLEMENTED!

        ASSERT_THROW(introspectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getString(), CppRuntimeException);
        ASSERT_THROW(introspectable->getBitBuffer(), CppRuntimeException);

        checkArithmeticCppTypeGetters(value, introspectable);

        checkNonCompound(introspectable);
        checkNonArray(introspectable);

        checkWriteRead(value, introspectable, readFunc, bitSize);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkIntegral(T value, const IIntrospectablePtr& introspectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize)
    {
        ASSERT_EQ(value, ((*introspectable).*getter)());

        ASSERT_EQ(value, introspectable->toDouble());
        ASSERT_EQ(zserio::toString(value), introspectable->toString());

        ASSERT_THROW(introspectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(introspectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(introspectable->getString(), CppRuntimeException);
        ASSERT_THROW(introspectable->getBitBuffer(), CppRuntimeException);

        checkArithmeticCppTypeGetters(value, introspectable);

        checkNonCompound(introspectable);
        checkNonArray(introspectable);

        checkWriteRead(value, introspectable, readFunc, bitSize);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkSignedIntegral(T value, const IIntrospectablePtr& introspectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, introspectable->toInt());
        ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->getBool(), CppRuntimeException); // bool is unsigned integral type

        checkIntegral(value, introspectable, getter, readFunc, bitSize);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkUnsignedIntegral(T value, const IIntrospectablePtr& introspectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, introspectable->toUInt());
        ASSERT_THROW(introspectable->toInt(), CppRuntimeException);

        checkIntegral(value, introspectable, getter, readFunc, bitSize);
    }

    void checkStringIntrospectable(StringView value, const IIntrospectablePtr& introspectable)
    {
        ASSERT_EQ(value, introspectable->getString());

        ASSERT_EQ(stringViewToString(value), introspectable->toString());
        ASSERT_THROW(introspectable->toInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toDouble(), CppRuntimeException);

        ASSERT_THROW(introspectable->getBool(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(introspectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(introspectable->getBitBuffer(), CppRuntimeException);

        checkNonCompound(introspectable);
        checkNonArray(introspectable);

        checkWriteRead(stringViewToString(value), introspectable,
                std::bind(&BitStreamReader::readString<>, _1, std::allocator<uint8_t>()),
                bitSizeOfVarSize(convertSizeToUInt32(value.size())) + value.size() * 8);
    }

    void checkBitmaskIntrospectable(DummyBitmask bitmask, const IIntrospectablePtr& introspectable)
    {
        ASSERT_EQ(bitmask.getValue(), introspectable->getUInt8());

        ASSERT_EQ(bitmask.getValue(), introspectable->toUInt());
        ASSERT_EQ(bitmask.getValue(), introspectable->toDouble());
        ASSERT_EQ(bitmask.toString(), introspectable->toString());
        ASSERT_THROW(introspectable->toInt(), CppRuntimeException);

        checkNonCompound(introspectable);
        checkNonArray(introspectable);

        checkWriteRead(bitmask, introspectable,
                [](BitStreamReader& reader) {
                    return DummyBitmask(reader);
                }, 8
        );
    }

    void checkEnumIntrospectable(DummyEnum enumeration, const IIntrospectablePtr& introspectable)
    {
        ASSERT_EQ(enumToValue(enumeration), introspectable->getInt8());

        ASSERT_EQ(enumToValue(enumeration), introspectable->toInt());
        ASSERT_EQ(enumToValue(enumeration), introspectable->toDouble());
        ASSERT_EQ(enumToString(enumeration), introspectable->toString());
        ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);

        checkNonCompound(introspectable);
        checkNonArray(introspectable);

        checkWriteRead(enumeration, introspectable,
                [](BitStreamReader& reader) {
                    return zserio::read<DummyEnum>(reader);
                }, 8
        );
    }

    void checkCompoundIntrospectable(const DummyParent& dummyParent, const IIntrospectablePtr& introspectable)
    {
        ASSERT_TRUE(TypeInfoUtil::isCompound(introspectable->getTypeInfo().getSchemaType()));

        // field getter
        ASSERT_EQ(dummyParent.getDummyChild().getValue(),
                introspectable->getField("dummyChild")->getField("value")->getUInt32());

        // find field
        ASSERT_EQ(dummyParent.getDummyChild().getValue(), introspectable->find("dummyChild.value")->toUInt());
        ASSERT_EQ(dummyParent.getDummyChild().getValue(), (*introspectable)["dummyChild.value"]->toDouble());

        // find parameter
        ASSERT_EQ(dummyParent.getDummyChild().getValue(),
                (*introspectable)["dummyChild.dummyParam"]->getUInt32());

        // find function
        ASSERT_EQ(dummyParent.getDummyChild().getValue(),
                (*introspectable)["dummyChild.getValue"]->getUInt32());

        // find failed
        ASSERT_EQ(nullptr, introspectable->find("dummyChild.nonexistent"));
        ASSERT_EQ(nullptr, introspectable->find("nonexistent"));
        ASSERT_EQ(nullptr, introspectable->find("dummyChild.value.nonexistent"));
        ASSERT_EQ(nullptr, introspectable->find("dummyChild.dummyParam.nonexistent"));
        ASSERT_EQ(nullptr, introspectable->find("dummyChild.getValue.nonexistent"));
        // find failed because the underlying code throws
        ASSERT_EQ(nullptr, introspectable->find("dummyChild.throwingFunction.nonexistent"));

        // setter
        introspectable->getField("dummyChild")->setField("value", AnyHolder<>(static_cast<uint32_t>(11)));
        ASSERT_EQ(11, dummyParent.getDummyChild().getValue());

        ASSERT_THROW(introspectable->getBool(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(introspectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(introspectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(introspectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(introspectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(introspectable->toInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(introspectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(introspectable->toString(), CppRuntimeException);

        checkNonArray(introspectable);

        checkWriteRead(dummyParent, introspectable,
                [](BitStreamReader& reader) {
                    return DummyParent(reader);
                }, 31
        );
    }
};

TEST_F(IntrospectableTest, boolIntrospectable)
{
    const bool value = true;
    auto introspectable = IntrospectableFactory::getBool(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getBool,
            std::bind(&BitStreamReader::readBool, _1));
}

TEST_F(IntrospectableTest, int8Introspectable)
{
    const int8_t value = -12;
    auto introspectable = IntrospectableFactory::getInt8(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, 8));
}

TEST_F(IntrospectableTest, int16Introspectable)
{
    const int16_t value = -1234;
    auto introspectable = IntrospectableFactory::getInt16(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, 16));
}

TEST_F(IntrospectableTest, int32Introspectable)
{
    const int32_t value = -123456;
    auto introspectable = IntrospectableFactory::getInt32(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, 32));
}

TEST_F(IntrospectableTest, int64Introspectable)
{
    const int64_t value = -1234567890;
    auto introspectable = IntrospectableFactory::getInt64(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, 64));
}

TEST_F(IntrospectableTest, uint8Introspectable)
{
    const uint8_t value = 0xFF;
    auto introspectable = IntrospectableFactory::getUInt8(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, 8));
}

TEST_F(IntrospectableTest, uint16Introspectable)
{
    const uint16_t value = 0xFFFF;
    auto introspectable = IntrospectableFactory::getUInt16(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, 16));
}

TEST_F(IntrospectableTest, uint32Introspectable)
{
    const uint32_t value = 0xFFFFFFFF;
    auto introspectable = IntrospectableFactory::getUInt32(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, 32));
}

TEST_F(IntrospectableTest, uint64Introspectable)
{
    const uint64_t value = 0xFFFFFFFFFFFF;
    auto introspectable = IntrospectableFactory::getUInt64(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, 64));
}

TEST_F(IntrospectableTest, fixedSignedBitField5) // mapped to int8_t
{
    const uint8_t numBits = 5;
    const int8_t value = 15;
    auto introspectable = IntrospectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(10, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedSignedBitField15) // mapped to int16_t
{
    const uint8_t numBits = 15;
    const int16_t value = -15;
    auto introspectable = IntrospectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(5, value), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(17, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedSignedBitField31) // mapped to int32_t
{
    const uint8_t numBits = 31;
    const int32_t value = -12345678;
    auto introspectable = IntrospectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(16, value), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(33, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedSignedBitField60) // mapped to int64_t
{
    const uint8_t numBits = 60;
    const int64_t value = 1234567890;
    auto introspectable = IntrospectableFactory::getFixedSignedBitField(numBits, value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(31, value), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getFixedSignedBitField(65, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedUnsignedBitField7) // mapped to uint8_t
{
    const uint8_t numBits = 7;
    const uint8_t value = 0x2F;
    auto introspectable = IntrospectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(9, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedUnsignedBitField9) // mapped to uint16_t
{
    const uint8_t numBits = 9;
    const uint16_t value = 0x1FF;
    auto introspectable = IntrospectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(8, value), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(17, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedUnsignedBitField31) // mapped to uint32_t
{
    const uint8_t numBits = 31;
    const uint32_t value = UINT32_MAX >> 1;
    auto introspectable = IntrospectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(16, value), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(33, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, fixedUnsignedBitField33) // mapped to uint64_t
{
    const uint8_t numBits = 33;
    const uint64_t value = static_cast<uint64_t>(UINT32_MAX) << 1;
    auto introspectable = IntrospectableFactory::getFixedUnsignedBitField(numBits, value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(32, value), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getFixedUnsignedBitField(65, value), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicSignedBitField5) // mapped to int8_t
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    const int8_t value = 15;
    auto introspectable = IntrospectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(9, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicSignedBitField15) // mapped to int16_t
{
    const uint8_t maxBitSize = 16;
    const uint8_t numBits = 15;
    const int16_t value = -15;
    auto introspectable = IntrospectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(8, value, numBits), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(17, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicSignedBitField31) // mapped to int32_t
{
    const uint8_t maxBitSize = 32;
    const uint8_t numBits = 31;
    const int32_t value = -12345678;
    auto introspectable = IntrospectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(16, value, numBits), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(33, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicSignedBitField60) // mapped to int64_t
{
    const uint8_t maxBitSize = 64;
    const uint8_t numBits = 60;
    const int64_t value = 1234567890;
    auto introspectable = IntrospectableFactory::getDynamicSignedBitField(maxBitSize, value, numBits);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(32, value, numBits), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getDynamicSignedBitField(65, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicUnsignedBitField7) // mapped to uint8_t
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 7;
    const uint8_t value = 0x2F;
    auto introspectable = IntrospectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(9, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicUnsignedBitField9) // mapped to uint16_t
{
    const uint8_t maxBitSize = 16;
    const uint8_t numBits = 9;
    const uint16_t value = 0x1FF;
    auto introspectable = IntrospectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(8, value, numBits), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(17, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicUnsignedBitField31) // mapped to uint32_t
{
    const uint8_t maxBitSize = 32;
    const uint8_t numBits = 31;
    const uint32_t value = UINT32_MAX >> 1;
    auto introspectable = IntrospectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(16, value, numBits), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(33, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicUnsignedBitField33) // mapped to uint64_t
{
    const uint8_t maxBitSize = 64;
    const uint8_t numBits = 33;
    const uint64_t value = static_cast<uint64_t>(UINT32_MAX) << 1;
    auto introspectable = IntrospectableFactory::getDynamicUnsignedBitField(maxBitSize, value, numBits);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, numBits), numBits);

    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(32, value, numBits), CppRuntimeException);
    ASSERT_THROW(IntrospectableFactory::getDynamicUnsignedBitField(65, value, numBits), CppRuntimeException);
}

TEST_F(IntrospectableTest, varint16Introspectable)
{
    const int16_t value = -1234;
    auto introspectable = IntrospectableFactory::getVarInt16(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt16,
            std::bind(&BitStreamReader::readVarInt16, _1), bitSizeOfVarInt16(value));
}

TEST_F(IntrospectableTest, varint32Introspectable)
{
    const int32_t value = 54321;
    auto introspectable = IntrospectableFactory::getVarInt32(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt32,
            std::bind(&BitStreamReader::readVarInt32, _1), bitSizeOfVarInt32(value));
}

TEST_F(IntrospectableTest, varint64Introspectable)
{
    const int64_t value = -87654321;
    auto introspectable = IntrospectableFactory::getVarInt64(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt64,
            std::bind(&BitStreamReader::readVarInt64, _1), bitSizeOfVarInt64(value));
}

TEST_F(IntrospectableTest, varintIntrospectable)
{
    const int64_t value = INT64_MAX;
    auto introspectable = IntrospectableFactory::getVarInt(value);
    checkSignedIntegral(value, introspectable, &IIntrospectable::getInt64,
            std::bind(&BitStreamReader::readVarInt, _1), bitSizeOfVarInt(value));
}

TEST_F(IntrospectableTest, varuint16Introspectable)
{
    const uint16_t value = 1234;
    auto introspectable = IntrospectableFactory::getVarUInt16(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt16,
            std::bind(&BitStreamReader::readVarUInt16, _1), bitSizeOfVarUInt16(value));
}

TEST_F(IntrospectableTest, varuint32Introspectable)
{
    const uint32_t value = 0x1FFFFFFF;
    auto introspectable = IntrospectableFactory::getVarUInt32(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt32,
            std::bind(&BitStreamReader::readVarUInt32, _1), bitSizeOfVarUInt32(value));
}

TEST_F(IntrospectableTest, varuint64Introspectable)
{
    const uint64_t value = 4242424242;
    auto introspectable = IntrospectableFactory::getVarUInt64(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt64,
            std::bind(&BitStreamReader::readVarUInt64, _1), bitSizeOfVarUInt64(value));
}

TEST_F(IntrospectableTest, varuintIntrospectable)
{
    const uint64_t value = UINT64_MAX;
    auto introspectable = IntrospectableFactory::getVarUInt(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt64,
            std::bind(&BitStreamReader::readVarUInt, _1), bitSizeOfVarUInt(value));
}

TEST_F(IntrospectableTest, varsizeIntrospectable)
{
    const uint32_t value = (UINT32_C(1) << (7+7+7+7+3)) - 1;
    auto introspectable = IntrospectableFactory::getVarSize(value);
    checkUnsignedIntegral(value, introspectable, &IIntrospectable::getUInt32,
            std::bind(&BitStreamReader::readVarSize, _1), bitSizeOfVarSize(value));
}

TEST_F(IntrospectableTest, float16Introspectable)
{
    const float value = 2.0f;
    auto introspectable = IntrospectableFactory::getFloat16(value);
    checkFloatingPoint(value, introspectable, &IIntrospectable::getFloat,
            std::bind(&BitStreamReader::readFloat16, _1));
}

TEST_F(IntrospectableTest, float32Introspectable)
{
    const float value = 1.2f;
    auto introspectable = IntrospectableFactory::getFloat32(value);
    checkFloatingPoint(value, introspectable, &IIntrospectable::getFloat,
            std::bind(&BitStreamReader::readFloat32, _1));
}

TEST_F(IntrospectableTest, float64Introspectable)
{
    const double value = 1.2;
    auto introspectable = IntrospectableFactory::getFloat64(value);
    checkFloatingPoint(value, introspectable, &IIntrospectable::getDouble,
            std::bind(&BitStreamReader::readFloat64, _1));
}

TEST_F(IntrospectableTest, stringIntrospectable)
{
    const std::string value = "some longer string value to have a chance that some allocation hopefully occurs";
    auto introspectable = IntrospectableFactory::getString(value);
    checkStringIntrospectable(value, introspectable);
}

TEST_F(IntrospectableTest, stringViewIntrospectable)
{
    auto view = makeStringView("some text as a string view");
    auto introspectable = IntrospectableFactory::getString(view);
    checkStringIntrospectable(view, introspectable);
}

TEST_F(IntrospectableTest, bitBufferIntrospectable)
{
    const BitBuffer value = BitBuffer{std::vector<uint8_t>{{0xAB, 0xF0}}, 12};
    auto introspectable = IntrospectableFactory::getBitBuffer(value);

    ASSERT_EQ(value, introspectable->getBitBuffer());

    ASSERT_THROW(introspectable->toString(), CppRuntimeException);
    ASSERT_THROW(introspectable->toDouble(), CppRuntimeException);
    ASSERT_THROW(introspectable->toInt(), CppRuntimeException);
    ASSERT_THROW(introspectable->toUInt(), CppRuntimeException);

    ASSERT_THROW(introspectable->getInt8(), CppRuntimeException);
    ASSERT_THROW(introspectable->getInt16(), CppRuntimeException);
    ASSERT_THROW(introspectable->getInt32(), CppRuntimeException);
    ASSERT_THROW(introspectable->getInt64(), CppRuntimeException);
    ASSERT_THROW(introspectable->getUInt8(), CppRuntimeException);
    ASSERT_THROW(introspectable->getUInt16(), CppRuntimeException);
    ASSERT_THROW(introspectable->getUInt32(), CppRuntimeException);
    ASSERT_THROW(introspectable->getUInt64(), CppRuntimeException);
    ASSERT_THROW(introspectable->getFloat(), CppRuntimeException);
    ASSERT_THROW(introspectable->getDouble(), CppRuntimeException);
    ASSERT_THROW(introspectable->getString(), CppRuntimeException);

    checkNonCompound(introspectable);
    checkNonArray(introspectable);

    checkWriteRead(value, introspectable,
            std::bind(&BitStreamReader::readBitBuffer<>, _1, std::allocator<uint8_t>()),
            bitSizeOfVarSize(convertSizeToUInt32(value.getBitSize())) + value.getBitSize());
}

TEST_F(IntrospectableTest, uint8Array)
{
    auto rawArray = std::vector<uint8_t>{{10, 20, 30, 40}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo::getUInt8();
    auto introspectable = IntrospectableFactory::getBuiltinArray(typeInfo, rawArray);
    checkArray(rawArray, introspectable,
            [&](uint8_t value, const IIntrospectablePtr& elementIntrospectable) {
                checkUnsignedIntegral(value, elementIntrospectable, &IIntrospectable::getUInt8,
                        std::bind(&BitStreamReader::readBits, _1, 8));
            }
    );

    // call version with dynamic bit size
    ASSERT_THROW(IntrospectableFactory::getBuiltinArray(typeInfo, rawArray, 8), CppRuntimeException);
}

TEST_F(IntrospectableTest, dynamicSignedBitField5Array)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize);
    auto introspectable = IntrospectableFactory::getBuiltinArray(typeInfo, rawArray, numBits);
    checkArray(rawArray, introspectable,
            [&](int8_t value, const IIntrospectablePtr& elementIntrospectable) {
                checkSignedIntegral(value, elementIntrospectable, &IIntrospectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            }
    );

    // call version without dynamic bit size
    ASSERT_THROW(IntrospectableFactory::getBuiltinArray(typeInfo, rawArray), CppRuntimeException);
}

TEST_F(IntrospectableTest, stringArray)
{
    auto rawArray = std::vector<std::string>{{"one", "two", "three"}};
    auto introspectable = IntrospectableFactory::getBuiltinArray(BuiltinTypeInfo::getString(), rawArray);
    checkArray(rawArray, introspectable,
            [&](StringView value, const IIntrospectablePtr& elementIntrospectable) {
                checkStringIntrospectable(value, elementIntrospectable);
            }
    );
}

TEST_F(IntrospectableTest, bitmask)
{
    DummyBitmask bitmask = DummyBitmask::Values::WRITE;
    auto introspectable = bitmask.introspectable();
    checkBitmaskIntrospectable(bitmask, introspectable);
}

TEST_F(IntrospectableTest, bitmaskArray)
{
    auto rawArray = std::vector<DummyBitmask>{{DummyBitmask::Values::WRITE, DummyBitmask::Values::CREATE}};
    auto introspectable = IntrospectableFactory::getBitmaskArray(rawArray);
    checkArray(rawArray, introspectable,
            [&](DummyBitmask value, const IIntrospectablePtr& elementIntrospectable) {
                checkBitmaskIntrospectable(value, elementIntrospectable);
            }
    );
}

TEST_F(IntrospectableTest, enumeration)
{
    const DummyEnum enumeration = DummyEnum::VALUE1;
    auto introspectable = enumIntrospectable(enumeration);
    checkEnumIntrospectable(enumeration, introspectable);
}

TEST_F(IntrospectableTest, enumArray)
{
    auto rawArray = std::vector<DummyEnum>{{DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3}};
    auto introspectable = IntrospectableFactory::getEnumArray(rawArray);
    checkArray(rawArray, introspectable,
            [&](DummyEnum value, const IIntrospectablePtr& elementIntrospectable) {
                checkEnumIntrospectable(value, elementIntrospectable);
            }
    );
}

TEST_F(IntrospectableTest, compound)
{
    DummyParent dummyParent = DummyParent{DummyChild{13}};
    auto introspectable = dummyParent.introspectable();
    checkCompoundIntrospectable(dummyParent, introspectable);
}

TEST_F(IntrospectableTest, compoundArray)
{
    auto rawArray = std::vector<DummyParent>{{DummyParent{DummyChild{13}}, DummyParent{DummyChild{42}}}};
    auto introspectable = IntrospectableFactory::getCompoundArray(rawArray);
    checkArray(rawArray, introspectable,
            [&](const DummyParent& value, const IIntrospectablePtr& elementIntrospectable) {
                checkCompoundIntrospectable(value, elementIntrospectable);
            }
    );
}

} // namespace zserio
