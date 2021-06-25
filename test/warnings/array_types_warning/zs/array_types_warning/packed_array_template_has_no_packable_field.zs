package array_types_warning.packed_array_template_has_no_packable_field;

struct Template<T>
{
    packed T array[];
};

struct Packable
{
    uint32 id;
    float64 value;
    string text;
};

struct Unpackable
{
    float64 value;
    string text;
};

instantiate Template<uint32> T_u32;
instantiate Template<string> T_str; // unpackable!
instantiate Template<Packable> T_packable;
instantiate Template<Unpackable> T_unpackable; // unpackable!
