#include "gtest/gtest.h"
#include "zserio/Array.h"
#include "zserio/BitBuffer.h"
#include "zserio/FloatUtil.h"
#include "zserio/HashCodeUtil.h"

namespace zserio
{

namespace
{

enum class Color : uint8_t
{
    NONE = UINT8_C(0),
    RED = UINT8_C(2),
    BLUE = UINT8_C(3),
    BLACK = UINT8_C(7)
};

class Permissions
{
public:
    using underlying_type = uint8_t;

    enum class Values : underlying_type
    {
        READ = UINT8_C(1),
        WRITE = UINT8_C(2),
        CREATE = UINT8_C(4)
    };

    constexpr Permissions(Values value) noexcept :
            m_value(static_cast<underlying_type>(value))
    {}

    constexpr underlying_type getValue() const
    {
        return m_value;
    }

    uint32_t hashCode() const
    {
        uint32_t result = HASH_SEED;
        result = calcHashCode(result, m_value);
        return result;
    }

private:
    underlying_type m_value;
};

class DummyObject
{
public:
    explicit DummyObject(uint32_t hashCode) :
            m_hashCode(hashCode)
    {}
    uint32_t hashCode() const
    {
        return m_hashCode;
    }

private:
    uint32_t m_hashCode;
};

} // namespace

template <>
uint32_t enumHashCode<Color>(Color value)
{
    uint32_t result = HASH_SEED;
    result = calcHashCode(result, enumToValue(value));
    return result;
}

TEST(HashCodeUtilTest, simpleTypes)
{
    const uint32_t hashSeed = 1;

    const int intValue = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, intValue));

    const bool boolValue = true;
    EXPECT_EQ(HASH_PRIME_NUMBER + 1, calcHashCode(hashSeed, boolValue));

    const uint8_t uint8Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, uint8Value));

    const uint16_t uint16Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, uint16Value));

    const uint32_t uint32Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, uint32Value));

    const uint64_t uint64Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, uint64Value));

    const int8_t int8Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, int8Value));

    const int16_t int16Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, int16Value));

    const int32_t int32Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, int32Value));
    const int32_t int32Value2 = -1;
    EXPECT_EQ(HASH_PRIME_NUMBER - 1, calcHashCode(hashSeed, int32Value2));

    const int64_t int64Value = -1;
    EXPECT_EQ(HASH_PRIME_NUMBER, calcHashCode(hashSeed, int64Value));

    const float floatValue = 10.0F;
    EXPECT_EQ(HASH_PRIME_NUMBER + convertFloatToUInt32(floatValue), calcHashCode(hashSeed, floatValue));

    const double doubleValue = 10.0;
    const uint64_t uint64DoubleValue = convertDoubleToUInt64(doubleValue);
    const uint32_t expectedHashCode =
            HASH_PRIME_NUMBER + static_cast<uint32_t>(uint64DoubleValue ^ (uint64DoubleValue >> 32U));
    EXPECT_EQ(expectedHashCode, calcHashCode(hashSeed, doubleValue));
}

TEST(HashCodeUtilTest, stringType)
{
    const uint32_t hashSeed = 1;
    const std::string stringValue = "0";
    EXPECT_EQ(HASH_PRIME_NUMBER + '0', calcHashCode(hashSeed, stringValue));
}

TEST(HashCodeUtilTest, bitBufferType)
{
    const uint32_t hashSeed = 1;
    const BitBuffer bitBufferValue;
    EXPECT_EQ(HASH_PRIME_NUMBER + HASH_SEED, calcHashCode(hashSeed, bitBufferValue));
}

TEST(HashCodeUtilTest, enumType)
{
    const uint32_t hashSeed = 1;
    EXPECT_EQ(HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + enumToValue(Color::NONE)),
            calcHashCode(hashSeed, Color::NONE));
    EXPECT_EQ(HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + enumToValue(Color::RED)),
            calcHashCode(hashSeed, Color::RED));
    EXPECT_EQ(HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + enumToValue(Color::BLUE)),
            calcHashCode(hashSeed, Color::BLUE));
    EXPECT_EQ(HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + enumToValue(Color::BLACK)),
            calcHashCode(hashSeed, Color::BLACK));
}

