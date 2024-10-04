package field_itself_not_available_error;

struct Container
{
    bool    hasExtraData;
    int32   extraData if extraData > 0; // error, extraData is still not available
    bool    hasSpecialData;
};
