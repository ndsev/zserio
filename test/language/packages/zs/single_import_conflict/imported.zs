package single_import_conflict.imported;

const uint32 CONST_A = 42;
const uint32 CONST_B = 0;


struct SimpleStructure
{
    uint32          value;
};

struct SimpleParamStructure(uint8 numBits)
{
    bit<numBits>    value;
};
