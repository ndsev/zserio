package functions.structure_array_param;

/* This Zserio uses the function which is used as parameter for array elements. This produced
 * the compilation error of generated Java and C++ sources in the past. */

struct ChildStructure(uint8 bitSize)
{
    bit<bitSize>    value;
};

struct ParentStructure
{
    uint8   numChildren;
    ChildStructure(getChildBitSize()) children[numChildren];

    function uint8 getChildBitSize()
    {
        return 19;
    }
};
