package packed_compound_array_242_error;

zserio_compatibility_version("2.4.2");

struct Compound
{
    uint32 field;
    string text;
};

choice TestChoice(bool selector) on selector
{
    case true:
        Compound array1[];
    case false:
        packed Compound array2[]; // binary encoding of packed arrays changed in 2.5.0
};
