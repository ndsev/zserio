package api_clashing.service_with_api_clash_error;

struct Response
{
    uint64 value;
};

struct Request
{
    int32 value;
};

service Api
{
    Response powerOfTwo(Request);
};
