#include <string>

#include "StringConvertUtil.h"

namespace zserio
{

static std::string convertIntToString(unsigned int value, bool isNegative)
{
    static const char DIGITS[] = "0001020304050607080910111213141516171819"
                                 "2021222324252627282930313233343536373839"
                                 "4041424344454647484950515253545556575859"
                                 "6061626364656667686970717273747576777879"
                                 "8081828384858687888990919293949596979899";
    const size_t BUFFER_SIZE = 24;
    char buffer[BUFFER_SIZE];
    char* bufferEnd = buffer + BUFFER_SIZE;

    while (value >= 100)
    {
        const unsigned int index = static_cast<unsigned int>((value % 100) * 2);
        value /= 100;
        *--bufferEnd = DIGITS[index + 1];
        *--bufferEnd = DIGITS[index];
    }

    if (value < 10)
    {
        *--bufferEnd = static_cast<char>('0' + value);
    }
    else
    {
        unsigned index = static_cast<unsigned>(value * 2);
        *--bufferEnd = DIGITS[index + 1];
        *--bufferEnd = DIGITS[index];
    }

    if (isNegative)
        *--bufferEnd = '-';

    return std::string(bufferEnd, buffer + BUFFER_SIZE - bufferEnd);
}

template<>
std::string convertToString<bool>(bool value)
{
    return value ? "true" : "false";
}

template<>
std::string convertToString<char>(char value)
{
    return convertToString<int>(static_cast<int>(value));
}

template<>
std::string convertToString<signed char>(signed char value)
{
    return convertToString<int>(static_cast<int>(value));
}

template<>
std::string convertToString<unsigned char>(unsigned char value)
{
    return convertToString<unsigned int>(static_cast<unsigned int>(value));
}

template<>
std::string convertToString<unsigned int>(unsigned int value)
{
    return convertIntToString(value, false);
}

template<>
std::string convertToString<int>(int value)
{
    unsigned int absValue = static_cast<unsigned int>(value);
    const bool isNegative = value < 0;
    if (isNegative)
        absValue = 0 - absValue;

    return convertIntToString(absValue, isNegative);
}

} // namespace zserio
