package duplicated_method_error;

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
    Response powerOfTwo(Request32);
    Response powerOfTwo(Request16);
};
