#ifndef ZSERIO_ZSERIO_TREE_CREATOR_TEST_OBJECT_H_INC
#define ZSERIO_ZSERIO_TREE_CREATOR_TEST_OBJECT_H_INC

#include "zserio/Array.h"
#include "zserio/TypeInfo.h"
#include "zserio/Reflectable.h"

namespace zserio
{

enum class DummyEnum : int8_t
{
    ONE = INT8_C(0),
    TWO = INT8_C(1),
    MinusOne = INT8_C(-1)
};

template <>
struct EnumTraits<DummyEnum>
{
    static constexpr ::std::array<const char*, 3> names =
    {{
        "ONE",
        "TWO",
        "MinusOne"
    }};

    static constexpr ::std::array<DummyEnum, 3> values =
    {{
        DummyEnum::ONE,
        DummyEnum::TWO,
        DummyEnum::MinusOne
    }};
};

template <>
size_t enumToOrdinal<DummyEnum>(DummyEnum value);

template <>
DummyEnum valueToEnum(int8_t rawValue);

template <>
const ITypeInfo& enumTypeInfo<DummyEnum, std::allocator<uint8_t>>();

template <>
IReflectablePtr enumReflectable(DummyEnum value, const ::std::allocator<uint8_t>& allocator);

class DummyBitmask
{
public:
    typedef uint8_t underlying_type;

    enum class Values : underlying_type
    {
        READ = UINT8_C(1),
        WRITE = UINT8_C(2)
    };

    constexpr DummyBitmask() noexcept :
        m_value(0)
    {}

    constexpr DummyBitmask(Values value) noexcept :
        m_value(static_cast<underlying_type>(value))
    {}

    constexpr explicit DummyBitmask(underlying_type value) noexcept :
        m_value(value)
    {}

    DummyBitmask(const DummyBitmask&) = default;
    DummyBitmask& operator=(const DummyBitmask&) = default;

    DummyBitmask(DummyBitmask&&) = default;

    constexpr underlying_type getValue() const
    {
        return m_value;
    }

    static const ITypeInfo& typeInfo();
    IReflectablePtr reflectable(const ::std::allocator<uint8_t>& allocator = ::std::allocator<uint8_t>()) const;

private:
    underlying_type m_value;
};

inline bool operator==(const DummyBitmask& lhs, const DummyBitmask& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

inline DummyBitmask operator|(DummyBitmask::Values lhs, DummyBitmask::Values rhs)
{
    return DummyBitmask(static_cast<DummyBitmask::underlying_type>(lhs) |
            static_cast<DummyBitmask::underlying_type>(rhs));
}

class DummyNested
{
public:
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyNested(const allocator_type& allocator = allocator_type()) noexcept;

    ~DummyNested() = default;

    static void createPackingContext(PackingContextNode&);

    static const ITypeInfo& typeInfo();
    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    void initialize(
            uint32_t param_);

    uint32_t getParam() const;

    uint32_t getValue() const;
    void setValue(uint32_t value_);

    string<>& getText();
    void setText(const string<>& text_);

    BitBuffer& getData();
    void setData(const BitBuffer& data_);

    DummyEnum getDummyEnum() const;
    void setDummyEnum(DummyEnum dummyEnum_);

    DummyBitmask getDummyBitmask() const;
    void setDummyBitmask(DummyBitmask dummyBitmask_);

private:
    uint32_t m_param_;
    bool m_isInitialized;
    uint32_t m_value_;
    string<> m_text_;
    BitBuffer m_data_;
    DummyEnum m_dummyEnum_;
    DummyBitmask m_dummyBitmask_;
};

class DummyObject
{
private:
    class ZserioElementFactory_nestedArray
    {
    public:

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

    using ZserioArrayType_nestedArray = Array<vector<DummyNested>, ObjectArrayTraits<DummyNested,
            ZserioElementFactory_nestedArray>, ArrayType::AUTO>;
    using ZserioArrayType_textArray = Array<vector<string<>>, StringArrayTraits, ArrayType::AUTO>;
    using ZserioArrayType_externArray = Array<vector<BitBuffer>, BitBufferArrayTraits, ArrayType::AUTO>;

public:
    using allocator_type = ::std::allocator<uint8_t>;

    explicit DummyObject(const allocator_type& allocator = allocator_type()) noexcept;
    ~DummyObject() = default;

    static const ITypeInfo& typeInfo();
    IReflectableConstPtr reflectable(const allocator_type& allocator = allocator_type()) const;
    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type());

    void initializeChildren();

    uint32_t getValue() const;
    void setValue(uint32_t value_);

    DummyNested& getNested();

    string<>& getText();
    void setText(const string<>& text_);

    vector<DummyNested>& getNestedArray();
    void setNestedArray(const vector<DummyNested>& nestedArray_);

    vector<string<>>& getTextArray();
    void setTextArray(const vector<string<>>& textArray_);

    bool isExternArraySet() const;
    vector<BitBuffer>& getExternArray();
    void setExternArray(const vector<BitBuffer>& externArray_);

    bool isOptionalBoolSet() const;
    bool getOptionalBool() const;
    void setOptionalBool(bool optionalBool_);
    void resetOptionalBool();

    bool isOptionalNestedSet() const;
    DummyNested& getOptionalNested();
    void setOptionalNested(const DummyNested& optionalNested_);

private:
    bool m_areChildrenInitialized;
    uint32_t m_value_;
    DummyNested m_nested_;
    string<> m_text_;
    ZserioArrayType_nestedArray m_nestedArray_;
    ZserioArrayType_textArray m_textArray_;
    InplaceOptionalHolder<ZserioArrayType_externArray> m_externArray_;
    InplaceOptionalHolder<bool> m_optionalBool_;
    InplaceOptionalHolder<DummyNested> m_optionalNested_;
};

} // namespace zserio

#endif // ZSERIO_ZSERIO_TREE_CREATOR_TEST_OBJECT_H_INC
