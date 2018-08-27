package parameterized_struct_error;

struct Response
{
    uint64 value;
};

// parameterized types cannot be deserialized correctly since parameters are not being written in the stream
struct Request(bit:8 size)
{
    int<size> value;
};

service Service
{
    rpc Response powerOfTwo(Request);
};

