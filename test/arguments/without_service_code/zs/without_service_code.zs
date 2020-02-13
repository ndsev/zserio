package without_service_code;

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
    Response powerOfTwo(Request);
};
