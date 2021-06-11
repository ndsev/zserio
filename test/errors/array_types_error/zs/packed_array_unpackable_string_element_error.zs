package packed_array_unpackable_string_element_error;

struct PackedArrayUnpackableStringElementError
{
    packed uint32 array1[];
    packed string array2[]; // unpackable
};
