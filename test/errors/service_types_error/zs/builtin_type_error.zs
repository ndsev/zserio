package builtin_type_error;

struct Response
{
    uint64 value;
};

service Service
{
    Response powerOfTwo(int32);
};
