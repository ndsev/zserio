package single_type_import.imported;

struct SimpleStructure
{
    uint32          value;
};

struct SimpleParamStructure(uint8 numBits)
{
    bit<numBits>    value;
};
