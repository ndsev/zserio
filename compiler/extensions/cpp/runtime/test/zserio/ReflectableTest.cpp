#include <string>
#include <functional>

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

        static const BitmaskTypeInfo typeInfo = {
            makeStringView("DummyBitmask"),
            BuiltinTypeInfo::getUInt8(), underlyingTypeArguments, values
        };

        return typeInfo;
    }

    IReflectablePtr reflectable(const std::allocator<uint8_t>& allocator = std::allocator<uint8_t>())
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

            virtual void write(BitStreamWriter& writer) override
            {
                m_bitmask.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition = 0) const override
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

    void write(BitStreamWriter& out, PreWriteAction = ALL_PRE_WRITE_ACTIONS) const
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

    explicit DummyChild(uint32_t value) : m_value(value) {}
    explicit DummyChild(BitStreamReader& in) : m_value(readValue(in)) {}

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

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

    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            explicit Reflectable(DummyChild& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(DummyChild::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IReflectablePtr getField(StringView name) const override
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

            virtual IReflectablePtr getParameter(StringView name) const override
            {
                if (name == makeStringView("dummyParam"))
                {
                    // dummyParam is just a hack to test more code in runtime
                    return ReflectableFactory::getFixedUnsignedBitField(
                            31, m_object.getValue(), get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Parameter '") + name + "' doesn't exist in 'DummyChild'!";
                }
            }

            virtual IReflectablePtr callFunction(StringView name) const override
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

            virtual void write(BitStreamWriter& writer) override
            {
                m_object.write(writer);
            }

            virtual size_t bitSizeOf(size_t bitPosition = 0) const override
            {
                return m_object.bitSizeOf(bitPosition);
            }

        private:
            DummyChild& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
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

    void write(BitStreamWriter& writer, PreWriteAction = ALL_PRE_WRITE_ACTIONS)
    {
        writer.writeBits(m_value, 31);
    }

    size_t bitSizeOf(size_t) const
    {
        return 31;
    }

private:
    uint32_t readValue(BitStreamReader& in)
    {
        return in.readBits(31);
    }

    uint32_t m_value;
};

class DummyParent
{
public:
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyParent(DummyChild dummyChild) : m_dummyChild(dummyChild) {}
    explicit DummyParent(BitStreamReader& in) : m_dummyChild(readDummyChild(in)) {}

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

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

        static const Span<ParameterInfo> parameters;

        static const Span<FunctionInfo> functions;

        static const StructTypeInfo typeInfo = {
            makeStringView("DummyParent"), templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
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

            virtual IReflectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("dummyChild"))
                {
                    return m_object.getDummyChild().reflectable(get_allocator());
                }
                else
                {
                    throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyParent'!";
                }
            }

            virtual void setField(StringView name, const AnyHolder<allocator_type>& value) override
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

            virtual size_t bitSizeOf(size_t bitPosition = 0) const override
            {
                return m_object.bitSizeOf(bitPosition);
            }

        private:
            DummyParent& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
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

    size_t bitSizeOf(size_t bitPosition = 0) const
    {
        return m_dummyChild.bitSizeOf(bitPosition);
    }

private:
    DummyChild readDummyChild(BitStreamReader& in)
    {
        return DummyChild(in);
    }

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

    static const EnumTypeInfo typeInfo = {
        makeStringView("DummyEnum"),
        BuiltinTypeInfo::getInt8(), underlyingTypeArguments, items
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

        virtual void write(BitStreamWriter& writer) override
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
    template <typename RAW_ARRAY, typename ELEMENT_CHECKER>
    void checkArray(const RAW_ARRAY& rawArray, const IReflectablePtr& reflectable,
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
        ASSERT_THROW(reflectable->getString(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException);

        BitBuffer bitBuffer(0);
        BitStreamWriter writer(bitBuffer);
        ASSERT_THROW(reflectable->write(writer), CppRuntimeException);

        checkNonCompound(reflectable);
    }

    template <typename T, typename READ_FUNC>
    void checkWriteRead(T value, const IReflectablePtr& reflectable, const READ_FUNC& readFunc,
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

    void checkNonArray(const IReflectablePtr& reflectable)
    {
        ASSERT_FALSE(reflectable->isArray());
        ASSERT_THROW(reflectable->size(), CppRuntimeException);
        ASSERT_THROW(reflectable->at(0), CppRuntimeException);
        ASSERT_THROW((*reflectable)[0], CppRuntimeException);
    }

    void checkNonCompound(const IReflectablePtr& reflectable)
    {
        ASSERT_THROW(reflectable->getField("field"), CppRuntimeException);
        ASSERT_THROW(reflectable->setField("field", AnyHolder<>{}), CppRuntimeException);
        ASSERT_THROW(reflectable->getParameter("parameter"), CppRuntimeException);
        ASSERT_THROW(reflectable->callFunction("function"), CppRuntimeException);
        ASSERT_THROW(reflectable->getChoice(), CppRuntimeException);

        ASSERT_EQ(nullptr, reflectable->find("some.field"));
        ASSERT_EQ(nullptr, (*reflectable)["some.field"]);
    }

    template <typename T, typename GETTER>
    void checkArithmeticCppTypeGetter(T value, const IReflectablePtr& reflectable,
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

    template <typename T>
    void checkArithmeticCppTypeGetters(T value, const IReflectablePtr& reflectable)
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

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkFloatingPoint(T value, const IReflectablePtr& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc,
            size_t bitSize = sizeof(T) * 8)
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
        ASSERT_THROW(reflectable->getString(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        checkArithmeticCppTypeGetters(value, reflectable);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable, readFunc, bitSize);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkIntegral(T value, const IReflectablePtr& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize)
    {
        ASSERT_EQ(value, ((*reflectable).*getter)());

        ASSERT_EQ(value, reflectable->toDouble());
        ASSERT_EQ(zserio::toString(value), reflectable->toString());

        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getString(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        checkArithmeticCppTypeGetters(value, reflectable);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable, readFunc, bitSize);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkSignedIntegral(T value, const IReflectablePtr& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, reflectable->toInt());
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBool(), CppRuntimeException); // bool is unsigned integral type

        checkIntegral(value, reflectable, getter, readFunc, bitSize);
    }

    template <typename T, typename GETTER, typename READ_FUNC>
    void checkUnsignedIntegral(T value, const IReflectablePtr& reflectable,
            const GETTER& getter, const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, reflectable->toUInt());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);

        checkIntegral(value, reflectable, getter, readFunc, bitSize);
    }

    void checkStringReflectable(StringView value, const IReflectablePtr& reflectable)
    {
        ASSERT_EQ(value, reflectable->getString());

        ASSERT_EQ(stringViewToString(value), reflectable->toString());
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

        checkWriteRead(stringViewToString(value), reflectable,
                std::bind(&BitStreamReader::readString<>, _1, std::allocator<uint8_t>()),
                bitSizeOfVarSize(convertSizeToUInt32(value.size())) + value.size() * 8);
    }

    void checkBitmaskReflectable(DummyBitmask bitmask, const IReflectablePtr& reflectable)
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

    void checkEnumReflectable(DummyEnum enumeration, const IReflectablePtr& reflectable)
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

    void checkCompoundReflectable(const DummyParent& dummyParent, const IReflectablePtr& reflectable)
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
        ASSERT_EQ(dummyParent.getDummyChild().getValue(), (*reflectable)["dummyChild.dummyParam"]->getUInt32());
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

        // setter
        reflectable->getField("dummyChild")->setField("value", AnyHolder<>(static_cast<uint32_t>(11)));
        ASSERT_EQ(11, dummyParent.getDummyChild().getValue());
        reflectable->setField("dummyChild", AnyHolder<>(DummyChild{42}));
        ASSERT_EQ(42, dummyParent.getDummyChild().getValue());
        ASSERT_THROW(reflectable->setField("nonexistent", AnyHolder<>()), CppRuntimeException);
        ASSERT_THROW(reflectable->find("dummyChild")->setField("nonexistent", AnyHolder<>()),
                CppRuntimeException);

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

        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException);

        checkNonArray(reflectable);

        checkWriteRead(dummyParent, reflectable,
                [](BitStreamReader& reader) {
                    return DummyParent(reader);
                }, 31
        );

        checkWriteRead(dummyParent.getDummyChild(), reflectable->getField("dummyChild"),
                [](BitStreamReader& reader) {
                    return DummyChild(reader);
                }, 31
        );
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
    checkStringReflectable(value, reflectable);
}

TEST_F(ReflectableTest, stringViewReflectable)
{
    auto view = makeStringView("some text as a string view");
    auto reflectable = ReflectableFactory::getString(view);
    checkStringReflectable(view, reflectable);
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
    ASSERT_THROW(reflectable->getString(), CppRuntimeException);

    checkNonCompound(reflectable);
    checkNonArray(reflectable);

    checkWriteRead(value, reflectable,
            std::bind(&BitStreamReader::readBitBuffer<>, _1, std::allocator<uint8_t>()),
            bitSizeOfVarSize(convertSizeToUInt32(value.getBitSize())) + value.getBitSize());
}

TEST_F(ReflectableTest, uint8Array)
{
    auto rawArray = std::vector<uint8_t>{{10, 20, 30, 40}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo::getUInt8();
    auto reflectable = ReflectableFactory::getBuiltinArray(typeInfo, rawArray);
    checkArray(rawArray, reflectable,
            [&](uint8_t value, const IReflectablePtr& elementReflectable) {
                checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                        std::bind(&BitStreamReader::readBits, _1, 8));
            }
    );

    // call version with dynamic bit size
    ASSERT_THROW(ReflectableFactory::getBuiltinArray(typeInfo, rawArray, 8), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField5Array)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    const ITypeInfo& typeInfo = BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize);
    auto reflectable = ReflectableFactory::getBuiltinArray(typeInfo, rawArray, numBits);
    checkArray(rawArray, reflectable,
            [&](int8_t value, const IReflectablePtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            }
    );

    // call version without dynamic bit size
    ASSERT_THROW(ReflectableFactory::getBuiltinArray(typeInfo, rawArray), CppRuntimeException);
}

TEST_F(ReflectableTest, stringArray)
{
    auto rawArray = std::vector<std::string>{{"one", "two", "three"}};
    auto reflectable = ReflectableFactory::getBuiltinArray(BuiltinTypeInfo::getString(), rawArray);
    checkArray(rawArray, reflectable,
            [&](StringView value, const IReflectablePtr& elementReflectable) {
                checkStringReflectable(value, elementReflectable);
            }
    );
}

TEST_F(ReflectableTest, bitmask)
{
    DummyBitmask bitmask = DummyBitmask::Values::WRITE;
    auto reflectable = bitmask.reflectable();
    checkBitmaskReflectable(bitmask, reflectable);
}

TEST_F(ReflectableTest, bitmaskArray)
{
    auto rawArray = std::vector<DummyBitmask>{{
            DummyBitmask::Values::WRITE, DummyBitmask::Values::CREATE, DummyBitmask::Values::READ
    }};
    auto reflectable = ReflectableFactory::getBitmaskArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](DummyBitmask value, const IReflectablePtr& elementReflectable) {
                checkBitmaskReflectable(value, elementReflectable);
            }
    );
}

TEST_F(ReflectableTest, enumeration)
{
    const DummyEnum enumeration = DummyEnum::VALUE1;
    auto reflectable = enumReflectable(enumeration);
    checkEnumReflectable(enumeration, reflectable);
}

TEST_F(ReflectableTest, enumArray)
{
    auto rawArray = std::vector<DummyEnum>{{DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3}};
    auto reflectable = ReflectableFactory::getEnumArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](DummyEnum value, const IReflectablePtr& elementReflectable) {
                checkEnumReflectable(value, elementReflectable);
            }
    );
}

TEST_F(ReflectableTest, compound)
{
    DummyParent dummyParent = DummyParent{DummyChild{13}};
    auto reflectable = dummyParent.reflectable();
    checkCompoundReflectable(dummyParent, reflectable);
}

TEST_F(ReflectableTest, compoundArray)
{
    auto rawArray = std::vector<DummyParent>{{DummyParent{DummyChild{13}}, DummyParent{DummyChild{42}}}};
    auto reflectable = ReflectableFactory::getCompoundArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const DummyParent& value, const IReflectablePtr& elementReflectable) {
                checkCompoundReflectable(value, elementReflectable);
            }
    );
}

} // namespace zserio
