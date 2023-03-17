#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "enumeration_types/varuint_enum/DarkColor.h"

namespace enumeration_types
{
namespace varuint_enum
{

class VarUIntEnumTest : public ::testing::Test
{
protected:
    static const size_t DARK_COLOR_NONE_BITSIZEOF;
    static const size_t DARK_COLOR_DARK_RED_BITSIZEOF;
    static const size_t DARK_COLOR_DARK_BLUE_BITSIZEOF;
    static const size_t DARK_COLOR_DARK_GREEN_BITSIZEOF;

    static const uint64_t NONE_VALUE;
    static const uint64_t DARK_RED_VALUE;
    static const uint64_t DARK_BLUE_VALUE;
    static const uint64_t DARK_GREEN_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t VarUIntEnumTest::DARK_COLOR_NONE_BITSIZEOF = 8;
const size_t VarUIntEnumTest::DARK_COLOR_DARK_RED_BITSIZEOF = 8;
const size_t VarUIntEnumTest::DARK_COLOR_DARK_BLUE_BITSIZEOF = 8;
const size_t VarUIntEnumTest::DARK_COLOR_DARK_GREEN_BITSIZEOF = 16;

const uint64_t VarUIntEnumTest::NONE_VALUE = 0;
const uint64_t VarUIntEnumTest::DARK_RED_VALUE = 1;
const uint64_t VarUIntEnumTest::DARK_BLUE_VALUE = 2;
const uint64_t VarUIntEnumTest::DARK_GREEN_VALUE = 255;

TEST_F(VarUIntEnumTest, EnumTraits)
{
    ASSERT_EQ(std::string("NONE"), zserio::EnumTraits<DarkColor>::names[0]);
    ASSERT_EQ(std::string("DARK_GREEN"), zserio::EnumTraits<DarkColor>::names[3]);
    ASSERT_EQ(4, zserio::EnumTraits<DarkColor>::names.size());

    ASSERT_EQ(DarkColor::DARK_RED, zserio::EnumTraits<DarkColor>::values[1]);
    ASSERT_EQ(DarkColor::DARK_BLUE, zserio::EnumTraits<DarkColor>::values[2]);
    ASSERT_EQ(4, zserio::EnumTraits<DarkColor>::values.size());
}

TEST_F(VarUIntEnumTest, enumToOrdinal)
{
    ASSERT_EQ(0, zserio::enumToOrdinal(DarkColor::NONE));
    ASSERT_EQ(1, zserio::enumToOrdinal(DarkColor::DARK_RED));
    ASSERT_EQ(2, zserio::enumToOrdinal(DarkColor::DARK_BLUE));
    ASSERT_EQ(3, zserio::enumToOrdinal(DarkColor::DARK_GREEN));
}

TEST_F(VarUIntEnumTest, valueToEnum)
{
    ASSERT_EQ(DarkColor::NONE, zserio::valueToEnum<DarkColor>(NONE_VALUE));
    ASSERT_EQ(DarkColor::DARK_RED, zserio::valueToEnum<DarkColor>(DARK_RED_VALUE));
    ASSERT_EQ(DarkColor::DARK_BLUE, zserio::valueToEnum<DarkColor>(DARK_BLUE_VALUE));
    ASSERT_EQ(DarkColor::DARK_GREEN, zserio::valueToEnum<DarkColor>(DARK_GREEN_VALUE));
}

TEST_F(VarUIntEnumTest, stringToEnum)
{
    ASSERT_EQ(DarkColor::NONE, zserio::stringToEnum<DarkColor>("NONE"));
    ASSERT_EQ(DarkColor::DARK_RED, zserio::stringToEnum<DarkColor>("DARK_RED"));
    ASSERT_EQ(DarkColor::DARK_BLUE, zserio::stringToEnum<DarkColor>("DARK_BLUE"));
    ASSERT_EQ(DarkColor::DARK_GREEN, zserio::stringToEnum<DarkColor>("DARK_GREEN"));
}

TEST_F(VarUIntEnumTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<DarkColor>(3), zserio::CppRuntimeException);
}

TEST_F(VarUIntEnumTest, stringToEnumFailure)
{
    ASSERT_THROW(zserio::stringToEnum<DarkColor>("NONEXISTING"), zserio::CppRuntimeException);
}

TEST_F(VarUIntEnumTest, enumHashCode)
{
    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1702, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::NONE));
    ASSERT_EQ(1703, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::DARK_RED));
    ASSERT_EQ(1704, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::DARK_BLUE));
    ASSERT_EQ(1957, zserio::calcHashCode(zserio::HASH_SEED, DarkColor::DARK_GREEN));
}

TEST_F(VarUIntEnumTest, bitSizeOf)
{
    ASSERT_TRUE(zserio::bitSizeOf(DarkColor::NONE) == DARK_COLOR_NONE_BITSIZEOF);
    ASSERT_TRUE(zserio::bitSizeOf(DarkColor::DARK_RED) == DARK_COLOR_DARK_RED_BITSIZEOF);
    ASSERT_TRUE(zserio::bitSizeOf(DarkColor::DARK_BLUE) == DARK_COLOR_DARK_BLUE_BITSIZEOF);
    ASSERT_TRUE(zserio::bitSizeOf(DarkColor::DARK_GREEN) == DARK_COLOR_DARK_GREEN_BITSIZEOF);
}

TEST_F(VarUIntEnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_TRUE(zserio::initializeOffsets(bitPosition, DarkColor::NONE) ==
            bitPosition + DARK_COLOR_NONE_BITSIZEOF);
    ASSERT_TRUE(zserio::initializeOffsets(bitPosition, DarkColor::DARK_RED) ==
            bitPosition + DARK_COLOR_DARK_RED_BITSIZEOF);
    ASSERT_TRUE(zserio::initializeOffsets(bitPosition, DarkColor::DARK_BLUE) ==
            bitPosition + DARK_COLOR_DARK_BLUE_BITSIZEOF);
    ASSERT_TRUE(zserio::initializeOffsets(bitPosition, DarkColor::DARK_GREEN) ==
            bitPosition + DARK_COLOR_DARK_GREEN_BITSIZEOF);
}

TEST_F(VarUIntEnumTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeVarUInt(static_cast<uint32_t>(DarkColor::DARK_RED));

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    DarkColor darkColor(zserio::read<DarkColor>(reader));
    ASSERT_EQ(DARK_RED_VALUE, zserio::enumToValue(darkColor));
}

TEST_F(VarUIntEnumTest, write)
{
    const DarkColor darkColor(DarkColor::DARK_GREEN);
    zserio::BitStreamWriter writer(bitBuffer);
    zserio::write(writer, darkColor);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(DARK_GREEN_VALUE, reader.readVarUInt());
}

} // namespace varuint_enum
} // namespace enumeration_types
