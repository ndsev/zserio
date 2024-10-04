package prefix_zserio.function_name_error;

struct Test
{
    uint32 value;

    function bool greaterThanTen()
    {
        return value > 10;
    }

    function bool zserioLessThenTen() // zserio prefix!
    {
        return value < 10;
    }
};
