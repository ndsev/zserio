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
    static const size_t DARK_COLOR_NONE_BITSIZEOF = 8;
    static const size_t DARK_COLOR_DARK_GREEN_BITSIZEOF = 16;
};

TEST_F(VarUIntEnumTest, emptyConstructor)
{
    const DarkColor darkColor;
    ASSERT_EQ(0, darkColor.getValue());
}

TEST_F(VarUIntEnumTest, valueConstructor)
{
    const DarkColor darkColor(DarkColor::DARK_RED);
    ASSERT_EQ(1, darkColor.getValue());
}

TEST_F(VarUIntEnumTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer;
    writer.writeVarUInt(static_cast<uint32_t>(DarkColor::DARK_GREEN));
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    const DarkColor darkColor(reader);
    ASSERT_EQ(255, darkColor.getValue());
}

TEST_F(VarUIntEnumTest, operatorFunction)
{
    const DarkColor darkColor(DarkColor::DARK_BLUE);
    const DarkColor::e_DarkColor colorEnum = darkColor;
    ASSERT_EQ(DarkColor::DARK_BLUE, colorEnum);
}

TEST_F(VarUIntEnumTest, getValue)
{
    const DarkColor darkColor(DarkColor::DARK_GREEN);
    ASSERT_EQ(255, darkColor.getValue());
}

TEST_F(VarUIntEnumTest, bitSizeOf)
{
    const DarkColor darkColor1(DarkColor::NONE);
    ASSERT_TRUE(darkColor1.bitSizeOf() == DARK_COLOR_NONE_BITSIZEOF);

    const DarkColor darkColor2(DarkColor::DARK_GREEN);
    ASSERT_TRUE(darkColor2.bitSizeOf() == DARK_COLOR_DARK_GREEN_BITSIZEOF);
}

TEST_F(VarUIntEnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    const DarkColor darkColor1(DarkColor::NONE);
    ASSERT_TRUE(darkColor1.initializeOffsets(bitPosition) == bitPosition + DARK_COLOR_NONE_BITSIZEOF);

    const DarkColor darkColor2(DarkColor::DARK_GREEN);
    ASSERT_TRUE(darkColor2.initializeOffsets(bitPosition) == bitPosition + DARK_COLOR_DARK_GREEN_BITSIZEOF);
}

TEST_F(VarUIntEnumTest, operatorEquality)
{
    const DarkColor color1(DarkColor::DARK_RED);
    const DarkColor color2;
    const DarkColor color3(DarkColor::DARK_GREEN);
    const DarkColor color4(DarkColor::DARK_RED);

    ASSERT_FALSE(color1 == color2);
    ASSERT_FALSE(color1 == color3);
    ASSERT_TRUE(color1 == color4);
}

TEST_F(VarUIntEnumTest, operatorEnumEquality)
{
    const DarkColor color1(DarkColor::DARK_RED);
    ASSERT_FALSE(color1 == DarkColor::DARK_GREEN);
    ASSERT_TRUE(color1 == DarkColor::DARK_RED);
}

TEST_F(VarUIntEnumTest, hashCode)
{
    const DarkColor color1(DarkColor::DARK_RED);
    const DarkColor color2;
    const DarkColor color3(DarkColor::DARK_GREEN);
    const DarkColor color4(DarkColor::DARK_RED);

    ASSERT_NE(color1.hashCode(), color2.hashCode());
    ASSERT_NE(color1.hashCode(), color3.hashCode());
    ASSERT_EQ(color1.hashCode(), color4.hashCode());
}

TEST_F(VarUIntEnumTest, read)
{
    zserio::BitStreamWriter writer;
    writer.writeVarUInt(static_cast<uint32_t>(DarkColor::DARK_RED));
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    DarkColor darkColor;
    ASSERT_NE(1, darkColor.getValue());
    darkColor.read(reader);
    ASSERT_EQ(1, darkColor.getValue());
}

TEST_F(VarUIntEnumTest, write)
{
    const DarkColor darkColor(DarkColor::DARK_BLUE);
    zserio::BitStreamWriter writer;
    darkColor.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    ASSERT_EQ(2, reader.readVarUInt());
}

TEST_F(VarUIntEnumTest, toString)
{
    ASSERT_EQ(std::string("NONE"), std::string(DarkColor(DarkColor::NONE).toString()));
    ASSERT_EQ(std::string("DARK_RED"), std::string(DarkColor(DarkColor::DARK_RED).toString()));
    ASSERT_EQ(std::string("DARK_BLUE"), std::string(DarkColor(DarkColor::DARK_BLUE).toString()));
    ASSERT_EQ(std::string("DARK_GREEN"), std::string(DarkColor(DarkColor::DARK_GREEN).toString()));
}

TEST_F(VarUIntEnumTest, toEnum)
{
    ASSERT_EQ(DarkColor::NONE, DarkColor::toEnum(0));
    ASSERT_EQ(DarkColor::DARK_RED, DarkColor::toEnum(1));
    ASSERT_EQ(DarkColor::DARK_BLUE, DarkColor::toEnum(2));
    ASSERT_EQ(DarkColor::DARK_GREEN, DarkColor::toEnum(255));
}

TEST_F(VarUIntEnumTest, toEnumFailure)
{
    ASSERT_THROW(DarkColor::toEnum(3), zserio::CppRuntimeException);
}

} // namespace varuint_enum
} // namespace enumeration_types
