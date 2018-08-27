package service_service_name_conflict_error;

struct Request
{
    int32 value;
};

struct Response
{
    uint64 value;
};

service Math
{
    rpc Response powerOfTwo(Request);
};

service Math
{
    rpc Response powerOfTwo(Request);
};
