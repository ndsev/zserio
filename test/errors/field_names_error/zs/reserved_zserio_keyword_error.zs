package reserved_zserio_keyword_error;

struct InvalidFieldNameError
{
    // varint is a reserved keyword!
    int32 varint;
};
