package generated_symbols.service_invalid_method_name_private_error;

struct Request
{};

struct Response
{};

service TestService
{
    Response _service(Request); // starts with '_' (and yet clashes with generated client's member)
};
