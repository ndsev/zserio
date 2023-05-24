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
     * Empty constructor.
     */
    BasicJsonDecoder() :
            AllocatorHolder<ALLOC>(ALLOC())
    {}

    /**
     * Constructor from given allocator.
     *
     * \param allocator Allocator to use.
     */
    explicit BasicJsonDecoder(const ALLOC& allocator) :
            AllocatorHolder<ALLOC>(allocator)
    {}

    /**
     * Decodes the JSON value from the input.
     *
     * \param input Input to decode from.
     *
     * \return Decoder result.
     */
    DecoderResult decodeValue(StringView input)
    {
        if (input.empty())
            return DecoderResult(0, get_allocator());

        switch (input[0])
        {
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
            if (input.size() > 1 && input[1] == 'I')
                return decodeLiteral(input, "-Infinity"_sv, -static_cast<double>(INFINITY));
            return decodeNumber(input);
        default:
            return decodeNumber(input);
        }
    }

private:
    template <typename T>
    DecoderResult decodeLiteral(StringView input, StringView literal, T&& value);
    DecoderResult decodeString(StringView input);
    static bool decodeUnicodeEscape(StringView input, StringView::const_iterator& inputIt,
            string<ALLOC>& value);
    static char decodeHex(char ch);
    size_t checkNumber(StringView input, bool& isDouble, bool& isSigned);
    DecoderResult decodeNumber(StringView input);
    DecoderResult decodeSigned(StringView input);
    DecoderResult decodeUnsigned(StringView input);
    DecoderResult decodeDouble(StringView input, size_t numChars);
};

