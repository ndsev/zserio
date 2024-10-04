#include "enumeration_types/uint64_enum/DarkColor.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace enumeration_types
{
namespace uint64_enum
{

class UInt64EnumTest : public ::testing::Test
{
protected:
    static const size_t DARK_COLOR_BITSIZEOF;

    static const uint64_t NONE_COLOR_VALUE;
    static const uint64_t DARK_RED_VALUE;
    static const uint64_t DARK_BLUE_VALUE;
    static const uint64_t DARK_GREEN_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t UInt64EnumTest::DARK_COLOR_BITSIZEOF = 64;

const uint64_t UInt64EnumTest::NONE_COLOR_VALUE = 0;
const uint64_t UInt64EnumTest::DARK_RED_VALUE = 1;
const uint64_t UInt64EnumTest::DARK_BLUE_VALUE = 2;
const uint64_t UInt64EnumTest::DARK_GREEN_VALUE = 7;

TEST_F(UInt64EnumTest, EnumTraits)
{
    ASSERT_EQ(std::string("noneColor"), zserio::EnumTraits<DarkColor>::names[0]);
    ASSERT_EQ(std::string("DarkGreen"), zserio::EnumTraits<DarkColor>::names[3]);
    ASSERT_EQ(4, zserio::EnumTraits<DarkColor>::names.size());

    ASSERT_EQ(DarkColor::DARK_RED, zserio::EnumTraits<DarkColor>::values[1]);
    ASSERT_EQ(DarkColor::dark_blue, zserio::EnumTraits<DarkColor>::values[2]);
    ASSERT_EQ(4, zserio::EnumTraits<DarkColor>::values.size());
}

TEST_F(UInt64EnumTest, enumToOrdinal)
{
    ASSERT_EQ(0, zserio::enumToOrdinal(DarkColor::noneColor));
    ASSERT_EQ(1, zserio::enumToOrdinal(DarkColor::DARK_RED));
    ASSERT_EQ(2, zserio::enumToOrdinal(DarkColor::dark_blue));
    ASSERT_EQ(3, zserio::enumToOrdinal(DarkColor::DarkGreen));
}

TEST_F(UInt64EnumTest, valueToEnum)
{
    ASSERT_EQ(DarkColor::noneColor, zserio::valueToEnum<DarkColor>(NONE_COLOR_VALUE));
    ASSERT_EQ(DarkColor::DARK_RED, zserio::valueToEnum<DarkColor>(DARK_RED_VALUE));
    ASSERT_EQ(DarkColor::dark_blue, zserio::valueToEnum<DarkColor>(DARK_BLUE_VALUE));
    ASSERT_EQ(DarkColor::DarkGreen, zserio::valueToEnum<DarkColor>(DARK_GREEN_VALUE));
}

TEST_F(UInt64EnumTest, stringToEnum)
{
    ASSERT_EQ(DarkColor::noneColor, zserio::stringToEnum<DarkColor>("noneColor"));
    ASSERT_EQ(DarkColor::DARK_RED, zserio::stringToEnum<DarkColor>("DARK_RED"));
    ASSERT_EQ(DarkColor::dark_blue, zserio::stringToEnum<DarkColor>("dark_blue"));
    ASSERT_EQ(DarkColor::DarkGreen, zserio::stringToEnum<DarkColor>("DarkGreen"));
}

TEST_F(UInt64EnumTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<DarkColor>(3), zserio::CppRuntimeException);
}

TEST_F(UInt64EnumTest, stringToEnumFailure)
{
    ASSERT_THROW(zserio::stringToEnum<DarkColor>("NONEXISTING"), zserio::CppRuntimeException);
}

TEST_F(UInt64EnumTest, enumHashCode)
{
    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1702, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::noneColor));
    ASSERT_EQ(1703, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::DARK_RED));
    ASSERT_EQ(1704, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::dark_blue));
    ASSERT_EQ(1709, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::DarkGreen));
}

TEST_F(UInt64EnumTest, bitSizeOf)
{
    ASSERT_TRUE(zserio::bitSizeOf(DarkColor::noneColor) == DARK_COLOR_BITSIZEOF);
}

TEST_F(UInt64EnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_TRUE(
            zserio::initializeOffsets(bitPosition, DarkColor::noneColor) == bitPosition + DARK_COLOR_BITSIZEOF);
}

TEST_F(UInt64EnumTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits64(static_cast<uint64_t>(DarkColor::DARK_RED), DARK_COLOR_BITSIZEOF);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    DarkColor darkColor(zserio::read<DarkColor>(reader));
    ASSERT_EQ(DARK_RED_VALUE, zserio::enumToValue(darkColor));
}

TEST_F(UInt64EnumTest, write)
{
    const DarkColor darkColor(DarkColor::dark_blue);
    zserio::BitStreamWriter writer(bitBuffer);
    zserio::write(writer, darkColor);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(DARK_BLUE_VALUE, reader.readBits64(DARK_COLOR_BITSIZEOF));
}

} // namespace uint64_enum
} // namespace enumeration_types
