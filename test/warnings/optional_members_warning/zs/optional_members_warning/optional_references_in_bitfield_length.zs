package optional_members_warning.optional_references_in_bitfield_length;

struct Container
{
    bool hasNumBits;
    bit:4 numBits if hasNumBits;
    bit<numBits> bitfield1 if hasNumBits; // no warning

    bit<numBits> bitfield2; // warning
    optional bit<numBits> bitfield3; // warning
};
