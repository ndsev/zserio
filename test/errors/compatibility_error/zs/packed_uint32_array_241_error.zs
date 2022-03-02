zserio_compatibility_version("2.4.1");

package packed_uint32_array_241_error;

struct TestStruct
{
    packed uint32 array[]; // binary encoding of packed arrays changed in 2.5.0
};
