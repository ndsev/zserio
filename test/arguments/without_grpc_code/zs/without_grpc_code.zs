package without_grpc_code;

struct Response
{
    uint64 value;
};

struct Request
{
    int32 value;
};

service Service
{
    rpc Response powerOfTwo(Request);
};
