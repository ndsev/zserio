#include <array>
#include <cmath>
#include <iomanip>

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
        switch (character)
        {
        case '\\':
        case '"':
            (void)stream.put('\\');
            (void)stream.put(character);
            break;
        case '\b':
            (void)stream.put('\\');
            (void)stream.put('b');
            break;
        case '\f':
            (void)stream.put('\\');
            (void)stream.put('f');
            break;
        case '\n':
            (void)stream.put('\\');
            (void)stream.put('n');
            break;
        case '\r':
            (void)stream.put('\\');
            (void)stream.put('r');
            break;
        case '\t':
            (void)stream.put('\\');
            (void)stream.put('t');
            break;
        default:
            if (static_cast<uint8_t>(character) <= 0x1F)
            {
                (void)stream.put('\\');
                (void)stream.put('u');
                (void)stream.put('0');
                (void)stream.put('0');
                (void)stream.put(HEX[static_cast<uint8_t>(static_cast<uint8_t>(character) >> 4U) & 0xFU]);
                (void)stream.put(HEX[static_cast<uint8_t>(character) & 0xFU]);
            }
            else
            {
                (void)stream.put(character);
            }
            break;
        }
    }
    (void)stream.put('"');
}

} // namespace zserio
