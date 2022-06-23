package zserio.runtime.json;

/**
 * Tokens used by Json Tokenizer.
 */
enum JsonToken
{
    BEGIN_OF_FILE,
    END_OF_FILE,
    BEGIN_OBJECT,
    END_OBJECT,
    BEGIN_ARRAY,
    END_ARRAY,
    KEY_SEPARATOR,
    ITEM_SEPARATOR,
    VALUE
}
