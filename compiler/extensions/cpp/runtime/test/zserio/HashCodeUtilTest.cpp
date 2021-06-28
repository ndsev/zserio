#include "zserio/HashCodeUtil.h"
#include "zserio/FloatUtil.h"

#include "gtest/gtest.h"

namespace
{

enum class Color : uint8_t
{
    NONE = UINT8_C(0),
    RED = UINT8_C(2),
    BLUE = UINT8_C(3),
    BLACK = UINT8_C(7)
};

class DummyObject
{
public:
    explicit DummyObject(uint32_t hashCode) : m_hashCode(hashCode) {}
    uint32_t hashCode() const { return m_hashCode; }

private:
    uint32_t m_hashCode;
};

} // namespace

namespace zserio
{

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

    const int64_t int64Value = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, int64Value));

    const float floatValue = 10.0;
    EXPECT_EQ(HASH_PRIME_NUMBER + convertFloatToUInt32(floatValue), calcHashCode(hashSeed, floatValue));

    const double doubleValue = 10.0;
    const uint64_t uint64DoubleValue = convertDoubleToUInt64(doubleValue);
    const uint32_t expectedHashCode = HASH_PRIME_NUMBER +
            static_cast<uint32_t>(uint64DoubleValue ^ (uint64DoubleValue >> 32));
    EXPECT_EQ(expectedHashCode, calcHashCode(hashSeed, doubleValue));
}

TEST(HashCodeUtilTest, stringType)
{
    const uint32_t hashSeed = 1;
    const std::string stringValue = "0";
    EXPECT_EQ(HASH_PRIME_NUMBER + '0', calcHashCode(hashSeed, stringValue));
}

TEST(HashCodeUtilTest, enumType)
{
    const uint32_t hashSeed = 1;
    EXPECT_EQ(HASH_PRIME_NUMBER + enumToValue(Color::NONE), calcHashCode(hashSeed, Color::NONE));
    EXPECT_EQ(HASH_PRIME_NUMBER + enumToValue(Color::RED), calcHashCode(hashSeed, Color::RED));
    EXPECT_EQ(HASH_PRIME_NUMBER + enumToValue(Color::BLUE), calcHashCode(hashSeed, Color::BLUE));
    EXPECT_EQ(HASH_PRIME_NUMBER + enumToValue(Color::BLACK), calcHashCode(hashSeed, Color::BLACK));
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
    const InplaceOptionalHolder<DummyObject> optionalHolder;
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

TEST(HashCodeUtilTest, simpleArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<uint8_t> array = {3, 7};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, array));
}

TEST(HashCodeUtilTest, objectArrayType)
{
    const uint32_t hashSeed = 1;
    const std::vector<DummyObject> array = {DummyObject(3), DummyObject(7)};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, array));
}

TEST(HashCodeUtilTest, optionalSimpleArrayType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<std::vector<uint32_t>> optionalArray = {{3, 7}};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, optionalArray));
}

TEST(HashCodeUtilTest, optionalObjectArrayType)
{
    const uint32_t hashSeed = 1;
    const InplaceOptionalHolder<std::vector<DummyObject>> optionalArray = {{DummyObject(3), DummyObject(7)}};
    EXPECT_EQ((HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7, calcHashCode(hashSeed, optionalArray));
}

} // namespace zserio
