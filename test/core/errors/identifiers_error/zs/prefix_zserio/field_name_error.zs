package prefix_zserio.field_name_error;

struct Test
{
    uint32 field1;
    uint32 field2ZserioAllowedHere;
    string zserioField; // zserio prefix!
};
