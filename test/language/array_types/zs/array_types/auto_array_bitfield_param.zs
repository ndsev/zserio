package array_types.auto_array_bitfield_param;

// There was a bug in C++ that empty constructor of such structure always threw.
struct ParameterizedBitfieldLength(bit:4 numBits)
{
    bit<numBits> dynamicBitfieldArray[];
};
