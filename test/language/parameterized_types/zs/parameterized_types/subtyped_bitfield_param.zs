package parameterized_types.subtyped_bitfield_param;

struct SubtypedBitfieldParamHolder
{
    SubtypedBitfieldParam(11)   subtypedBitfieldParam;
};

// This structure must be defined after SubtypedBitfieldParamHolder to check evaluation phase properly.
struct SubtypedBitfieldParam(ParamType param)
{
    uint16    value;
    uint32    extraValue if param == 11;
};

// This subtype must be defined after SubtypedBitfieldParam to check evaluation phase properly.
subtype bit:5 ParamType;
