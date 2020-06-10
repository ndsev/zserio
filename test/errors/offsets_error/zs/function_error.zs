package function_error;

struct Holder
{
    uint32 offsets[];
};

struct Test
{
    Holder holder;

getHolderField().offsets[@index]:
    uint32 array[];

    function Holder getHolderField()
    {
        return holder;
    }
};
