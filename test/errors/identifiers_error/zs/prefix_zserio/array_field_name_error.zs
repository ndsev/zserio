package prefix_zserio.array_field_name_error;

union Test
{
    string arrayZserio[];
    uint32 zsErIoArray[]; // zserio prefix!
};
