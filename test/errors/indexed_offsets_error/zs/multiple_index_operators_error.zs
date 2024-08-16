package multiple_index_operators_error;

struct Holder
{
    uint32 offsets[];
};

struct MultipleIndexOperatorsError
{
    Holder holders[];
holders[@index].offsets[@index]:
    string fields[];
};
