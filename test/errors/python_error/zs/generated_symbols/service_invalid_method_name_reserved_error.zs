package generated_symbols.service_invalid_method_name_reserved_error;

struct Request
{};

struct Response
{};

service TestService
{
    Response __eq__(Request); // starts with '_'
};
