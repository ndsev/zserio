package service_types_warning;

struct Request
{
    uint32 value;
};

struct Response
{
    uint64 value;
};

service Math
{
    rpc Response powerOfTwo(Request);
};

service Accumulator
{
    rpc Response accumulate(Request);
};
