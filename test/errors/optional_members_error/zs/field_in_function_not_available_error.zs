package field_in_function_not_available_error;

struct Container
{
    bool    hasExtraData;
    int32   extraData if hasExtra();
    int32   specialData if hasSpecial();
    bool    hasSpecialData;

    function bool hasExtra()
    {
        return hasExtraData; // hasExtraData is available in the call on line 6
    }

    function bool hasSpecial()
    {
        return hasSpecialData; // hasSpecialData not available in the call on line 7
    }
};

