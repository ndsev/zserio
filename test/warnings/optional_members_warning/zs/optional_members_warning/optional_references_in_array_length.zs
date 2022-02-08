package optional_members_warning.optional_references_in_array_length;

struct Container
{
    bool hasArray1;
    uint8 arrayLength if hasArray1;
    uint8 array1[arrayLength] if hasArray1; // no warning

    uint8 array2[arrayLength]; // warning
    optional uint8 array3[arrayLength]; // warning
};
