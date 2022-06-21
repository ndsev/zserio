#include "zserio/JsonTokenizer.h"

namespace zserio
{

const char* jsonTokenName(JsonToken token)
{
    switch (token)
    {
    case JsonToken::UNKNOWN:
        return "UNKNOWN";
    case JsonToken::BEGIN_OF_FILE:
        return "BEGIN_OF_FILE";
    case JsonToken::END_OF_FILE:
        return "END_OF_FILE";
    case JsonToken::BEGIN_OBJECT:
        return "BEGIN_OBJECT";
    case JsonToken::END_OBJECT:
        return "END_OBJECT";
    case JsonToken::BEGIN_ARRAY:
        return "BEGIN_ARRAY";
    case JsonToken::END_ARRAY:
        return "END_ARRAY";
    case JsonToken::KEY_SEPARATOR:
        return "KEY_SEPARATOR";
    case JsonToken::ITEM_SEPARATOR:
        return "ITEM_SEPARATOR";
    default: // VALUE
        return "VALUE";
    }
}

} // namespace zserio
