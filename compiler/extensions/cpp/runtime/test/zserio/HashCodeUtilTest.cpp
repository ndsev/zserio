#include "zserio/HashCodeUtil.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(HashCodeUtilTest, SimpleTypes)
{
    const int hashSeed = 1;

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
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, floatValue));
}

TEST(HashCodeUtilTest, StringType)
{
    const int hashSeed = 1;
    const std::string stringValue = "0";
    EXPECT_EQ(HASH_PRIME_NUMBER + '0', calcHashCode(hashSeed, stringValue));
}

struct DummyObject
{
    DummyObject() {}
    int hashCode() const { return 10; }
};

TEST(HashCodeUtilTest, ObjectType)
{
    const int hashSeed = 1;
    const DummyObject objectValue;
    EXPECT_EQ(HASH_PRIME_NUMBER + 10, calcHashCode(hashSeed, objectValue));
}

} // namespace zserio
