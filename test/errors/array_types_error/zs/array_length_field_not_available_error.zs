package array_length_field_not_available_error;

struct Container
{
    int16 array1Size;
    int32 array1[array1Size]; // array1Size is visible
    int32 array2[array2Size]; // array2Size not available
    int16 array2Size;
};
