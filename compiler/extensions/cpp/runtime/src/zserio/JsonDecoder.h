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
#include "zserio/StringView.h"

namespace zserio
{

/**
 * JSON value decoder.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonDecoder : public AllocatorHolder<ALLOC>
{
public:
    using AllocatorHolder<ALLOC>::get_allocator;

    /**
     * Decoder result value.
     */
    struct DecoderResult
    {
        /**
         * Constructor used for decoder failure.
         *
         * \param numRead Number of processed characters.
         * \param allocator Allocator to use.
         */
        DecoderResult(size_t numRead, const ALLOC& allocator) :
                numReadChars(numRead), value(allocator), integerOverflow(false)
        {}

        /**
         * Constructor for decoder success.
         *
         * \param numRead Number of processed characters.
         * \param decodedValue Value decoded from JSON stream.
         * \param allocator Allocator to use.
         */
        template <typename T>
        DecoderResult(size_t numRead, T&& decodedValue, const ALLOC& allocator) :
                numReadChars(numRead), value(std::forward<T>(decodedValue), allocator), integerOverflow(false)
        {}

        /**
         * Constructor used for integer decoder.
         *
         * \param numRead Number of processed characters.
         * \param decodedValue Value decoded from JSON stream.
         * \param overflow True in case of integer overflow.
         * \param allocator Allocator to use.
         */
        template <typename T>
        DecoderResult(size_t numRead, T&& decodedValue, bool overflow, const ALLOC& allocator) :
                numReadChars(numRead), value(createValue(decodedValue, overflow, allocator)),
                        integerOverflow(overflow)
        {}

        size_t numReadChars; /**< Number of processed characters. */
        AnyHolder<ALLOC> value; /**< Decoded value. Empty on failure. */
        bool integerOverflow; /**< True if decoded value was bigger than UINT64_MAX or was not in interval
                                   <INT64_MIN, INT64_MAX>. */

    private:
        template <typename T>
        AnyHolder<ALLOC> createValue(T&& decodedValue, bool overflow, const ALLOC& allocator)
        {
            return overflow ? AnyHolder<ALLOC>(allocator) :
                    AnyHolder<ALLOC>(std::forward<T>(decodedValue), allocator);
        }
    };

    /**
     * Constructor.
     *
     * \param allocator Allocator to use.
     */
    BasicJsonDecoder(const ALLOC& allocator = ALLOC()) :
            AllocatorHolder<ALLOC>(allocator)
    {}

    /**
     * Decodes the JSON value from the input.
     *
     * \param input Input to decode from.
     *
     * \return Decoder result.
     */
    DecoderResult decodeValue(const char* input)
    {
        switch (input[0])
        {
        case '\0':
            return DecoderResult(0, get_allocator());
        case 'n':
            return decodeLiteral(input, "null"_sv, nullptr);
        case 't':
            return decodeLiteral(input, "true"_sv, true);
        case 'f':
            return decodeLiteral(input, "false"_sv, false);
        case 'N':
            return decodeLiteral(input, "NaN"_sv, static_cast<double>(NAN));
        case 'I':
            return decodeLiteral(input, "Infinity"_sv, static_cast<double>(INFINITY));
        case '"':
            return decodeString(input);
        case '-':
            if (input[1] == 'I') // note that input is zero-terminated, thus 1 still must be a valid index
                return decodeLiteral(input, "-Infinity"_sv, -static_cast<double>(INFINITY));
            return decodeNumber(input);
        default:
            return decodeNumber(input);
        }
    }

private:
    template <typename T>
    DecoderResult decodeLiteral(const char* input, StringView literal, T&& value);
    DecoderResult decodeString(const char* input);
    static bool decodeUnicodeEscape(const char*& input, string<ALLOC>& value);
    static char decodeHex(char ch);
    size_t checkNumber(const char* input, bool& isDouble);
    DecoderResult decodeNumber(const char* input);
    DecoderResult decodeSigned(const char* input, size_t numChars);
    DecoderResult decodeUnsigned(const char* input, size_t numChars);
    DecoderResult decodeDouble(const char* input, size_t numChars);
};

