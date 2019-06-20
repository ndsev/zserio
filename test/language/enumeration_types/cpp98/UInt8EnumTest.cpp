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

TEST_F(UInt8EnumTest, emptyConstructor)
{
    const DarkColor darkColor;
    ASSERT_EQ(0, darkColor.getValue());
}

TEST_F(UInt8EnumTest, valueConstructor)
{
    const DarkColor darkColor(DarkColor::DARK_RED);
    ASSERT_EQ(1, darkColor.getValue());
}

TEST_F(UInt8EnumTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(DarkColor::DARK_BLACK), DARK_COLOR_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    const DarkColor darkColor(reader);
    ASSERT_EQ(7, darkColor.getValue());
}

TEST_F(UInt8EnumTest, operatorFunction)
{
    const DarkColor darkColor(DarkColor::DARK_BLUE);
    const DarkColor::e_DarkColor colorEnum = darkColor;
    ASSERT_EQ(DarkColor::DARK_BLUE, colorEnum);
}

TEST_F(UInt8EnumTest, getValue)
{
    const DarkColor darkColor(DarkColor::DARK_BLACK);
    ASSERT_EQ(7, darkColor.getValue());
}

TEST_F(UInt8EnumTest, bitSizeOf)
{
    const DarkColor darkColor(DarkColor::DARK_BLACK);
    ASSERT_TRUE(darkColor.bitSizeOf() == DARK_COLOR_BITSIZEOF);
}

TEST_F(UInt8EnumTest, initializeOffsets)
{
    const DarkColor darkColor(DarkColor::DARK_BLACK);
    const size_t bitPosition = 1;
    ASSERT_TRUE(darkColor.initializeOffsets(bitPosition) == bitPosition + DARK_COLOR_BITSIZEOF);
}

TEST_F(UInt8EnumTest, operatorEquality)
{
    const DarkColor color1(DarkColor::DARK_RED);
    const DarkColor color2;
    const DarkColor color3(DarkColor::DARK_BLACK);
    const DarkColor color4(DarkColor::DARK_RED);

    ASSERT_FALSE(color1 == color2);
    ASSERT_FALSE(color1 == color3);
    ASSERT_TRUE(color1 == color4);
}

TEST_F(UInt8EnumTest, operatorEnumEquality)
{
    const DarkColor color1(DarkColor::DARK_RED);
    ASSERT_FALSE(color1 == DarkColor::DARK_BLACK);
    ASSERT_TRUE(color1 == DarkColor::DARK_RED);
}

TEST_F(UInt8EnumTest, hashCode)
{
    const DarkColor color1(DarkColor::DARK_RED);
    const DarkColor color2;
    const DarkColor color3(DarkColor::DARK_BLACK);
    const DarkColor color4(DarkColor::DARK_RED);

    ASSERT_NE(color1.hashCode(), color2.hashCode());
    ASSERT_NE(color1.hashCode(), color3.hashCode());
    ASSERT_EQ(color1.hashCode(), color4.hashCode());
}

TEST_F(UInt8EnumTest, read)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(DarkColor::DARK_RED), DARK_COLOR_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    DarkColor darkColor;
    ASSERT_NE(1, darkColor.getValue());
    darkColor.read(reader);
    ASSERT_EQ(1, darkColor.getValue());
}

TEST_F(UInt8EnumTest, write)
{
    const DarkColor darkColor(DarkColor::DARK_BLUE);
    zserio::BitStreamWriter writer;
    darkColor.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    ASSERT_EQ(2, reader.readBits(DARK_COLOR_BITSIZEOF));
}

TEST_F(UInt8EnumTest, toString)
{
    ASSERT_EQ(std::string("NONE"), std::string(DarkColor(DarkColor::NONE).toString()));
    ASSERT_EQ(std::string("DARK_RED"), std::string(DarkColor(DarkColor::DARK_RED).toString()));
    ASSERT_EQ(std::string("DARK_BLUE"), std::string(DarkColor(DarkColor::DARK_BLUE).toString()));
    ASSERT_EQ(std::string("DARK_BLACK"), std::string(DarkColor(DarkColor::DARK_BLACK).toString()));
}

TEST_F(UInt8EnumTest, toEnum)
{
    ASSERT_EQ(DarkColor::NONE, DarkColor::toEnum(0));
    ASSERT_EQ(DarkColor::DARK_RED, DarkColor::toEnum(1));
    ASSERT_EQ(DarkColor::DARK_BLUE, DarkColor::toEnum(2));
    ASSERT_EQ(DarkColor::DARK_BLACK, DarkColor::toEnum(7));
}

TEST_F(UInt8EnumTest, toEnumFailure)
{
    ASSERT_THROW(DarkColor::toEnum(3), zserio::CppRuntimeException);
}

} // namespace uint8_enum
} // namespace enumeration_types
