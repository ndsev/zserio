package reserved_keywords.cpp_keyword_struct_name_error;

struct auto // reserved keyword in generated C++, however this will fail due to first lower case letter
{
    uint16  param;
    uint32  value;
};
