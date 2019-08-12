#include <string>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "enumeration_types/bitfield_enum/Color.h"

namespace enumeration_types
{
namespace bitfield_enum
{

class BitfieldEnumTest : public ::testing::Test
{
protected:
    static const size_t COLOR_BITSIZEOF = 3;
};

TEST_F(BitfieldEnumTest, EnumTraits)
{
    ASSERT_EQ("NONE", zserio::EnumTraits<Color>::names[0]);
    ASSERT_EQ("BLACK", zserio::EnumTraits<Color>::names[3]);
    ASSERT_EQ(4, zserio::EnumTraits<Color>::names.size());

    ASSERT_EQ(Color::RED, zserio::EnumTraits<Color>::values[1]);
    ASSERT_EQ(Color::BLUE, zserio::EnumTraits<Color>::values[2]);
    ASSERT_EQ(4, zserio::EnumTraits<Color>::values.size());
}

TEST_F(BitfieldEnumTest, enumToOrdinal)
{
    ASSERT_EQ(1, zserio::enumToOrdinal(Color::RED));
    ASSERT_EQ(3, zserio::enumToOrdinal(Color::BLACK));
}

TEST_F(BitfieldEnumTest, valueToEnum)
{
    ASSERT_EQ(Color::NONE, zserio::valueToEnum<Color>(0));
    ASSERT_EQ(Color::BLUE, zserio::valueToEnum<Color>(3));
}

TEST_F(BitfieldEnumTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<Color>(1), zserio::CppRuntimeException);
}

TEST_F(BitfieldEnumTest, bitSizeOf)
{
    ASSERT_TRUE(zserio::bitSizeOf<Color>() == COLOR_BITSIZEOF);
}

TEST_F(BitfieldEnumTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_TRUE(zserio::initializeOffsets<Color>(bitPosition) == bitPosition + COLOR_BITSIZEOF);
}

TEST_F(BitfieldEnumTest, read)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(Color::RED), COLOR_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    Color color(zserio::read<Color>(reader));
    ASSERT_EQ(2, zserio::enumToValue(color));
}

TEST_F(BitfieldEnumTest, write)
{
    const Color color(Color::BLUE);
    zserio::BitStreamWriter writer;
    zserio::write(writer, color);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    ASSERT_EQ(3, reader.readBits(COLOR_BITSIZEOF));
}

} // namespace bitfield_enum
} // namespace enumeration_types
