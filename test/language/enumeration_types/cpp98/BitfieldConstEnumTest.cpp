#include <string>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "enumeration_types/bitfield_const_enum/Color.h"

namespace enumeration_types
{
namespace bitfield_const_enum
{

class BitfieldConstEnumTest : public ::testing::Test
{
protected:
    static const size_t COLOR_BITSIZEOF = 5;
};

TEST_F(BitfieldConstEnumTest, emptyConstructor)
{
    const Color color;
    ASSERT_EQ(0, color.getValue());
}

TEST_F(BitfieldConstEnumTest, valueConstructor)
{
    const Color color(Color::RED);
    ASSERT_EQ(2, color.getValue());
}

TEST_F(BitfieldConstEnumTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(Color::GREEN), COLOR_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    const Color color(reader);
    ASSERT_EQ(7, color.getValue());
}

TEST_F(BitfieldConstEnumTest, operatorFunction)
{
    const Color color(Color::BLUE);
    const Color::e_Color colorEnum = color;
    ASSERT_EQ(Color::BLUE, colorEnum);
}

TEST_F(BitfieldConstEnumTest, getValue)
{
    const Color color(Color::GREEN);
    ASSERT_EQ(7, color.getValue());
}

TEST_F(BitfieldConstEnumTest, bitSizeOf)
{
    const Color color(Color::GREEN);
    ASSERT_TRUE(color.bitSizeOf() == COLOR_BITSIZEOF);
}

TEST_F(BitfieldConstEnumTest, initializeOffsets)
{
    const Color color(Color::GREEN);
    const size_t bitPosition = 1;
    ASSERT_TRUE(color.initializeOffsets(bitPosition) == bitPosition + COLOR_BITSIZEOF);
}

TEST_F(BitfieldConstEnumTest, operatorEquality)
{
    const Color color1(Color::RED);
    const Color color2;
    const Color color3(Color::GREEN);
    const Color color4(Color::RED);

    ASSERT_FALSE(color1 == color2);
    ASSERT_FALSE(color1 == color3);
    ASSERT_TRUE(color1 == color4);
}

TEST_F(BitfieldConstEnumTest, operatorEnumEquality)
{
    const Color color1(Color::RED);
    ASSERT_FALSE(color1 == Color::GREEN);
    ASSERT_TRUE(color1 == Color::RED);
}

TEST_F(BitfieldConstEnumTest, hashCode)
{
    const Color color1(Color::RED);
    const Color color2;
    const Color color3(Color::GREEN);
    const Color color4(Color::RED);

    ASSERT_NE(color1.hashCode(), color2.hashCode());
    ASSERT_NE(color1.hashCode(), color3.hashCode());
    ASSERT_EQ(color1.hashCode(), color4.hashCode());
}

TEST_F(BitfieldConstEnumTest, read)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(Color::RED), COLOR_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    Color color;
    ASSERT_NE(2, color.getValue());
    color.read(reader);
    ASSERT_EQ(2, color.getValue());
}

TEST_F(BitfieldConstEnumTest, write)
{
    const Color color(Color::BLUE);
    zserio::BitStreamWriter writer;
    color.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    ASSERT_EQ(3, reader.readBits(COLOR_BITSIZEOF));
}

TEST_F(BitfieldConstEnumTest, toString)
{
    ASSERT_EQ(std::string("NONE"), std::string(Color(Color::NONE).toString()));
    ASSERT_EQ(std::string("RED"), std::string(Color(Color::RED).toString()));
    ASSERT_EQ(std::string("BLUE"), std::string(Color(Color::BLUE).toString()));
    ASSERT_EQ(std::string("GREEN"), std::string(Color(Color::GREEN).toString()));
}

TEST_F(BitfieldConstEnumTest, toEnum)
{
    ASSERT_EQ(Color::NONE, Color::toEnum(0));
    ASSERT_EQ(Color::RED, Color::toEnum(2));
    ASSERT_EQ(Color::BLUE, Color::toEnum(3));
    ASSERT_EQ(Color::GREEN, Color::toEnum(7));
}

TEST_F(BitfieldConstEnumTest, toEnumFailure)
{
    ASSERT_THROW(Color::toEnum(1), zserio::CppRuntimeException);
}

} // namespace bitfield_const_enum
} // namespace enumeration_types
