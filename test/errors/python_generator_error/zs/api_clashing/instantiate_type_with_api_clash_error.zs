package api_clashing.instantiate_type_with_api_clash_error;

struct ApiTemplate<T>
{
    T field;
};

instantiate ApiTemplate<uint32> Api;
