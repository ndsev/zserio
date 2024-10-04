package wrong_return_type_error;

struct WrongReturnTypeFunction
{
    bit:1   spacer;

    function bool wrongFunction()
    {
        return 1;
    }
};
