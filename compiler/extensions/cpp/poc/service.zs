package service_poc;

struct Response
{
    uint64 value;
};

struct Request
{
    int32 value;
};

service SimpleService
{
    Response powerOfTwo(Request);
    Response powerOfFour(Request);
};
