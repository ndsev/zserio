#ifndef ZSERIO_JSON_DECODER_H_INC
#define ZSERIO_JSON_DECODER_H_INC

#include <utility>
#include <cerrno>
#include <cmath>
#include <cstdlib>
#include <cstring>

#include "zserio/AllocatorHolder.h"
#include "zserio/AnyHolder.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/String.h"

namespace zserio
{

template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonDecoder : public AllocatorHolder<ALLOC>
{
public:
    using AllocatorHolder<ALLOC>::get_allocator;

    BasicJsonDecoder(const ALLOC& allocator = ALLOC()) :
            AllocatorHolder<ALLOC>(allocator)
    {}

    AnyHolder<ALLOC> decodeValue(const char* input, size_t& numRead)
    {
        switch (input[0])
        {
        case '\0':
            return AnyHolder<ALLOC>(get_allocator());
        case 'n':
            return decodeNull(input, numRead);
        case 't':
        case 'f':
            return decodeBool(input, numRead);
        case 'N':
            return decodeNan(input, numRead);
        case 'I':
            return decodeInfinity(input, numRead);
        case '"':
            return decodeString(input, numRead);
        case '-':
            if (input[1] == 'I') // note that input is zero-terminated, thus 1 still must be a valid index
                return decodeInfinity(input, numRead, true);
            return decodeSigned(input, numRead);
        default:
            return decodeUnsigned(input, numRead);
        }
    }

private:
    AnyHolder<ALLOC> decodeNull(const char* input, size_t& numRead);
    AnyHolder<ALLOC> decodeBool(const char* input, size_t& numRead);
    AnyHolder<ALLOC> decodeNan(const char* input, size_t& numRead);
    AnyHolder<ALLOC> decodeInfinity(const char* input, size_t& numRead, bool isNegative=false);
    AnyHolder<ALLOC> decodeString(const char* input, size_t& numRead);
    static bool decodeUnicodeEscape(const char* input, string<ALLOC>& value);
    static char decodeHex(char ch);
    AnyHolder<ALLOC> decodeSigned(const char* input, size_t& numRead);
    AnyHolder<ALLOC> decodeUnsigned(const char* input, size_t& numRead);
    AnyHolder<ALLOC> decodeDouble(const char* input, size_t& numRead);
};

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeNull(const char* input, size_t& numRead)
{
    if (strncmp(input, "null", 4) == 0)
    {
        numRead = 4;
        return AnyHolder<ALLOC>(nullptr, get_allocator());
    }

    return AnyHolder<ALLOC>(get_allocator());
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeBool(const char* input, size_t& numRead)
{
    if (strncmp(input, "true", 4) == 0)
    {
        numRead = 4;
        return AnyHolder<ALLOC>(true, get_allocator());
    }
    else if (strncmp(input, "false",  5) == 0)
    {
        numRead = 5;
        return AnyHolder<ALLOC>(false, get_allocator());
    }

    return AnyHolder<ALLOC>(get_allocator());
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeNan(const char* input, size_t& numRead)
{
    if (strncmp(input, "NaN", 3) == 0)
    {
        numRead = 3;
        return AnyHolder<ALLOC>(static_cast<double>(NAN), get_allocator());
    }

    return AnyHolder<ALLOC>(get_allocator());
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeInfinity(const char* input, size_t& numRead, bool isNegative)
{
    if (isNegative)
    {
        if (strncmp(input, "-Infinity", 9) == 0)
        {
            numRead = 9;
            return AnyHolder<ALLOC>(-static_cast<double>(INFINITY), get_allocator());
        }
    }
    else if (strncmp(input, "Infinity", 8) == 0)
    {
        numRead = 8;
        return AnyHolder<ALLOC>(static_cast<double>(INFINITY), get_allocator());
    }

    return AnyHolder<ALLOC>(get_allocator());
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeString(const char* input, size_t& numRead)
{
    const char* pInput = input;
    if (*pInput++ != '"')
        return AnyHolder<ALLOC>();

    string<ALLOC> value;
    while (true)
    {
        if (*pInput == '\\')
        {
            char ch = *(++pInput);
            switch (ch)
            {
            case '\\':
            case '"':
                value.push_back(ch);
                pInput++;
                break;
            case 'b':
                value.push_back('\b');
                pInput++;
                break;
            case 'f':
                value.push_back('\f');
                pInput++;
                break;
            case 'n':
                value.push_back('\n');
                pInput++;
                break;
            case 'r':
                value.push_back('\r');
                pInput++;
                break;
            case 't':
                value.push_back('\t');
                pInput++;
                break;
            case 'u': // unicode escape
                if (!decodeUnicodeEscape(++pInput, value))
                    return AnyHolder<ALLOC>(); // unsupported unicode escape
                pInput += 4;
                break;
            default:
                // unknown character, not decoded...
                return AnyHolder<ALLOC>();
            }
        }
        else if (*pInput == '"')
        {
            pInput++;
            break;
        }
        else if (*pInput == '\0')
        {
            return AnyHolder<ALLOC>(); // unterminated string, not decoded...
        }
        else
        {
            value.push_back(*pInput++);
        }
    }

    numRead = static_cast<size_t>(pInput - input);
    return AnyHolder<ALLOC>(std::move(value));
}

template <typename ALLOC>
bool BasicJsonDecoder<ALLOC>::decodeUnicodeEscape(const char* input, string<ALLOC>& value)
{
    if (*input++ != '0' || *input++ != '0')
        return false;

    char ch1 = decodeHex(*input++);
    if (ch1 == - 1)
        return false;

    char ch2 = decodeHex(*input++);
    if (ch2 == -1)
        return false;

    value.push_back(static_cast<char>(ch1 << 4) | ch2);
    return true;
}

template <typename ALLOC>
char BasicJsonDecoder<ALLOC>::decodeHex(char ch)
{
    if (ch >= '0' && ch <= '9')
        return ch - '0';
    else if (ch >= 'a' && ch <= 'f')
        return ch - 'a' + 10;
    else if (ch >= 'A' && ch <= 'F')
        return ch - 'A' + 10;

    return -1;
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeSigned(const char* input, size_t& numRead)
{
    // TODO[Mi-L@]: Do we care about size overflow (INT_MIN / INT_MAX)?

    char* pEnd = nullptr;
    const int64_t value = std::strtoll(input, &pEnd, 10);
    numRead = static_cast<size_t>(pEnd - input);
    if (numRead == 0)
        return AnyHolder<ALLOC>(get_allocator());
    if (*pEnd == '.' || *pEnd == 'e' || *pEnd == 'E')
        return decodeDouble(input, numRead);

    return AnyHolder<ALLOC>(value, get_allocator());
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeUnsigned(const char* input, size_t& numRead)
{
    // TODO[Mi-L@]: Do we care about size overflow (UINT_MAX)?

    char* pEnd = nullptr;
    const uint64_t value = std::strtoull(input, &pEnd, 10);
    numRead = static_cast<size_t>(pEnd - input);
    if (numRead == 0)
        return AnyHolder<ALLOC>(get_allocator());
    if (*pEnd == '.' || *pEnd == 'e' || *pEnd == 'E')
        return decodeDouble(input, numRead);

    return AnyHolder<ALLOC>(value, get_allocator());
}

template <typename ALLOC>
AnyHolder<ALLOC> BasicJsonDecoder<ALLOC>::decodeDouble(const char* input, size_t& numRead)
{
    char* pEnd = nullptr;
    const double value = std::strtod(input, &pEnd);
    numRead = static_cast<size_t>(pEnd - input);
    if (numRead == 0)
        return AnyHolder<ALLOC>(get_allocator());

    return AnyHolder<ALLOC>(value, get_allocator());
}

using JsonDecoder = BasicJsonDecoder<>;

} // namespace zserio

#endif // ZSERIO_JSON_DECODER_H_INC
