package service_types.simple_service;

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
    bytes powerOfTwoRaw(bytes);
};
