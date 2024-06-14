#include "builtin_types/dynamic_bitfield_length_bounds/Container.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace builtin_types
{
namespace dynamic_bitfield_length_bounds
{

using allocator_type = Container::allocator_type;
using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class DynamicBitFieldLengthBoundsTest : public ::testing::Test
{
protected:
    void writeContainer(const Container container)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writer.writeBits(container.getUnsignedBitLength(), 4);
        if (container.getUnsignedBitLength() == 0)
        {
            return;
        }
        writer.writeBits(container.getUnsignedValue(), container.getUnsignedBitLength());
        writer.writeBits(container.getUnsignedBigBitLength(), 8);
        if (container.getUnsignedBigBitLength() == 0 || container.getUnsignedBigBitLength() > 64)
        {
            return;
        }
        writer.writeBits64(container.getUnsignedBigValue(), container.getUnsignedBigBitLength());
        writer.writeBits64(container.getSignedBitLength(), 64);
        if (container.getSignedBitLength() == 0 || container.getSignedBitLength() > 64)
        {
            return;
        }
        writer.writeSignedBits64(
                container.getSignedValue(), static_cast<uint8_t>(container.getSignedBitLength()));
    }

    static constexpr uint8_t UNSIGNED_BIT_LENGTH = 15;
    static constexpr uint16_t UNSIGNED_VALUE = (1U << UNSIGNED_BIT_LENGTH) - 1;
    static constexpr uint8_t UNSIGNED_BIG_BIT_LENGTH = 13;
    static constexpr uint64_t UNSIGNED_BIG_VALUE = (1U << UNSIGNED_BIG_BIT_LENGTH) - 1;
    static constexpr uint64_t SIGNED_BIT_LENGTH = 7;
    static constexpr int64_t SIGNED_VALUE = -static_cast<int64_t>(1U << (SIGNED_BIT_LENGTH - 1));

    BitBuffer bitBuffer = BitBuffer(1024);
};

TEST_F(DynamicBitFieldLengthBoundsTest, writeRead)
{
    Container container = {UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH, UNSIGNED_BIG_VALUE,
            SIGNED_BIT_LENGTH, SIGNED_VALUE};

    bitBuffer = zserio::serialize(container);
    const Container readContainer = zserio::deserialize<Container>(bitBuffer);
    ASSERT_EQ(container, readContainer);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    Container readContainer2(reader);
    ASSERT_EQ(container, readContainer2);
}

TEST_F(DynamicBitFieldLengthBoundsTest, unsignedBitLengthZero)
{
    Container container = {
            0, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH, UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, unsignedBitLengthZeroValueZero)
{
    Container container = {0, 0, UNSIGNED_BIG_BIT_LENGTH, UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, unsignedBigBitLengthZero)
{
    Container container = {
            UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, 0, UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, unsignedBigBitLengthZeroValueZero)
{
    Container container = {UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, 0, 0, SIGNED_BIT_LENGTH, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, unsignedBigBitLengthOverMax)
{
    Container container = {
            UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, 65, UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, signedBitLengthZero)
{
    Container container = {
            UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH, UNSIGNED_BIG_VALUE, 0, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, signedBitLengthZeroValueZero)
{
    Container container = {
            UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH, UNSIGNED_BIG_VALUE, 0, 0};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

TEST_F(DynamicBitFieldLengthBoundsTest, signedBitLengthOverMax)
{
    Container container = {
            UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH, UNSIGNED_BIG_VALUE, 65, SIGNED_VALUE};

    ASSERT_THROW(zserio::serialize(container), zserio::CppRuntimeException);

    writeContainer(container);
    zserio::BitStreamReader reader(bitBuffer);
    ASSERT_THROW(Container readContainer(reader), zserio::CppRuntimeException);
}

} // namespace dynamic_bitfield_length_bounds
} // namespace builtin_types
