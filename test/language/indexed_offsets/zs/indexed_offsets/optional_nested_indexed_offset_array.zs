package indexed_offsets.optional_nested_indexed_offset_array;

struct Header
{
    uint16  length;
    uint32  offsets[length];
};

struct OptionalNestedIndexedOffsetArray
{
    Header  header;

header.offsets[@index]:
    string  data[header.length] if header.length > 0;

    bit:6   field;
};
