package non_compound_subtype_error;

struct Response
{
    uint64 value;
};

subtype int32 Request;

service Service
{
    rpc Response powerOfTwo(Request);
};
