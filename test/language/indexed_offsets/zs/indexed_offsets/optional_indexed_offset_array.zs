package indexed_offsets.optional_indexed_offset_array;

const uint8 NUM_OPTIONAL_ELEMENTS = 5;

struct OptionalIndexedOffsetArray
{
    uint32  offsets[NUM_OPTIONAL_ELEMENTS];
    bool    hasOptional;

offsets[@index]:
    string  data[NUM_OPTIONAL_ELEMENTS] if hasOptional == true;

    bit:6   field;
};
