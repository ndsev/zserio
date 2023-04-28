#include <iomanip>
#include <cmath>
#include <array>

#include "zserio/JsonEncoder.h"

namespace zserio
{

void JsonEncoder::encodeNull(std::ostream& os)
{
    os << "null";
}

void JsonEncoder::encodeBool(std::ostream& os, bool value)
{
    os << std::boolalpha << value << std::noboolalpha;
}

void JsonEncoder::encodeFloatingPoint(std::ostream& os, double value)
{
    if (std::isnan(value))
    {
        os << "NaN";
    }
    else if (std::isinf(value))
    {
        if (value < 0.0)
            os << "-";
        os << "Infinity";
    }
    else
    {
        double intPart = 1e16;
        const double fractPart = std::modf(value, &intPart);
        // trying to get closer to behavior of Python
        if (fractPart == 0.0 && intPart > -1e16 && intPart < 1e16)
        {
            os << std::fixed << std::setprecision(1) << value << std::defaultfloat;
        }
        else
        {
            os << std::setprecision(15) << value << std::defaultfloat;
        }
    }
}

void JsonEncoder::encodeString(std::ostream& os, StringView value)
{
    static const std::array<char, 17> HEX = {"0123456789abcdef"};

    os.put('"');
    for (char ch : value)
    {
        switch (ch)
        {
        case '\\':
        case '"':
            os.put('\\');
            os.put(ch);
            break;
        case '\b':
            os.put('\\');
            os.put('b');
            break;
        case '\f':
            os.put('\\');
            os.put('f');
            break;
        case '\n':
            os.put('\\');
            os.put('n');
            break;
        case '\r':
            os.put('\\');
            os.put('r');
            break;
        case '\t':
            os.put('\\');
            os.put('t');
            break;
        default:
            if (static_cast<uint8_t>(ch) <= 0x1F)
            {
                os.put('\\');
                os.put('u');
                os.put('0');
                os.put('0');
                os.put(HEX[static_cast<uint8_t>(static_cast<uint8_t>(ch) >> 4U) & 0xFU]);
                os.put(HEX[static_cast<uint8_t>(ch) & 0xFU]);
            }
            else
            {
                os.put(ch);
            }
            break;
        }
    }
    os.put('"');
}

} // namespace zserio
