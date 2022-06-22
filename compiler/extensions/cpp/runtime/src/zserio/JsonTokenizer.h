#ifndef ZSERIO_JSON_TOKENIZER_H_INC
#define ZSERIO_JSON_TOKENIZER_H_INC

#include <memory>
#include <istream>

#include "zserio/AnyHolder.h"
#include "zserio/JsonDecoder.h"
#include "zserio/Types.h"

namespace zserio
{

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

const char* jsonTokenName(JsonToken token);

template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonTokenizer
{
public:
    BasicJsonTokenizer(std::istream& in, const ALLOC& allocator):
        m_in(in), m_decoder(allocator), m_content(allocator), m_value(allocator)
    {
        std::getline(m_in, m_content);
    }

    JsonToken next();

    JsonToken getToken() const { return m_token; }
    const AnyHolder<ALLOC>& getValue() const { return m_value; }
    size_t getLine() const { return m_lineNumber; }

private:
    bool decodeNext();
    bool decodeValue();
    bool checkEof();
    void skipWs();

    std::istream& m_in;
    BasicJsonDecoder<ALLOC> m_decoder;
    string<ALLOC> m_content;
    size_t m_lineNumber = 1;
    size_t m_pos = 0;
    JsonToken m_token = JsonToken::BEGIN_OF_FILE;
    AnyHolder<ALLOC> m_value;
};

template <typename ALLOC>
JsonToken BasicJsonTokenizer<ALLOC>::next()
{
    size_t currentLineNumber = m_lineNumber;
    while (!decodeNext())
    {
        string<ALLOC> newContent(m_content.get_allocator());
        if (!std::getline(m_in, newContent))
        {
            if (m_token == JsonToken::END_OF_FILE)
                return m_token;
            throw CppRuntimeException("JsonParser line ") + currentLineNumber +
                    ": Unknown token: '" + m_value.template get<char>() +
                    "' (" + jsonTokenName(m_token) + ")!";
        }
        m_lineNumber += 1;
        m_content = m_content.substr(m_pos) + newContent;
        m_pos = 0;
    }

    return m_token;
}

template <typename ALLOC>
bool BasicJsonTokenizer<ALLOC>::decodeNext()
{
    if (checkEof())
        return false;
    skipWs();
    if (checkEof())
        return false;

    char nextChar = m_content[m_pos];
    switch (nextChar)
    {
    case '{':
        m_token = JsonToken::BEGIN_OBJECT;
        break;
    case '}':
        m_token = JsonToken::END_OBJECT;
        break;
    case '[':
        m_token = JsonToken::BEGIN_ARRAY;
        break;
    case ']':
        m_token = JsonToken::END_ARRAY;
        break;
    case ':':
        m_token = JsonToken::KEY_SEPARATOR;
        break;
    case ',':
        m_token = JsonToken::ITEM_SEPARATOR;
        break;
    default:
        return decodeValue();
    }

    ++m_pos;
    m_value.set(nextChar);
    return true;
}

template <typename ALLOC>
bool BasicJsonTokenizer<ALLOC>::decodeValue()
{
    size_t numRead = 0;
    m_value = m_decoder.decodeValue(m_content.c_str() + m_pos, numRead);

    if (m_value.hasValue())
    {
        m_pos += numRead;
        m_token = JsonToken::VALUE;
        return true;
    }
    else
    {
        m_token = JsonToken::UNKNOWN;
        m_value.set(m_content[m_pos]);
        return false;
    }
}

template <typename ALLOC>
bool BasicJsonTokenizer<ALLOC>::checkEof()
{
    if (m_pos >= m_content.size())
    {
        m_token = JsonToken::END_OF_FILE;
        m_value.reset();
        return true;
    }

    return false;
}

template <typename ALLOC>
void BasicJsonTokenizer<ALLOC>::skipWs()
{
    while (m_pos < m_content.size() && (
            m_content[m_pos] == ' ' ||
            m_content[m_pos] == '\t' ||
            m_content[m_pos] == '\n' ||
            m_content[m_pos] == '\r'))
    {
        ++m_pos;
    }
}

using JsonTokenizer = BasicJsonTokenizer<>;

} // namespace zserio

#endif // ZSERIO_JSON_TOKENIZER_H_INC
