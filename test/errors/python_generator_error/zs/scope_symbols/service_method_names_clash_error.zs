package scope_symbols.service_method_names_clash_error;

struct Request
{
    int32 value;
};

struct Response
{
    uint64 value;
};

service TestService
{
    Response powerOfTwo(Request);
    Response power_of_two(Request);
};
