package prefix_zserio.parameter_name_error;

struct Test(uint32 paramZserio)
{
    bool b;
};

struct TestZserio(string zserioParam) // zserio prefix!
{
    uint32 field;
};
