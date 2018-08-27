package duplicated_rpc_error;

struct Response
{
    uint64 value;
};

struct Request32
{
    int32 value;
};

struct Request16
{
    int16 value;
};

service Service
{
    rpc Response powerOfTwo(Request32);
    rpc Response powerOfTwo(Request16);
};