TEST(HashCodeUtilTest, bitmaskType)
{
    const uint32_t hashSeed = 1;
    EXPECT_EQ(HASH_PRIME_NUMBER +
                    (HASH_PRIME_NUMBER * HASH_SEED + Permissions(Permissions::Values::READ).getValue()),
            calcHashCode(hashSeed, Permissions(Permissions::Values::READ)));
    EXPECT_EQ(HASH_PRIME_NUMBER +
                    (HASH_PRIME_NUMBER * HASH_SEED + Permissions(Permissions::Values::WRITE).getValue()),
            calcHashCode(hashSeed, Permissions(Permissions::Values::WRITE)));
    EXPECT_EQ(HASH_PRIME_NUMBER +
                    (HASH_PRIME_NUMBER * HASH_SEED + Permissions(Permissions::Values::CREATE).getValue()),
            calcHashCode(hashSeed, Permissions(Permissions::Values::CREATE)));
}

TEST(HashCodeUtilTest, objectType)
{
    const uint32_t hashSeed = 1;
    const DummyObject objectValue(10);
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, objectValue));
}

TEST(HashCodeUtilTest, emptyOptionalHolderType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<DummyObject> optionalHolder{};
    EXPECT_EQ(HASH_PRIME_NUMBER, calcHashCode(hashSeed, optionalHolder));
}

TEST(HashCodeUtilTest, simpleOptionalHolderType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<uint8_t> optionalHolder(3);
    EXPECT_EQ(HASH_PRIME_NUMBER + 3, calcHashCode(hashSeed, optionalHolder));
}

TEST(HashCodeUtilTest, objectOptionalHolderType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<DummyObject> optionalHolder(DummyObject(3));
    EXPECT_EQ(HASH_PRIME_NUMBER + 3, calcHashCode(hashSeed, optionalHolder));
}

TEST(HashCodeUtilTest, emptyHeapOptionalHolderType)
{
    const uint32_t hashSeed = 1;
    const HeapOptionalHolder<DummyObject> optionalHolder;
    EXPECT_EQ(HASH_PRIME_NUMBER, calcHashCode(hashSeed, optionalHolder));
}

TEST(HashCodeUtilTest, objectHeapOptionalHolderType)
{
    const uint32_t hashSeed = 1;
    const HeapOptionalHolder<DummyObject> optionalHolder(DummyObject(13));
    EXPECT_EQ(HASH_PRIME_NUMBER + 13, calcHashCode(hashSeed, optionalHolder));
}

TEST(HashCodeUtilTest, arrayType)
{
    const uint32_t hashSeed = 1;
    Array<std::vector<int32_t>, StdIntArrayTraits<int32_t>, ArrayType::NORMAL> arrayValue(
            std::vector<int32_t>{{3, 7}});
    const uint32_t rawArrayHashCode = (HASH_PRIME_NUMBER * HASH_SEED + 3) * HASH_PRIME_NUMBER + 7;
    EXPECT_EQ(HASH_PRIME_NUMBER + rawArrayHashCode, calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtilTest, simpleArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<uint8_t> arrayValue = {3, 7};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtil, stringArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<std::string> arrayValue = {"0"};
    EXPECT_EQ(HASH_PRIME_NUMBER + '0', calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtil, bitBufferArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<BitBuffer> arrayValue = {BitBuffer()};
    EXPECT_EQ(HASH_PRIME_NUMBER + HASH_SEED, calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtil, bytesArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<std::vector<uint8_t>> arrayValue = {{1}};
    EXPECT_EQ(HASH_PRIME_NUMBER + 1, calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtil, enumArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<Color> arrayValue = {Color::NONE};
    EXPECT_EQ(HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + enumToValue(Color::NONE)),
            calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtil, bitmaskArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<Permissions> arrayValue = {Permissions::Values::READ};
    EXPECT_EQ(HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + enumToValue(Permissions::Values::READ)),
            calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtilTest, objectArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<DummyObject> arrayValue = {DummyObject(3), DummyObject(7)};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, arrayValue));
}

TEST(HashCodeUtilTest, optionalSimpleArrayType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<std::vector<uint32_t>> optionalArrayValue = {{3, 7}};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, optionalArrayValue));
}

TEST(HashCodeUtilTest, optionalObjectArrayType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<std::vector<DummyObject>> optionalArrayValue = {
            {DummyObject(3), DummyObject(7)}};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, optionalArrayValue));
}

} // namespace zserio
