#include <array>

#include "zserio/Enums.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"

#include "gtest/gtest.h"

namespace zserio
{

enum class Color : uint8_t
{
    NONE = UINT8_C(0),
    RED = UINT8_C(2),
    BLUE = UINT8_C(3),
    BLACK = UINT8_C(7)
};

template<>
struct EnumTraits<Color>
{
    static constexpr std::array<const char*, 4> names = {{"NONE", "RED", "BLUE", "BLACK"}};
    static constexpr std::array<Color, 4> values = {{Color::NONE, Color::RED, Color::BLUE, Color::BLACK}};
};

constexpr std::array<const char*, 4> EnumTraits<Color>::names;
constexpr std::array<Color, 4> EnumTraits<Color>::values;

template<>
inline size_t enumToOrdinal<Color>(Color value)
{
    switch (value)
    {
    case Color::NONE:
        return 0;
    case Color::RED:
        return 1;
    case Color::BLUE:
        return 2;
    case Color::BLACK:
        return 3;
    default:
        throw zserio::CppRuntimeException("Unknown value for enumeration Color: " +
                zserio::convertToString(static_cast<uint8_t>(value)) + "!");
    }
}

template<>
inline Color valueToEnum<Color>(typename std::underlying_type<Color>::type rawValue)
{
    switch (rawValue)
    {
    case UINT8_C(0):
    case UINT8_C(2):
    case UINT8_C(3):
    case UINT8_C(7):
        return Color(rawValue);
    default:
        throw zserio::CppRuntimeException("Unknown value for enumeration Color: " +
                zserio::convertToString(rawValue) + "!");
    }
}

template <>
inline size_t bitSizeOf<Color>(Color)
{
    return 3;
}

template <>
inline size_t initializeOffsets<Color>(size_t bitPosition, Color)
{
    return bitPosition + 3;
}

template <>
inline Color read<Color>(zserio::BitStreamReader& in)
{
    return valueToEnum<Color>(static_cast<typename std::underlying_type<Color>::type>(in.readSignedBits(3)));
}

template <>
inline void write<Color>(BitStreamWriter& out, Color value)
{
    out.writeSignedBits(enumToValue(value), 3);
}

TEST(EnumsTest, enumToOrdinal)
{
    EXPECT_EQ(0, enumToOrdinal(Color::NONE));
    EXPECT_EQ(1, enumToOrdinal(Color::RED));
    EXPECT_EQ(2, enumToOrdinal(Color::BLUE));
    EXPECT_EQ(3, enumToOrdinal(Color::BLACK));

    EXPECT_THROW(enumToOrdinal(valueToEnum<Color>(1)), CppRuntimeException);
}

TEST(EnumsTest, valueToEnum)
{
    EXPECT_EQ(Color::NONE, valueToEnum<Color>(0));
    EXPECT_EQ(Color::RED, valueToEnum<Color>(2));
    EXPECT_EQ(Color::BLUE, valueToEnum<Color>(3));
    EXPECT_EQ(Color::BLACK, valueToEnum<Color>(7));

    EXPECT_THROW(valueToEnum<Color>(1), CppRuntimeException);
}

TEST(EnumsTest, enumToValue)
{
    EXPECT_EQ(0, enumToValue(Color::NONE));
    EXPECT_EQ(2, enumToValue(Color::RED));
    EXPECT_EQ(3, enumToValue(Color::BLUE));
    EXPECT_EQ(7, enumToValue(Color::BLACK));

    EXPECT_THROW(enumToValue(valueToEnum<Color>(1)), CppRuntimeException);
}

TEST(EnumsTest, enumToString)
{
    // use std::string to prevent comparison of pointer values (which happened on MSVC in debug)
    EXPECT_EQ(std::string("NONE"), enumToString(Color::NONE));
    EXPECT_EQ(std::string("RED"), enumToString(Color::RED));
    EXPECT_EQ(std::string("BLUE"), enumToString(Color::BLUE));
    EXPECT_EQ(std::string("BLACK"), enumToString(Color::BLACK));

    EXPECT_THROW(enumToString(valueToEnum<Color>(1)), CppRuntimeException);
}

TEST(EnumsTest, bitSizeOf)
{
    EXPECT_EQ(3, bitSizeOf(Color::NONE));
}

TEST(EnumsTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    EXPECT_EQ(3 + bitPosition, initializeOffsets(bitPosition, Color::NONE));
}

TEST(EnumsTest, writeAndRead)
{
    BitStreamWriter out;
    const Color writeColor = Color::NONE;
    write(out, writeColor);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = out.getWriteBuffer(writeBufferByteSize);
    BitStreamReader in(writeBuffer, writeBufferByteSize);

    const Color readColor = read<Color>(in);
    EXPECT_EQ(writeColor, readColor);
}

} // namespace zserio
