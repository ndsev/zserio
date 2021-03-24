package reserved_keywords.cpp_keyword_function_name_error;

struct Item
{
    uint16  param;
    function bool auto() // reserved keyword in generated C++
    {
        return param != 0;
    }
};
