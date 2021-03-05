package package_symbols.pubsub_with_service_clash_error;

struct Request
{
    int32 value;
};

struct Response
{
    uint64 value;
};

service Math_Service
{
    Response powerOfTwo(Request);
};

pubsub MathService
{
    topic("math_service/request") Request powerOfTwoRequest;
    topic("math_service/response") Response powerOfTwoResponse;
};
