#include <string>

#include "enumeration_types/bitfield_enum/Color.h"
#include "gtest/gtest.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/SerializeUtil.h"

namespace enumeration_types
{
namespace bitfield_enum
{

class BitfieldEnumTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;

    static const size_t COLOR_BITSIZEOF;

    static const uint8_t NONE_VALUE;
    static const uint8_t RED_VALUE;
    static const uint8_t BLUE_VALUE;
    static const uint8_t GREEN_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string BitfieldEnumTest::BLOB_NAME = "language/enumeration_types/bitfield_enum.blob";

const size_t BitfieldEnumTest::COLOR_BITSIZEOF = 3;

const uint8_t BitfieldEnumTest::NONE_VALUE = 0;
const uint8_t BitfieldEnumTest::RED_VALUE = 2;
const uint8_t BitfieldEnumTest::BLUE_VALUE = 3;
const uint8_t BitfieldEnumTest::GREEN_VALUE = 7;

TEST_F(BitfieldEnumTest, EnumTraits)
{
    ASSERT_EQ(std::string("NONE"), zserio::EnumTraits<Color>::names[0]);
    ASSERT_EQ(std::string("GREEN"), zserio::EnumTraits<Color>::names[3]);
    ASSERT_EQ(4, zserio::EnumTraits<Color>::names.size());

    ASSERT_EQ(Color::RED, zserio::EnumTraits<Color>::values[1]);
    ASSERT_EQ(Color::BLUE, zserio::EnumTraits<Color>::values[2]);
    ASSERT_EQ(4, zserio::EnumTraits<Color>::values.size());
}

TEST_F(BitfieldEnumTest, enumToOrdinal)
{
    ASSERT_EQ(0, zserio::enumToOrdinal(Color::NONE));
    ASSERT_EQ(1, zserio::enumToOrdinal(Color::RED));
    ASSERT_EQ(2, zserio::enumToOrdinal(Color::BLUE));
    ASSERT_EQ(3, zserio::enumToOrdinal(Color::GREEN));
}

TEST_F(BitfieldEnumTest, valueToEnum)
{
    ASSERT_EQ(Color::NONE, zserio::valueToEnum<Color>(NONE_VALUE));
    ASSERT_EQ(Color::RED, zserio::valueToEnum<Color>(RED_VALUE));
    ASSERT_EQ(Color::BLUE, zserio::valueToEnum<Color>(BLUE_VALUE));
    ASSERT_EQ(Color::GREEN, zserio::valueToEnum<Color>(GREEN_VALUE));
}

TEST_F(BitfieldEnumTest, stringToEnum)
{
    ASSERT_EQ(Color::NONE, zserio::stringToEnum<Color>("NONE"));
    ASSERT_EQ(Color::RED, zserio::stringToEnum<Color>("RED"));
    ASSERT_EQ(Color::BLUE, zserio::stringToEnum<Color>("BLUE"));
    ASSERT_EQ(Color::GREEN, zserio::stringToEnum<Color>("GREEN"));
}

TEST_F(BitfieldEnumTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<Color>(1), zserio::CppRuntimeException);
}

TEST_F(BitfieldEnumTest, stringToEnumFailure)
{
    ASSERT_THROW(zserio::stringToEnum<Color>("NONEXISTING"), zserio::CppRuntimeException);
}

TEST_F(BitfieldEnumTest, enumHashCode)
{
    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1702, zserio::calcHashCode(zserio::HASH_SEED, Color::NONE));
    ASSERT_EQ(1704, zserio::calcHashCode(zserio::HASH_SEED, Color::RED));
    ASSERT_EQ(1705, zserio::calcHashCode(zserio::HASH_SEED, Color::BLUE));
    ASSERT_EQ(1709, zserio::calcHashCode(zserio::HASH_SEED, Color::GREEN));
}

TEST_F(BitfieldEnumTest, bitSizeOf)
{
    ASSERT_TRUE(zserio::bitSizeOf(Color::NONE) == COLOR_BITSIZEOF);
}

TEST_F(BitfieldEnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_TRUE(zserio::initializeOffsets(bitPosition, Color::NONE) == bitPosition + COLOR_BITSIZEOF);
}

TEST_F(BitfieldEnumTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(static_cast<uint32_t>(Color::RED), COLOR_BITSIZEOF);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Color color(zserio::read<Color>(reader));
    ASSERT_EQ(RED_VALUE, zserio::enumToValue(color));
}

TEST_F(BitfieldEnumTest, writeRead)
{
    const Color color(Color::BLUE);
    zserio::BitStreamWriter writer(bitBuffer);
    zserio::write(writer, color);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(BLUE_VALUE, reader.readBits(COLOR_BITSIZEOF));
}

TEST_F(BitfieldEnumTest, writeReadFile)
{
    const Color color(Color::GREEN);
    zserio::serializeToFile(color, BLOB_NAME);

    const Color readColor = zserio::deserializeFromFile<Color>(BLOB_NAME);
    ASSERT_EQ(color, readColor);
}

} // namespace bitfield_enum
} // namespace enumeration_types
