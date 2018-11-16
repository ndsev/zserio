package compound_field_not_available_error;

struct Container
{
    Header  header1;
    int32   field1 if hasOptional1();   // header1 already defined
    int32   field2 if hasOptional2();   // header2 not available
    Header  header2;

    function bool hasOptional1()
    {
        return header1.hasOptional;
    }

    function bool hasOptional2()
    {
        return header2.hasOptional;
    }
};

// check that Header can be defined after Container
struct Header
{
    bool hasOptional;
};
