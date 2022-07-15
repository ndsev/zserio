#include "zserio/JsonTokenizer.h"

namespace zserio
{

CppRuntimeException& operator<<(CppRuntimeException& exception, JsonToken token)
{
    switch (token)
    {
    case JsonToken::UNKNOWN:
        return exception << "UNKNOWN";
    case JsonToken::BEGIN_OF_FILE:
        return exception << "BEGIN_OF_FILE";
    case JsonToken::END_OF_FILE:
        return exception << "END_OF_FILE";
    case JsonToken::BEGIN_OBJECT:
        return exception << "BEGIN_OBJECT";
    case JsonToken::END_OBJECT:
        return exception << "END_OBJECT";
    case JsonToken::BEGIN_ARRAY:
        return exception << "BEGIN_ARRAY";
    case JsonToken::END_ARRAY:
        return exception << "END_ARRAY";
    case JsonToken::KEY_SEPARATOR:
        return exception << "KEY_SEPARATOR";
    case JsonToken::ITEM_SEPARATOR:
        return exception << "ITEM_SEPARATOR";
    default: // VALUE
        return exception << "VALUE";
    }
}

} // namespace zserio
