package non_constant_string_concatenation_error;

struct NonConstantStringConcatenationError
{
    string data;

    function string wrong()
    {
        return "Some string " + data;
    }
};
