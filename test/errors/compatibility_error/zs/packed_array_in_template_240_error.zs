zserio_compatibility_version("2.4.0");

package packed_array_in_template_240_error;

union Templated<T>
{
    packed T array[];
    uint32 other;
};
