package reserved_keywords.zserio_keyword_field_name_error;

struct InvalidFieldNameError
{
    // varint is a reserved keyword!
    int32 varint;
};
