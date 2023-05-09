#ifndef ZSERIO_JSON_TOKENIZER_H_INC
#define ZSERIO_JSON_TOKENIZER_H_INC

#include <memory>
#include <istream>
#include <array>

#include "zserio/AnyHolder.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/JsonDecoder.h"
#include "zserio/Types.h"

namespace zserio
{

/**
 * Tokens used by Json Tokenizer.
 */
enum class JsonToken : int8_t
{
    UNKNOWN = -1,
    BEGIN_OF_FILE,
    END_OF_FILE,
    BEGIN_OBJECT,
    END_OBJECT,
    BEGIN_ARRAY,
    END_ARRAY,
    KEY_SEPARATOR,
    ITEM_SEPARATOR,
    VALUE
};


/**
 * Exception used to distinguish exceptions from the JsonParser.
 */
class JsonParserException : public CppRuntimeException
{
public:
    using CppRuntimeException::CppRuntimeException;
};

/**
 * Allows to append JsonToken to CppRuntimeException.
 *
 * \param exception Exception to modify.
 * \param token JSON Token to append.
 *
 * \return Reference to the exception to allow operator chaining.
 */
CppRuntimeException& operator<<(CppRuntimeException& exception, JsonToken token);

/**
 * Json Tokenizer used by Json Parser.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonTokenizer
{
public:
    /**
     * Constructor.
     *
     * \param in Input stream to tokenize.
     * \param allocator Allocator to use.
     */
    BasicJsonTokenizer(std::istream& in, const ALLOC& allocator):
            m_buffer(), m_in(in), m_decoder(allocator), m_decoderResult(0, allocator),
            m_content(readContent(allocator)), m_value(allocator)
    {
        m_token = m_content.empty() ? JsonToken::END_OF_FILE : JsonToken::BEGIN_OF_FILE;
    }

    /**
     * Move to the next token.
     *
     * \return Next token.
     * \throw JsonParserException In case that tokenizing fails - i.e. unknown token is reached.
     */
    JsonToken next();

    /**
     * Gets current token.
     *
     * \return Current token.
     */
    JsonToken getToken() const { return m_token; }

    /**
     * Gets current value.
     *
     * Any holder can be either unset - i.e. beginning or end of the input,
     * or it can hold one of the types defined in IObserver::visitValue.
     *
     * \return Current value as an AnyHolder.
     */
    const AnyHolder<ALLOC>& getValue() const { return m_value; }

    /**
     * Gets line number of the current token.
     *
     * \return Line number.
     */
    size_t getLine() const { return m_lineNumber; }

    /**
     * Gets column number of the current token.
     *
     * \return Column number.
     */
    size_t getColumn() const { return m_tokenColumnNumber; }

private:
    string<ALLOC> readContent(const ALLOC& allocator);

    bool decodeNext();
    bool skipWhitespaces();

    template <typename T>
    void setToken(JsonToken token, T&& value);
    void setToken(JsonToken token, AnyHolder<ALLOC>&& value);
    void setToken(JsonToken token);
    void setPosition(size_t newPos, size_t newColumnNumber);
    void setTokenValue();

    static constexpr size_t BUFFER_SIZE = 64 * 1024;
    std::array<char, BUFFER_SIZE> m_buffer;

    std::istream& m_in;
    BasicJsonDecoder<ALLOC> m_decoder;
    typename BasicJsonDecoder<ALLOC>::DecoderResult m_decoderResult;
    string<ALLOC> m_content;
    size_t m_lineNumber = 1;
    size_t m_columnNumber = 1;
    size_t m_tokenColumnNumber = 1;
    size_t m_pos = 0;
    JsonToken m_token;
    AnyHolder<ALLOC> m_value;
};

template <typename ALLOC>
JsonToken BasicJsonTokenizer<ALLOC>::next()
{
    while (!decodeNext())
    {
        string<ALLOC> newContent = readContent(m_content.get_allocator());
        if (newContent.empty())
        {
            if (m_token == JsonToken::END_OF_FILE)
            {
                m_tokenColumnNumber = m_columnNumber;
            }
            else
            {
                // stream is finished but last token is not EOF => value must be at the end
                setTokenValue();
            }

            return m_token;
        }

        m_content = m_content.substr(m_pos) + newContent;
        m_pos = 0;
    }

    return m_token;
}