template <typename ALLOC>
template <typename T>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeLiteral(
        const char* input, StringView literal, T&& value)
{
    if (strncmp(input, literal.data(), literal.size()) == 0)
        return DecoderResult(literal.size(), std::forward<T>(value), get_allocator());

    const size_t numReadChars = strnlen(input, literal.size());
    return DecoderResult(numReadChars, get_allocator());
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeString(const char* input)
{
    const char* pInput = input + 1; // we know that at the beginning is '"'
    string<ALLOC> value(get_allocator());

    while (true)
    {
        if (*pInput == '\\')
        {
            char nextChar = *(++pInput);
            switch (nextChar)
            {
            case '\\':
            case '"':
                value.push_back(nextChar);
                ++pInput;
                break;
            case 'b':
                value.push_back('\b');
                ++pInput;
                break;
            case 'f':
                value.push_back('\f');
                ++pInput;
                break;
            case 'n':
                value.push_back('\n');
                ++pInput;
                break;
            case 'r':
                value.push_back('\r');
                ++pInput;
                break;
            case 't':
                value.push_back('\t');
                ++pInput;
                break;
            case 'u': // unicode escape
                {
                    ++pInput;
                    const size_t unicodeEscapeLen = 4;
                    const size_t numReadChars = strnlen(pInput, unicodeEscapeLen);
                    if (numReadChars < unicodeEscapeLen)
                        return DecoderResult(static_cast<size_t>(pInput + numReadChars - input), get_allocator());
                    if (!decodeUnicodeEscape(pInput, value))
                    {
                        // unsupported unicode escape
                        return DecoderResult(static_cast<size_t>(pInput - input), get_allocator());
                    }
                    break;
                }
            case '\0': // in case of terminating zero do not consume it
                return DecoderResult(static_cast<size_t>(pInput - input), get_allocator());
            default:
                ++pInput;
                // unknown character, not decoded...
                return DecoderResult(static_cast<size_t>(pInput - input), get_allocator());
            }
        }
        else if (*pInput == '"')
        {
            ++pInput;
            break;
        }
        else if (*pInput == '\0')
        {
            // unterminated string, not decoded...
            return DecoderResult(static_cast<size_t>(pInput - input), get_allocator());
        }
        else
        {
            value.push_back(*pInput++);
        }
    }

    return DecoderResult(static_cast<size_t>(pInput - input), std::move(value), get_allocator());
}

template <typename ALLOC>
bool BasicJsonDecoder<ALLOC>::decodeUnicodeEscape(const char*& pInput, string<ALLOC>& value)
{
    // TODO[Mi-L@]: Simplified just to decode what zserio encodes, for complex solution we could use
    //              std::wstring_convert but it's deprecated in C++17.
    if (*pInput++ != '0' || *pInput++ != '0')
        return false;

    const char ch1 = decodeHex(*pInput++);
    if (ch1 == -1)
        return false;

    const char ch2 = decodeHex(*pInput++);
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
size_t BasicJsonDecoder<ALLOC>::checkNumber(const char* input, bool& isDouble)
{
    const char* pInput = input;
    bool acceptSign = false;

    char nextChar = *pInput;
    if (nextChar == '-')
        nextChar = *++pInput;

    while (nextChar != '\0')
    {
        if (acceptSign)
        {
            acceptSign = false;
            if (nextChar == '+' || nextChar == '-')
            {
                nextChar = *++pInput;
                continue;
            }
        }
        if (nextChar >= '0' && nextChar <= '9')
        {
            nextChar = *++pInput;
            continue;
        }
        if (isDouble == false && (nextChar == '.' || nextChar == 'e' || nextChar == 'E'))
        {
            isDouble = true;
            if (nextChar == 'e' || nextChar == 'E')
                acceptSign = true;
            nextChar = *++pInput;
            continue;
        }

        break; // end of a number
    }

    return static_cast<size_t>(pInput - input);
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeNumber(const char* input)
{
    bool isDouble = false;
    const size_t numChars = checkNumber(input, isDouble);
    if (numChars == 0)
        return DecoderResult(1, get_allocator());

    if (isDouble)
        return decodeDouble(input, numChars);
    else if (*input == '-')
        return decodeSigned(input, numChars);
    else
        return decodeUnsigned(input, numChars);
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeSigned(
        const char* input, size_t numChars)
{
    char* pEnd = nullptr;
    errno = 0; // no library function sets its value back to zero once changed
    const int64_t value = std::strtoll(input, &pEnd, 10);
    if (static_cast<size_t>(pEnd - input) != numChars)
        return DecoderResult(numChars, get_allocator());

    const bool overflow = (errno == ERANGE);

    return DecoderResult(numChars, value, overflow, get_allocator());
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeUnsigned(
        const char* input, size_t numChars)
{
    char* pEnd = nullptr;
    errno = 0; // no library function sets its value back to zero once changed
    const uint64_t value = std::strtoull(input, &pEnd, 10);
    if (static_cast<size_t>(pEnd - input) != numChars)
        return DecoderResult(numChars, get_allocator());

    const bool overflow = (errno == ERANGE);

    return DecoderResult(numChars, value, overflow, get_allocator());
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeDouble(
        const char* input, size_t numChars)
{
    char* pEnd = nullptr;
    const double value = std::strtod(input, &pEnd);
    if (static_cast<size_t>(pEnd - input) != numChars)
        return DecoderResult(numChars, get_allocator());

    return DecoderResult(numChars, value, get_allocator());
}

} // namespace zserio

#endif // ZSERIO_JSON_DECODER_H_INC
