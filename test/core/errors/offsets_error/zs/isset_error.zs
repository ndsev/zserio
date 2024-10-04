package isset_error;

bitmask uint32 Bitmask
{
    FIRST,
    SECOND
};

struct IsSetError
{
    Bitmask bm;
isset(bm, Bitmask.FIRST):
    string field;
};
