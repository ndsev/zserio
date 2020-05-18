package string_integer_concatenation_error;

struct StringIntegerConcatenationError
{
    int32  data;

    function string Wrong()
    {
        return "Some string " + data;
    }
};
