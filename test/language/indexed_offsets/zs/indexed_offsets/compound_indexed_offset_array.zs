package indexed_offsets.compound_indexed_offset_array;

const uint8 NUM_COMPOUND_ELEMENTS = 5;

struct Compound
{
    uint32  id;
    bit:3   value;
};

struct CompoundIndexedOffsetArray
{
    uint32      offsets[NUM_COMPOUND_ELEMENTS];
    bit:1       spacer;

offsets[@index]:
    Compound    data[NUM_COMPOUND_ELEMENTS];
};