template <typename ALLOC>
template <typename T>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeLiteral(
        StringView input, StringView literal, T&& value)
{
    StringView::const_iterator literalIt = literal.begin();
    StringView::const_iterator inputIt = input.begin();
    while (inputIt != input.end() && literalIt != literal.end())
    {
        if (*inputIt++ != *literalIt++)
        {
            // failure, not decoded
            return DecoderResult(static_cast<size_t>(inputIt - input.begin()), get_allocator());
        }
    }

    if (literalIt != literal.end())
    {
        // short input, not decoded
        return DecoderResult(input.size(), get_allocator());
    }

    // success
    return DecoderResult(literal.size(), std::forward<T>(value), get_allocator());
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeString(StringView input)
{
    StringView::const_iterator inputIt = input.begin() + 1; // we know that at the beginning is '"'
    string<ALLOC> value(get_allocator());

    while (inputIt != input.end())
    {
        if (*inputIt == '\\')
        {
            ++inputIt;
            if (inputIt == input.end())
            {
                // wrong escape, not decoded
                return DecoderResult(static_cast<size_t>(inputIt - input.begin()), get_allocator());
            }

            char nextChar = *inputIt;
            switch (nextChar)
            {
            case '\\':
            case '"':
                value.push_back(nextChar);
                ++inputIt;
                break;
            case 'b':
                value.push_back('\b');
                ++inputIt;
                break;
            case 'f':
                value.push_back('\f');
                ++inputIt;
                break;
            case 'n':
                value.push_back('\n');
                ++inputIt;
                break;
            case 'r':
                value.push_back('\r');
                ++inputIt;
                break;
            case 't':
                value.push_back('\t');
                ++inputIt;
                break;
            case 'u': // unicode escape
                {
                    ++inputIt;
                    if (!decodeUnicodeEscape(input, inputIt, value))
                    {
                        // unsupported unicode escape, not decoded
                        return DecoderResult(static_cast<size_t>(inputIt - input.begin()), get_allocator());
                    }
                    break;
                }
            default:
                ++inputIt;
                // unknown escape, not decoded
                return DecoderResult(static_cast<size_t>(inputIt - input.begin()), get_allocator());
            }
        }
        else if (*inputIt == '"')
        {
            ++inputIt;
            // successfully decoded
            return DecoderResult(static_cast<size_t>(inputIt - input.begin()), std::move(value),
                    get_allocator());
        }
        else
        {
            value.push_back(*inputIt++);
        }
    }

    // unterminated string, not decoded
    return DecoderResult(input.size(), get_allocator());
}

template <typename ALLOC>
bool BasicJsonDecoder<ALLOC>::decodeUnicodeEscape(StringView input, StringView::const_iterator& inputIt,
        string<ALLOC>& value)
{
    // TODO[Mi-L@]: Simplified just to decode what zserio encodes, for complex solution we could use
    //              std::wstring_convert but it's deprecated in C++17.
    if (inputIt == input.end() || *inputIt++ != '0')
        return false;
    if (inputIt == input.end() || *inputIt++ != '0')
        return false;

    if (inputIt == input.end())
        return false;
    const char ch1 = decodeHex(*inputIt++);
    if (ch1 == -1)
        return false;

    if (inputIt == input.end())
        return false;
    const char ch2 = decodeHex(*inputIt++);
    if (ch2 == -1)
        return false;

    value.push_back(static_cast<char>((static_cast<uint32_t>(ch1) << 4U) | static_cast<uint32_t>(ch2)));
    return true;
}

template <typename ALLOC>
char BasicJsonDecoder<ALLOC>::decodeHex(char ch)
{
    if (ch >= '0' && ch <= '9')
        return static_cast<char>(ch - '0');
    else if (ch >= 'a' && ch <= 'f')
        return static_cast<char>(ch - 'a' + 10);
    else if (ch >= 'A' && ch <= 'F')
        return static_cast<char>(ch - 'A' + 10);

    return -1;
}

template <typename ALLOC>
size_t BasicJsonDecoder<ALLOC>::checkNumber(StringView input, bool& isDouble, bool& isSigned)
{
    StringView::const_iterator inputIt = input.begin();
    bool acceptExpSign = false;
    isDouble = false;

    if (*inputIt == '-') // we know that at the beginning is at least one character
    {
        ++inputIt;
        isSigned = true;
    }
    else
    {
        isSigned = false;
    }

    while (inputIt != input.end())
    {
        if (acceptExpSign)
        {
            acceptExpSign = false;
            if (*inputIt == '+' || *inputIt == '-')
            {
                ++inputIt;
                continue;
            }
        }
        if (*inputIt >= '0' && *inputIt <= '9')
        {
            ++inputIt;
            continue;
        }
        if (!isDouble && (*inputIt == '.' || *inputIt == 'e' || *inputIt == 'E'))
        {
            isDouble = true;
            if (*inputIt == 'e' || *inputIt == 'E')
                acceptExpSign = true;
            ++inputIt;
            continue;
        }

        break; // end of a number
    }

    const size_t numberLen = static_cast<size_t>(inputIt - input.begin());
    if (isSigned && numberLen == 1)
        return 0; // single minus is not a number

    return numberLen;
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeNumber(StringView input)
{
    bool isDouble = false;
    bool isSigned = false;
    const size_t numChars = checkNumber(input, isDouble, isSigned);
    if (numChars == 0)
        return DecoderResult(1, get_allocator());

    // for decodeSigned and decodeUnsigned, we know that all numChars will be processed because checkNumber
    // already checked this
    if (isDouble)
        return decodeDouble(input, numChars);
    else if (isSigned)
        return decodeSigned(input);
    else
        return decodeUnsigned(input);
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeSigned(StringView input)
{
    char* pEnd = nullptr;
    errno = 0; // no library function sets its value back to zero once changed
    const int64_t value = std::strtoll(input.begin(), &pEnd, 10);

    const bool overflow = (errno == ERANGE);

    return DecoderResult(static_cast<size_t>(pEnd - input.begin()), value, overflow, get_allocator());
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeUnsigned(StringView input)
{
    char* pEnd = nullptr;
    errno = 0; // no library function sets its value back to zero once changed
    const uint64_t value = std::strtoull(input.begin(), &pEnd, 10);

    const bool overflow = (errno == ERANGE);

    return DecoderResult(static_cast<size_t>(pEnd - input.begin()), value, overflow, get_allocator());
}

template <typename ALLOC>
typename BasicJsonDecoder<ALLOC>::DecoderResult BasicJsonDecoder<ALLOC>::decodeDouble(
        StringView input, size_t numChars)
{
    char* pEnd = nullptr;
    const double value = std::strtod(input.begin(), &pEnd);
    if (static_cast<size_t>(pEnd - input.begin()) != numChars)
        return DecoderResult(numChars, get_allocator());

    return DecoderResult(numChars, value, get_allocator());
}

} // namespace zserio

#endif // ZSERIO_JSON_DECODER_H_INC
