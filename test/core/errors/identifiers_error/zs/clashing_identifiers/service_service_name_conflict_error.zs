package clashing_identifiers.service_service_name_conflict_error;

struct Request
{
    int32 value;
};

struct Response
{
    uint64 value;
};

service Math
{
    Response powerOfTwo(Request);
};

service Math
{
    Response powerOfTwo(Request);
};
