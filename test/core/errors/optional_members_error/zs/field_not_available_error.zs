package field_not_available_error;

struct Container
{
    bool    hasExtraData;
    int32   extraData if hasExtraData;      // ok, hasExtraData already defined
    int32   specialData if hasSpecialData;  // hasSpecialData is not available
    bool    hasSpecialData;
};
