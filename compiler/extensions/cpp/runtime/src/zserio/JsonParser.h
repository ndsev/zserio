#ifndef ZSERIO_JSON_PARSER_H_INC
#define ZSERIO_JSON_PARSER_H_INC

#include "zserio/AnyHolder.h"
#include "zserio/JsonDecoder.h"
#include "zserio/JsonTokenizer.h"
#include "zserio/Span.h"

namespace zserio
{

template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonParser
{
public:
    class IObserver
    {
    public:
        virtual ~IObserver() {}

        virtual void beginObject() = 0;
        virtual void endObject() = 0;

        virtual void beginArray() = 0;
        virtual void endArray() = 0;

        virtual void visitKey(StringView key) = 0;

        virtual void visitValue(std::nullptr_t nullValue) = 0;
        virtual void visitValue(bool boolValue) = 0;
        virtual void visitValue(int64_t intValue) = 0;
        virtual void visitValue(uint64_t uintValue) = 0;
        virtual void visitValue(double doubleValue) = 0;
        virtual void visitValue(StringView stringValue) = 0;
    };

    BasicJsonParser(std::istream& in, IObserver& observer, const ALLOC& allocator = ALLOC()) :
            m_tokenizer(in, allocator), m_observer(observer)
    {}

    bool parse()
    {
        if (m_tokenizer.getToken() == JsonToken::BEGIN_OF_FILE)
        {
            if (m_tokenizer.next() == JsonToken::END_OF_FILE)
                return true;
        }

        parseElement();

        return m_tokenizer.getToken() == JsonToken::END_OF_FILE;
    }

    size_t getLine() const
    {
        return m_tokenizer.getLine();
    }

private:
    void parseElement();
    void parseObject();
    void parseMembers();
    void parseMember();
    void parseArray();
    void parseElements();

    void parseValue();
    void visitValue() const;

    void checkToken(JsonToken token);
    void consumeToken(JsonToken token);
    void throwUnexpectedToken(Span<const JsonToken> expecting) const;

    static const std::array<JsonToken, 3> ELEMENT_TOKENS;

    JsonTokenizer m_tokenizer;
    IObserver& m_observer;
};

template <typename ALLOC>
const std::array<JsonToken, 3> BasicJsonParser<ALLOC>::ELEMENT_TOKENS = {
        JsonToken::BEGIN_OBJECT,
        JsonToken::BEGIN_ARRAY,
        JsonToken::VALUE
};

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseElement()
{
    JsonToken token = m_tokenizer.getToken();

    if (token == JsonToken::BEGIN_ARRAY)
        parseArray();
    else if (token == JsonToken::BEGIN_OBJECT)
        parseObject();
    else if (token == JsonToken::VALUE)
        parseValue();
    else
        throwUnexpectedToken(ELEMENT_TOKENS);
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseObject()
{
    consumeToken(JsonToken::BEGIN_OBJECT);
    m_observer.beginObject();

    if (m_tokenizer.getToken() == JsonToken::VALUE)
        parseMembers();

    consumeToken(JsonToken::END_OBJECT);
    m_observer.endObject();
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseMembers()
{
    parseMember();
    while (m_tokenizer.getToken() == JsonToken::ITEM_SEPARATOR)
    {
        m_tokenizer.next();
        parseMember();
    }
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseMember()
{
    checkToken(JsonToken::VALUE);
    const AnyHolder<ALLOC>& key = m_tokenizer.getValue();
    if (!key.template isType<string<ALLOC>>())
        throw CppRuntimeException("JsonParser line ") + getLine() + ": Key must be a string value!";
    m_observer.visitKey(key.template get<string<ALLOC>>());
    m_tokenizer.next();

    consumeToken(JsonToken::KEY_SEPARATOR);

    parseElement();
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseArray()
{
    consumeToken(JsonToken::BEGIN_ARRAY);
    m_observer.beginArray();

    if (std::find(ELEMENT_TOKENS.begin(), ELEMENT_TOKENS.end(), m_tokenizer.getToken()) != ELEMENT_TOKENS.end())
        parseElements();

    consumeToken(JsonToken::END_ARRAY);
    m_observer.endArray();
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseElements()
{
    parseElement();
    while (m_tokenizer.getToken() == JsonToken::ITEM_SEPARATOR)
    {
        m_tokenizer.next();
        parseElement();
    }
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::parseValue()
{
    visitValue();
    m_tokenizer.next();
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::visitValue() const
{
    const AnyHolder<ALLOC>& value = m_tokenizer.getValue();

    if (value.template isType<std::nullptr_t>())
    {
        m_observer.visitValue(nullptr);
    }
    else if (value.template isType<bool>())
    {
        m_observer.visitValue(value.template get<bool>());
    }
    else if (value.template isType<int64_t>())
    {
        m_observer.visitValue(value.template get<int64_t>());
    }
    else if (value.template isType<uint64_t>())
    {
        m_observer.visitValue(value.template get<uint64_t>());
    }
    else if (value.template isType<double>())
    {
        m_observer.visitValue(value.template get<double>());
    }
    else
    {
        m_observer.visitValue(value.template get<string<ALLOC>>());
    }
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::checkToken(JsonToken token)
{
    if (m_tokenizer.getToken() != token)
        throwUnexpectedToken({{token}});
}


template <typename ALLOC>
void BasicJsonParser<ALLOC>::consumeToken(JsonToken token)
{
    checkToken(token);
    m_tokenizer.next();
}

template <typename ALLOC>
void BasicJsonParser<ALLOC>::throwUnexpectedToken(Span<const JsonToken> expecting) const
{
    CppRuntimeException error("JsonParser line ");
    error + getLine() + ": unexpected token: " + jsonTokenName(m_tokenizer.getToken());
    if (expecting.size() == 1)
    {
        error + ", expecting " + jsonTokenName(expecting[0]) + "!";
    }
    else
    {
        error + ", expecting one of [";
        for (size_t i = 0; i < expecting.size(); ++i)
        {
            if (i > 0)
                error + ", ";
            error + jsonTokenName(expecting[i]);
        }
        error + "]!";
    }
    throw error;
}

using JsonParser = BasicJsonParser<>;

} // namespace

#endif // ZSERIO_JSON_PARSER_H_INC
