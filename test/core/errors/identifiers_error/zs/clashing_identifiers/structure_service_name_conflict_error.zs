package clashing_identifiers.structure_service_name_conflict_error;

struct Math
{
    float64 pi;
    float64 pi_2;
};

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