template <typename ALLOC>
string<ALLOC> BasicJsonTokenizer<ALLOC>::readContent(const ALLOC& allocator)
{
    const size_t count = static_cast<size_t>(m_in.rdbuf()->sgetn(m_buffer.data(), BUFFER_SIZE));
    return string<ALLOC>(m_buffer.data(), count, allocator);
}

template <typename ALLOC>
bool BasicJsonTokenizer<ALLOC>::decodeNext()
{
    if (!skipWhitespaces())
        return false;

    m_tokenColumnNumber = m_columnNumber;

    const char nextChar = m_content[m_pos];
    switch (nextChar)
    {
    case '{':
        setToken(JsonToken::BEGIN_OBJECT, nextChar);
        setPosition(m_pos + 1, m_columnNumber + 1);
        break;
    case '}':
        setToken(JsonToken::END_OBJECT, nextChar);
        setPosition(m_pos + 1, m_columnNumber + 1);
        break;
    case '[':
        setToken(JsonToken::BEGIN_ARRAY, nextChar);
        setPosition(m_pos + 1, m_columnNumber + 1);
        break;
    case ']':
        setToken(JsonToken::END_ARRAY, nextChar);
        setPosition(m_pos + 1, m_columnNumber + 1);
        break;
    case ':':
        setToken(JsonToken::KEY_SEPARATOR, nextChar);
        setPosition(m_pos + 1, m_columnNumber + 1);
        break;
    case ',':
        setToken(JsonToken::ITEM_SEPARATOR, nextChar);
        setPosition(m_pos + 1, m_columnNumber + 1);
        break;
    default:
        m_decoderResult = m_decoder.decodeValue(StringView(m_content.data()).substr(m_pos));
        if (m_pos + m_decoderResult.numReadChars >= m_content.size())
            return false; // we are at the end of chunk => read more

        setTokenValue();
        break;
    }

    return true;
}

template <typename ALLOC>
bool BasicJsonTokenizer<ALLOC>::skipWhitespaces()
{
    while (true)
    {
        if (m_pos >= m_content.size())
        {
            setToken(JsonToken::END_OF_FILE);
            return false;
        }

        const char nextChar = m_content[m_pos];
        switch (nextChar)
        {
        case ' ':
        case '\t':
            setPosition(m_pos + 1, m_columnNumber + 1);
            break;
        case '\n':
            m_lineNumber++;
            setPosition(m_pos + 1, 1);
            break;
        case '\r':
            if (m_pos + 1 >= m_content.size())
            {
                setToken(JsonToken::END_OF_FILE);
                return false;
            }
            m_lineNumber++;
            setPosition(m_pos + (m_content[m_pos + 1] == '\n' ? 2 : 1), 1);
            break;
        default:
            return true;
        }
    }
}

template <typename ALLOC>
template <typename T>
void BasicJsonTokenizer<ALLOC>::setToken(JsonToken token, T&& value)
{
    m_token = token;
    m_value.set(std::forward<T>(value));
}

template <typename ALLOC>
void BasicJsonTokenizer<ALLOC>::setToken(JsonToken token, AnyHolder<ALLOC>&& value)
{
    m_token = token;
    m_value = std::move(value);
}

template <typename ALLOC>
void BasicJsonTokenizer<ALLOC>::setToken(JsonToken token)
{
    m_token = token;
    m_value.reset();
}

template <typename ALLOC>
void BasicJsonTokenizer<ALLOC>::setPosition(size_t newPos, size_t newColumnNumber)
{
    m_pos = newPos;
    m_columnNumber = newColumnNumber;
}

template <typename ALLOC>
void BasicJsonTokenizer<ALLOC>::setTokenValue()
{
    if (!m_decoderResult.value.hasValue())
    {
        throw JsonParserException("JsonTokenizer:") << m_lineNumber << ":" << m_tokenColumnNumber << ": " <<
                (m_decoderResult.integerOverflow
                        ? "Value is outside of the 64-bit integer range!"
                        : "Unknown token!");
    }

    setToken(JsonToken::VALUE, std::move(m_decoderResult.value));
    setPosition(m_pos + m_decoderResult.numReadChars, m_columnNumber + m_decoderResult.numReadChars);
}

} // namespace zserio

#endif // ZSERIO_JSON_TOKENIZER_H_INC
