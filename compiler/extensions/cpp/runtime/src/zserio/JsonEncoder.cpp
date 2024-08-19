#include <array>
#include <cmath>
#include <iomanip>
#include <string>

#include "zserio/JsonEncoder.h"

namespace zserio
{

void JsonEncoder::encodeNull(std::ostream& stream)
{
    stream << "null";
}

void JsonEncoder::encodeBool(std::ostream& stream, bool value)
{
    stream << std::boolalpha << value << std::noboolalpha;
}

void JsonEncoder::encodeFloatingPoint(std::ostream& stream, double value)
{
    if (std::isnan(value))
    {
        stream << "NaN";
    }
    else if (std::isinf(value))
    {
        if (value < 0.0)
        {
            stream << "-";
        }
        stream << "Infinity";
    }
    else
    {
        double intPart = 1e16;
        const double fractPart = std::modf(value, &intPart);
        // trying to get closer to behavior of Python
        if (fractPart == 0.0 && intPart > -1e16 && intPart < 1e16)
        {
            stream << std::fixed << std::setprecision(1) << value << std::defaultfloat;
        }
        else
        {
            stream << std::setprecision(15) << value << std::defaultfloat;
        }
    }
}

void JsonEncoder::encodeString(std::ostream& stream, StringView value)
{
    static const std::array<char, 17> HEX = {"0123456789abcdef"};

    (void)stream.put('"');
    for (char character : value)
    {
        if (character == '\\' || character == '"')
        {
            (void)stream.put('\\');
            (void)stream.put(character);
        }
        else if (character == '\b')
        {
            (void)stream.put('\\');
            (void)stream.put('b');
        }
        else if (character == '\f')
        {
            (void)stream.put('\\');
            (void)stream.put('f');
        }
        else if (character == '\n')
        {
            (void)stream.put('\\');
            (void)stream.put('n');
        }
        else if (character == '\r')
        {
            (void)stream.put('\\');
            (void)stream.put('r');
        }
        else if (character == '\t')
        {
            (void)stream.put('\\');
            (void)stream.put('t');
        }
        else
        {
            const unsigned int characterInt =
                    static_cast<unsigned int>(std::char_traits<char>::to_int_type(character));
            if (characterInt <= 0x1F)
            {
                (void)stream.put('\\');
                (void)stream.put('u');
                (void)stream.put('0');
                (void)stream.put('0');
                (void)stream.put(HEX[(characterInt >> 4U) & 0xFU]);
                (void)stream.put(HEX[characterInt & 0xFU]);
            }
            else
            {
                (void)stream.put(character);
            }
        }
    }
    (void)stream.put('"');
}

} // namespace zserio
