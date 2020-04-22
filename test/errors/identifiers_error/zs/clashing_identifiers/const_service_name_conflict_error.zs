package clashing_identifiers.const_service_name_conflict_error;

const uint32 Math = 13;

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
