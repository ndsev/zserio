#include <string>

#include "enumeration_types/bitfield_const_enum/Color.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/HashCodeUtil.h"

namespace enumeration_types
{
namespace bitfield_const_enum
{

class BitfieldConstEnumTest : public ::testing::Test
{
protected:
    static const size_t COLOR_BITSIZEOF;

    static const uint8_t NONE_VALUE;
    static const uint8_t RED_VALUE;
    static const uint8_t BLUE_VALUE;
    static const uint8_t GREEN_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t BitfieldConstEnumTest::COLOR_BITSIZEOF = 5;

const uint8_t BitfieldConstEnumTest::NONE_VALUE = 0;
const uint8_t BitfieldConstEnumTest::RED_VALUE = 2;
const uint8_t BitfieldConstEnumTest::BLUE_VALUE = 3;
const uint8_t BitfieldConstEnumTest::GREEN_VALUE = 7;

TEST_F(BitfieldConstEnumTest, EnumTraits)
{
    ASSERT_EQ(std::string("NONE"), zserio::EnumTraits<Color>::names[0]);
    ASSERT_EQ(std::string("GREEN"), zserio::EnumTraits<Color>::names[3]);
    ASSERT_EQ(4, zserio::EnumTraits<Color>::names.size());

    ASSERT_EQ(Color::RED, zserio::EnumTraits<Color>::values[1]);
    ASSERT_EQ(Color::BLUE, zserio::EnumTraits<Color>::values[2]);
    ASSERT_EQ(4, zserio::EnumTraits<Color>::values.size());
}

TEST_F(BitfieldConstEnumTest, enumToOrdinal)
{
    ASSERT_EQ(0, zserio::enumToOrdinal(Color::NONE));
    ASSERT_EQ(1, zserio::enumToOrdinal(Color::RED));
    ASSERT_EQ(2, zserio::enumToOrdinal(Color::BLUE));
    ASSERT_EQ(3, zserio::enumToOrdinal(Color::GREEN));
}

TEST_F(BitfieldConstEnumTest, valueToEnum)
{
    ASSERT_EQ(Color::NONE, zserio::valueToEnum<Color>(NONE_VALUE));
    ASSERT_EQ(Color::RED, zserio::valueToEnum<Color>(RED_VALUE));
    ASSERT_EQ(Color::BLUE, zserio::valueToEnum<Color>(BLUE_VALUE));
    ASSERT_EQ(Color::GREEN, zserio::valueToEnum<Color>(GREEN_VALUE));
}

TEST_F(BitfieldConstEnumTest, stringToEnum)
{
    ASSERT_EQ(Color::NONE, zserio::stringToEnum<Color>("NONE"));
    ASSERT_EQ(Color::RED, zserio::stringToEnum<Color>("RED"));
    ASSERT_EQ(Color::BLUE, zserio::stringToEnum<Color>("BLUE"));
    ASSERT_EQ(Color::GREEN, zserio::stringToEnum<Color>("GREEN"));
}

TEST_F(BitfieldConstEnumTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<Color>(1), zserio::CppRuntimeException);
}

TEST_F(BitfieldConstEnumTest, stringToEnumFailure)
{
    ASSERT_THROW(zserio::stringToEnum<Color>("NONEXISTING"), zserio::CppRuntimeException);
}

TEST_F(BitfieldConstEnumTest, calcHashCode)
{
    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1702, zserio::calcHashCode(zserio::HASH_SEED, Color::NONE));
    ASSERT_EQ(1704, zserio::calcHashCode(zserio::HASH_SEED, Color::RED));
    ASSERT_EQ(1705, zserio::calcHashCode(zserio::HASH_SEED, Color::BLUE));
    ASSERT_EQ(1709, zserio::calcHashCode(zserio::HASH_SEED, Color::GREEN));
}

TEST_F(BitfieldConstEnumTest, bitSizeOf)
{
    ASSERT_TRUE(zserio::bitSizeOf(Color::NONE) == COLOR_BITSIZEOF);
}

TEST_F(BitfieldConstEnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_TRUE(zserio::initializeOffsets(bitPosition, Color::NONE) == bitPosition + COLOR_BITSIZEOF);
}

TEST_F(BitfieldConstEnumTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(static_cast<uint32_t>(Color::RED), COLOR_BITSIZEOF);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Color color(zserio::read<Color>(reader));
    ASSERT_EQ(RED_VALUE, zserio::enumToValue(color));
}

TEST_F(BitfieldConstEnumTest, write)
{
    const Color color(Color::BLUE);
    zserio::BitStreamWriter writer(bitBuffer);
    zserio::write(writer, color);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(BLUE_VALUE, reader.readBits(COLOR_BITSIZEOF));
}

} // namespace bitfield_const_enum
} // namespace enumeration_types
