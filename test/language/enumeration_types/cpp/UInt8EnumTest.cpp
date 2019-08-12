#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "enumeration_types/uint8_enum/DarkColor.h"

namespace enumeration_types
{
namespace uint8_enum
{

class UInt8EnumTest : public ::testing::Test
{
protected:
    static const size_t DARK_COLOR_BITSIZEOF = 8;
};

TEST_F(UInt8EnumTest, EnumTraits)
{
    ASSERT_EQ("NONE", zserio::EnumTraits<DarkColor>::names[0]);
    ASSERT_EQ("DARK_BLACK", zserio::EnumTraits<DarkColor>::names[3]);
    ASSERT_EQ(4, zserio::EnumTraits<DarkColor>::names.size());

    ASSERT_EQ(DarkColor::DARK_RED, zserio::EnumTraits<DarkColor>::values[1]);
    ASSERT_EQ(DarkColor::DARK_BLUE, zserio::EnumTraits<DarkColor>::values[2]);
    ASSERT_EQ(4, zserio::EnumTraits<DarkColor>::values.size());
}

TEST_F(UInt8EnumTest, enumToOrdinal)
{
    ASSERT_EQ(1, zserio::enumToOrdinal(DarkColor::DARK_RED));
    ASSERT_EQ(3, zserio::enumToOrdinal(DarkColor::DARK_BLACK));
}

TEST_F(UInt8EnumTest, valueToEnum)
{
    ASSERT_EQ(DarkColor::NONE, zserio::valueToEnum<DarkColor>(0));
    ASSERT_EQ(DarkColor::DARK_BLUE, zserio::valueToEnum<DarkColor>(2));
}

TEST_F(UInt8EnumTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<DarkColor>(3), zserio::CppRuntimeException);
}

TEST_F(UInt8EnumTest, bitSizeOf)
{
    ASSERT_TRUE(zserio::bitSizeOf<DarkColor>() == DARK_COLOR_BITSIZEOF);
}

TEST_F(UInt8EnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_TRUE(zserio::initializeOffsets<DarkColor>(bitPosition) == bitPosition + DARK_COLOR_BITSIZEOF);
}

TEST_F(UInt8EnumTest, read)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(DarkColor::DARK_RED), DARK_COLOR_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    DarkColor darkColor(zserio::read<DarkColor>(reader));
    ASSERT_EQ(1, zserio::enumToValue(darkColor));
}

TEST_F(UInt8EnumTest, write)
{
    const DarkColor darkColor(DarkColor::DARK_BLUE);
    zserio::BitStreamWriter writer;
    zserio::write(writer, darkColor);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    ASSERT_EQ(2, reader.readBits(DARK_COLOR_BITSIZEOF));
}

} // namespace uint8_enum
} // namespace enumeration_types
