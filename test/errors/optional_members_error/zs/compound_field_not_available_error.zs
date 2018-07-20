package compound_field_not_available_error;

struct Container
{
    Header  header1;
    int32   field1 if header1.hasOptional;  // header1 already defined
    int32   field2 if header2.hasOptional;  // header2 not available
    Header  header2;
};

// check that Header can be defined after Container
struct Header
{
    bool hasOptional;
};
